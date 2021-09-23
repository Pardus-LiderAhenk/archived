# lider-ahenk-ad-migration-tool

Active Directory-to-OpenLDAP migration tool.

## How to Build

1. Clone the project by running `git clone https://github.com/Pardus-LiderAhenk/lider-ahenk-ad-migration-tool.git`.
2. Open Eclipse and import the project into Eclipse as 'Existing Maven Projects'.
3. Navigate to _lider-ahenk-ad-migration-tool/scripts_ directory and run `build-tool.sh`.

## How to Run

> Make sure you have configured the correct LDAP parameters (host, port, pwd etc) in `config.properties` file.

1. Run `sudo java -jar lider-ahenk-ad-migration-tool.jar`.

> __Note__: Log messages can be found in `/var/log/ad-migration-tool.log`.

## Contribution

We encourage contributions to the project. To contribute:

* Fork the project and create a new bug or feature branch.
* Make your commits with clean, understandable comments
* Perform a pull request

## Other Lider Ahenk Projects

* [Lider Console](https://github.com/Pardus-LiderAhenk/lider-console): Administration console built as Eclipse RCP project.
* [Ahenk](https://github.com/Pardus-LiderAhenk/ahenk): Agent service running on remote machines.
* [Lider Ahenk Installer](https://github.com/Pardus-LiderAhenk/lider-ahenk-installer): Installation wizard for Ahenk and Lider (and also its LDAP, database, XMPP servers).
* [Lider Ahenk Archetype](https://github.com/Pardus-LiderAhenk/lider-ahenk-archetype): Maven archetype for easy plugin development.

## License

Lider Ahenk and its sub projects are licensed under the [LGPL v3](https://github.com/Pardus-LiderAhenk/lider-ahenk-ad-migration-tool/blob/master/LICENSE).
