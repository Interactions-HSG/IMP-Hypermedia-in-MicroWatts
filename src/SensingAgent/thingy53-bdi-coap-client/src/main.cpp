/**
 * Thing:53 BDI CoAP Client
 *
 * main.cpp
 */

#include <dk_buttons_and_leds.h>
#include <ram_pwrdn.h>
#include <zephyr/pm/device.h>
#include <zephyr/pm/pm.h>
//#include <zephyr/drivers/gpio.h>
#include <zephyr/sys/poweroff.h>
#include <ram_pwrdn.h>

#include "main.h"
#include "mas-abstractions/organization/Organization.h"
// #include "mas-abstractions/agent/ReactiveAgent.h"

#define CONFIG_THINGY_LOW_POWER
#define CONFIG_THINGY_USE_SENSOR
#if defined(CONFIG_THINGY_LOW_POWER)
#include "low_power.h"
#endif


LOG_MODULE_REGISTER(main, CONFIG_COAP_CLIENT_LOG_LEVEL);

//#define GPIO0_NODE DT_NODELABEL(gpio0)
//#define PIN 9

/**************************************************************************************************
  Agent ID string.
**************************************************************************************************/
std::string agent_id = "sensing_agent-" + std::to_string(AGENT_ID);

/**************************************************************************************************
  LED defines.
**************************************************************************************************/
#define RED_LED				DK_LED1
#define GREEN_LED			DK_LED2
#define BLUE_LED			DK_LED3


/**************************************************************************************************
  Variables for BDI-reasoner
**************************************************************************************************/
int32_t agent_period = DEFAULT_AGENT_PERIOD;
//

/**************************************************************************************************
  Global Variables.
**************************************************************************************************/
const char *ENTRYPOINT = "fd54:d167:b555:2:0:0:c0a8:75"; //fd4b:a8d1:ad71:211:0:ff:fe00:fc11
char AGENT_URI[180];
static const int YGGDRASIL_PORT = 5683;
int DATALAKE_PORT;
int ORGMANAGER_PORT;
static const char *const MY_WORKSPACE = "room1";
static const char *const ROOT_WORKSPACE = "root";
static const char *const DATALAKE = "datalake";
static const char *const ORG_MANAGER = "omi";
const char *EMPTY_STRING = "";


int read_artifact(const char* workspace, const char* artifactName);

/**************************************************************************************************
  CoAP Variables.
**************************************************************************************************/
#ifdef CONFIG_THINGY_USE_SENSOR
BME688* m_p_bme_sensor;
#endif
/*
#ifdef USE_EMB_BDI
	Agent* m_p_sensor_agent;
#else
	ReactiveAgent* m_p_sensor_agent;
#endif
*/
/**************************************************************************************************
  Thread Declarations.
**************************************************************************************************/

/**************************************************************************************************
  Measurement Thread Declaration.
**************************************************************************************************/
void sensing_thread_cb(void);
K_THREAD_DEFINE(sensing_thread_id, SENSING_STACKSIZE, sensing_thread_cb, NULL, NULL, NULL, 
					SENSOR_PRIORITY, 0, 0);

/**************************************************************************************************
  Transmission Thread Declaration.
**************************************************************************************************/
void transmission_thread_cb(void);
K_THREAD_DEFINE(transmission_thread_id, TRANSMISSION_STACKSIZE, transmission_thread_cb, NULL, NULL, NULL, 
					TRANSMISSION_PRIORITY, 0, 0);



/**************************************************************************************************
  Toggles MED and SED mode when enabled in build.
**************************************************************************************************/
//typedef struct otLinkModeConfig
//{
//    bool mRxOnWhenIdle : 1; ///< 1, if the sender has its receiver on when not transmitting. 0, otherwise.
//    bool mDeviceType : 1;   ///< 1, if the sender is an FTD. 0, otherwise.
//    bool mNetworkData : 1;  ///< 1, if the sender requires the full Network Data. 0, otherwise.
//}

static void on_mtd_mode_toggle(/*uint32_t med*/otLinkModeConfig mode)
{
// #if IS_ENABLED(CONFIG_PM_DEVICE)
	//const struct device *cons = DEVICE_DT_GET(DT_CHOSEN(zephyr_console));

	// if (!device_is_ready(cons)) {
	// 	return;
	// }

	uint32_t rxon = mode.mRxOnWhenIdle;
	uint32_t ftd = mode.mDeviceType;
	dk_set_led_off(BLUE_LED);
	dk_set_led_off(RED_LED);
	dk_set_led_off(GREEN_LED);
	if (rxon || ftd) {
		// pm_device_action_run(cons, PM_DEVICE_ACTION_RESUME);
		dk_set_led_on(RED_LED);
	} else {
		// pm_device_action_run(cons, PM_DEVICE_ACTION_SUSPEND);
		dk_set_led_on(GREEN_LED);
	}
// #endif
	
}

