./gradlew build
rm --recursive -f run
./gradlew clean
git commit -am "Pushing for sync"
git push