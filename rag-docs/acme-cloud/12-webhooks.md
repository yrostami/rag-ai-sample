# Webhooks

Webhooks let your systems receive HTTP callbacks when events happen in your workspace.

## Overview

- Delivery is at-least-once. Handle duplicate events by event ID.
- Events are sent as `POST` requests with a JSON body within 30 seconds of the event.
- All plans support webhooks. Endpoint limits: Free 3, Pro 10, Enterprise custom.

## Configuration

1. Go to **Settings > Integrations > Webhooks**.
2. Add an HTTPS endpoint URL.
3. Select the events to receive.
4. Copy the signing secret (starts with `whsec_`). It is shown only once.

## Event format

```json
{
  "id": "evt_1024",
  "type": "task.created",
  "workspace_id": "ws_123",
  "created_at": "2026-10-01T09:00:00Z",
  "data": {
    "task_id": "tsk_789",
    "title": "Write launch checklist"
  }
}
```

## Event types

| Type | Fired when |
|---|---|
| `project.created` | A project is created |
| `task.created` | A task is created |
| `task.updated` | A task changes (status, assignee, due date) |
| `task.completed` | A task moves to `done` |
| `comment.created` | A comment is posted |
| `member.invited` | An invite is sent |
| `member.joined` | An invite is accepted |
| `export.completed` | A data export finishes |

## Signatures

Every delivery includes the header `X-Acme-Signature: sha256=<hex>`, computed as HMAC-SHA256 of the raw request body using your signing secret.

- Verify the signature before processing the event.
- Compare using a constant-time function.
- Reject deliveries with a missing or invalid signature.

## Retries and disabling

- A delivery that returns a non-2xx status or times out (10 seconds) is retried up to 5 times over 24 hours with exponential backoff.
- An endpoint that fails continuously for 7 days is disabled, and workspace owners are notified by email.

## Best practices

- Respond with a `2xx` quickly and process the event asynchronously.
- Make handlers idempotent using the event `id`.
- Use HTTPS endpoints only.
