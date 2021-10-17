# Friday

## CREANTOR-TEILLARD

- Running backend : `./mvnw mn:run`
- Launching backend tests : `./mvnw test`
- Building jar and svelte app : `./mvnw package` 
  - the generated jar is in the `target/` folder
  - the frontend app is in `src/main/ui/public/`

***Important :***

To add a salt to the SHA-512 password hasher, create a resource file named `salt.txt`
containing the 16 byte salt (Encoded to UTF-8).