#################################
# Repository Environment        #
#################################
resource "github_repository_environment" "github_repository_environment" {
  environment = var.env
  repository  = local.github.repository
  # filter teams reviewers from github_organization_teams
  # if reviewers_teams is null no reviewers will be configured for environment
  dynamic "reviewers" {
    for_each = (var.github_repository_environment.reviewers_teams == null || var.env_short != "p" ? [] : [1])
    content {
      teams = matchkeys(
        data.github_organization_teams.all.teams.*.id,
        data.github_organization_teams.all.teams.*.name,
        var.github_repository_environment.reviewers_teams
      )
    }
  }
  deployment_branch_policy {
    protected_branches     = var.github_repository_environment.protected_branches
    custom_branch_policies = var.github_repository_environment.custom_branch_policies
  }
}

#################################
# Environment Deployment Policy #
#################################

resource "github_repository_environment_deployment_policy" "this" {
  repository     = local.github.repository
  environment    = var.env
  branch_pattern = local.map_repo[var.env]

  depends_on = [
    github_repository_environment.github_repository_environment
  ]
}

#############################
# Secrets of the Repository #
#############################
resource "github_actions_secret" "repo_secrets" {
  for_each = var.env_short == "p" ? local.repo_secrets : {}

  repository      = local.github.repository
  secret_name     = each.key
  plaintext_value = each.value
}
