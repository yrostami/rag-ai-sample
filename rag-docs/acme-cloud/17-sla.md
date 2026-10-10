# Service Level Agreement (Enterprise)

The SLA applies to Enterprise workspaces. Pro has a 99.5% uptime target with no credits. Free has no SLA.

## Uptime

- Uptime target: 99.9% per calendar month, measured at `app.acme.example` and the REST API.
- Scheduled maintenance is announced at least 48 hours in advance and capped at 4 hours per month. It does not count against uptime.

## Service credits

| Monthly uptime | Credit |
|---|---|
| 99.0% – 99.89% | 10% of monthly fee |
| 95.0% – 98.99% | 25% of monthly fee |
| Below 95.0% | 50% of monthly fee |

- Credits cap at one month of service per month.
- Submit credit requests to `support@acme.example` within 30 days of the incident.

## Support response times

Enterprise support is 24/7 via `support@acme.example`.

| Priority | Definition | First response |
|---|---|---|
| P1 | Service down or data loss | Under 1 hour, hourly updates |
| P2 | Major feature unusable, no workaround | Under 4 business hours |
| P3 | Degraded with a workaround | Under 1 business day |
| P4 | Question or minor issue | Under 3 business days |

Pro support is by email with a first response within 1 business day.

## Status page

Live and historical uptime is published at `status.acme.example` with an RSS feed for updates.
