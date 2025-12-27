package com.tave.PromptMate.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tave.PromptMate.config.JudgeProperties;
import com.tave.PromptMate.dto.evaluation.AiEvaluationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JudgeRunner {

    private final JudgeProperties props;
    private final ObjectMapper objectMapper;

    public AiEvaluationResult run(String beforePrompt, String afterPrompt) {
        List<String> cmd = new ArrayList<>();
        File workDir = null;

        try {
            cmd.add(props.getPython());
            cmd.add(props.getRunner());
            cmd.add("--before");
            cmd.add(beforePrompt);
            cmd.add("--after");
            cmd.add(afterPrompt);

            ProcessBuilder pb = new ProcessBuilder(cmd);
            // ✅ stderr/stdout 분리해서 봐야 원인이 보임
            pb.redirectErrorStream(false);

            // ✅ workdir 설정 (runner.py 또는 judge_inference.py import 기준)
            if (props.getWorkdir() != null && !props.getWorkdir().isBlank()) {
                workDir = new File(props.getWorkdir());
                pb.directory(workDir);
            }

            Process p = pb.start();

            boolean finished = p.waitFor(props.getTimeoutSeconds(), TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                throw new RuntimeException("judge python timeout");
            }

            String stdout = readAll(p.getInputStream());
            String stderr = readAll(p.getErrorStream());
            int exit = p.exitValue();

            if (exit != 0) {
                throw new RuntimeException(
                        "judge python failed\n" +
                                "- exitCode: " + exit + "\n" +
                                "- cmd: " + String.join(" ", safeCmdForLog(cmd)) + "\n" +
                                "- workdir: " + (workDir == null ? "(null)" : workDir.getAbsolutePath()) + "\n" +
                                "- stdout: " + stdout + "\n" +
                                "- stderr: " + stderr
                );
            }

            // stdout가 JSON이어야 함
            return objectMapper.readValue(stdout, AiEvaluationResult.class);

        } catch (Exception e) {
            // ✅ 여기서 원인 전체를 볼 수 있게 메시지 포함
            throw new RuntimeException(
                    "JudgeRunner error\n" +
                            "- cmd: " + String.join(" ", safeCmdForLog(cmd)) + "\n" +
                            "- workdir: " + (workDir == null ? "(null)" : workDir.getAbsolutePath()) + "\n" +
                            "- cause: " + e.getMessage(),
                    e
            );
        }
    }

    private String readAll(java.io.InputStream is) throws Exception {
        StringBuilder out = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                out.append(line).append("\n");
            }
        }
        return out.toString().trim();
    }

    // 로그에 프롬프트 전체가 찍히면 너무 길어서, cmd 로그용으로만 축약
    private List<String> safeCmdForLog(List<String> cmd) {
        List<String> copy = new ArrayList<>();
        for (String s : cmd) {
            if (s == null) { copy.add("null"); continue; }
            if (s.length() > 120) copy.add(s.substring(0, 120) + "...(truncated)");
            else copy.add(s);
        }
        return copy;
    }
}
