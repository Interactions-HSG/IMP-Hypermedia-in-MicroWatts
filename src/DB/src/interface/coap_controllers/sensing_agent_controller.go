package coap_controllers

import (
	"bytes"
	"github.com/plgd-dev/go-coap/v3/message"
	"github.com/plgd-dev/go-coap/v3/message/codes"
	"github.com/plgd-dev/go-coap/v3/mux"
	"log"
)

type SensingAgentController struct {
}

func ProvideSensingAgentController() *SensingAgentController {
	return &SensingAgentController{}
}

func (s *SensingAgentController) GetMessage(w mux.ResponseWriter, r *mux.Message) {

	err := w.SetResponse(codes.Content, message.TextPlain, bytes.NewReader([]byte("hello world")))
	if err != nil {
		log.Printf("cannot set response: %v", err)
	}
}
