/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import java.util.regex.Pattern;

public abstract class HtmlUtils {
    private static final Pattern HTML_COMMENT_PATTERN = Pattern.compile("<!--.*?-->", 2);
    private static final Pattern HTML_SCRIPT_PATTERN = Pattern.compile("<script[^>]*?>[\\s\\S]*?<\\/script>", 2);
    private static final Pattern HTML_STYLE_PATTERN = Pattern.compile("<style[^>]*?>[\\s\\S]*?<\\/style>", 2);
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*?>", 2);

    public static final String removeHtmlElement(String htmlText) {
        htmlText = HTML_COMMENT_PATTERN.matcher(htmlText).replaceAll("");
        htmlText = HTML_SCRIPT_PATTERN.matcher(htmlText).replaceAll("");
        htmlText = HTML_STYLE_PATTERN.matcher(htmlText).replaceAll("");
        htmlText = HTML_TAG_PATTERN.matcher(htmlText).replaceAll("");
        return htmlText;
    }
}
