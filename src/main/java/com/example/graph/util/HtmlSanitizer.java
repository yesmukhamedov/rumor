package com.example.graph.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class HtmlSanitizer {
    private static final Safelist SAFE_LIST = Safelist.none()
        .addTags("b", "i", "u", "p", "br", "ul", "ol", "li", "a")
        .addAttributes("a", "href")
        .addProtocols("a", "href", "http", "https", "mailto");

    public String sanitize(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        String cleaned = Jsoup.clean(input, SAFE_LIST);
        return cleaned.isBlank() ? null : cleaned.trim();
    }
}
