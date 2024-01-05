package com.samuel.zuo.markdown;


import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

/**
 * description: MarkdownUtil
 * date: 2023/12/29 12:46
 * author: samuel_zuo
 * version: 1.0
 */
public class MarkdownUtil {
    private static final PegDownProcessor pegDownProcessor = new PegDownProcessor(Extensions.TABLES | Extensions.TASKLISTITEMS
    | Extensions.FENCED_CODE_BLOCKS | Extensions.WIKILINKS);
    public static String markdownToHtml(String markdownText) {
        // 将Markdown文本转换为HTML
        String htmlText = pegDownProcessor.markdownToHtml(markdownText);
        return htmlText;
    }
}
