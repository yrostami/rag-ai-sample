# Known Issues

Last updated: 2026-10-05.

| Issue | Status | Workaround |
|---|---|---|
| Search does not match accented characters | Open | Search using the unaccented form |
| SSO login loop when IdP clock drifts more than 90 seconds | Open | Sync the IdP clock with NTP |
| Webhook events can be delivered more than once during retries | Known | Make handlers idempotent using the event `id` |
| Slack notifications delayed by several minutes in very large workspaces | Investigating | Filter to fewer channels |
| CSV export file names show `_` instead of non-Latin characters | Fixed in v1.4.1 | Upgrade to v1.4.1 or later |
| Mobile app: edited comments do not refresh in real time | Fixed in v1.4.1 | Upgrade to v1.4.1 or later |

Fixed issues remain listed for 90 days after the fix release. Report anything not listed here to `support@acme.example`.
