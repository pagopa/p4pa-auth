locals {
  # Repo
  github = {
    org        = "pagopa"
    repository = "p4pa-auth"
  }

  repo_secrets = var.env_short == "p" ? {
    SLACK_WEBHOOK_URL = data.azurerm_key_vault_secret.slack_webhook[0].value
  } : {}

  map_repo = {
    "dev" : "*",
    "uat" : "uat"
    "prod" : "main"
  }
}
