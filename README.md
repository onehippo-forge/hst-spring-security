[![Build Status](https://travis-ci.org/bloomreach-forge/hst-spring-security.svg?branch=develop)](https://travis-ci.org/bloomreach-forge/hst-spring-security)

# HST Spring Security Support

[Spring Security](http://projects.spring.io/spring-security/) is a powerful and highly customizable authentication
and access-control framework.

**HST Spring Security Support** project provides seamless integration with Spring Security for HST-2 based applications.

Mainly, **HST Spring Security Support** project provides the following:

- An Authentication Provider which allows authentication against users and groups stored in the Hippo Repository.
  See the Javadoc of [```org.onehippo.forge.security.support.springsecurity.authentication.HippoAuthenticationProvider```](src/main/java/org/onehippo/forge/security/support/springsecurity/authentication/HippoAuthenticationProvider.java).
  for details.
- A Spring Security Valve in order to translate Spring Security Authentication to an HST-2 aware Subject.
  See the Javadoc of [```org.onehippo.forge.security.support.springsecurity.container.SpringSecurityValve```](src/main/java/org/onehippo/forge/security/support/springsecurity/container/SpringSecurityValve.java) for details.

Because Spring Security provides a lot of out-of-box security integration solutions such as HTTP Basic/Digest authentication, LDAP, Form-based, Open ID, JA-SIG CAS authentication, you can take advantage of those with HST-2! 

## Demo Project

- Demo project is available as Essentials plugin [essentials-hippo-security-plugin](essentials-hippo-security-plugin/) folder. 
Create an empty project by following [GET STARTED WITH HIPPO CMS](https://www.onehippo.org/trails/getting-started/hippo-essentials-getting-started.html) page.

After empty project is created: 

add following dependency to main project pom *pom.xml*
```xml
       <dependency>
          <groupId>org.onehippo.essentials</groupId>
          <artifactId>essentials-hippo-security-plugin</artifactId>
          <version>1.0.0-SNAPSHOT</version>
        </dependency>
```
add following dependency to *essentials/pom.xml*

```xml
       <repository>
         <id>hippo-maven2-forge</id>
         <name>Hippo Maven 2 Forge</name>
         <url>http://maven.onehippo.com/maven2-forge</url>
       </repository>
```

 

# Documentation (Local)

The documentation can generated locally by this command:

```bash
$ mvn clean install
$ mvn clean site
```

The output is in the ```target/site/``` directory by default. You can open ```target/site/index.html``` in a browser.

# Documentation (GitHub Pages)

Documentation is available at [https://bloomreach-forge.github.io/content-export-import/](https://bloomreach-forge.github.io/content-export-import/).

You can generate the GitHub pages only from ```master``` branch by this command:

```bash
$ mvn clean install
$ find docs -name "*.html" -exec rm {} \;
$ mvn -Pgithub.pages clean site
```

The output is in the ```docs/``` directory by default. You can open ```docs/index.html``` in a browser.

You can push it and GitHub Pages will be served for the site automatically.
