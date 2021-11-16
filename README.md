# Friday

## CREANTOR-TEILLARD

- Download the dependencies beforehand : `./mvnw clean install -U`
  - Don't let IntelliJ do that for you because it will give you false flags
    due to Java 17.
- Running backend : `./mvnw mn:run`
- Launching backend tests : `./mvnw test`
  - **Notice** : Test classes that involve the database are annotated with a special annotation to modify the database
    properties used. In particular, the database url used by the tests is `jdbc:h2:mem:test`, such that everything is
    purged after the tests.
- Building jar and svelte app : `./mvnw package`
  - the generated jar is in the `target/` folder
  - the frontend app is in `src/main/ui/public/`

***Important :***

To add a salt to the SHA-512 password hasher, create a resource file named `salt.txt`
containing the 16 byte salt (Encoded to UTF-8).

You can set the following environment variables for the database datasource:

- JDBC_URL : the database url (default: `jdbc:h2:file:./db/friday`)
- JDBC_USER : the username to connect to (default: `root`)
- JDBC_PASSWORD : the password of the user (default: `root`)
- JDBC_DRIVER : the driver class (default: `org.h2.Driver`)