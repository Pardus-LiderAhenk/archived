<<<<<<< HEAD
# lider-ahenk-archetype [![Build Status](https://travis-ci.org/Pardus-LiderAhenk/lider-ahenk-archetype.svg?branch=master)](https://travis-ci.org/Pardus-LiderAhenk/lider-ahenk-archetype)

**Lider Ahenk** is an open source project which provides solutions to manage, monitor and audit unlimited number of different systems and users on a network.

lider-ahenk-archetype is a Maven archetype project for Lider Ahenk plugins. Each plugin may consist of three sub-modules for **Lider**, **Lider Console** and **Ahenk** components.

## Prerequisites

**Maven 3**

- Get [Maven 3](http://maven.apache.org/install.html).
- Maven [home](https://maven.apache.org/) (download, docs)

## How to Create a New Plugin Project?

New plugins can easily be created using the archetype. Just run the following command:

```bash
mvn archetype:generate \
-DarchetypeGroupId=tr.org.liderahenk \
-DarchetypeArtifactId=lider-ahenk-archetype \
-DgroupId=tr.org.liderahenk \
-DartifactId=PROJECT_NAME_IN_HYPHEN_NOTATION \
-Dpackage=tr.org.liderahenk.PROJECT_NAME_IN_DOTTED_NOTATION
```

Or run without any project parameters to see a sample plugin:

```bash
mvn archetype:generate \
-DarchetypeGroupId=tr.org.liderahenk \
-DarchetypeArtifactId=lider-ahenk-archetype
-DinteractiveMode=false
```

#### Optional Parameters

While creating a new plugin project, these optional parameters can also be used:

* version:
Version number used for pom.xml files and MANIFEST.MF file.

  **Type**: String (Only use numeric values, do not use 1.0.0-SNAPSHOT, 2.0.Final etc.)
  **Default**: 1.0.0

* package:
Base package name which is used by both Lider and Lider Console sub-modules.

  **Type**: String
  **Default**: tr.org.liderahenk.sample

* pluginClassName:
Base class name which is used by both Lider and Lider Console sub-modules.

  **Type**: String
  **Default**: Sample

#### Tutorial

If you're still unsure about how to start, see this example:

> **Warning**: Make sure you have setup development environment for all the core components ([Lider](https://github.com/Pardus-LiderAhenk/lider/wiki/01.-Setup-Development-Environment), [Lider Console](https://github.com/Pardus-LiderAhenk/lider-console/wiki/01.-Setup-Development-Environment) and [Ahenk](https://github.com/Pardus-LiderAhenk/ahenk/wiki/01.-Setup-Development-Environment)) before creating a new plugin project!

1. Create new project for our new plugin _package manager_:

    ```
mvn archetype:generate \
-DarchetypeGroupId=tr.org.liderahenk \
-DarchetypeArtifactId=lider-ahenk-archetype \
-DgroupId=tr.org.liderahenk \
-DartifactId=package-manager \
-Dpackage=tr.org.liderahenk.packagemanager \
-DpluginClassName=PackageManager
    ```

    This will create all the necessary files for Lider, Ahenk and Console sub-modules.

2. We use _lider-ahenk-*-plugin_ as our naming convention. So we need to rename parent folder _package-manager_ to _lider-ahenk-package-manager-plugin_. Type `mv package-manager/ lider-ahenk-package-manager-plugin` to rename it.
3. Change directory to lider-ahenk-package-manager-plugin and type `mvn clean install -DskipTests` to build it.
4. That's it! Now we can import this project into Eclipse or push to some VCS repository.

> **Tip**: Here is a [tutorial about Lider Ahenk plugin development](https://github.com/Pardus-LiderAhenk/lider/wiki/Lider-Ahenk-Plugin-Tutorial) if you are interested!

## Contribution

We encourage contributions to the project. To contribute:

* Fork the project and create a new bug or feature branch.
* Make your commits with clean, understandable comments
* Perform a pull request

## Other Lider Ahenk Projects

* [Lider](https://github.com/Pardus-LiderAhenk/lider): Business layer running on Karaf container.
* [Lider Console](https://github.com/Pardus-LiderAhenk/lider-console): Administration console built as Eclipse RCP application.
* [Ahenk](https://github.com/Pardus-LiderAhenk/ahenk): Agent service running on remote machines.
* [Lider Ahenk Installer](https://github.com/Pardus-LiderAhenk/lider-ahenk-installer): Installation wizard for Ahenk and Lider (and also its LDAP, database, XMPP servers).

## Changelog

See [changelog](https://github.com/Pardus-LiderAhenk/lider/wiki/Changelog) to learn what we have been up to.

## Roadmap

#### Today

* 30+ plugins
* Linux agent service written in Python
* Administration console built as Eclipse RCP
* Open sourced, easy to access and setup, stable Lider Ahenk v1.0.0

#### 2016

* Scalable infrastructure suitable for million+ users & systems
* 10+ new plugins (such as file distribution via torrent, remote installation)
* New reporting module & dashboard

#### 2017

* Agents for Windows and mobile platforms
* Platform-independent administration console
* Inventory scan & management
* Printer management

## License

Lider Ahenk and its sub projects are licensed under the [LGPL v3](https://github.com/Pardus-LiderAhenk/lider-ahenk-archetype/blob/master/LICENSE).
=======
# lider-ahenk-archetype
>>>>>>> bdb72c3f9cf2564f08048eaf273a68300e789b32
