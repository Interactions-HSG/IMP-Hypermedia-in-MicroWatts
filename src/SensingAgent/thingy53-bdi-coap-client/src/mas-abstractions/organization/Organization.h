#ifndef __ORGANIZATION_H__
#define __ORGANIZATION_H__

#include <zephyr/logging/log.h>
#include <dk_buttons_and_leds.h>
#include <iostream>
#include <list>
#include "mas-abstractions/organization/GroupRole.h"
#include "coap/CoapClient.h"

extern "C" {
  //#include "peripherals/sensors.h"
  //#include "peripherals/battery.h"
  #include "peripherals/gpio.h"
  #include "thread/thread_helper.h"
}


static const uint8_t ctx_get_artifacts_in_workspace = 11;
static const uint8_t ctx_read_artifact = 12;
static const uint8_t ctx_join_workspace = 13;
static const uint8_t ctx_leave_workspace = 14;
static const uint8_t ctx_get_roles = 18;
static const uint8_t ctx_adopt_role = 19;
static const uint8_t ctx_get_goals = 20;
static const uint8_t ctx_sent_sensor_data = 21;
static const uint8_t ctx_commit_mission = 22;

class Organization 
{

    public:
        Organization();
        static void onDirectResponse(void* pContext,  uint8_t *p_message, uint16_t length, otCoapType type, otCoapCode code);
    private:
        
        
};

#endif