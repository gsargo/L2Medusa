# L2JServer in Docker
Run L2J server and it's CLI using Docker.

After running the setup, you will have:

- a login server with it's own database and the CLI available
- a game server with it's own database

Why separated? This way you can have the login server in one 
machine and the game server in other so the login server doesn't
consume resources from the game server. 

## Setting up Login server
Initial container build

```sh
cd login
docker-compose build
```

Start MariaDB with `docker-compose up db`

Once the previous command finishes (it should print `mysqld: ready for connections.`) 
open up a new terminal and create the database

```sh
docker-compose run --rm cli
db install -sql login/sql -t LOGIN -c -mods -m FULL
> Seems database already exists, do you want to continue installing? (y/N): y
quit
```

And now we can start the login server with `docker-compose up`

## Setting up game server
Initial container build

```sh
cd game
docker-compose build
```

Start MariaDB with `docker-compose up db`

Once the previous command finishes (it should print `mysqld: ready for connections.`) 
open up a new terminal and create the database

```sh
docker-compose run --rm cli
db install -sql game/sql -t GAME -c -mods -m FULL
> Seems database already exists, do you want to continue installing? (y/N): y
quit
```

Start game server with `docker-compose up`

## After initial setup
Simply start the servers with `cd login && docker-compose up -d` and 
`cd game && docker-compose up -d`. This will leave both servers running in
background

## Notes
All the services will restart automatically in case of failure.

## Configuration
If you need to attach configuration or custom files, you can do so using volumes. 
Check login/docker-compose.yml:

```yml
    volumes:
      - "./config:/usr/loginserver/config"
```

In this case, we are overwriting the configuration with whatever 
we got on `./config`
