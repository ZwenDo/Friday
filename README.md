# Friday

## CREANTOR-TEILLARD

- Running backend : `./mvnw mn:run`
- Launching backend tests : `./mvnw test`
  - **Notice** : It is recommended to run `export JDBC_URL=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
    before launching the tests to run on a temporary database for test suite.
    ```sh
    export JDBC_URL=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    ./mvnw test
    ```
- Building jar and svelte app : `./mvnw package`
  - the generated jar is in the `target/` folder
  - the frontend app is in `src/main/ui/public/`

***Important :***

To add a salt to the SHA-512 password hasher, create a resource file named `salt.txt`
containing the 16 byte salt (Encoded to UTF-8).

You can set the following environment variables for the database datasource:

- JDBC_URL : the database url (default: `jdbc:h2:file:./db/friday;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`)
- JDBC_USER : the username to connect to (default: `root`)
- JDBC_PASSWORD : the password of the user (default: `root`)
- JDBC_DRIVER : the driver class (default: `org.h2.Driver`)