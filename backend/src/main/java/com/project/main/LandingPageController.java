package com.project.main;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class LandingPageController {
    @GetMapping("/")
    public String initial() {
        System.out.println("Hello world");
        return "Hello World";
    }

    @PostMapping("/evaluate/link")
    public String evaluateLink(@RequestBody Map<String, Object> data) {
        String link = (String) data.get("link");
        Map<String, String> selectedValues = (Map<String, String>) data.get("selectedValues");

        System.out.println("Link entered: " + link);
        System.out.println(selectedValues);
        return link;
    }

    @PostMapping("/evaluate/prompt")
    public String evaluatePrompt(@RequestBody Map<String, Object> data) {
        String prompt = (String) data.get("prompt");
        Map<String, String> selectedValues = (Map<String, String>) data.get("selectedValues");

        System.out.println("Prompt entered: " + prompt);
        System.out.println(selectedValues);
        return prompt;
    }

}
