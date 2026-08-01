package com.techno.aiproject;

import com.techno.aiproject.utils.MarkdownUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ExampleUnitTest {
    @Test
    public void markdownToHtml_convertsHeadings() {
        String markdown = "## Common Name\n\n## Scientific Name";
        String html = MarkdownUtils.markdownToHtml(markdown);
        assertTrue(html.contains("<h2>Common Name</h2>"));
        assertTrue(html.contains("<h2>Scientific Name</h2>"));
    }
}