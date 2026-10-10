# Migration Guide

How to import existing work into Acme Cloud.

## Import tasks from CSV

1. Download the template from **Settings > Import**.
2. Required columns: `title`, `status`, `assignee_email`, `due_date`.
3. Dates use ISO 8601 format (`2026-10-05`).
4. Status mapping:

| Your status | Acme Cloud status |
|---|---|
| to do, open | `todo` |
| in progress, doing | `in_progress` |
| blocked, waiting | `blocked` |
| done, closed | `done` |

- Maximum 10,000 rows per file. Split larger files and import in batches.
- Assignees are matched by email; the member must already be in the workspace.

## Import via API

1. Create the project with `POST /projects`.
2. Create tasks with `POST /tasks`, sending an `Idempotency-Key` header so retries do not create duplicates.

## From Trello or Jira

1. Export your board or project to CSV in the source tool.
2. Rename columns to the Acme Cloud template (title, status, assignee email, due date).
3. Jira assignees are matched by email address.

## What is not imported

- Attachments (re-upload them to the project)
- Comment history
- Custom fields from the source tool

## After import

- Spot-check task counts per project.
- Members receive notifications for newly assigned tasks; consider muting notifications during large imports by importing outside working hours.
