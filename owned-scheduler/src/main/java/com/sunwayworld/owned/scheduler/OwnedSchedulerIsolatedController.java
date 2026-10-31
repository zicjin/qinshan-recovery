package com.sunwayworld.owned.scheduler;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/** Explicit test entry, separate from business menus and disabled unless explicitly configured. */
@RequestMapping("/secure/owned-scheduler")
@ResponseBody
public final class OwnedSchedulerIsolatedController {
    private final OwnedSchedulerService service; private final Path tokenFile;
    public OwnedSchedulerIsolatedController(OwnedSchedulerService service,Path tokenFile)throws IOException{
        this.service=service;this.tokenFile=tokenFile.toAbsolutePath().normalize();readToken();
    }
    @GetMapping(value="/page",produces="text/html;charset=UTF-8") public ResponseEntity<String> page(){
        return ResponseEntity.ok().header("Cache-Control","no-store").header("Content-Security-Policy","default-src 'self'; script-src 'self'; style-src 'self'; frame-ancestors 'none'; base-uri 'none'").body(PAGE);
    }
    @GetMapping(value="/page.js",produces="application/javascript") public String script(){return SCRIPT;}
    @GetMapping("/tasks") public ResponseEntity<?> tasks(HttpServletRequest req){if(!authorized(req))return denied();return ResponseEntity.ok(service.tasks());}
    @PostMapping("/tasks/{task}/trigger") public ResponseEntity<?> trigger(HttpServletRequest req,@PathVariable("task")String task){
        if(!authorized(req))return denied();
        if(req.getParameterMap().size()!=2||!single(req,"scenario")||!single(req,"requestId"))return response(HttpStatus.BAD_REQUEST,"invalid request");
        try{OwnedSchedulerRun result=service.trigger(task,req.getParameter("scenario"),req.getParameter("requestId"));return response(result.getStatus()==OwnedSchedulerRun.Status.DISABLED?HttpStatus.FORBIDDEN:HttpStatus.OK,result);}
        catch(IllegalArgumentException invalid){return response(HttpStatus.BAD_REQUEST,"invalid request");}
        catch(NoSuchElementException unknown){return response(HttpStatus.NOT_FOUND,"unknown task");}
        catch(IllegalStateException conflict){return response(HttpStatus.CONFLICT,"request conflict or capacity reached");}
        catch(IOException failure){return response(HttpStatus.SERVICE_UNAVAILABLE,"replay journal unavailable");}
    }
    @GetMapping("/tasks/{task}/runs") public ResponseEntity<?> runs(HttpServletRequest req,@PathVariable("task")String task){if(!authorized(req))return denied();return ResponseEntity.ok(service.history(task));}
    private boolean authorized(HttpServletRequest req){
        try{
            if(!InetAddress.getByName(req.getRemoteAddr()).isLoopbackAddress())return false;
            String[] headers=Collections.list(req.getHeaders("X-Owned-Scheduler-Test-Token")).toArray(new String[0]);
            if(headers.length!=1||headers[0].length()>256)return false;
            return MessageDigest.isEqual(readToken(),headers[0].getBytes(StandardCharsets.UTF_8));
        }catch(IOException failure){return false;}
    }
    private byte[] readToken()throws IOException{
        for(Path p=tokenFile;p!=null;p=p.getParent())if(Files.isSymbolicLink(p))throw new IOException("token symlink disallowed");
        if(!Files.isRegularFile(tokenFile,LinkOption.NOFOLLOW_LINKS)||Files.size(tokenFile)>256)throw new IOException("invalid token file");
        byte[] token=new String(Files.readAllBytes(tokenFile),StandardCharsets.UTF_8).trim().getBytes(StandardCharsets.UTF_8);
        if(token.length<32)throw new IOException("test token must contain at least 32 bytes");return token;
    }
    private static boolean single(HttpServletRequest r,String name){return r.getParameterValues(name)!=null&&r.getParameterValues(name).length==1;}
    private static ResponseEntity<?> denied(){return response(HttpStatus.FORBIDDEN,"test entry unauthorized");}
    private static ResponseEntity<?> response(HttpStatus status,Object body){return ResponseEntity.status(status).header("Cache-Control","no-store").body(body);}
    private static final String PAGE="<!doctype html><html lang='en'><head><meta charset='UTF-8'><title>Isolated scheduler</title><script defer src='/secure/owned-scheduler/page.js'></script></head><body><h1>Isolated scheduler test entry</h1><p>Source: isolated-replay. Historical acquisition jobs remain disabled.</p><label>Test access token <input id='token' type='password' autocomplete='off'></label><button id='load'>Load tasks</button><pre id='tasks'></pre><label>Scenario <select id='scenario'><option>success</option><option>failure</option><option>retry</option></select></label><button id='trigger'>Trigger isolated replay</button><button id='history'>Refresh history</button><pre id='result' role='status'></pre></body></html>";
    private static final String SCRIPT="const $=id=>document.getElementById(id);async function call(path,body){const r=await fetch('/secure/owned-scheduler/'+path,{method:body?'POST':'GET',headers:{'X-Owned-Scheduler-Test-Token':$('token').value,'Content-Type':'application/x-www-form-urlencoded'},body});const text=await r.text();let value;try{value=JSON.parse(text);}catch(e){value=text;}return {status:r.status,value};}$('load').onclick=async()=>{$('tasks').textContent=JSON.stringify(await call('tasks'),null,2);};$('trigger').onclick=async()=>{$('result').textContent=JSON.stringify(await call('tasks/owned.scheduler.replay/trigger','scenario='+$('scenario').value+'&requestId=ui-'+Date.now()),null,2);};$('history').onclick=async()=>{$('result').textContent=JSON.stringify(await call('tasks/owned.scheduler.replay/runs'),null,2);};";
}
