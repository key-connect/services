# Release process

## Preparing the release

Create a new branch from `main` for the release. In this example we're cutting `1.0.0` release with next snapshot being `1.10-SNAPSHOT`.

Create the release branch `release/1.0`

```
git checkout -b release/1.0
```

Set release version to `1.0.0-SNAPSHOT`

```
mvn versions:set -DnewVersion="1.0.0-SNAPSHOT" -DskipTests
```

Commit the release change on `release/1.0` branch

```
git commit -m "Release candidate 1.0.0-SNAPSHOT"
```

Push the release branch

```
git push --set-upstream origin release/1.0
```

Switch to `main` branch:

```
git checkout main
```

Set next main version to `1.10-SNAPSHOT`

```
mvn versions:set -DnewVersion="1.10-SNAPSHOT" -DskipTests
```

Commit the `main` version change

```
git commit -m "Snapshot version 1.10-SNAPSHOT"
```

Push main branch:

```
git push origin main
```

## Tagging the release

The release doesn't get pushed until it is tagged. Here we will prepare the release branch for tagging.

Checkout the `release/1.0` branch

```
git checkout release/1.0
```

Update maven version to release version.

```
mvn versions:set -DnewVersion="1.0.0" -DskipTests
```

Commit pom changes

```
git commit -m "Release version 1.0.0"
```

Push changes to the release branch

```
git push origin
```

Go to Github release and [create the release](https://github.com/key-connect/services/releases). Ensure:

* Release description contains adequate changelog information
* Release tag is correct
* Release is tagged from `release/1.0` branch

Once the release is created, it should kick off the maven signing and upload process to [Nexus](https://oss.sonatype.org/#stagingRepositories). If the build is successful, logon to Nexus, inspect the artifacts ensuring all is in order and close the release. This might take some time. Once the release is closed, "Release" the release artifacts from Nexus staging repository to Maven Central. This will take approximately 10 minutes for the sync. The maven indexes could take up to 2 hours for the artifact to be searchable on [search.maven.org](https://search.maven.org/search?q=keyconnect).

Lastly, ensure any documentation that reflects latest release version is updated accordingly. Any changes to API documentation should be generated and uploaded to the [website codebase](https://github.com/key-connect/key-connect.github.io).

Celebrate 🎉
