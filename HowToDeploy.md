# Getting SNAPSHOT releases #

While a handful of developers may want to post snapshot releases, a larger number of users may want to get hold of a particular snapshot release.

Read the MavenSupport page to find out what, why and how.

# Deploying SNAPSHOT releases #

Once you've got source code that you've managed to clean (to ensure that all the build steps are working) and install into your local repository, you're ready to upload a snapshot release.

It's neighbourly to also post a jar of your current source code onto the snapshot repository along with your .jar binaries. Snapshot releases change quite frequently so users will probably want to use Eclipse's `Right Click --> Maven --> Download Sources` so that when debugging they can see exactly what code was posted up most recently.

Ensure that the following version numbers are consistent and have -SNAPSHOT on them. It's the -SNAPSHOT which will allow Maven to decide that it wants to deploy to the snapshot repository:
  * Parent pom.xml
  * facebook-java-api pom.xml (version and parent version reference)
  * facebook-java-api-schema pom.xml (version and parent version reference)

Run the following command to deploy the snapshot:

`mvn clean install deploy`

## Permissions to upload to happyfern.com ##

We use SFTP (SSH) to copy files up to the snapshot repository. A public / private keypair to authenticate you. If you wish to upload files, your public key must be registered at happyfern.com. Post a message on the Java Facebook API group requesting access to the snapshot repository and include your public key as an attached file (obviously, don't include your private key); an administrator will approve you and add your credentials to the list of authorised users.

### Generating a keypair ###

PuttyGen from http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html is a good tool for creating a keypair. When using this tool, select:
Type of key to generate: SSH-2 DSA

Save your public key ready to send off for approval and save your private key somewhere safe. Then, use the _Conversions_ menu to _Export OpenSSH key_. You'll need to save this as file ~/.ssh/id\_dsa (where ~ is your home directory). Copy your public key to ~/.ssh/id\_dsa.pub

_note: Windows Explorer won't let you create a directory called ".ssh". Go to the command prompt and use mkdir to create it_

_note: You are prompted to put a pass phrase on your private key. Do that, but if you have any problems in later steps then it may be worth trying things without a pass phrase just to eliminate that as a possible cause of problems_

If you're running Linux or similar, you should already have ssh installed and should be able to test whether you can ssh to mrepo.happyfern.com. If you're running on Windows, it's best to download this version of OpenSSH for Windows http://sshwindows.sourceforge.net/download/ and use that to try to ssh to mrepo.happyfern.com. Using putty doesn't help. Putty maintains all it's known\_hosts and certificates in the Windows registry. The Maven Wagon SSH implementation uses the ~/.ssh configuration and that's what you need to setup with the OpenSSH tools.

Once you can connect with OpenSSH, you'll hopefully be ready to go with Maven.

# Creating release .zip files to post on this site #

`mvn clean install assembly:assembly` or just simply `mvn assembly:assembly` if you're sure the release is already clean will build the .zip file.

# Releasing to production Maven repository #

## Releasing a BETA version ##

The [Maven Metadata file for facebook-java-api](http://repo2.maven.org/maven2/com/google/code/facebookapi/facebook-java-api/maven-metadata.xml) controls which version of the Facebook Java API a user gets if they don't specify a version number or if they specify RELEASE as the version.

This section will contain information here about how you can perform a release that does not become the LATEST or RELEASE version in Maven. For example, if you want to produce a Beta release to be made available in the main repository but don't want users to be automatically upgraded if they're asking for the latest release.

## A) Maven deploy ##

**Please note, you'll want to run all these commands from the command line rather than through Eclipse.** Eclipse seems to run mvn in a non-interactive mode and accepts all defaults without prompting the user. You'll see below that we want to use a specific release tag, so it's not appropriate to accept the default.

### release:prepare ###

From the parent directory (with the parent pom.xml in it) issue the command:

mvn release:prepare -DdryRun=true
  * it will ask what the release version is (3 times) (should use default)
  * it will ask what the tag directory name for release will be, it should be release-#####
    * You can see all the [existing tags here](http://code.google.com/p/facebook-java-api/source/browse/tags) so you can be sure that you're following the naming convention for releases.
  * it will ask what the next development version is (3 times) (should use default)

Here's an example of what was prompted when releasing version 2.0.6:
```
What is the release version for "Facebook Java Library - Parent Pom"? (com.google.code.facebookapi:parent-pom) 2.0.6: :
What is the release version for "Facebook Java Library"? (com.google.code.facebookapi:facebook-java-api-schema) 2.0.6: :
What is the release version for "Facebook Java Library"? (com.google.code.facebookapi:facebook-java-api) 2.0.6: :
What is SCM release tag or label for "Facebook Java Library - Parent Pom"? (com.google.code.facebookapi:parent-pom) parent-pom-2.0.6: : release-2.0.6
What is the new development version for "Facebook Java Library - Parent Pom"? (com.google.code.facebookapi:parent-pom) 2.0.7-SNAPSHOT: :
What is the new development version for "Facebook Java Library"? (com.google.code.facebookapi:facebook-java-api-schema) 2.0.7-SNAPSHOT: :
What is the new development version for "Facebook Java Library"? (com.google.code.facebookapi:facebook-java-api) 2.0.7-SNAPSHOT: :
```

Note that I've tried adding the following to the parent pom.xml so that it prompts with the correct default release tag. Unfortunately, the ${releaseVersion} resolves to "null" so you end up with "release-null". Hopefully this will be fixed (or maybe there's another way of doing it) so that this document can be simplified to just "accept all the defaults".
```
          <!-- Doesn't work. ${releaseVersion} resolves to "null"-->
          <tag>release-${releaseVersion}</tag>
```

This "dry run" should have boosted your confidence that the process is going to go well. So, now do the release preparation for real:

mvn release:prepare -Dresume=false
  * this will actually create a directory in source control under /tags/release-#####
  * it will check in new versions of all your pom.xml files specifying their versions as what you said was your "next development version" with -SNAPSHOT appended.

If you get any errors about "file already exists" then you may be suffering from [bug MRELEASE-409](http://jira.codehaus.org/browse/MRELEASE-409). You can solve the problem by issuing an _svn update_ and then retry the release:prepare command.

### If something is wrong ###

The [maven-release-plugin documentation](http://maven.apache.org/plugins/maven-release-plugin/index.html) describes the **release:rollback** goal. The release:prepare goal kept backups of your old pom.xml files. The release:rollback goal checks these backups in over the top of the ones that were checked in to move onto the next development version. So, the trunk of source control ends up like it was before. However, the tag that you've created will still be there unless you were just doing a dry run. You may also find the release:clean goal useful if you want to start afresh.

### release:perform ###

mvn release:perform
  * it will do a full build and deploy into repository
    * this will include jar files, source files and javadoc files (but not distribution file)
    * will have to have proper ssh identities setup so as to not force password or failure

You'll now be able to see that your release has been uploaded to the [happyfern Maven release repository](http://mrepo.happyfern.com/maven2/com/google/code/facebookapi/facebook-java-api/).

### Getting the release onto repo1.maven.org (central repository) ###

Fern has [setup an automatic sync](http://jira.codehaus.org/browse/MAVENUPLOAD-2224) between happyfern.com and the central repository.

So, there's nothing to do. But anyway, here's the [central repository upload guide](http://maven.apache.org/guides/mini/guide-central-repository-upload.html) in case you have any questions.

Your new release should be available on the central repository within a day.

## B) Make up distribution zip file ##

  * Check out a clean copy from SVN of your tag. This will have a release tag specified in the pom.xml not a -SNAPSHOT tag. The HEAD code is now on the new -SNAPSHOT tag so we can't work with that any further.

  * cd /tags/release-#####
  * mvn clean install site site:deploy
    * do another clean install
    * build the site (for dist)
    * deploy the site

  * cd facebook-java-api
  * mvn assembly:assembly
    * build the assembly (zip distribution file)
    * don't worry, all the schema-generated files get pulled in because the main facebook-java-api module declares a dependency on the facebook-java-api-schema module.

  * DONE, you should see a message near the end like so:
    * [INFO](INFO.md) Building zip: ######/facebook-java-api/tags/release-#####/facebook-java-api/target/facebook-java-api-#####-bin.zip

## C) Upload to Google Code ##
  * upload to: http://code.google.com/p/facebook-java-api/downloads/list
  * set as Featured (to show up on project homepage)
  * un feature old release (to remove from project homepage)
  * maybe mark a really old release as deprecated ( so it will not show up by default )

## D) Update front page ##
  * update: http://code.google.com/p/facebook-java-api/admin
    * to say we have new release..

## E) Update wiki ##
  * update: http://code.google.com/p/facebook-java-api/w/edit/Releases