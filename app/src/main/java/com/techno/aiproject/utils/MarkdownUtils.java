package com.techno.aiproject.utils;

public class MarkdownUtils {
    public static String markdownToHtml(String markdown) {
        if (markdown == null) return "";
        String[] lines = markdown.split("\n");
        StringBuilder html = new StringBuilder();
        boolean inList = false;

        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty()) {
                if (!inList) {
                    html.append("<br>");
                }
                continue;
            }

            if (line.startsWith("* ") || line.startsWith("- ")) {
                if (!inList) {
                    html.append("<ul>");
                    inList = true;
                }

                String content = line.substring(2);
                content = content.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
                content = content.replaceAll("(?<!\\*)\\*(?!\\*)(.*?)\\*(?!\\*)", "<i>$1</i>");

                html.append("<li>").append(content).append("</li>");
            } else {
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }

                if (line.startsWith("### ")) {
                    String content = line.substring(4).trim();
                    content = content.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
                    content = content.replaceAll("(?<!\\*)\\*(?!\\*)(.*?)\\*(?!\\*)", "<i>$1</i>");
                    html.append("<h3>").append(content).append("</h3>");
                } else if (line.startsWith("## ")) {
                    String content = line.substring(3).trim();
                    content = content.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
                    content = content.replaceAll("(?<!\\*)\\*(?!\\*)(.*?)\\*(?!\\*)", "<i>$1</i>");
                    html.append("<h2>").append(content).append("</h2>");
                } else if (line.startsWith("# ")) {
                    String content = line.substring(2).trim();
                    content = content.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
                    content = content.replaceAll("(?<!\\*)\\*(?!\\*)(.*?)\\*(?!\\*)", "<i>$1</i>");
                    html.append("<h1>").append(content).append("</h1>");
                } else {
                    String content = line.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
                    content = content.replaceAll("(?<!\\*)\\*(?!\\*)(.*?)\\*(?!\\*)", "<i>$1</i>");
                    html.append(content).append("<br>");
                }
            }
        }

        if (inList) {
            html.append("</ul>");
        }

        return html.toString();
    }
}
