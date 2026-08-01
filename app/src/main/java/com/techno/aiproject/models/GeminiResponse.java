package com.techno.aiproject.models;

import java.util.List;

public class GeminiResponse {
    public List<Candidate> candidates;

    public static class Candidate {
        public Content content;
    }

    public static class Content {
        public List<Part> parts;
    }

    public static class Part {
        public String text;
    }

    public String getText() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate candidate = candidates.get(0);
            if (candidate.content != null && candidate.content.parts != null && !candidate.content.parts.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Part part : candidate.content.parts) {
                    sb.append(part.text);
                }
                return sb.toString();
            }
        }
        return "";
    }
}
