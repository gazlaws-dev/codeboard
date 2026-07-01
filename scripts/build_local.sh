#!/bin/bash
# build_local.sh
# Local codeboard build — same as GitHub Actions
#
# Usage:
#   bash scripts/build_local.sh <TAG> [--full]
#
# Example:
#   bash scripts/build_local.sh V6.0.3          → build only
#   bash scripts/build_local.sh V6.0.3 --full   → build + push release
#
# Required environment variables:
#   SIGNING_KEYSTORE_BASE64   → keystore in base64 (optional)
#   SIGNING_STORE_PASSWORD    → keystore password
#   SIGNING_KEY_ALIAS         → key alias
#   SIGNING_KEY_PASSWORD      → key password
#   GITHUB_TOKEN              → GitHub token (for --full)
#
# Or store in local.properties:
#   signing.keystore.path=/path/to/release.keystore
#   signing.store.password=
#   signing.key.alias=
#   signing.key.password=

set -e

# ── Colors ──────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'
step()  { echo -e "\n${BLUE}▶ $1${NC}"; }
ok()    { echo -e "${GREEN}✓ $1${NC}"; }
warn()  { echo -e "${YELLOW}⚠ $1${NC}"; }
error() { echo -e "${RED}✗ $1${NC}"; exit 1; }

# ── Arguments ─────────────────────────────────────────────────────────────────
TAG="${1:-dev}"
FULL_MODE=false
[[ "$2" == "--full" ]] && FULL_MODE=true

echo -e "${BLUE}======================================"
echo " Codeboard Local Build"
echo " TAG  : $TAG"
echo " Mode : $([ "$FULL_MODE" == true ] && echo 'FULL' || echo 'BUILD ONLY')"
echo -e "======================================${NC}"

# ── Check dependencies ──────────────────────────────────────────────────────
step "Checking dependencies"
command -v java >/dev/null 2>&1 || error "Java not found. Install JDK 17."
command -v git  >/dev/null 2>&1 || error "Git not found."
[ -f "gradlew" ] || error "Run this from the project root."
chmod +x gradlew
ok "All dependencies available"

# ── Extract version ───────────────────────────────────────────────────────────
step "Extracting version"
VERSION_CODE=$(grep 'versionCode' app/build.gradle | tr -dc '0-9')
VERSION_NAME=$(grep 'versionName' app/build.gradle | sed 's/.*"\(.*\)".*/\1/')
ok "Version code : $VERSION_CODE"
ok "Version name : $VERSION_NAME"

# ── Keystore ──────────────────────────────────────────────────────────────────
step "Checking keystore"
HAS_KEYSTORE=false
mkdir -p app/keystore

if [ -n "$SIGNING_KEYSTORE_BASE64" ]; then
  # From env variable (base64)
  echo "$SIGNING_KEYSTORE_BASE64" | base64 -d > app/keystore/release.keystore
  export SIGNING_KEYSTORE_PATH="$(pwd)/app/keystore/release.keystore"
  HAS_KEYSTORE=true
  ok "Keystore from SIGNING_KEYSTORE_BASE64"

elif [ -n "$SIGNING_KEYSTORE_PATH" ] && [ -f "$SIGNING_KEYSTORE_PATH" ]; then
  # From direct env variable path
  HAS_KEYSTORE=true
  ok "Keystore from SIGNING_KEYSTORE_PATH: $SIGNING_KEYSTORE_PATH"

elif [ -f "local.properties" ]; then
  # From local.properties (Android Studio style)
  LOCAL_PATH=$(grep 'signing.keystore.path' local.properties | cut -d'=' -f2 | tr -d ' ')
  if [ -n "$LOCAL_PATH" ] && [ -f "$LOCAL_PATH" ]; then
    HAS_KEYSTORE=true
    ok "Keystore from local.properties: $LOCAL_PATH"
  fi
fi

if [ "$HAS_KEYSTORE" != true ]; then
  warn "Keystore not found → using debug keystore"
fi

# ── Build Debug ───────────────────────────────────────────────────────────────
step "Build Debug APK"
./gradlew assembleDebug --no-daemon
ok "Debug APK done"

# ── Build Release ─────────────────────────────────────────────────────────────
step "Build Release APK"
./gradlew assembleRelease --no-daemon
if [ "$HAS_KEYSTORE" == true ]; then
  ok "Release APK done (signed with release keystore)"
else
  ok "Release APK done (signed with debug keystore)"
fi

# ── Rename APK ────────────────────────────────────────────────────────────────
step "Rename APK"
mkdir -p artifacts
cp app/build/outputs/apk/debug/app-debug.apk     artifacts/codeboard-${TAG}-debug.apk
cp app/build/outputs/apk/release/app-release.apk artifacts/codeboard-${TAG}-release.apk
cp artifacts/codeboard-${TAG}-release.apk          artifacts/codeboard-release.apk
ok "artifacts/codeboard-${TAG}-debug.apk"
ok "artifacts/codeboard-${TAG}-release.apk"
ok "artifacts/codeboard-release.apk"

# ── Done if not full mode ──────────────────────────────────────────────────────
if [ "$FULL_MODE" != true ]; then
  echo -e "\n${GREEN}======================================"
  echo " Build done!"
  echo " APK is in the artifacts/ folder"
  echo -e "======================================${NC}"
  exit 0
fi

# ── [FULL] Push Release to GitHub ───────────────────────────────────────────
[ -n "$GITHUB_TOKEN" ] || error "GITHUB_TOKEN is not set."
command -v gh >/dev/null 2>&1 || error "GitHub CLI (gh) not found."

if [[ "$TAG" == V* ]]; then
  step "Push Release to GitHub"
  CHANGELOG=$(git log -1 --pretty=%s)
  gh release create "$TAG" \
    artifacts/codeboard-${TAG}-release.apk \
    --title "Codeboard $TAG" \
    --notes "$CHANGELOG" \
    --latest
  ok "Release pushed successfully"
else
  warn "TAG is not V* → skipping release push"
fi

# ── Clean up temporary keystore ───────────────────────────────────────────────
[ -f app/keystore/release.keystore ] && rm app/keystore/release.keystore

echo -e "\n${GREEN}======================================"
echo " All done!"
echo " Tag : $TAG"
echo " APK : artifacts/codeboard-${TAG}-release.apk"
echo -e "======================================${NC}"
