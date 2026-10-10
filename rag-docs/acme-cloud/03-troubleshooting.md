# Troubleshooting

## 401 Unauthorized
**Cause:** Missing or invalid API key.  
**Solution:** Regenerate the key in **Settings > API Keys** and include it as `Authorization: Bearer <token>`.

## 429 Too Many Requests
**Cause:** Rate limit exceeded.  
**Solution:** Wait 60 seconds or reduce request frequency. The Free plan allows 60 requests/minute.

## Export fails
**Cause:** Large date range or too many records.  
**Solution:** Export in smaller date ranges, then combine the files.

## Invite email not received
**Cause:** Spam filtering or incorrect email.  
**Solution:** Check spam, verify the address, and resend the invite. Invites expire after 14 days.

## 403 Forbidden on the API
**Cause:** The API key is valid but the workspace role lacks permission.  
**Solution:** Ask an Owner or Admin to check your role under **Settings > Members**.

## Webhook not received
**Cause:** Invalid signature, non-HTTPS endpoint, or the endpoint was disabled after 7 days of continuous failure.  
**Solution:** Verify the `X-Acme-Signature` check uses the raw body and the `whsec_` secret, confirm the endpoint returns 2xx within 10 seconds, and re-enable the endpoint in **Settings > Integrations > Webhooks**.

## Duplicate webhook events
**Cause:** Retries after a failed delivery; delivery is at-least-once.  
**Solution:** Make your handler idempotent using the event `id`.

## SSO login loop
**Cause:** IdP clock drift over 90 seconds.  
**Solution:** Sync the identity provider clock with NTP and retry.

## 2FA code rejected
**Cause:** Device clock drift or an outdated QR code.  
**Solution:** Sync the authenticator device clock, then rescan the QR code. Owners can use their recovery code.

## File upload rejected
**Cause:** File exceeds the plan limit (25 MB Free, 1 GB Pro, 5 GB Enterprise).  
**Solution:** Compress the file or upgrade the plan. Check workspace storage usage under **Settings > Billing**.

## Search does not find recent changes
**Cause:** The search index lags a few minutes behind.  
**Solution:** Wait a few minutes and search again.

## CSV import rejects my file
**Cause:** Wrong columns or too many rows.  
**Solution:** Use the template columns `title,status,assignee_email,due_date` and keep files under 10,000 rows.

## API returns a maximum of 100 items
**Cause:** The `limit` parameter caps at 100.  
**Solution:** Page through results with `limit` and `offset` until `data` is empty.