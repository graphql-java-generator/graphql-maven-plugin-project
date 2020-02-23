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

REM To be sure we have that all artefacts are built, we rebuild them (publishing a release won't install the samples)
mvn install -Dmaven.test.skip=true

REM The next command is long to execute
@echo on
call mvn site -Prelease "-DlastReleasedVersion=%version%"
@echo off
pause

call mvn site:stage
pause



##################################################################
# Replacement of the ant script: START

# Below, the previous ant script, but it doesn't like the long filename generated on the site
#call mvn antrun:run -Prelease
#pause

SET GH_BRANCH=target\gh-pages_branch
SET GH_BRANCH2=target\gh-pages_branch2

rmdir %GH_BRANCH% /S /Q
rmdir %GH_BRANCH2% /S /Q
pause

mkdir %GH_BRANCH%
mkdir %GH_BRANCH2%
pause

subst w: /d
subst w: %GH_BRANCH%
pause

git config --system core.longpaths true
pause

git clone https://github.com/graphql-java-generator/graphql-maven-plugin-project --branch gh-pages --single-branch %GH_BRANCH%
pause

mkdir %GH_BRANCH2%\.git
xcopy %GH_BRANCH%\.git %GH_BRANCH2%\.git /E /C /Q /H
pause

copy %GH_BRANCH%\CNAME %GH_BRANCH2%
pause

xcopy target\staging %GH_BRANCH2% /E /C /Q /H
pause

cd %GH_BRANCH2%
git add *
pause

git commit 

# Replacement of the ant script: START
##################################################################


cd ..\..




