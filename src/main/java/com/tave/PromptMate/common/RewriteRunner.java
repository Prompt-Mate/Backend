package com.tave.PromptMate.common;

import com.tave.PromptMate.dto.rewrite.AiRewriteResult;

public interface RewriteRunner {
    AiRewriteResult run(String beforeText);
}
