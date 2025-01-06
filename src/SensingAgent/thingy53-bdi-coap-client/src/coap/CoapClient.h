/**
 * coap_client.h
 */

#ifndef __COAP_CLIENT_H__
#define __COAP_CLIENT_H__

#include <coap_server_client_interface.h>

#include <stdio.h>
#include <zephyr/kernel.h>
#include <net/coap_utils.h>
#include <zephyr/logging/log.h>
#include <zephyr/net/openthread.h>
//#include <zephyr/net/socket.h>
#include <openthread/thread.h>
#include <openthread/coap.h>
#include "mas-abstractions/organization/Organization.h"
extern "C" {
  //#include "peripherals/sensors.h"
  //#include "peripherals/battery.h"
  #include "peripherals/gpio.h"
  #include "thread/thread_helper.h"
}


/**************************************************************************************************
  IPv6 Address of CoAP Server
**************************************************************************************************/
static const char *const serverAddress = "fd54:d167:b555:2:0:0:c0a8:88e"; //fd4b:a8d1:ad71:211:0:ff:fe00:fc11


typedef void (*coap_response_cb_t)(void *p_context,  otMessage *p_message,  const otMessageInfo *p_message_info, otError result);

class CoapClient
{
    public:
        static otError initialize();
        static otError sendRequest2(const char* uri, const char* query, const char* payload, otCoapType requestType, otCoapCode requestCode, uint32_t requestObserve, void* pContext);
        static otError sendRequest(const char* to, const char* uri, const int port, const char* query, const char* payload, otCoapType requestType, otCoapCode requestCode, uint32_t requestObserve,const void* pContext);
        static otError sendRequestBlock(const char* to, const char* uri, const char* query, const char* payload, otCoapType requestType, otCoapCode requestCode, uint32_t requestObserve, void* pContext);
        static otError cleanup();
        static void coap_logging_response_handler(void *p_context, otMessage *p_message, const otMessageInfo *p_message_info, otError aResult) ;
    private:
        static void coap_response_handler(void *p_context,	otMessage *p_message,const otMessageInfo *p_message_info, otError aResult);
        static void coap_default_handler(void *p_context,	otMessage *p_message,const otMessageInfo *p_message_info);
        static otError init_coap_message(otMessage *aMessage, const char *aUriPath, const char *aQuery, const char* payload, otCoapType aType, 	  otCoapCode aCode, uint32_t aObserve);
};

#endif