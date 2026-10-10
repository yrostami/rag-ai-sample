# Backups and Restore

## Backups

- Daily automatic backups of all workspace data.
- Retained for 7 days.
- Stored encrypted (AES-256) in a second region, separate from primary storage.

## Restore requests

1. A workspace Owner emails `support@acme.example`.
2. Include the workspace name, what was lost, and the target date/time.
3. Acme support confirms scope before restoring.

- Restore granularity is the whole workspace, not a single project or file.
- Restored data is placed in a new workspace named `<name>-restored-<date>`. Nothing is overwritten.
- Completion time: 2 business days (Enterprise: 1 business day).

## Self-service protection

- Before large changes, export your data under **Settings > Export** (CSV or JSON, choose a date range).
- Files deleted by accident stay in the project trash for 30 days and can be restored by any Member without support.
- Backups are for disaster recovery; they do not replace exports.
