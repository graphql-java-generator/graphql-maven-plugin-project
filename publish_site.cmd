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

REM We need the correct release number
set /p version="Enter the last released version (e.g.: 1.0): "

REM The next command is long to execute
@echo on
call mvn site -Prelease "-DlastReleasedVersion=%version%"
@echo off
pause

call mvn site:stage
pause

call mvn antrun:run -Prelease
pause

cd target\gh-pages_branch\graphql-maven-plugin-project
git push
cd ..\..\..

echo Go to https://github.com/graphql-java-generator/graphql-maven-plugin-project/settings
echo Put graphql-maven-plugin-project.graphql-java-generator.com
echo into the "Custom domain" parameter


