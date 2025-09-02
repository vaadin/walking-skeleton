#!/usr/bin/env bash

set -euo pipefail  # Exit on error, undefined variables, and pipe failures

# Read Vaadin Pro key if available
VAADIN_PRO_KEY=""
PRO_KEY_FILE="$HOME/.vaadin/proKey"
DOCKER_TAG=""

if [ $# -gt 0 ]; then
  DOCKER_TAG="$1"
else
  ARTIFACT_ID=$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout 2>/dev/null)
  DOCKER_TAG="$ARTIFACT_ID:latest"
fi

if [ -f "$PRO_KEY_FILE" ]; then
    if command -v jq >/dev/null 2>&1; then
        # Use jq for proper JSON parsing if available
        if VAADIN_PRO_KEY=$(jq -r '.proKey // empty' "$PRO_KEY_FILE" 2>/dev/null) && [ -n "$VAADIN_PRO_KEY" ]; then
            echo "✅ Found Vaadin Pro key"
        else
            echo "⚠️ Could not parse Vaadin Pro key from $PRO_KEY_FILE" >&2
            VAADIN_PRO_KEY=""
        fi
    else
        # Fallback: regex
        if [ -r "$PRO_KEY_FILE" ]; then
            VAADIN_PRO_KEY=$(grep -Po '(?<="proKey"\s*:\s*")[^"]*' "$PRO_KEY_FILE" 2>/dev/null || true)

            if [ -n "$VAADIN_PRO_KEY" ]; then
                echo "✅ Found Vaadin Pro key"
            else
                echo "⚠️ Could not parse Vaadin Pro key from $PRO_KEY_FILE (consider installing jq for better JSON parsing)" >&2
            fi
        else
            echo "⚠️ Cannot read $PRO_KEY_FILE (permission denied)" >&2
        fi
    fi
else
    echo "ℹ️ No Vaadin Pro key found at $PRO_KEY_FILE (continuing without it)"
fi

# Validate Docker is available
if ! command -v docker >/dev/null 2>&1; then
    echo "❌ Error: Docker is not installed or not in PATH" >&2
    exit 1
fi

# Build the Docker image with Vaadin Pro key if available
echo "🔨 Building Docker image $DOCKER_TAG..."

if [ -n "$VAADIN_PRO_KEY" ]; then
    if ! docker build -t "$DOCKER_TAG" --build-arg VAADIN_PRO_KEY="$VAADIN_PRO_KEY" .; then
        echo "❌ Docker build failed with Vaadin Pro key" >&2
        exit 1
    fi
else
    if ! docker build -t "$DOCKER_TAG" .; then
        echo "❌ Docker build failed" >&2
        exit 1
    fi
fi

echo "✅ Docker build completed successfully"