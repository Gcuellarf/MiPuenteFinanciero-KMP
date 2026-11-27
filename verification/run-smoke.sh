#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
base=composeApp/src/commonMain/kotlin/co/edu/mipuente/kmp
jar=$(mktemp --suffix=.jar)
trap 'rm -f "$jar"' EXIT
kotlinc "$base/domain/Models.kt" "$base/data/SqlStore.kt" "$base/data/FinanceRepository.kt" "$base/navigation/Navigation.kt" verification/RepositorySmoke.kt -include-runtime -d "$jar"
java -jar "$jar"
