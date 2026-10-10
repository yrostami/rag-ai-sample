# Roles and Permissions

Workspace roles are **Owner**, **Admin**, **Member**, and **Viewer**. Roles are assigned when a member is invited and can be changed by an Owner in **Settings > Members**.

## Permission matrix

| Action | Owner | Admin | Member | Viewer |
|---|---|---|---|---|
| View projects, tasks, files | Yes | Yes | Yes | Yes |
| Create and edit tasks | Yes | Yes | Yes | No |
| Post comments | Yes | Yes | Yes | No |
| Upload files | Yes | Yes | Yes | No |
| Manage members and invites | Yes | Yes | No | No |
| Regenerate API keys | Yes | Yes | No | No |
| Configure webhooks and integrations | Yes | Yes | No | No |
| Configure SSO and SCIM | Yes | No | No | No |
| Manage billing | Yes | No | No | No |
| Delete workspace | Yes | No | No | No |
| Transfer ownership | Yes | No | No | No |

## Notes

- Members can create and edit tasks and comments in projects they belong to.
- Viewers are read-only across the whole workspace.
- Every workspace must have at least one Owner. Ownership can be transferred only to an Admin.
- All roles count toward the workspace user limit.
- Every role change is recorded in the audit log (Enterprise).
