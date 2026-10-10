# SSO Setup

Single sign-on is available on the Pro and Enterprise plans. Configure it under **Settings > Security > SSO** (Owner required).

## SAML 2.0

1. In **Settings > Security > SSO**, choose **SAML**.
2. Provide your identity provider (IdP) with these values:
   - ACS URL: `https://app.acme.example/sso/acs`
   - Audience / Entity ID: `https://app.acme.example/sso/acs`
   - Name ID format: email
   - Signed assertions: required
3. Upload your IdP metadata XML to Acme Cloud.
4. Click **Test configuration** and complete a test login.
5. Enable **Enforce SSO** to disable password login for everyone except owners.

## OIDC

- Redirect URI: `https://app.acme.example/sso/oidc/callback`
- Required scopes: `openid`, `email`, `profile`
- Paste the client ID and client secret from your provider and test the login.

## SCIM provisioning (Enterprise)

- Base URL: `https://api.acme.example/scim/v2`
- Bearer token: generate under **Settings > Security > SCIM**.
- Supports create, update, and deactivate of users, plus group membership push.
- Deactivating a user via SCIM removes their workspace access but keeps their comments and task history.

## Notes

- Keep clock sync (NTP) on your IdP; clock drift over 90 seconds causes login loops.
- Owners keep password login as break-glass access plus a recovery code generated at setup.
- SSO sessions last 7 days.
