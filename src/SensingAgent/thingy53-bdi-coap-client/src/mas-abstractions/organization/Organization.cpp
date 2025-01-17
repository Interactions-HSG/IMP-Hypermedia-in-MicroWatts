#include "mas-abstractions/organization/Organization.h"
#include <algorithm>
#include "main.h"

LOG_MODULE_REGISTER(Organization, CONFIG_COAP_CLIENT_UTILS_LOG_LEVEL);

extern bool FOUND_NEEDED_ARTIFACTS;
extern bool IN_WORKSPACE;
extern bool RECEIVED_OPEN_ROLES;
extern bool DECIDED_ON_GOAL;
extern char *EMPTY_STRING;
extern char *ENTRYPOINT;
extern bool GROUP_WELL_FORMED;
extern bool COMMITTED_TO_MISSION;
extern bool ADOPTED_ROLE;

static std::string group_name = "workspaces/w1";
static std::string player_name = "unknown";

/**************************************************************************************************
  Global Variables.
**************************************************************************************************/

extern int ORGMANAGER_PORT;
extern int DATALAKE_PORT;
extern char AGENT_URI[];
static const char *const MY_WORKSPACE = "room1";
static bool FOUND_DB = false;
static bool FOUND_OMI = false;
static bool ROLE_AVAILABLE = false;



static void adopt_role() {
	otCoapType requestType = otCoapType::OT_COAP_TYPE_CONFIRMABLE;
	otCoapCode requestCode = otCoapCode::OT_COAP_CODE_POST;
	std::string payload = "{\"agentId\":\"" + std::string(AGENT_URI) + "\", \"groupId\":\"room1\", \"roleId\":\"sensing_agent\"}";
	CoapClient::sendRequest(ENTRYPOINT, MY_WORKSPACE,ORGMANAGER_PORT, EMPTY_STRING, payload.c_str(), requestType , requestCode, 1, &ctx_adopt_role);
}


/**************************************************************************************************
  COMMIT TO MISSON AND SEND DATA - CTX 10
**************************************************************************************************/
static void commit_to_mission()
{
    otCoapType requestType = otCoapType::OT_COAP_TYPE_CONFIRMABLE;
    otCoapCode requestCode = otCoapCode::OT_COAP_CODE_PUT;
    std::string payload = "{\"agentId\":\"http://yggdrasil:8080/workspaces/room1/artifacts/body_sensing_agent_" + std::to_string(AGENT_ID) + "\", \"missionId\":\"telemetry_measuring_mission\", \"schemeId\":\"sch_01_monitoring_scheme\", \"goalId\":\"measure_room_temperature\"}";
    CoapClient::sendRequest(ENTRYPOINT, MY_WORKSPACE, ORGMANAGER_PORT, EMPTY_STRING, payload.c_str(), requestType, requestCode, 0, &ctx_commit_mission);
}

static void handleGetArtifactsInWorkspace(char *payload, uint16_t length)
{
    LOG_INF("Received Response for artifacts in workspace");

    struct ArtifactInfos artifactInfos;

    // See: https://blog.golioth.io/how-to-parse-json-data-in-zephyr/
    int ret = json_arr_parse(payload, length,
                             ArtifactInfos_desc,
                             &artifactInfos);

    if (ret < 0)
    {
        LOG_ERR("JSON Parse Error: %d, ", ret);
    }
    else
    {
        struct ArtifactInfo info;
        for (int i = 0; i < artifactInfos.n; i++)
        {
            info = artifactInfos.e[i];
            if (strcmp("datalake", info.name) == 0)
            {
                FOUND_DB = true;
                DATALAKE_PORT = info.port;
            }
            if (strcmp("omi", info.name) == 0)
            {
                FOUND_OMI = true;
                ORGMANAGER_PORT = info.port;
            }
            if (FOUND_DB && FOUND_OMI)
            {
                FOUND_NEEDED_ARTIFACTS = true;
                break;
            }
        }
    }
}

static void handleJoinWorkspace(char *payload, int32_t length, otCoapType type, otCoapCode code)
{
    LOG_INF("Received Response for joining workspace");
    if (code != OT_COAP_CODE_CREATED)
    {
        LOG_INF("Failed to join Workspace");
        return;
    }
    LOG_INF("Successfully joined workspace");
    for (int32_t i = 0; i < length && 180; i++) {
        AGENT_URI[i] = payload[i];
    }
    AGENT_URI[length] = '\0';
    LOG_INF("agent uri: %s", AGENT_URI);
    IN_WORKSPACE = true;
}

