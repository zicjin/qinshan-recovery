#Requires -Version 5.1
<#
.SYNOPSIS
Expose an existing WSL localhost application through a Windows LAN address.
.DESCRIPTION
Run in an elevated Windows PowerShell. This creates one TCP portproxy entry
and one scoped Windows firewall rule. It does not restart WSL or the app.
Use -Remove to remove only the matching entry and this script's firewall rule.
#>
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$ListenAddress,
    [ValidateRange(1024, 65535)]
    [int]$Port = 18087,
    [switch]$Remove
)

$ErrorActionPreference = 'Stop'
$ruleName = "LIMS-WSL-LAN-$($ListenAddress.Replace('.', '-'))-$Port"
$identity = [Security.Principal.WindowsIdentity]::GetCurrent()
$principal = New-Object Security.Principal.WindowsPrincipal($identity)
if (-not $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    throw 'Open Windows PowerShell as Administrator, then run this script again.'
}

$address = $null
if (-not [Net.IPAddress]::TryParse($ListenAddress, [ref]$address) -or
    $address.AddressFamily -ne [Net.Sockets.AddressFamily]::InterNetwork) {
    throw 'ListenAddress must be a literal RFC1918 IPv4 address.'
}
$octets = $address.GetAddressBytes()
$privateAddress = $octets[0] -eq 10 -or
    ($octets[0] -eq 172 -and $octets[1] -ge 16 -and $octets[1] -le 31) -or
    ($octets[0] -eq 192 -and $octets[1] -eq 168)
if (-not $privateAddress) {
    throw 'ListenAddress must be an RFC1918 address on the Windows LAN interface.'
}
$ListenAddress = $address.ToString()

function Invoke-PortProxy {
    param([string[]]$NetshArguments)
    & netsh.exe interface portproxy @NetshArguments
    if ($LASTEXITCODE -ne 0) {
        throw "netsh portproxy failed (exit $LASTEXITCODE)."
    }
}

function Get-ExistingProxy {
    $entries = & netsh.exe interface portproxy show v4tov4
    if ($LASTEXITCODE -ne 0) { throw 'Cannot inspect existing portproxy entries.' }
    foreach ($line in $entries) {
        if ($line -match '^\s*(\d+\.\d+\.\d+\.\d+)\s+(\d+)\s+(\d+\.\d+\.\d+\.\d+)\s+(\d+)\s*$') {
            if ($Matches[1] -eq $ListenAddress -and [int]$Matches[2] -eq $Port) {
                return [pscustomobject]@{
                    Address = $Matches[3]
                    Port = [int]$Matches[4]
                }
            }
        }
    }
    return $null
}

function Assert-LocalHttp {
    param([string]$HostAddress)
    # An explicit proxy bypass makes this check independent of Windows/VPN proxy settings.
    $request = [Net.HttpWebRequest]::Create("http://${HostAddress}:$Port/")
    $request.Proxy = $null
    $request.Timeout = 8000
    $request.AllowAutoRedirect = $false
    $response = $request.GetResponse()
    try {
        if ([int]$response.StatusCode -ne 200) {
            throw "Unexpected HTTP status from ${HostAddress}: $([int]$response.StatusCode)."
        }
    } finally {
        $response.Close()
    }
}

$existing = Get-ExistingProxy
if ($existing -and ($existing.Address -ne '127.0.0.1' -or $existing.Port -ne $Port)) {
    throw 'A different portproxy target already uses this LAN address/port; left unchanged.'
}

if ($Remove) {
    if ($existing) {
        Invoke-PortProxy -NetshArguments @('delete', 'v4tov4', "listenaddress=$ListenAddress", "listenport=$Port")
    }
    Get-NetFirewallRule -Name $ruleName -ErrorAction SilentlyContinue |
        Remove-NetFirewallRule
    Write-Output "Removed the matching LIMS LAN mapping and its firewall rule."
    return
}

if (-not (Get-NetIPAddress -AddressFamily IPv4 -IPAddress $ListenAddress -ErrorAction SilentlyContinue)) {
    throw 'ListenAddress is not assigned to a Windows interface.'
}
Assert-LocalHttp '127.0.0.1'
$service = Get-Service iphlpsvc
if ($service.Status -ne 'Running') { Start-Service iphlpsvc }

$addedProxy = $false
$addedRule = $false
try {
    if (-not $existing) {
        $listeners = @(Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
            Where-Object { $_.LocalAddress -in @($ListenAddress, '0.0.0.0', '::') })
        if ($listeners.Count -gt 0) {
            throw 'Another Windows listener occupies this address/port; left unchanged.'
        }
        Invoke-PortProxy -NetshArguments @('add', 'v4tov4', "listenaddress=$ListenAddress", "listenport=$Port",
            'connectaddress=127.0.0.1', "connectport=$Port")
        $addedProxy = $true
    }
    if (-not (Get-NetFirewallRule -Name $ruleName -ErrorAction SilentlyContinue)) {
        New-NetFirewallRule -Name $ruleName -DisplayName "LIMS WSL LAN $Port ($ListenAddress)" `
            -Direction Inbound -Action Allow -Protocol TCP -LocalAddress $ListenAddress `
            -LocalPort $Port -RemoteAddress LocalSubnet -Profile Any | Out-Null
        $addedRule = $true
    }
    $ready = $false
    for ($attempt = 0; $attempt -lt 5; $attempt++) {
        try { Assert-LocalHttp $ListenAddress; $ready = $true; break }
        catch { if ($attempt -eq 4) { throw }; Start-Sleep -Milliseconds 300 }
    }
    if (-not $ready) { throw 'Windows LAN HTTP verification failed.' }
} catch {
    if ($addedRule) {
        Get-NetFirewallRule -Name $ruleName -ErrorAction SilentlyContinue | Remove-NetFirewallRule
    }
    if ($addedProxy) {
        Invoke-PortProxy -NetshArguments @('delete', 'v4tov4', "listenaddress=$ListenAddress", "listenport=$Port")
    }
    throw
}

Write-Output "Windows localhost and LAN HTTP checks passed: http://${ListenAddress}:$Port/"
Write-Output 'Check the same URL from another LAN device to verify the network path.'
