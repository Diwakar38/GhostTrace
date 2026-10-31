package com.ghosttrace;

import com.ghosttrace.developer.CodeAnalysisResult;
import com.ghosttrace.developer.DeveloperEngine;
import com.ghosttrace.developer.DeveloperIntent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeveloperEngineTest {

    private final DeveloperEngine engine = new DeveloperEngine();

    @Test
    void shouldDetectProgrammingLanguages() {
        assertEquals("Java", engine.detectLanguage("public class GhostTraceApp {}"));
        assertEquals("Python", engine.detectLanguage("def ghost_haunt():\n    import time"));
        assertEquals("Rust", engine.detectLanguage("fn main() {\n    let mut x = 5;\n}"));
        assertEquals("Go", engine.detectLanguage("package main\n\nfunc main() {}"));
        assertEquals("JavaScript/TypeScript", engine.detectLanguage("const haunted = () => { console.log('boo'); };"));
        assertEquals("Shell", engine.detectLanguage("#!/bin/bash\necho 'haunted'"));
    }

    @Test
    void shouldIdentifyEmptyCatchBlocksAndSwallowedExceptions() {
        String code = """
            public class BadCode {
                void execute() {
                    try {
                        doSomething();
                    } catch (Exception e) {}
                }
            }
            """;

        CodeAnalysisResult result = engine.analyzeCode(code);
        assertEquals("Java", result.language());
        assertTrue(result.potentialIssues().stream().anyMatch(i -> i.contains("Swallowed exception")));
    }

    @Test
    void shouldIdentifyHardcodedSecrets() {
        String code = "String apiKey = \"sk-1234567890abcdef\";";
        CodeAnalysisResult result = engine.analyzeCode(code);
        assertTrue(result.securityRisks().stream().anyMatch(r -> r.contains("hardcoded credential")));
    }

    @Test
    void shouldGenerateUnitTestTemplates() {
        String report = engine.handleIntent(DeveloperIntent.TESTS, "public class Service {}");
        assertNotNull(report);
        assertTrue(report.contains("@Test"));
    }
}
