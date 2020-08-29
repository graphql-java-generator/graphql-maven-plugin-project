# HowTo publish the site

This page is more an internal howto, to remind of the step to publish the project web site, as publishing a site is really complex, from the maven-site-plugin, to the github configuration to use a specific domain, through the issue of the multi-module stuff.

And of course, if it can be useful for anyone to publish a site, then, it's nice! :)

## Check list

### Before publishing:

* Check that the user has access on the owner of the git repository, as the last step works only if connected on githib with the owner of the repository.

* Check the TODO list: some done work may still be on the TODO list.

* Update the CHANGELOG.md:
    * Check the version for the updates being released
    * Check that all the done job is described 

### After publishing

The maven plugin should be stored in the OSSRH repository, waiting to be replicated into the central repository. This can be checked by browsing into it: [https://repo1.maven.org/maven2/com/graphql-java-generator/graphql-maven-plugin/](https://repo1.maven.org/maven2/com/graphql-java-generator/graphql-maven-plugin/).

At the end of the process, the plugin should be available [https://mvnrepository.com/artifact/com.graphql-java-generator/graphql-maven-plugin](https://mvnrepository.com/artifact/com.graphql-java-generator/graphql-maven-plugin).

## Configuring the domain name

The standard way of publishing a web site on github, is to push the site on the gh-pages branch of the project. Then, check the settings of the project. At this stage, the project site is available at this URL: [https://graphql-java-generator.github.io/graphql-maven-plugin-project/](https://graphql-java-generator.github.io/graphql-maven-plugin-project/).
 
It's possible to define a custom domain in the settings.

On the DNS configuration for the graphql-java-generator.com domain, let's add this line:
```
graphql-maven-plugin-project 1800 IN CNAME graphql-java-generator.github.io.
```

And in the settings, define the Custom domain to be: graphql-maven-plugin-project.graphql-java-generator.com

Then, the project site is available at [https://graphql-maven-plugin-project.graphql-java-generator.com](graphql-maven-plugin-project.graphql-java-generator.com) with the site published on the gh-pages branch of the project.


## Generating the site

The use of [maven-site-plugin](https://maven.apache.org/plugins/maven-site-plugin/) is complex, but well described. 

Here are the steps to execute:

* cd target/checkout
    * This allows to go to the just performed release, and get all the code in the relevant version
* publish_site    which is a Windows command file, that wraps:
    * mvn antrun:run -Prelease 
    * git push

## Publishing the site

At this step, you 'just' have to push the staged site to github. But that's not that simple.

_Note:_ there is a github site-maven-plugin. Don't use it ! It's no more maintained. And it's not compatible with multi-module projects.

Instead of using the github site-maven-plugin, read [this article](http://wiki.bitplan.com/index.php/Multi-Module_Maven_with_github_pages). The next steps are largely inspired from this excellent page (thanks to them):

* mvn antrun:run -Prelease
    * This checks out the gh-pages branch into the target/gh-pages_branch folder, then synchronize the target/staging folder onto it
    * It lacks only the "git push" command, as it needs the github credentials.
* cd target\gh-pages_branch\graphql-maven-plugin-project
* git push


Thanks to the _CNAME_ file being preserved, in the ant file, the custom domain configuration should not disappear... But it may be worth a check, to get to [the settings](https://github.com/graphql-java-generator/graphql-maven-plugin-project/settings) of the project, to restore the custom domain, which is graphql-maven-plugin-project.graphql-java-generator.com