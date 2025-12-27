import torch
import json
import unsloth
from unsloth import FastLanguageModel


# ================================================================
# 1) Judge 모델 로드
# ================================================================
JUDGE_MODEL_NAME = "unsloth/Meta-Llama-3.1-8B-Instruct-bnb-4bit"

judge_model, judge_tokenizer = FastLanguageModel.from_pretrained(
    model_name=JUDGE_MODEL_NAME,
    load_in_4bit=True,
)
FastLanguageModel.for_inference(judge_model)


# ================================================================
# 2) Judge Prompt 생성기
# ================================================================
def build_judge_prompt(user_prompt: str):
    return f"""
You are a fully deterministic prompt-quality evaluator.
Your output MUST be ONLY a single JSON object.
Do NOT output anything before or after the JSON.
Do NOT output comments, markdown, notes, or explanations.

Evaluate ONLY the quality of the **prompt itself**, not any answer.

================================================================
# Evaluation Dimensions (Strict Rules)
================================================================

1. Clarity (0–100)
- Ambiguous expressions → -10 each
- Goal/purpose missing → -20

2. Specificity & Instruction Alignment (0–100)
- Missing WHO/WHAT/WHEN/WHERE/WHY/HOW → -5 each
- Missing explicit instruction → -15
- Missing constraints → -10
- Missing output format → -10

3. Structure (0–100)
- Missing Instruction → -20
- Missing Context → -10
- Missing Input material → -10
- Missing Constraints → -10
- Missing Output format → -10

4. Language Quality (0–100)
- Grammar/spelling awkwardness → -5 each
- Unnatural phrasing → -5 each

5. Consistency (0–100)
- Contradictory instructions → score = 50
- Conflicting requirements → -30
- Tone/style conflicts → -20
- Constraint vs instruction conflict → -20
- Multiple interpretations due to contradictions → -10 each
- No issues → 100

================================================================
# LANGUAGE RESTRICTION RULE (한국어 전용)
================================================================
All comments and feedback MUST be written 100% in natural Korean.
Do NOT include any English words or abbreviations.

Replace:
- Instruction → 지시문
- Constraint → 제약 조건
- Conflict → 충돌
- Grammar → 문법
- Specificity → 구체성
- WHO/WHAT/WHEN/WHERE/WHY/HOW → 누가 / 무엇을 / 언제 / 어디서 / 왜 / 어떻게

================================================================
# Arithmetic Rule (MUST FOLLOW)
================================================================
overall_score = round(
    (clarity_score + specificity_score + structure_score +
     language_score + consistency_score) / 5
)

================================================================
# JSON ONLY FORMAT
================================================================
{{
  "overall_score": <0-100>,
  "clarity_score": <0-100>,
  "specificity_score": <0-100>,
  "structure_score": <0-100>,
  "language_score": <0-100>,
  "consistency_score": <0-100>,
  "clarity_comment": "<Korean comment>",
  "specificity_comment": "<Korean comment>",
  "structure_comment": "<Korean comment>",
  "language_comment": "<Korean comment>",
  "consistency_comment": "<Korean comment>",
  "summary_feedback": "<Korean actionable summary>"
}}

================================================================
# Target Prompt:
================================================================
{user_prompt}
""".strip()


# ================================================================
# 3) 모델 generate 함수
# ================================================================
def generate(model, tokenizer, messages, max_new_tokens=512):

    input_ids = tokenizer.apply_chat_template(
        messages,
        add_generation_prompt=True,
        return_tensors="pt"
    ).to(model.device)

    # pad_token_id = eos_token_id 문제 해결
    attention_mask = (input_ids != tokenizer.eos_token_id).long()

    with torch.no_grad():
        output = model.generate(
            input_ids=input_ids,
            attention_mask=attention_mask,
            max_new_tokens=max_new_tokens,
            do_sample=False,
            temperature=0.0,
            top_p=1.0,
            top_k=50,
            pad_token_id=tokenizer.eos_token_id,
        )

    text = tokenizer.decode(
        output[0][input_ids.shape[-1]:],
        skip_special_tokens=True
    ).strip()

    return text


# ================================================================
# 4) JSON 파서
# ================================================================
def safe_json_extract(text: str):
    start = text.find("{")
    end = text.rfind("}")

    if start == -1 or end == -1:
        raise ValueError("JSON not found:\n" + text)

    json_str = text[start:end+1]

    try:
        return json.loads(json_str)
    except json.JSONDecodeError:
        json_str = json_str.replace("\n", "")
        json_str = json_str.replace(",}", "}")
        return json.loads(json_str)


# ================================================================
# 5) 최종 평가 함수
# ================================================================
def evaluate_prompt_only(user_prompt: str) -> dict:
    judge_prompt = build_judge_prompt(user_prompt)

    messages = [
        {"role": "system", "content": "You evaluate prompt quality deterministically."},
        {"role": "user", "content": judge_prompt},
    ]

    raw_output = generate(judge_model, judge_tokenizer, messages)
    return safe_json_extract(raw_output)


# ================================================================
# 6) 테스트
# ================================================================
if __name__ == "__main__":
    test = "AI 시대에 자기소개서 잘 쓰는 법 알려줘."
    print(json.dumps(evaluate_prompt_only(test), ensure_ascii=False, indent=2))
