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
the endpoints that are of interest to them. The SensingAgent for example looks for the datalake service and tries to find the
endpoint where it can send its data to. Additionally it will join the workspace that represents the room in the building where
it has been placed.

## ROADMAP
The rough steps to make this project a success

- [x] Finish setting up the embedded systems development environment
- [x] Implement rudimentary Sensing Agent that can find datalake at runtime
- [ ] Implement rudimentary Sensing Agent that dynamically registers itself with the organisation
- [ ] Implement Organisation Manager that adds itself to the Yggdrasil instance so it may be found
- [x] Implement coAP functionality in Yggdrasil
- [x] Sensing Agent joins his workspace, metadata that describes the agent is added.
- [ ] Increase complexity of embedded bdi agents
- [ ] Whole system dynamic and operable
- [ ] Add demos that illustrate flexibility and robustness of chosen architecture


### Yggdrasil TO-DOs
- [x] Set up new vertx verticle that acts as coAP server
- [x] Functionality shoud be same as over HTTP (restricted)
- [x] Add functionality to expand #platform representation so that e.g. orgManager is easily found -> Root level workspace (later .well-known endpoint)

### Yggdrasil Roadblocks
- [ ] What to do with base uri ? If coAP agent disregards base Uri and just uses the paths it works. 
But that would mean that the agent cannot take the actual uris advertised in the representations.
-> Create both representations in yggdrasil. Coap representations could not advertise HTTP methods.
- [x] How should request payloads look like? /join uses http headers -> coap has options. Currently using 500 and 600
- [ ] Should we add another representation factory that gives tds specifically for coap?


### loose ideas
- Yggdrasil doesnt need much added complexity except for coAP and custom #platform representation. The additional services such as orgManager and GraphDB / digital twins should all be standalone software that can register themselves on Yggdrasil through creating an artifact of themselves in the wanted workspace.
- Only hardcoded URI should be the entrypoint to yggdrasil (Digital Twin could be found through yggdrasil as well technically)
- Without digital twin embedded agent would register itself directly -> create a digital Artifact that has minimal initialization params so that the embedded agent does not need to send the entire graph string that is its own representation. OR simply set different rules for makingArtifact with coAP where we do not need a specialised artifact
- BLE Beacon to be used for location tracking of embedded agents. Could be used in order to enable easy moving of agents between different locations.
- Using the Agent body artifact to expose signifiers that point to the location of Data storage. There could be one central DB service that exposes a query endpoint and the agents would point to that endpoint, with each agent indicating in their representation what parameters must be used on the query endpoint to get the data they have stored.
- Could have a program sitting in network that listens for new devices joining and sends them location of yggdrasil endpoint -> requires a set endpoint for the agents to receive the yggdrasil endpoint -> like .well-known
- Could have a program that listens for new devices joining and sends them the location of the orgManager -> requires a set endpoint for the agents to receive the orgManager endpoint -> like .well-known -> would enable no hardcoding of even entrypoint


embedded device:

startup
-> connect thread network

( ble get entrypoint )
( room hardcoded )

-> /root/artifacts/

-> /root/artifacts/orgManager
-> /root/artifacts/datalake

OrgManagerlogic:
-> join org

-> join group ( room )
-> get roles
-> evaluates roles
-> adopt a role

-> send measurements ( either immediately or upon group well-formed )

-> quit roles


Full logic - Constrained Sensing Agent:
-> join workspace ( room )

<- groupname groupid ( room )

-> join group ( room )
<- rollen ( room )
-> adopt role ( room )
<- success ( room )

--- idle ---

<- well-formed ( room )
-> send measurements ( room )

<- not well-formed ( room )
-> stop sending measurements ( room )

--- out of power ---
-> quit workspaces ( room )



++++ add to yggdrasil update messages of new / deleted devices the location header of the device