static void handleLeaveWorkspace(otCoapType type, otCoapCode code)
{
    LOG_INF("Received Response for leaving workspace");
    if (code != OT_COAP_CODE_DELETED)
    {
        LOG_INF("Received unexpected response code for leaving workspace");
    }
    IN_WORKSPACE = false;
    COMMITTED_TO_MISSION = false;
}

static void handleGetGoalsResponse(char *payload, uint16_t length)
{
    LOG_INF("Received Response for get goals request");

    struct GroupGetResponse groupGetResponse;

    char *copy = (char *)malloc(length + 1);
    if (!copy)
    {
        LOG_ERR("Memory allocation failed");
        return;
    }
    memcpy(copy, payload, length);
    copy[length] = '\0';

    int ret = json_obj_parse(copy, length, GroupGetResponse_desc, ARRAY_SIZE(GroupGetResponse_desc), &groupGetResponse);
    if (ret < 0)
    {
        LOG_ERR("JSON Parse Error: %d, ", ret);
    }
    else
    {
        LOG_INF("Get Goal Response Status: %s", groupGetResponse.status);
        if (strcmp("well-formed", groupGetResponse.status) == 0)
        {
            if (!COMMITTED_TO_MISSION && ADOPTED_ROLE)
            {
                struct Goal g;
                for (int32_t i = 0; i < groupGetResponse.n; i++)
                {
                    g = groupGetResponse.goals[i];
                    if (strcmp("measure_room_temperature", g.goalId) == 0)
                    {
                        GROUP_WELL_FORMED = true;
                        commit_to_mission();
                        COMMITTED_TO_MISSION = true;
                    }
                }
            }
        }
        else
        {
            GROUP_WELL_FORMED = false;
            COMMITTED_TO_MISSION = false;
            if (!ADOPTED_ROLE) {
                adopt_role();
            }
        }
    }

    free(copy);
}

void handleAdoptRole(char *payload, uint16_t length, otCoapType type, otCoapCode code)
{
    LOG_INF("Received response for joining group: \n payload: %s \n type: %d \n code: %d\n", payload, type, code);
    if (code == OT_COAP_CODE_CREATED) {
        LOG_INF("Successfully adopted role");
        ADOPTED_ROLE = true;
    }
}

void handleReceiveRoles(char *payload, uint16_t length) {
    LOG_INF("Received response for get roles: \n %s",payload);

    struct Roles roles;

    int ret = json_arr_parse(payload, length,
                             Roles_desc,
                             &roles);

    if (ret < 0) {
        LOG_ERR("Error Json parsing: %d", ret);
    } else {
        for (int32_t i = 0; i < roles.n; i++)
        {
            if(strcmp("sensing_agent", roles.e[i].name) == 0) {
                ROLE_AVAILABLE = true;
                return;
            }
        }
    }
    ROLE_AVAILABLE = false;
}

void Organization::onDirectResponse(void *pContext, uint8_t *p_message, uint16_t length, otCoapType type, otCoapCode code)
{
    uint8_t *context = (uint8_t *)pContext;
    switch (*context)
    {
    case ctx_get_artifacts_in_workspace:
        if (!FOUND_NEEDED_ARTIFACTS)
        {
            handleGetArtifactsInWorkspace((char *)p_message, length);
        }
        break;
    case ctx_join_workspace:
        if (!IN_WORKSPACE)
        {
            handleJoinWorkspace((char *) p_message, length, type, code);
        }
        break;
    case ctx_leave_workspace:
        if (IN_WORKSPACE)
        {
            handleLeaveWorkspace(type, code);
        }
        break;
    case ctx_get_roles:
        handleReceiveRoles((char *)p_message, length);
        RECEIVED_OPEN_ROLES = true;
        break;
    case ctx_adopt_role:
        handleAdoptRole((char *)p_message, length, type, code);
        break;
    case ctx_sent_sensor_data:
        LOG_INF("Received response for data sent: \n %s", (char *)p_message);
        break;
    case ctx_commit_mission:
        LOG_INF("Received response for commit mission: \n %s", (char *)p_message);
        break;
    case ctx_get_goals:
        handleGetGoalsResponse((char *)p_message, length);
        break;
    default:
        LOG_INF("Received response with unknown context: %d", *context);
        break;
    }
}