/**************************************************************************************************
  JOIN WORKSPACE - CTX 2
**************************************************************************************************/
static void join_workspace() {
	const std::string workspace = std::string("workspaces/") + MY_WORKSPACE + "/join";

	otCoapType requestType = otCoapType::OT_COAP_TYPE_CONFIRMABLE;
	otCoapCode requestCode = otCoapCode::OT_COAP_CODE_POST;
	std::string payload = "{\"agentId\":\"sensing_agent_" + std::to_string(AGENT_ID) + "\"}";
	CoapClient::sendRequest(ENTRYPOINT,workspace.c_str(),YGGDRASIL_PORT, EMPTY_STRING, payload.c_str(), requestType , requestCode, 0, &ctx_join_workspace);
}

/**************************************************************************************************
  LEAVE WORKSPACE - CTX 3
**************************************************************************************************/
static void leave_workspace() {
	const std::string workspace = std::string("workspaces/") + MY_WORKSPACE + "/leave";
	otCoapType requestType = otCoapType::OT_COAP_TYPE_CONFIRMABLE;
	otCoapCode requestCode = otCoapCode::OT_COAP_CODE_POST;
	std::string payload = "{\"agentId\":\"sensing_agent_" + std::to_string(AGENT_ID) + "\"}";
	CoapClient::sendRequest(ENTRYPOINT,workspace.c_str(),YGGDRASIL_PORT, EMPTY_STRING, payload.c_str(), requestType , requestCode, 0, &ctx_leave_workspace);
}

/**************************************************************************************************
  JOIN GROUP AND ADOPT ROLE - CTX 9
**************************************************************************************************/
static void adopt_role() {
	otCoapType requestType = otCoapType::OT_COAP_TYPE_CONFIRMABLE;
	otCoapCode requestCode = otCoapCode::OT_COAP_CODE_POST;
	std::string payload = "{\"agentId\":\"" + std::string(AGENT_URI) + "\", \"groupId\":\"room1\", \"roleId\":\"sensing_agent\"}";
	CoapClient::sendRequest(ENTRYPOINT, MY_WORKSPACE,ORGMANAGER_PORT, EMPTY_STRING, payload.c_str(), requestType , requestCode, 1, &ctx_adopt_role);
}


/**************************************************************************************************
  GET AVAILABLE ROLES - CTX 8
**************************************************************************************************/
static void get_roles() {
	const std::string roles = MY_WORKSPACE + std::string("/roles");

	otCoapType requestType = otCoapType::OT_COAP_TYPE_CONFIRMABLE;
	otCoapCode requestCode = otCoapCode::OT_COAP_CODE_GET;
	std::string payload = ""; //"{\"agentId\":\"sensing_agent\", \"groupName\":\"room1\", \"role\":\"sensing\"}";
	CoapClient::sendRequest(ENTRYPOINT,roles.c_str(),ORGMANAGER_PORT, EMPTY_STRING, payload.c_str(), requestType , requestCode, 0, &ctx_get_roles);
}

/**************************************************************************************************
  GET GOALS - CTX 
**************************************************************************************************/
static void get_goals() {
	const std::string roles = MY_WORKSPACE;

	otCoapType requestType = otCoapType::OT_COAP_TYPE_CONFIRMABLE;
	otCoapCode requestCode = otCoapCode::OT_COAP_CODE_GET;
	std::string payload = AGENT_URI; //"{\"agentId\":\"sensing_agent\", \"groupName\":\"room1\", \"role\":\"sensing\"}";
	CoapClient::sendRequest(ENTRYPOINT,roles.c_str(),ORGMANAGER_PORT, EMPTY_STRING, payload.c_str(), requestType , requestCode, 1, &ctx_get_goals);
}


/**************************************************************************************************
  CHECK ROOT WORKSPACE FOR ARTIFACTS - CTX 1
**************************************************************************************************/
static void check_artifacts_in_workspace(const char* whichWorkspace) {
	const std::string rootWorkspaceArtifacts = std::string("workspaces/") + whichWorkspace + "/artifacts";
	otCoapType requestType = otCoapType::OT_COAP_TYPE_CONFIRMABLE;
	otCoapCode requestCode = otCoapCode::OT_COAP_CODE_GET;
	CoapClient::sendRequest(ENTRYPOINT,rootWorkspaceArtifacts.c_str(),YGGDRASIL_PORT, EMPTY_STRING, EMPTY_STRING, requestType , requestCode, 0, &ctx_get_artifacts_in_workspace);
}

