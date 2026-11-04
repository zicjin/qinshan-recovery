/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.exception.MppException;
import java.io.File;
import java.util.List;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.mpp.MPPReader;

public class MppUtils {
    public static List<Task> readMppFile(File file) {
        try {
            MPPReader mppRead = new MPPReader();
            ProjectFile pf = mppRead.read(file);
            List<Task> taskList = pf.getChildTasks();
            return taskList;
        }
        catch (Exception e) {
            throw new MppException(e);
        }
    }
}
