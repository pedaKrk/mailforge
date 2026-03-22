import json
import sys

def main():
    try:
        raw = sys.stdin.read()

        if not raw.strip():
            raise ValueError("No input received")

        data = json.loads(raw)

        # Debug: einfach zurückgeben + bisschen Info
        result = {
            "status": "ok",
            "receivedSubject": data.get("subject"),
            "hasTextBody": bool(data.get("textBody")),
            "hasHtmlBody": bool(data.get("htmlBody")),
            "attachmentCount": len(data.get("attachments", []))
        }

        sys.stdout.write(json.dumps(result, ensure_ascii=False))
        sys.stdout.flush()

    except Exception as e:
        error = {
            "status": "error",
            "message": str(e)
        }
        sys.stderr.write(json.dumps(error))
        sys.stderr.flush()
        sys.exit(1)


if __name__ == "__main__":
    main()