/**************************************************************************************************
  SEND DATA TO DATALAKE - CTX 10
**************************************************************************************************/
static void send_data_to_datalake() {
	// trailing slashes will not work!
	const std::string uri = "data";
	const std::string param = "";
	m_p_bme_sensor->measure();
	std::string payload = m_p_bme_sensor->getPayload();
	otCoapType requestType = otCoapType::OT_COAP_TYPE_CONFIRMABLE;
	otCoapCode requestCode = otCoapCode::OT_COAP_CODE_POST;
	int observe = 0;	
	CoapClient::sendRequest(ENTRYPOINT,uri.c_str(),DATALAKE_PORT, param.c_str(), payload.c_str(), requestType , requestCode, observe, &ctx_sent_sensor_data);
}

/**************************************************************************************************
  AGENT THREAD STUFF
**************************************************************************************************/
enum AgentState {
    AGENT_POWERED_OFF,
    AGENT_CHECKING_ARTIFACTS,
    AGENT_JOINING_WORKSPACE,
    AGENT_WAITING_FOR_ROLES,
	AGENT_JOIN_GROUP,
    AGENT_RUNNING,
};

static AgentState current_state = AGENT_POWERED_OFF;

/* Flags */
bool RECEIVED_OPEN_ROLES = false;
bool FOUND_NEEDED_ARTIFACTS = false;
bool IN_WORKSPACE = false;
bool GROUP_WELL_FORMED = false;
bool COMMITTED_TO_MISSION = false;
bool ADOPTED_ROLE = false;

static void agent_state_handler(void) {
    switch (current_state) {
        case AGENT_POWERED_OFF:
			// Reset beliefs
			LOG_INF("Agent offline...");
			RECEIVED_OPEN_ROLES = false;
			FOUND_NEEDED_ARTIFACTS = false;
			IN_WORKSPACE = false;
			GROUP_WELL_FORMED = false;
			COMMITTED_TO_MISSION = false;
			ADOPTED_ROLE = false;
            break;

        case AGENT_CHECKING_ARTIFACTS:
            if (FOUND_NEEDED_ARTIFACTS) {
                LOG_INF("Found needed artifacts.");
                current_state = AGENT_JOINING_WORKSPACE;
            } else {
     	        LOG_INF("Checking artifacts...");
    	        check_artifacts_in_workspace("root");
			}
            break;

        case AGENT_JOINING_WORKSPACE:
            if (IN_WORKSPACE) {
                LOG_INF("Joined workspace.");
                current_state = AGENT_WAITING_FOR_ROLES;
            } else {
            	LOG_INF("Joining workspace...");
				join_workspace();
			}
            break;

        case AGENT_WAITING_FOR_ROLES:
            if (RECEIVED_OPEN_ROLES) {
				get_goals();
                current_state = AGENT_JOIN_GROUP;
            } else {
            	LOG_INF("Waiting for roles...");
				get_roles();
			}
            break;
		case AGENT_JOIN_GROUP:
            LOG_INF("Received open roles.");
            adopt_role();
			current_state = AGENT_RUNNING;	
			break;

        case AGENT_RUNNING:
            LOG_INF("Agent running...");
			if (ADOPTED_ROLE && GROUP_WELL_FORMED) {
				send_data_to_datalake();
			}
            break;

        default:
            LOG_ERR("Unknown state!");
            break;
    }
}

// Add a periodic work queue or thread to handle agent states
void agent_worker_thread(void) {
    while (true) {
        agent_state_handler();
        k_msleep(agent_period);  // Adjust polling interval as needed
    }
}

K_THREAD_DEFINE(agent_worker_thread_id, STACKSIZE, agent_worker_thread, NULL, NULL, NULL, AGENT_PRIORITY, 0, 0);


/**************************************************************************************************
  Button Actions.
**************************************************************************************************/
static void on_button_changed(uint32_t button_state, uint32_t has_changed) {
	uint32_t buttons = button_state & has_changed;
	
	if (buttons & DK_BTN1_MSK) {
		dk_set_led_off(BLUE_LED);
		dk_set_led_off(RED_LED);
		dk_set_led_off(GREEN_LED);
		if (current_state == AGENT_POWERED_OFF) {
			dk_set_led_on(GREEN_LED);
			LOG_INF("Powering on...");
			current_state = AGENT_CHECKING_ARTIFACTS;
		} else {
			LOG_INF("Powering off...");
			leave_workspace();
			current_state = AGENT_POWERED_OFF;
			dk_set_led_on(RED_LED);
		}
	}
}

/**************************************************************************************************
  Sensor Semaphore.
**************************************************************************************************/
K_SEM_DEFINE(sensor_sem, 0, 1);

