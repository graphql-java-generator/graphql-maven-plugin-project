# HowTo publish the site

This page is more an internal howto, to remind of the step to publish the project web site, as publishing a site is really complex, from the maven-site-plugin, to the github configuration to use a specific domain, through the issue of the multi-module stuff.

And of course, if it can be useful for anyone to publish a site, then, it's nice! :)

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
* mvn site -Prelease -DlastReleasedVersion=x.y.z
    * As the site configuration is in the "release" profile, to avoid polluting standard build
    * Don't use mvn site-deploy, as this won't work to stage then deploy the site on github. If that's wrong, please provide a comment on this one !  :)
    * After that, the site for each module is ready. But there is no link between each module's site.
    * lastReleasedVersion can be optionally defined, so that the samples in the site have this version number.
* mvn site:stage
    * This agregate all the module sites built by the previous command into the target/staging folder of the root project.


__Note:__ All these steps are automated through the publish_site.cmd Windows command file (very easy to adapt to a Unix shell)

## Publishing the site

At this step, you 'just' have to push the staged site to github. But that's not that simple.

_Note:_ there is a github site-maven-plugin. Don't use it ! It's no more maintained. And it's not compatible with multi-module projects.

Instead of using the github site-maven-plugin, read [this article](http://wiki.bitplan.com/index.php/Multi-Module_Maven_with_github_pages). The next steps are largely inspired from this excellent page (thanks to them):

* mvn antrun:run -Prelease
    * This checks out the gh-pages branch into the target/gh-pages_branch folder, then synchronize the target/staging folder onto it
    * It lacks only the "git push" command, as it needs the github credentials.
* cd target\gh-pages_branch\graphql-maven-plugin-project
* git push

Currently, after each push, the custom domain configuration disappears... So we need to get to [the settings](https://github.com/graphql-java-generator/graphql-maven-plugin-project/settings) of the project, to restore the custom domain, which is graphql-maven-plugin-project.graphql-java-generator.com