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



mvn antrun:run -Prelease
pause

cd target\gh-pages_branch\graphql-maven-plugin-project
git push
