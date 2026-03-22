import os
from dotenv import load_dotenv
import json
import sys
from google import genai

load_dotenv()
client = genai.Client()


def build_prompt(text: str) -> str:
    return f"""
You are an information extraction system.

Extract all contact information from the following email text.

Return ONLY valid JSON in this format:

{{
  "contacts": [
    {{
      "type": "email|phone",
      "value": "string",
      "confidence": float
    }}
  ]
}}

Email:
\"\"\"
{text}
\"\"\"
"""


def main():
    try:
        raw = sys.stdin.read()

        if not raw.strip():
            raise ValueError("No JSON input received")

        payload = json.loads(raw)

        text = payload.get("textBody") or payload.get("htmlBody") or ""

        prompt = build_prompt(text)

        response = client.models.generate_content(
            model="gemini-3-flash-preview",
            contents=prompt
        )

        content = response.text

        result = {
            "status": "ok",
            "raw": content
        }

        sys.stdout.write(json.dumps(result, ensure_ascii=False))
        sys.stdout.flush()
    except Exception as e:
        sys.stderr.write(json.dumps({
            "status": "error",
            "message": str(e)
        }, ensure_ascii=False))
        sys.stderr.flush()
        sys.exit(1)


if __name__ == "__main__":
    main()