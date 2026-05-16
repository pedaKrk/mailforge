from typing import List

from dotenv import load_dotenv
import json
import sys
from google import genai
from google.genai import types
from pydantic import BaseModel, Field

load_dotenv()
client = genai.Client()

sys.stdin.reconfigure(encoding="utf-8")
sys.stdout.reconfigure(encoding="utf-8")
sys.stderr.reconfigure(encoding="utf-8")

class Classification(BaseModel):
    label: str
    confidence: float = Field(ge=0.0, le=1.0)

class AttachmentClassification(BaseModel):
    attachmentId: str
    label: str
    confidence: float = Field(ge=0.0, le=1.0)

class Keyword(BaseModel):
    value: str
    confidence: float = Field(ge=0.0, le=1.0)

class Contact(BaseModel):
    name: str
    email: str
    phone: str
    organization: str
    source: str
    confidence: float = Field(ge=0.0, le=1.0)

class SemanticLink(BaseModel):
    attachmentId: str
    relation: str
    evidence: str
    confidence: float = Field(ge=0.0, le=1.0)

class AiAnalysisResultDto(BaseModel):
    emailClassification: Classification
    attachmentClassifications: List[AttachmentClassification]
    keywords: List[Keyword]
    contacts: List[Contact]
    semanticLinks: List[SemanticLink]


def build_prompt(input_data: dict) -> str:
    return f"""
Analysiere die folgende E-Mail inklusive Anhänge.

Extrahiere:
- E-Mail-Klassifikation
- Klassifikation jedes Anhangs
- relevante Keywords
- Kontakte aus Body und Anhängen
- semantische Beziehungen zwischen Body und Anhängen

Regeln:
- Antworte ausschließlich im vorgegebenen JSON-Schema.
- confidence muss immer zwischen 0.0 und 1.0 liegen.
- Wenn keine Daten gefunden werden, verwende leere Listen.
- Erfinde keine Kontakte.
- Nutze attachmentId exakt aus der Eingabe.

Eingabe:
{json.dumps(input_data, ensure_ascii=False)}
""".strip()


def main():
    try:
        input_data = json.load(sys.stdin)

        prompt = build_prompt(input_data)

        response = client.models.generate_content(
            model="gemini-3-flash-preview",
            contents=prompt,
            config=types.GenerateContentConfig(
                response_mime_type="application/json",
                response_schema=AiAnalysisResultDto,
            ),
        )

        result = AiAnalysisResultDto.model_validate_json(response.text)

        print(result.model_dump_json())

    except Exception as e:
        print(f"AI analysis failed: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()