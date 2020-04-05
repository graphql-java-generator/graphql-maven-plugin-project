@echo off

REM
REM This Windows script:
REM - generates the site in target/stage
REM - checkout the gh_pages branch of the project in target/site_checkout
REM - synchronize the target/stage folder with target/site_checkout
REM - then commit the result, that actually publish the project site
REM
REM Thanks a lot to this site for the help:
REM http://wiki.bitplan.com/index.php/Multi-Module_Maven_with_github_pages
REM
REM
REM This script executes several commands, with PAUSE in between, to let the user check that everything went well

echo "Working in the current dir. Want to use target/checkout instead ?    (after a release)"
pause

REM in Ant REM We need the correct release number
REM in Ant set /p version="Enter the last released version (e.g.: 1.0):

REM in Ant REM To be sure we have that all artefacts are built, we rebuild them (publishing a release won't install the samples)
REM in Ant call mvn install -Dmaven.test.skip=true
REM in Ant pause

REM The next command is long to execute
REM in Ant @echo on
REM in Ant call mvn site -Prelease "-DlastReleasedVersion=%version%"
REM in Ant @echo off
REM in Ant pause

REM in Ant call mvn site:stage


call mvn antrun:run -Prelease
pause

cd target\gh-pages_branch\graphql-maven-plugin-project
echo Pushing to github
git push
cd ..\..\..

echo Go to https://github.com/graphql-java-generator/graphql-maven-plugin-project/settings
echo Check that graphql-maven-plugin-project.graphql-java-generator.com
echo is into the "Custom domain" parameter


