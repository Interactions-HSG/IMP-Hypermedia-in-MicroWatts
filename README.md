# IMP-Hypermedia-in-MicroWatts


![drawio image](documentation/images/layout.drawio.png)

## Introduction
Smart Buildings could be a lot smarter if the sensors could act with some degree of autonomy. Most sensor nowadays
are strong enough to run an agent based program that could make decisions dynamically and depending on exterior factors.
Currently, the power of these sensors is not used to its full potential. With this project we want to demonstrate, that not
only will using an agent based approach make the system more robust and flexible, but it should also be more energy efficient.


## Demo
If you run the docker compose file you will see that some services will be created. It will spin up an instance of yggdrasil,
that will enable the hypermedia driven capabilities. The other services will also be started and do their initial setup.
The OrgManager and Datalake will create an artifact in the root workspace of yggdrasil. This will enable other agents to find
the endpoints that are of interest to them. Sensing agent will have to be started seperately as they require dedicated hardware.
If started the sensing agents will join the room they belief to be in (room1). They will try to adopt a fitting role and just 
sleep until a change happens. To simulate user driven data demand / adhoc demand, send a post request to the automation agent. 
This will proc the automation agent to also join the workspace and adopt the proper role, making the group well-formed. This will
cause the sensing agents to realize that they need to start send data. In the end you will receive the current temperature data, s
showing that the system works. It can be further demonstrated by adding a second sensing agent and in the middle of task completion
turning the participating sensor off, the system will then adapt and continue to work, by using the other sensor.

! Please check if the ip address used lines up with your setup. There is only one ip address that needs to be correct and it can
be found in the sensings agents source code. In the main file there is an entrypoint string, make sure this lines up with the ip address of the computer running the docker containers.
Additionally if running multiple Thingy53s adjust the agent id in the main.h file to be unique.

### Start docker container
```bash
docker-compose up
```

### Create the workspace 'room1'
```bash
curl --location --request POST 'http://localhost:8080/workspaces/' \
--header 'X-Agent-WebID: http://localhost:8081' \
--header 'Slug: room1' \
--header 'Content-Type: text/turtle' \
--data ''
```

With this the workspace 'room1' is created and the agents can join it. The Organization manager will automatically update the groups if needed.

### Send post request to automation agent
This will initiate a request from the user to get the current temperature in the room.
```bash
curl --location 'localhost:8081/agents/bob/inbox' \
--header 'Content-Type: application/json' \
--data '{"performative":"tell","sender":"user-1","receiver":"bob","content":"get_temperature(room1)","msgId":"45"}'
```



