# Courier report email configuration

The finite Oracle seed includes the published `emc.reportSendExpress` page v20,
Oracle mapper v14 and Java service v23 with their original effective histories,
page component/translation graphs and menu `20250200002`. Installation reads
only the delivered SQL and metadata. The raw acquisition files and parent
renderer are generation inputs and are not runtime dependencies.

Before invoking the original `sendEmail` operation, set
`owned.report-send.mail.footer-text` in the target's private external Spring
configuration. Supply the target organization, contact, address and any required
confidentiality text there. The delivered example leaves the property commented
out and supplies no source organization or default. In a Java `.properties`
file, `\n` represents a line break; protect the private file with the same access
controls as the other deployment configuration.

The service requires this property on each email invocation through the existing
`ApplicationContextHelper.getEnvironment().getRequiredProperty()`. Missing
configuration fails before MIME creation, SMTP sending or updates to report
sending state. Application startup, page retrieval and list queries do not read
this mail-only property.

The original body retains its two format placeholders for report numbers and
business contact information. The target footer is appended after formatting,
so `%` remains plain text. Spring `HtmlUtils.htmlEscape()` escapes the footer's
HTML characters, then CRLF, CR and LF become HTML line breaks. The property is
plain footer text, including any URL operators choose to display; it does not
configure an HTTP destination. Actual mail delivery continues through the
existing `MailHelper`, `JavaMailSender` and private `spring.mail.*` settings.
Use an isolated capture transport for acceptance and keep production SMTP
disconnected from recovery validation.

`bootstrap-provenance.json` records the pinned source receipts and original row
hashes. The active Java resource and its effective history each retain their own
`CONTENT` and `CLASSCONTENT` source hashes; their class names differ and are not
reconstructed from one another. Each of these four source fields has the same
finite private footer removed and the required property appended. The two row
records additionally contain `delivered_row_sha256`, field-level source and
delivered hashes and the explicit transformation rule. The INSERT hash and exact
read-only SQL verifier bind the complete delivered fields. All other values
retain exact pinned source equality.

The page references `emc.reportSendExpress.page.reportSendExpressDetailPage`
when the project cell is clicked. That path and resource name are absent from
the pinned whole active-resource/effective-history captures (54 active resources,
33 effective histories at capture time). The original reference is preserved;
no detail page is fabricated. This is a source limitation at that acquisition
time and does not assert that every historical or deleted resource is absent.

This installation addition does not establish complete business/API acceptance.
The normal partial installation guard and all full-system blockers remain in
place. The SQL seed still requires empty target configuration and is not an
existing-database upgrade command.
