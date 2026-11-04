/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.word.utils;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@GikamBean
@Component
public class ScienNumberChangeUtil {
    private Pattern sciencePattern = Pattern.compile("^((-|\\+)?\\d+(\\.\\d+)?)(E|e)(\\+|-)?(\\d+)$");
    private Pattern upSciencePattern = Pattern.compile("^((-|\\+)?\\d+(\\.\\d+)?)\u00d710(\u207b|\u207a)?([\u2070\u00b9\u00b2\u00b3\u2074\u2075\u2076\u2077\u2078\u2079]+)$");
    private Map<Character, Character> specialCharacterMap = new HashMap<Character, Character>();

    @PostConstruct
    public void init() {
        this.specialCharacterMap.put(Character.valueOf('0'), Character.valueOf('\u2070'));
        this.specialCharacterMap.put(Character.valueOf('1'), Character.valueOf('\u00b9'));
        this.specialCharacterMap.put(Character.valueOf('2'), Character.valueOf('\u00b2'));
        this.specialCharacterMap.put(Character.valueOf('3'), Character.valueOf('\u00b3'));
        this.specialCharacterMap.put(Character.valueOf('4'), Character.valueOf('\u2074'));
        this.specialCharacterMap.put(Character.valueOf('5'), Character.valueOf('\u2075'));
        this.specialCharacterMap.put(Character.valueOf('6'), Character.valueOf('\u2076'));
        this.specialCharacterMap.put(Character.valueOf('7'), Character.valueOf('\u2077'));
        this.specialCharacterMap.put(Character.valueOf('8'), Character.valueOf('\u2078'));
        this.specialCharacterMap.put(Character.valueOf('9'), Character.valueOf('\u2079'));
        this.specialCharacterMap.put(Character.valueOf('\u2070'), Character.valueOf('0'));
        this.specialCharacterMap.put(Character.valueOf('\u00b9'), Character.valueOf('1'));
        this.specialCharacterMap.put(Character.valueOf('\u00b2'), Character.valueOf('2'));
        this.specialCharacterMap.put(Character.valueOf('\u00b3'), Character.valueOf('3'));
        this.specialCharacterMap.put(Character.valueOf('\u2074'), Character.valueOf('4'));
        this.specialCharacterMap.put(Character.valueOf('\u2075'), Character.valueOf('5'));
        this.specialCharacterMap.put(Character.valueOf('\u2076'), Character.valueOf('6'));
        this.specialCharacterMap.put(Character.valueOf('\u2077'), Character.valueOf('7'));
        this.specialCharacterMap.put(Character.valueOf('\u2078'), Character.valueOf('8'));
        this.specialCharacterMap.put(Character.valueOf('\u2079'), Character.valueOf('9'));
    }

    public String changeUpScienNumberShow(String word) {
        if (StringUtils.isEmpty(word)) {
            return word;
        }
        Matcher scienceMatcher = this.sciencePattern.matcher(word);
        boolean match = scienceMatcher.find();
        if (!match) {
            return word;
        }
        String preNumber = scienceMatcher.group(1);
        String flag = scienceMatcher.group(5);
        String lastNumber = scienceMatcher.group(6);
        ArrayList<Character> upCharList = new ArrayList<Character>();
        for (char upChar : lastNumber.toCharArray()) {
            upCharList.add(Character.valueOf(upChar));
        }
        String changeLastNumber = upCharList.stream().map(e -> this.specialCharacterMap.get(e) + "").reduce((a, b) -> a + b).get();
        String specialUp = "-".equals(flag) ? "\u207b" + changeLastNumber : changeLastNumber;
        String changeWord = String.format("%s\u00d710%s", Double.parseDouble(preNumber), specialUp);
        return changeWord;
    }

    public String upBackScienNumberShow(String word) {
        if (StringUtils.isEmpty(word)) {
            return word;
        }
        Matcher scienceMatcher = this.upSciencePattern.matcher(word);
        boolean match = scienceMatcher.find();
        if (!match) {
            return word;
        }
        String preNumber = scienceMatcher.group(1);
        String flag = scienceMatcher.group(4);
        String lastNumber = scienceMatcher.group(5);
        ArrayList<Character> upCharList = new ArrayList<Character>();
        for (char upChar : lastNumber.toCharArray()) {
            upCharList.add(Character.valueOf(upChar));
        }
        String changeLastNumber = upCharList.stream().map(e -> this.specialCharacterMap.get(e) + "").reduce((a, b) -> a + b).get();
        String specialUp = "\u207b".equals(flag) ? "-" + changeLastNumber : changeLastNumber;
        String changeWord = String.format("%sE%s", preNumber, specialUp);
        return changeWord;
    }
}
