./gradlew :app:assembleGitRelease
version_name=`grep 'versionName' app/build.gradle | awk '{for(i=1;i<=NF;i++){ if($i ~ /[0-9].*[0-9]/){print $i} } }' | tr -d \"`
notes_file=`mktemp XXXXXXXXXX.v$version_name.md`
cp release-template.md $notes_file
vim $notes_file
title_file=`mktemp XXXXXXXXXX.release_title.txt`
vim $title_file
release_title=`cat $title_file`
echo "$release_title"
gh release create v$version_name -F $notes_file -t "$release_title" "$PWD/app/build/outputs/apk/gitRelease/dev.corruptedark.openchaoschess-$version_name-gitRelease.apk"
rm $notes_file
rm $title_file
