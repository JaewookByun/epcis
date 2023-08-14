# EPCIS Docker

This docker compose configuration will Build and Run the EPCIS server, together with a Mongo database server.

### Requirements
To use this docker configuration, you need to install [Docker](https://www.docker.com). It is recommended to install the newest version available, but if that is not possible, then simply install version 24 or any newer version.
 
### Configurations
The 2 files in the `/epcis-docker/configs` folder will get copied into the EPCIS server container and used by the server process. Updating them will change the settings of the server.

### Connection Ports
The EPCIS server will be exposed on port `8080`, while the mongo server will be on port `27017`. If these ports are not available on your system, please modify the `docker-compose.yaml` file to use different ports. For example, changing `- 8080:8080` to `- 3000:8080` will make it so that port 8080 on the container gets mapped to port 3000 on your computer.

### Database Connection
The database connection string is: `mongodb://root:example@localhost:27017`

### DB Persistance
The Mongo data will be persisted on a docker volume called `epcis_mongo_volume`. 

# WARNING

This docker compose configuration is not intended for production environments.

Only use it for educational, development or testing purposes.

There are several security issues with this configuration:
 - the database credentials are extremely simple.
 - the database port is exposed to the outside network
 - the EPCIS server container is not lean enough (it has unnecessary applications and libraries)

# How to Use it:

Here are the most common terminal commands to know when using the docker-compose configuration.

> Note: These commands only work if you are in the `epcis-docker` folder.

## Start the EPCIS Server
```docker compose up -d```
> Hint: the "-d" part of the command will run the containers in the background, without blocking your command line. If you wish to run the containers as a process in your command line instead, simply remove the "-d".

## Rebuild the container and Start the EPCIS Server
```docker compose up -d --build```
> Hint: Adding the "--build" command forces the container to be rebuilt even if it was already available. Use this when there were changes in the source code of the project, or in the configuration files.

## Stop the EPCIS Server and delete the containers 
```docker compose down```
> Hint: If you wish to remove the data stored in the volumes of the containers (for example - the MongoDB data), you can add the "-v" option at the end of this command.

## Delete all MongoDB data (in order to start with an empty DB)
```docker volume rm epcis-docker_epcis_mongo_volume```
> Hint: This only works when the EPCIS containers are down.

## Full Reset (down -> delete data -> up)
```docker compose down -v && docker compose up -d --build```
> Hint: This is most usable for testing and development. Especially if you are also using scripts that populate the DB with seed data.

## Copy the JAR files from the EPCIS container
```docker cp oliot_epcis:/app/epcis-server-light.jar ../epcis-server-light.jar && docker cp oliot_epcis:/app/epcis-server-fat.jar ../epcis-server-fat.jar```
> Hint: This only works when the EPCIS server container is up and running

## Copy the Log files from the EPCIS container

```mkdir -p ../log && docker cp oliot_epcis:/log/epcis-docker.log ../log/epcis-docker.log && docker cp oliot_epcis:/log/accessLog-docker.log ../log/accessLog-docker.log```
> Hint: This only works when the EPCIS server container is up and running

