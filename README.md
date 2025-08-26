# onebot-springboot
A OneBot v11 protocol backend implemented in Java with Spring Boot 3.

Please modify the `application.yaml` as needed before starting the project.

> The project currently focuses primarily on adaptation for QQ bots. Theoretically, it should work with any platform that supports the OneBot v11 protocol, though some code modifications may be required for broader and more universal compatibility.

# How to Use?

You only need to refer to the example code under the *plugins* module to add and write commands for your own bot. The *core* and *forwarder* will operate according to the configuration file.

# Implemented Features

- Modular Design (Core, Adapter and Plugins)
- Concurrently identify and route user commands (as a WebSocket server)
- Authorization check
- Forward (as a WebSocket client, achieve by multi-strategy message queue)
- Containerized PostgreSQL and Redis support

# Version Dependencies

OpenJDK 17+

Spring Boot 3.5

Docker 27.5

PostgreSQL 16 (docker)

Redis 7 (docker)

# Recommend Bot Protocol Client

I recommend the following clients, as they have been tested and verified to work:

- [NapCatQQ](https://www.napcat.wiki/)
- [Lagrange.OneBot](https://lagrangedev.github.io/Lagrange.Doc/v1/Lagrange.OneBot/)

Our Spring Boot application requires a bot protocol client to obtain information.

# Containers (docker)

Please start the Docker containers for the database and cache before launching the Spring Boot application.

The SQL table initialization file (.sql) is located at the path: `bot/src/main/resources/db/migration`

```bash
# Start containers (and build if not exists) in detached mode
docker compose -f docker-compose.dev.yaml up -d

# Enter the bash interactive shell of the PostgreSQL container
docker exec -it dev-postgres bash
psql -U app -d appdb

# Enter the sh interactive shell of the Redis container
docker exec -it dev-redis sh
redis-cli

# Shutdown all containers
docker stop $(docker ps -q)
```

# Project Structure

```
├───adapter
│   ├───controller
│   ├───parser
│   │   └───impl
│   ├───sender
│   │   ├───action
│   │   │   └───impl
│   │   └───impl
│   └───session
├───core
│   ├───authorization
│   │   ├───annotation
│   │   ├───aop
│   │   └───model
│   ├───common
│   │   ├───dto
│   │   │   └───segment
│   │   ├───exception
│   │   │   └───aop
│   │   └───util
│   ├───config
│   │   └───dev
│   ├───domain
│   │   ├───model
│   │   └───service
│   │       └───impl
│   ├───infrastructure
│   │   ├───forwarder
│   │   │   └───matcher
│   │   │       └───impl
│   │   └───persistence
│   ├───mapper
│   └───routing
└───plugins
```

