# IMP-Hypermedia-in-MicroWatts


![drawio image](images/layout.drawio.png)

## ROADMAP
The rough steps to make this project a success

- [ ] Finish setting up the embedded systems development environment
- [ ] Implement rudimentary Sensing Agent that dynamically registers itself with the organisation
- [ ] Implement Organisation Manager that adds itself to the Yggdrasil instance so it may be found
- [ ] Implement coAP functionality in Yggdrasil
- [ ] Create Digital Twins of embedded agents
- [ ] Increase complexity of embedded bdi agents
- [ ] Whole system dynamic and operable
- [ ] Add demos that illustrate flexibility and robustness of chosen architecture


### Yggdrasil TO-DOs
- [ ] Set up new vertx verticle that acts as coAP server
- [ ] Functionality shoud be same as over HTTP
- [ ] Add functionality to expand #platform representation so that e.g. orgManager is easily found



### loose ideas
- Yggdrasil doesnt need much added complexity except for coAP and custom #platform representation. The additional services such as orgManager and GraphDB / digital twins should all be standalone software that can register themselves on Yggdrasil through creating an artifact of themselves in the wanted workspace.
- Only hardcoded URI should be the entrypoint to yggdrasil (Digital Twin could be found through yggdrasil as well technically)
- Without digital twin embedded agent would register itself directly -> create a digital Artifact that has minimal initialization params so that the embedded agent does not need to send the entire graph string that is its own representation. OR simply set different rules for makingArtifact with coAP where we do not need a specialised artifact