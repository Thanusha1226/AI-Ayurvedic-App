package com.techno.aiproject.models;

import java.util.ArrayList;
import java.util.List;

public class GeminiRequest {
    public List<Content> contents;

    public GeminiRequest(String text) {
        this.contents = new ArrayList<>();
        Content content = new Content();
        content.parts = new ArrayList<>();
        Part part = new Part();
        part.text = text;
        content.parts.add(part);
        this.contents.add(content);
    }

    public GeminiRequest(String text, String mimeType, String base64Data) {
        this.contents = new ArrayList<>();
        Content content = new Content();
        content.parts = new ArrayList<>();
        
        Part textPart = new Part();
        textPart.text = text;
        content.parts.add(textPart);

        Part imagePart = new Part();
        imagePart.inlineData = new InlineData(mimeType, base64Data);
        content.parts.add(imagePart);

        this.contents.add(content);
    }

    public static class Content {
        public List<Part> parts;
    }

    public static class Part {
        public String text;
        public InlineData inlineData;
    }

    public static class InlineData {
        public String mimeType;
        public String data;

        public InlineData(String mimeType, String data) {
            this.mimeType = mimeType;
            this.data = data;
        }
    }
}
