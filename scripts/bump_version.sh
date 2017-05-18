#!/bin/bash
set -euo pipefail

. `dirname $0`/setVars.sh

if [[ $currentVersion == *SNAPSHOT ]]; then
    echo SNAPSHOT version detected
    echo Bumping version to ${RELEASE_VERSION}
    cd $repoRoot
    mvn versions:set -DnewVersion=${RELEASE_VERSION} versions:commit
else
    echo Release version detected
    #Remove build number from next snapshot version
    version=`echo $RELEASE_VERSION | sed -r "s/(.*)-[0-9]+$/\1/g"`
    numberToIncrease=`echo $version | sed -r 's/.*[^0-9]+([0-9]+)/\1/g'`
    nextVersion=`echo $version | sed -r "s/(.*[^0-9]+)[0-9]+([^0-9]*)\$/\1$((numberToIncrease + 1))\2-SNAPSHOT/g"`
    echo Bumping version to ${nextVersion}
    cd $repoRoot
    mvn versions:set -DnewVersion=${nextVersion} versions:commit
fi
