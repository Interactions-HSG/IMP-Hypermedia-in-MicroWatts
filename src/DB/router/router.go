package router

import (
	"cmd/main/src/interface/coap_controllers"
	"github.com/plgd-dev/go-coap/v3/mux"
	"log"
)

func loggingMiddleware(next mux.Handler) mux.Handler {
	return mux.HandlerFunc(func(w mux.ResponseWriter, r *mux.Message) {
		log.Printf("ClientAddress %v, %v\n", w.Conn().RemoteAddr(), r.String())
		next.ServeCOAP(w, r)
	})
}

func NewRouter(
	sensingAgentController *coap_controllers.SensingAgentController,
) *mux.Router {

	// configure the router
	router := mux.NewRouter()
	router.Use(loggingMiddleware)

	// routes
	router.Handle("/a", mux.HandlerFunc(sensingAgentController.GetMessage))

	return router
}
