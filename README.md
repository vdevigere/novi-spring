# Novi-Spring

novi-spring is the spring boot based implementation of [Novi](https://github.com/vdevigere/Novi): A dynamic feature flag
platform.
The architecture and APIs are more or less similar.

## Usage

If you have docker, run `docker compose up` in the parent directory, which executes the following steps

- The script downloads the postgresql image
- and builds the code and executes it while connecting to the postgres instance.
- Creates tables and sample data from the db-init-scripts folder is inserted into the tables

If you make code changes, run `docker compose build web` to rebuild the image and then run `docker compose up`
