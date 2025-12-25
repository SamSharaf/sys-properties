#!/bin/bash
set -e  # Stop if any command fails

DEPLOY_BRANCH="deploy"
MAIN_BRANCH="main"
FOLDER_TO_REMOVE="src"

echo "🚀 Starting deployment..."

# Ensure we are on main and up to date (if remote branch exists)
git checkout "$MAIN_BRANCH"
if git ls-remote --exit-code --heads origin "$MAIN_BRANCH" >/dev/null 2>&1; then
    git pull origin "$MAIN_BRANCH"
else
    echo "⚠️  Remote branch '$MAIN_BRANCH' not found on origin. Using local branch only."
fi

# Switch to deploy branch (create if missing)
if ! git rev-parse --verify "$DEPLOY_BRANCH" >/dev/null 2>&1; then
    git checkout -b "$DEPLOY_BRANCH"
else
    git checkout "$DEPLOY_BRANCH"
fi

# Merge latest main changes
git merge "$MAIN_BRANCH" --no-edit

# Remove folder from tracking (but keep locally)
git rm -r --cached "$FOLDER_TO_REMOVE" || true

# Commit only if there are changes
if ! git diff --cached --quiet; then
    git commit -m "Deploy without $FOLDER_TO_REMOVE folder"
fi

# Push deploy branch
git push origin "$DEPLOY_BRANCH"

# Switch back to main
git checkout "$MAIN_BRANCH"

echo "✅ Deployment complete! Branch '$DEPLOY_BRANCH' updated without '$FOLDER_TO_REMOVE'."
