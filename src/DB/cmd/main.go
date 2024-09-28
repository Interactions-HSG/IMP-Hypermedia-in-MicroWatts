package main

import (
	"cmd/main/router"
	"cmd/main/src/interface/coap_controllers"
	"github.com/plgd-dev/go-coap/v3"
)

func main() {

	sensingAgentController := coap_controllers.ProvideSensingAgentController()

	routes := router.NewRouter(sensingAgentController)

	coap.ListenAndServe("udp", ":5683", routes)

}
