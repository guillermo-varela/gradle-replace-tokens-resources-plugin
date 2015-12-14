# gradle-replace-tokens-resources-plugin

This plugin for Gradle adds the [ReplaceTokens] feature from Ant to the [processResources] Gradle task, which is commonly used during the build process for projects with code to be executed in the [JVM] (like Java and Groovy).

It takes the project's resource files (by default in "src/main/resources") and replaces the text between "@" symbols with values in ".properties" files inside a specific folder in the project o from a system property, which allows to replace the values according to the [deployment environment].

### Example
We can have a project with the following structure:
```sh
|   build.gradle
|   gradle.properties
|
+---config
|   +---local
|   |       application.properties
|   |       server.properties
|   |
|   +---prod_1
|   |       application.properties
|   |       server.properties
|   |
|   +---prod_2
|   |       application.properties
|   |       server.properties
|   |
|   \---qa
|           application.properties
|           server.properties
|
\---src
    \---main
        +---java
        |
        \---resources
                application.properties
                server.properties
```
By default this plugin will look for the deployment environment folders inside the folder "**config**" on the project's root. Each subfolder (deployment environment) inside can have any name you like and can be as many as you need, the ones used here are just an example.

Let's say we have this on "**src/main/resources/application.properties**"
```sh
app.instance.name=Jetty-Server
app.instance.number=@app.instance.number@
```
"**config/local/application.properties**"
```sh
app.instance.number=1
```
"**config/prod_2/application.properties**"
```sh
app.instance.number=2
```

Building the project using the command "**gradle build**" will generate the artifact with the following values on "**application.properties**":
```sh
app.instance.name=Jetty-Server
app.instance.number=1
```

The property "app.instance.name" wasn't modified, since it's not between "@", but the value for "**app.instance.number**" was replaced with "**1**", taken from "**local**", the default environment.

Now building the project using the command "**gradle build -Denv=prod_2**" will generate the artifact with the following values on "**application.properties**":
```sh
app.instance.name=Jetty-Server
app.instance.number=2
```
As expected "**app.instance.number**" is now equal to "**2**", from the file inside "**prod_2**".

But that's not all, the values can be overriden using system properties. Using the command "**gradle build -Denv=prod_2 -Dapp.instance.number=5**" will generate the artifact with the following values on "**application.properties**":
```sh
app.instance.name=Jetty-Server
app.instance.number=5
```
This way the values are completely customizable and there is no need to include sensitive data inside the project's code, if any.

Although only values in "**app.instance.number**" were shown, it's important to note that all "**.properties**" files inside the deployment environment folder will be processed, like in this case for "**server.properties**".

Also it's worth mentioning that on the resources folder any kind of text file will be processed looking for text between "@" to replace. That means you can have for instance XML files with values to be replaced.

### How to Apply the Plugin
Please follow the instructions given at: https://plugins.gradle.org/plugin/com.blogspot.nombre-temp.replace.tokens.resources

### Demo
https://github.com/guillermo-varela/jetty-jersey-multi-env-plugin-example

### Customization
Not only the values inside the values can be overriden, but also:
  - The name of the "**config**" folder containing the deployment environments can be changed adding an extra property to the Gradle project (in the build script before applying this plugin or in the file "gradle.properties") named "**configEnvironmentFolder**".
  - The default deployment environment can be changed from "**local**" to any other adding an extra property to the Gradle project (in the build script before applying this plugin or in the file "gradle.properties") named "**defaultEnvironment**".

This way you could have a folder "environments" and a default environment "dev" with the following entries in "**gradle.properties**":
```sh
configEnvironmentFolder=environments
defaultEnvironment=dev
```

### Limitations
On the example shown there are two files on the resources and on the deployment environment folders. Currently the plugin takes all values from both files on the deployment environment folders and use them to replace all the tokens in the resource files, without making distinction between files.

That means each property must have a unique key for all files, otherwise the values will collide and use wrong values.

### How it was Developed
A tutorial can be found (in spanish) at: http://nombre-temp.blogspot.com/2015/12/desarrollando-un-plugin-basico-de-grade.html

License
----

MIT

   [ReplaceTokens]: <https://ant.apache.org/manual/Types/filterchain.html#replacetokens>
   [processResources]: <https://docs.gradle.org/current/javadoc/org/gradle/language/jvm/tasks/ProcessResources.html>
   [JVM]: <https://en.wikipedia.org/wiki/Java_virtual_machine>
   [deployment environment]: <https://en.wikipedia.org/wiki/Deployment_environment>
