#!/bin/bash
set -euo pipefail

. `dirname $0`/setVars.sh

if [ -z ${dryRun+x} ] || [ "$dryRun" = "" ]; then
    export dryRun=false
fi
export projectName=sparkbit-commons

cd $repoRoot

scripts/bump_version.sh
if [ $dryRun = "false" ]; then
    mvn install deploy
else
    echo dryRun mode - Skipping deploying to nexus
    mvn install
fi

git commit -a -m "RELEASE: release $projectName-$RELEASE_VERSION"
if [ $dryRun = "false" ]; then
    git tag -a $projectName-$RELEASE_VERSION
    git push origin master
else
    echo dryRun mode - Skipping tagging version and pushing changes to remote
fi

unset currentVersion

scripts/bump_version.sh
git commit -a -m "RELEASE: prepare for next development iteration"
if [ $dryRun = "false" ]; then
    git push origin master
else
     echo dryRun mode - Skipping pushing changes to remote
fi
