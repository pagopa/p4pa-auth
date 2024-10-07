# Secret
data "azurerm_key_vault" "key_vault" {
  name                = "${var.prefix}-${var.env_short}-${var.location_short}-core-kv"
  resource_group_name = "${var.prefix}-${var.env_short}-${var.location_short}-core-sec-rg"
}

data "azurerm_key_vault_secret" "slack_webhook" {
  count = var.env_short == "p" ? 1 : 0

  key_vault_id = data.azurerm_key_vault.key_vault.id
  name         = "slack-webhook-url"
}

# Github
data "github_organization_teams" "all" {
  root_teams_only = true
  summary_only    = true
}

