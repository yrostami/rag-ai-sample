# API Quickstart

Acme Cloud provides a REST API for workspaces, projects, and members. This quickstart shows how to authenticate and make your first API requests.

## Prerequisites

- An Acme Cloud account
- A workspace
- An API key from **Settings > API Keys**

## Base URL

```text
https://api.acme.example/v1
```

## Authentication

All API requests require an API key in the `Authorization` header:

```text
Authorization: Bearer <API_KEY>
```

For requests that send JSON, also include:

```text
Content-Type: application/json
```

Set your API key as an environment variable:

```bash
export ACME_API_KEY="acme_live_your_key_here"
```

## 1. List workspaces

```bash
curl -s https://api.acme.example/v1/workspaces \
  -H "Authorization: Bearer $ACME_API_KEY"
```

Example response:

```json
{
  "data": [
    {
      "id": "ws_123",
      "name": "Demo Workspace"
    }
  ]
}
```

## 2. List projects

```bash
curl -s "https://api.acme.example/v1/projects?workspace_id=ws_123" \
  -H "Authorization: Bearer $ACME_API_KEY"
```

## 3. Create a project

```bash
curl -s -X POST https://api.acme.example/v1/projects \
  -H "Authorization: Bearer $ACME_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Demo Project",
    "workspace_id": "ws_123",
    "description": "Created from the API quickstart"
  }'
```

Example response:

```json
{
  "id": "prj_456",
  "name": "Demo Project",
  "workspace_id": "ws_123",
  "created_at": "2026-10-05T12:00:00Z"
}
```

## 4. Get a project

```bash
curl -s https://api.acme.example/v1/projects/prj_456 \
  -H "Authorization: Bearer $ACME_API_KEY"
```

## Endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/workspaces` | List workspaces |
| `GET` | `/projects` | List projects |
| `POST` | `/projects` | Create a project |
| `GET` | `/projects/{id}` | Get a project |

## Common errors

| Status | Meaning | Fix |
|---|---|---|
| `401` | Unauthorized | Check your API key and `Authorization` header |
| `403` | Forbidden | Check your workspace role or permissions |
| `404` | Not found | Check the resource ID |
| `429` | Too many requests | Wait 60 seconds or reduce request frequency |

## Rate limits

The Free plan allows **60 requests per minute**. Paid plans have higher limits. If you receive a `429`, wait 60 seconds before retrying.

## Pagination

Use `limit` and `offset` to paginate list endpoints. The default `limit` is 20 and the maximum is 100:

```text
GET /projects?limit=20&offset=0
```

Example:

```bash
curl -s "https://api.acme.example/v1/projects?limit=20&offset=0" \
  -H "Authorization: Bearer $ACME_API_KEY"
```

## Python example

```python
import os
import requests

api_key = os.environ["ACME_API_KEY"]
base_url = "https://api.acme.example/v1"
headers = {"Authorization": f"Bearer {api_key}"}

response = requests.get(
    f"{base_url}/projects",
    headers=headers,
    timeout=10,
)
response.raise_for_status()

projects = response.json()["data"]
print(projects)
```

## Security notes

- Never commit API keys to source control.
- Never expose API keys in browser or client-side code.
- Regenerate a key immediately if it is leaked.
- Use environment variables or a secrets manager.

## Next steps

- See **Troubleshooting** for `401` and `429` errors.
- See **Getting Started** for workspace and project setup.
- See **Pricing** for plan limits and features.