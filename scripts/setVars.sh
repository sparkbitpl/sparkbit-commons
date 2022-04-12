#!/bin/bash
set -euo pipefail

if [ -z ${repoRoot+x} ] || [ "$repoRoot" = "" ]; then
    export repoRoot=`git rev-parse --show-toplevel`
fi



if [ -z ${currentVersion+x} ] || [ "$currentVersion" = "" ]; then
    oldPwd=`pwd`
    cd $repoRoot
    currentVersion=`printf 'VERSION=${project.version}\n0\n' | $MVN help:evaluate | grep '^VERSION' | sed 's/VERSION=//'`
    cd $oldPwd
fi

if [ -z ${RELEASE_VERSION+x} ] || [ "$RELEASE_VERSION" = "" ] ; then
    RELEASE_VERSION=`echo $currentVersion | sed 's/-SNAPSHOT$//'`
fi

export currentVersion
export RELEASE_VERSION