#!/bin/sh

die () {
	echo "$*" >&2
	exit 1
}

BATCH_MODE=--batch-mode
SKIP_PUSH=
ALT_REPOSITORY=
while test $# -gt 0
do
	case "$1" in
	--no-batch-mode) BATCH_MODE=;;
	--skip-push) SKIP_PUSH=t;;
	--alt-repository=imagej)
		ALT_REPOSITORY=-DaltDeploymentRepository=imagej.releases::default::dav:http://maven.imagej.net/content/repositories/thirdparty;;
	--alt-repository=*|--alt-deployment-repository=*)
		ALT_REPOSITORY="${1#--*=}";;
	-*) echo "Unknown option: $1" >&2; break;;
	*) break;;
	esac
	shift
done

test $# = 1 && test "a$1" = "a${1#-}" ||
die "Usage: $0 [--no-batch-mode] [--skip-push] [--alt-repository=<repository>] <release-version>"

REMOTE="${REMOTE:-origin}"

git update-index -q --refresh &&
git diff-files --quiet --ignore-submodules &&
git diff-index --cached --quiet --ignore-submodules HEAD -- ||
die "There are uncommitted changes!"

test refs/heads/master = "$(git rev-parse --symbolic-full-name HEAD)" ||
die "Not on 'master' branch"

HEAD="$(git rev-parse HEAD)" &&
git fetch "$REMOTE" master &&
FETCH_HEAD="$(git rev-parse FETCH_HEAD)" &&
test $FETCH_HEAD = HEAD ||
test $FETCH_HEAD = "$(git merge-base $FETCH_HEAD $HEAD)" ||
die "'master' is not up-to-date"

# Prepare new release without pushing (requires the release plugin >= 2.1)
mvn $BATCH_MODE release:prepare -DpushChanges=false -Dresume=false \
        -DreleaseVersion="$1" &&

# Squash the two commits on the current branch into one
git reset --soft HEAD^^ &&
git commit -s -m "Bump to next development cycle" &&

# push the current branch and the tag
tag=$(sed -n 's/^scm.tag=//p' < release.properties) &&
test -n "$tag" &&
if test -z "$SKIP_PUSH"
then
	git push "$REMOTE" HEAD &&
	git push "$REMOTE" $tag
fi ||
exit

git checkout $tag &&
mvn clean verify &&
mvn $ALT_REPOSITORY -DupdateReleaseInfo=true deploy &&
git checkout @{-1}
