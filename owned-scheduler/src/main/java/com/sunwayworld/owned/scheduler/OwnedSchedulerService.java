package com.sunwayworld.owned.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

/** Isolated file replay executor. Historical acquisition jobs never run here. */
public final class OwnedSchedulerService {
    public static final String SOURCE="isolated-replay", REPLAY_TASK="owned.scheduler.replay";
    private final Path root, journal;
    private final int maxAttempts;
    private final List<OwnedSchedulerRun> records=new ArrayList<OwnedSchedulerRun>();
    private final Map<String,OwnedSchedulerRun> completed=new LinkedHashMap<String,OwnedSchedulerRun>();
    public OwnedSchedulerService(Path directory,int maxAttempts) throws IOException {
        if(maxAttempts<1||maxAttempts>3)throw new IllegalArgumentException("maxAttempts must be 1..3");
        for(Path parent=directory.toAbsolutePath();parent!=null;parent=parent.getParent())if(Files.isSymbolicLink(parent))throw new IOException("isolated root symlink disallowed");
        root=directory.toRealPath(LinkOption.NOFOLLOW_LINKS);this.maxAttempts=maxAttempts;
        if(Files.isSymbolicLink(root)||!Files.isDirectory(root))throw new IOException("invalid isolated root");
        journal=root.resolve("runs.jsonl");
        if(Files.exists(journal,LinkOption.NOFOLLOW_LINKS)) {
            safe(journal);if(Files.size(journal)>1048576)throw new IOException("journal capacity exceeded");
            for(String line:Files.readAllLines(journal,StandardCharsets.UTF_8)){
                JSONObject o=JSON.parseObject(line);Map<String,String> m=new LinkedHashMap<String,String>();
                for(Map.Entry<String,Object> e:o.getJSONObject("metadata").entrySet())m.put(e.getKey(),String.valueOf(e.getValue()));
                OwnedSchedulerRun run=new OwnedSchedulerRun(o.getString("id"),o.getString("task"),o.getString("source"),o.getString("scenario"),OwnedSchedulerRun.Status.valueOf(o.getString("status")),o.getIntValue("attempt"),o.getString("message"),Instant.parse(o.getString("startedAt")),m);
                records.add(run);if(run.getStatus()!=OwnedSchedulerRun.Status.RETRYING)completed.put(run.getId(),run);
            }
        }
    }
    public List<Map<String,String>> tasks(){
        List<Map<String,String>> result=new ArrayList<Map<String,String>>();
        result.add(task(REPLAY_TASK,"enabled","owned file-copy test task; no historical business equivalence claim"));
        result.add(task("dcuAcquisition","disabled-in-isolated-env","PfEquiptAcqFileServiceImpl#dcuAcquisition; fixedDelay=10000"));
        result.add(task("dcuSceneAcquisition","disabled-in-isolated-env","PfEquiptAcqFileServiceImpl#dcuSceneAcquisition; fixedDelay=15000"));
        result.add(task("mergeEquiptAcqFile","disabled-in-isolated-env","PfEquiptAcqFileServiceImpl#mergeEquiptAcqFile; fixedDelay=20000"));
        result.add(task("emc.dcuAcquisition","disabled-in-isolated-env","EmcEquiptAcqFileServiceImpl#dcuAcquisition; profile equipt-acq-file; fixedDelay=15000"));
        return result;
    }
    public synchronized OwnedSchedulerRun trigger(String task,String scenario,String id) throws IOException {
        if(!id.matches("[a-zA-Z0-9_-]{1,64}"))throw new IllegalArgumentException("invalid request id");
        boolean known=false;for(Map<String,String> item:tasks())if(task.equals(item.get("name")))known=true;
        if(!known)throw new NoSuchElementException("unknown task");
        if(!Arrays.asList("success","failure","retry").contains(scenario))throw new IllegalArgumentException("invalid scenario");
        OwnedSchedulerRun prior=completed.get(id);
        if(prior!=null){if(!task.equals(prior.getTask())||!scenario.equals(prior.getScenario()))throw new IllegalStateException("request id conflict");return prior;}
        for(OwnedSchedulerRun priorAttempt:records)if(id.equals(priorAttempt.getId()))throw new IllegalStateException("incomplete prior request requires new request id");
        if(records.size()+maxAttempts>1000)throw new IllegalStateException("journal capacity exceeded");
        if(!REPLAY_TASK.equals(task))return record(id,task,scenario,OwnedSchedulerRun.Status.DISABLED,0,"disabled-in-isolated-env",Collections.<String,String>emptyMap());
        Path outputs=root.resolve("outputs"); safe(outputs);
        Path target=outputs.resolve(id+".bin");safe(target);
        if(Files.exists(target,LinkOption.NOFOLLOW_LINKS))throw new IllegalStateException("output already exists");
        OwnedSchedulerRun last=null;
        for(int attempt=1;attempt<=maxAttempts;attempt++){
            // Each failed attempt actually opens a missing fixture. The retry plan selects
            // the valid fixture on its third attempt; it never contacts an external service.
            boolean available="success".equals(scenario)||("retry".equals(scenario)&&attempt==3);
            Path input=root.resolve("fixtures").resolve(available?"success.bin":"missing.bin");safe(input);
            try {
                Map<String,String> metadata=new LinkedHashMap<String,String>();metadata.put("sha256",OwnedSchedulerReplay.copy(input,target));metadata.put("bytes",String.valueOf(Files.size(target)));
                return record(id,task,scenario,OwnedSchedulerRun.Status.SUCCESS,attempt,"fixture copied",metadata);
            }catch(IOException failure){
                Files.deleteIfExists(target);
                last=record(id,task,scenario,attempt==maxAttempts?OwnedSchedulerRun.Status.FAILED:OwnedSchedulerRun.Status.RETRYING,attempt,"fixture copy failed",Collections.<String,String>emptyMap());
            }
        }
        return last;
    }
    public synchronized List<OwnedSchedulerRun> history(String task){List<OwnedSchedulerRun> result=new ArrayList<OwnedSchedulerRun>();for(OwnedSchedulerRun run:records)if(task.equals(run.getTask()))result.add(run);return result;}
    private OwnedSchedulerRun record(String id,String task,String scenario,OwnedSchedulerRun.Status status,int attempt,String message,Map<String,String> details)throws IOException{
        Map<String,String> metadata=new LinkedHashMap<String,String>(details);metadata.put("externalWrites","blocked");metadata.put("endpoint","none");
        OwnedSchedulerRun result=new OwnedSchedulerRun(id,task,SOURCE,scenario,status,attempt,message,Instant.now(),metadata);
        Map<String,Object> data=new LinkedHashMap<String,Object>();data.put("id",id);data.put("task",task);data.put("source",SOURCE);data.put("scenario",scenario);data.put("status",status.name());data.put("attempt",attempt);data.put("message",message);data.put("startedAt",result.getStartedAt().toString());data.put("metadata",metadata);
        safe(journal);byte[] line=(JSON.toJSONString(data)+"\n").getBytes(StandardCharsets.UTF_8);
        if(Files.exists(journal)&&Files.size(journal)+line.length>1048576)throw new IOException("journal capacity exceeded");
        try(java.nio.channels.FileChannel file=java.nio.channels.FileChannel.open(journal,StandardOpenOption.CREATE,StandardOpenOption.WRITE,StandardOpenOption.APPEND)){java.nio.ByteBuffer b=java.nio.ByteBuffer.wrap(line);while(b.hasRemaining())file.write(b);file.force(true);}
        records.add(result);if(status!=OwnedSchedulerRun.Status.RETRYING)completed.put(id,result);return result;
    }
    private void safe(Path path)throws IOException{if(!path.normalize().startsWith(root))throw new IOException("outside isolated root");for(Path p=path;p!=null&&p.startsWith(root);p=p.getParent())if(Files.isSymbolicLink(p))throw new IOException("symbolic links disallowed");}
    private static Map<String,String> task(String name,String status,String reference){Map<String,String> item=new LinkedHashMap<String,String>();item.put("name",name);item.put("status",status);item.put("reference",reference);return item;}
}
