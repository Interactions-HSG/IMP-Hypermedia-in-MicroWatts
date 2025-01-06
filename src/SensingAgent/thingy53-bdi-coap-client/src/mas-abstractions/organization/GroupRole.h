#ifndef __GROUPROLE_H__
#define __GROUPROLE_H__

#include <stdint.h>
#include <zephyr/data/json.h>


struct ArtifactInfo
{
  char *name;
  int32_t port;
};

static const struct json_obj_descr ArtifactInfo_desc[] = {
  JSON_OBJ_DESCR_PRIM(struct ArtifactInfo, name, JSON_TOK_STRING),
  JSON_OBJ_DESCR_PRIM(struct ArtifactInfo, port, JSON_TOK_NUMBER),
};

struct ArtifactInfos {
  struct ArtifactInfo e[2];
  int32_t n;
};

static const struct json_obj_descr ArtifactInfos_desc[] = {
  JSON_OBJ_DESCR_OBJ_ARRAY(struct ArtifactInfos, e, 2, n,
  ArtifactInfo_desc, ARRAY_SIZE(ArtifactInfo_desc)),
};


struct Goal 
{
  char *goalId;
  char *missionId;
  char *schemeId;
};

static const struct json_obj_descr Goal_desc[] = {
  JSON_OBJ_DESCR_PRIM(struct Goal, goalId, JSON_TOK_STRING),
  JSON_OBJ_DESCR_PRIM(struct Goal, missionId, JSON_TOK_STRING),
  JSON_OBJ_DESCR_PRIM(struct Goal, schemeId, JSON_TOK_STRING),
};

struct GroupGetResponse 
{
  char *status;
  struct Goal goals[10];
  int32_t n;
};

static const struct json_obj_descr GroupGetResponse_desc[] = {
  JSON_OBJ_DESCR_PRIM(struct GroupGetResponse, status, JSON_TOK_STRING),
  JSON_OBJ_DESCR_OBJ_ARRAY(struct GroupGetResponse, goals, 10, n,
  Goal_desc, ARRAY_SIZE(Goal_desc)),
};


struct Role
{
  char *name;
};

static const struct json_obj_descr Role_desc[] = {
  JSON_OBJ_DESCR_PRIM(struct Role, name, JSON_TOK_STRING)
};

struct Roles
{
  struct Role e[5];
  int32_t n;
};

static const struct json_obj_descr Roles_desc[] = {
  JSON_OBJ_DESCR_OBJ_ARRAY(struct Roles, e, 5, n, 
  Role_desc, ARRAY_SIZE(Role_desc)),
};


#endif