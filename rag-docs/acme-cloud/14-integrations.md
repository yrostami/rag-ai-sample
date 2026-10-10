# Integrations

Integrations connect Acme Cloud to the tools your team already uses. Configure them in **Settings > Integrations** (Owner or Admin required).

## Slack

- Receive notifications for task and comment events in a channel you choose.
- Use the `/acme` slash command to create a task from any Slack message.
- Notification delay for very large workspaces can be several minutes (see Known Issues).

## GitHub

- Link a pull request or commit to a task by including the task ID in the message (for example, `TSK-789`).
- Linked tasks show the related commits in their timeline.

## Google Calendar

- Each project has a read-only calendar feed of task due dates.
- Enable the feed from the project menu and subscribe in your calendar app.

## Zapier

- Triggers: `task.created`, `comment.created`.
- Actions: create a task, add a comment.
- Use the same API key permissions as the REST API.

## Email notifications

- Mention notifications are sent immediately.
- Activity digests can be daily or weekly per member under **Settings > Notifications**.

## Plan limits

| Limit | Free | Pro | Enterprise |
|---|---|---|---|
| Active integrations | 2 | Unlimited | Unlimited |

Removing an integration does not delete data already synced into Acme Cloud.
