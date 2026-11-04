/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.code.level;

import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;

public abstract class LevelCodeManager {
    public static String getChildLevelCode(String parentLevelCode, int levelSize) {
        return parentLevelCode + StringUtils.leftPad("1", levelSize);
    }

    public static String getNextLevelCode(String levelCode, int levelSize) {
        if (StringUtils.isBlank(levelCode)) {
            return StringUtils.leftPad("1", levelSize);
        }
        int length = levelCode.length();
        return levelCode.substring(0, length - levelSize) + StringUtils.leftPad("" + (NumberUtils.parseInt(levelCode.substring(length - levelSize, length)) + 1), levelSize);
    }

    public static String getParentLevelCode(String levelCode, int levelSize) {
        int length = levelCode.length();
        if (StringUtils.isBlank(levelCode) || length <= levelSize) {
            return null;
        }
        return levelCode.substring(0, length - levelSize);
    }

    public static List<String> getMissingUpperLevelCodeList(List<String> levelCodeList, int levelSize) {
        ArrayList<String> missingUpperLevelCodeList = new ArrayList<String>();
        block0: for (String levelCode : levelCodeList) {
            if (levelCode.length() <= levelSize) continue;
            String parentLevelCode = levelCode.substring(0, levelCode.length() - levelSize);
            while (true) {
                if (!levelCodeList.contains(parentLevelCode) && !missingUpperLevelCodeList.contains(parentLevelCode)) {
                    missingUpperLevelCodeList.add(parentLevelCode);
                }
                if (parentLevelCode.length() <= levelSize) continue block0;
                parentLevelCode = parentLevelCode.substring(0, parentLevelCode.length() - levelSize);
            }
        }
        return missingUpperLevelCodeList;
    }

    public static List<String> getWithUpperLevelCodeList(List<String> levelCodeList, int levelSize) {
        ArrayList<String> withUpperLevelCodeList = new ArrayList<String>();
        for (String levelCode : levelCodeList) {
            if (StringUtils.isBlank(levelCode) || withUpperLevelCodeList.contains(levelCode)) continue;
            withUpperLevelCodeList.add(levelCode);
            while (levelCode.length() > levelSize && !withUpperLevelCodeList.contains(levelCode = levelCode.substring(0, levelCode.length() - levelSize))) {
                withUpperLevelCodeList.add(levelCode);
            }
        }
        return withUpperLevelCodeList;
    }

    public static String nextLevelCode(String identity, int levelSize, @Nullable Supplier<String> parentLevelCodeSupplier, @Nullable Supplier<String> lastLevelCodeSupplier) {
        return LevelCodeManager.nextLevelCodeList(identity, 1, levelSize, parentLevelCodeSupplier, lastLevelCodeSupplier).get(0);
    }

    public static List<String> nextLevelCodeList(String identity, int qty, int levelSize, @Nullable Supplier<String> parentLevelCodeSupplier, @Nullable Supplier<String> lastLevelCodeSupplier) {
        String lastLevelCode;
        GikamConcurrentLocker.block(identity);
        String string = lastLevelCode = lastLevelCodeSupplier == null ? null : lastLevelCodeSupplier.get();
        if (StringUtils.isBlank(lastLevelCode)) {
            String parentLevelCode = parentLevelCodeSupplier == null ? "" : parentLevelCodeSupplier.get();
            ArrayList<String> levelCodeList = new ArrayList<String>();
            for (int i = 0; i < qty; ++i) {
                levelCodeList.add(parentLevelCode + StringUtils.leftPad("" + (i + 1), levelSize));
            }
            return levelCodeList;
        }
        int length = lastLevelCode.length();
        if (length % levelSize != 0) {
            throw new ApplicationRuntimeException("LEVELCODE.ERROR.INVALID_LEVELCODE_AND_LEVELSIZE", new String[0]);
        }
        if (length > levelSize) {
            String prefix = lastLevelCode.substring(0, length - levelSize);
            return LevelCodeManager.nextLevelCodeList(lastLevelCode.substring(length - levelSize, length), qty, levelSize).stream().map(c -> prefix + c).collect(Collectors.toList());
        }
        return LevelCodeManager.nextLevelCodeList(lastLevelCode, qty, levelSize);
    }

    private static List<String> nextLevelCodeList(String lastLevelCode, int qty, int levelSize) {
        int lastLevelCodeInteger = NumberUtils.parseInt(lastLevelCode);
        ArrayList<String> levelCodeList = new ArrayList<String>();
        for (int i = 0; i < qty; ++i) {
            int nextLevelCodeInteger = lastLevelCodeInteger + (i + 1);
            String nextLevelCode = StringUtils.leftPad("" + nextLevelCodeInteger, levelSize);
            if (nextLevelCode.length() > levelSize) {
                throw new ApplicationRuntimeException("LEVELCODE.ERROR.REACH_MAX_LEVELCODE", new String[0]);
            }
            levelCodeList.add(nextLevelCode);
        }
        return levelCodeList;
    }
}
