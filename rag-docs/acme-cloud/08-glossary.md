# Glossary

- **Workspace**: Top-level container for projects, members, and billing.
- **Project**: A collection of tasks, files, and comments.
- **Member**: A user in a workspace.
- **Task**: A unit of work with a title, assignee, due date, and status (`todo`, `in_progress`, `blocked`, `done`).
- **Comment**: A real-time message attached to a project; supports `@mentions` and Markdown.
- **API Key**: Secret token for API access; live keys start with `acme_live_`.
- **SSO**: Single sign-on via SAML 2.0 or OIDC.
- **SCIM**: Automated user provisioning and deprovisioning (Enterprise).
- **RBAC**: Role-based access control using Owner, Admin, Member, and Viewer.
- **Webhook**: HTTP callback sent to your system when events occur; signed with HMAC-SHA256.
- **Audit log**: Record of workspace actions, retained 1 year (Enterprise).
- **Data residency**: Region pinning (`us-east` or `eu-west`) chosen at workspace creation.
- **Rate limit**: Maximum API requests per minute; 60 on Free.
- **SLA**: Service level agreement; 99.9% uptime with service credits on Enterprise.
- **Retention**: How long data is kept; 30 days after deletion, 90 days for usage logs.