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
REM NB: this side provide a bash script for that, but I'm on Windows. So here is a simplified script, which works only for this project
REM (adaptation for other projects should be simple)

  local l_project="$1"
  local l_ghpages="$2"
  local l_modules="$3"
  
  color_msg $green "creating site for $l_project $l_modules"
  cd $ws/$l_project
	stage=/tmp/stage$$
	sitelog=/tmp/sitelog$$.txt
	rm -rf $stage
	# the stagingDirectory needs to be subdirectory 
	mkdir -p $stage/$l_project

	# run the staging of the site against this directory and log the results
	mvn -U clean install site site:stage -DstagingDirectory=$stage/$l_project | tee $sitelog
	
	# rsync the result into the github-pages folder
	rsync -avz --del $stage/* $l_ghpages/$l_project/
	
	# is this a multi module project?
	if [ "$l_modules" != "" ]
	then
		cd $l_ghpages/$l_project/
		if [ ! -f index.html ]
		then
cat << EOF > index.html
<!DOCTYPE html>
<html>
<head>
   <!-- HTML meta refresh URL redirection -->
   <meta http-equiv="refresh"
   content="0; url=./$l_project/$l_project/index.html">
</head>
<body>
   <p>This is a multimodule mvn site click below to get to the index.html of 
   <a href="./$l_project/$l_project/index.html">$l_project</a></p>
</body>
</html>	
EOF
		fi
		# add potentially new files
		git add *
		# commit results
		git commit -m "checked in by checksite script"
		# push results
		git push
	fi
	if [ "$debug" = "false" ]
	then
	  rm -rf $stage
		rm $sitelog
	fi