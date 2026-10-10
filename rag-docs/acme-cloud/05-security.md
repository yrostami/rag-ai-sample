# Security

- Encryption at rest: AES-256
- Encryption in transit: TLS 1.3
- Compliance: SOC 2 Type II
- Access control: Owner, Admin, Member, Viewer
- Data retention: 30 days after account deletion
- Backups: Daily, retained for 7 days, stored in a second region

## Authentication

- Two-factor authentication (TOTP) is available on all plans.
- Sessions expire after 7 days.
- Passwords are hashed; no plaintext is ever stored or emailed.

## API keys

- Live keys start with `acme_live_`, test keys with `acme_test_`.
- Keys are shown once at creation; only the last 4 characters are visible afterwards.
- Optional expiry dates can be set per key.
- Regenerating a key invalidates the old key immediately.

## Enterprise controls

- Audit logs: who did what and when, retained for 1 year, exportable.
- IP allowlist: restrict workspace and API access to approved IP ranges.
- SCIM provisioning for automated joiner/mover/leaver flows.
- Custom data retention policies.

## Data residency

Workspaces are pinned to `us-east` or `eu-west` at creation. Backups stay in-region pairs and never leave the selected region.

## Reporting a vulnerability
Email `security@acme.example`. Do not disclose publicly before a fix is available.