
 
SET SOURCE_FOLDER=target\staging
SET TARGET_FOLDER=target\gh-pages_branch
SET TARGET_FOLDER2=target\gh-pages_branch2

IF EXIST %SOURCE_FOLDER% GOTO :staging_exists
echo The site must have been staged before
goto :Error

:staging_exists

RMDIR %TARGET_FOLDER% /S /Q
IF ERRORLEVEL 1 GOTO :Error

MKDIR %TARGET_FOLDER%
IF ERRORLEVEL 1 GOTO :Error

REM Let's clone the existing gh_pages
git clone https://github.com/graphql-java-generator/graphql-maven-plugin-project --branch gh-pages --single-branch %TARGET_FOLDER%
IF ERRORLEVEL 1 GOTO :Error

REM Let's create a folder the compile the ".git" sub-folder, and the staged site.
DEL %TARGET_FOLDER2% /F /S /Q
IF ERRORLEVEL 1 GOTO :Error

MKDIR %TARGET_FOLDER2%
IF ERRORLEVEL 1 GOTO :Error

MKDIR %TARGET_FOLDER2%\.git
IF ERRORLEVEL 1 GOTO :Error

XCOPY %TARGET_FOLDER%\.git\*.* %TARGET_FOLDER2%\.git /E /Q /K	
IF ERRORLEVEL 1 GOTO :Error

XCOPY %SOURCE_FOLDER%\*.* %TARGET_FOLDER2% /E /Q /K	
IF ERRORLEVEL 1 GOTO :Error

REM CAUTION: DIRECTORY CHANGE
cd %TARGET_FOLDER2%

git add *
IF ERRORLEVEL 1 GOTO :Error

git commit -m "Checking new site in"
IF ERRORLEVEL 1 GOTO :Error

git push
IF ERRORLEVEL 1 GOTO :Error

REM Let's go back in the previous directory
cd ..\..


goto :ThEnd

:Error
ECHO An error occured...
:TheEnd