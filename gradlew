#!/usr/bin/env sh
set -eu
GRADLE_VERSION=8.9
GRADLE_HOME="${HOME}/.gradle/manual/gradle-${GRADLE_VERSION}"
if [ ! -x "${GRADLE_HOME}/bin/gradle" ]; then
  echo "Downloading Gradle ${GRADLE_VERSION}..."
  mkdir -p "${HOME}/.gradle/manual"
  TMP_ZIP="${TMPDIR:-/tmp}/gradle-${GRADLE_VERSION}-bin.zip"
  curl -fL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "${TMP_ZIP}"
  unzip -q -o "${TMP_ZIP}" -d "${HOME}/.gradle/manual"
fi
exec "${GRADLE_HOME}/bin/gradle" "$@"