/**************************************************************************************************
  Sensing Thread. Lower number -> higher prio
**************************************************************************************************/
void sensing_thread_cb(void)
{
	k_sleep(K_FOREVER);
	/* Run sensing loop */
	while(true){
		/* Sleep as long as no role is assigned */
		//LOG_INF("%i: Sensing Thread...\n", log_iterator++);
		/* Read BME688 enviromental sensor */		
		#ifdef CONFIG_THINGY_USE_SENSOR
		m_p_bme_sensor->measure();

		/* Release transmission when buffer is full */
		if (m_p_bme_sensor->isBufferFull())
		{ 
			k_sem_give(&sensor_sem);
		}
		#endif
		/* sleep */
		//k_sleep(1000 /*roles[current_role].getMeasurementPeriod()*/);
	}
}

/**************************************************************************************************
  Transmission Thread.
**************************************************************************************************/
void transmission_thread_cb(void)
{
	//k_sleep(K_FOREVER);
	/* Run transmission loop */
	while(true){
		/* Transmit to Server */
		k_sem_take(&sensor_sem, K_FOREVER); //Add timeout to prevent deadlock...
		//LOG_INF("%i: Transmit sensor data:\n---> %s\n", log_iterator++, m_p_bme_sensor->getPayload() .c_str());

		/* Reset buffer */
		#ifdef CONFIG_THINGY_USE_SENSOR
		m_p_bme_sensor->clearBuffer();
		#endif
		/* sleep */
		k_sleep(K_FOREVER);
	}
}


/**************************************************************************************************
  Function to read artifact -> get URI and endpoints
**************************************************************************************************/
int read_artifact(const char* workspace, const char* artifactName) {
	const std::string artifactUri = std::string("workspaces/") + workspace + "/artifacts/" + artifactName;
	otCoapType requestType = otCoapType::OT_COAP_TYPE_CONFIRMABLE;
	otCoapCode requestCode = otCoapCode::OT_COAP_CODE_GET;
	CoapClient::sendRequest(ENTRYPOINT, artifactUri.c_str(),YGGDRASIL_PORT, EMPTY_STRING, EMPTY_STRING, requestType , requestCode, 0, &ctx_read_artifact);
	return 0;
}


/**************************************************************************************************
  Main Function.
**************************************************************************************************/
int main(void)
{
	LOG_INF("%i: Start Thingy:53 BDI CoAP Client...\n", log_iterator++);

	int ret;
	#if USE_GPIO_OUTPUT
		/* Initialize GPIOs */
		ret = gpio_output_init();
		if (ret) {
			LOG_ERR("Cannot init gpios, (error: %d)", ret);
			return 0;
		}
		gpio_pin_set_dt(&out0, 0);
		gpio_pin_set_dt(&out1, 0);
		gpio_pin_set_dt(&out2, 0);
	#endif /* USE_GPIO_OUTPUT */

	LOG_INF("%i: Initialized GPIOS sucessfully", log_iterator++);

	/* Enabled if built as sleepy end device */
	if (IS_ENABLED(CONFIG_RAM_POWER_DOWN_LIBRARY)) {
		//power_down_unused_ram();
	}

	/* Initialize buttons */
	ret = dk_buttons_init(on_button_changed);
	if (ret) {
		LOG_ERR("Cannot init buttons (error: %d)", ret);
		return 0;
	}
	LOG_INF("%i: Initialized Buttons sucessfully", log_iterator++);

	/* Initialize LEDs */
	ret = dk_leds_init();
	if (ret) {
		LOG_ERR("Cannot init leds, (error: %d)", ret);
		return 0;
	}

	LOG_INF("%i: Initialized LEDS sucessfully", log_iterator++);

	/* Initialize Sensor object*/
	#ifdef CONFIG_THINGY_USE_SENSOR
	m_p_bme_sensor = new BME688(agent_id);
	#endif
	// m_p_sensor_agent = new ReactiveAgent();

	LOG_INF("Creating CoAP channel %d",0);
	startThread(on_mtd_mode_toggle);	
	k_sleep(K_MSEC(10000));

	dk_set_led_off(BLUE_LED);
	dk_set_led_off(RED_LED);
	dk_set_led_off(GREEN_LED);

	
	LOG_INF("Initializing coap client %d",1);
	LOG_INF("Entrypoint is: %s", ENTRYPOINT);
	CoapClient::initialize();
	dk_set_led_on(BLUE_LED);
	k_sleep(K_MSEC(10000));
	dk_set_led_off(BLUE_LED);
	dk_set_led_off(RED_LED);
	dk_set_led_off(GREEN_LED);


	
}