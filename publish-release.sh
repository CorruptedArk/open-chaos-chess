version_name=`grep 'versionName' app/build.gradle | awk '{for(i=1;i<=NF;i++){ if($i ~ /[0-9].*[0-9]/){print $i} } }' | tr -d \"`
cp release-template.md v$version_name.md
vim v$version_name.md
read -p "Enter the release title: " release_title
gh release create v$version_name -F v$version_name.md -t "$release_title" "$PWD/app/release/dev.corruptedark.openchaoschess-$version_name-release.apk"
rm v$version_name.md
