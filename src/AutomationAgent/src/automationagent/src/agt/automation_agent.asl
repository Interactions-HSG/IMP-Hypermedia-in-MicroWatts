/* Initial beliefs and rules */
role(automation_agent).

hasRole(Role) :- role(Role) & roles(Roles) & .member(Role, Roles).

groupWellFormed(false).

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
    .print("hello world.");
    .date(Y,M,D); .time(H,Min,Sec,MilSec); // get current date & time
    +started(Y,M,D,H,Min,Sec).           // add a new belief
    // !findHyperMediaEnvironment. // First thing Automation Agent will do is join Organisation

/* Receiving belief from a user to determine the temperature of a room
1. Find the workspace Room
2. Enter the workspace Room
 *
 */
+get_temperatur(Room)[source(User)] : true <-
    .print("Log: Retrieve the temperature in ", Room, ".");
    !joinWorkspace(Room);
    !adoptRole;
    
    getSensorData("Temperature", Payload, Success);
    .print("Log: The newest value for temperature: " + Payload) ;
    .print("Log: Mission completed.").

+get_humidity(Room)[source(User)] : true <-
    .print("Log: Retrieve the humidity in ", Room, ".");
    !joinWorkspace(Room);
    !adoptRole;

    getSensorData("Humidity", Payload, Success);
    .print("Log: The newest value for humidity: " + Payload) ;
    .print("Log: Mission completed.").


/* Receiving belief from a user to stop determining the temperature of a room.
 *
 */
-stop_temperature(Room)[source(User)] : true <-
    .print("Log: Stop receiving temperature in ", Room, ".");
    !leaveWorkspace(Room).

-stop_humidty(Room)[source(User)] : true <-
    .print("Log: Stop receiving humidity in ", Room, ".");
    !leaveWorkspace(Room).

/* Receiving belief from the OMI to join a group
 * 
 */
+group(GroupName)[source(Sender)] : true <- 
    .print("Log: New group ", GroupName, " received from ", Sender, ".").
    

/* Receiving belief from the OMI to exit a group
 *
 */
-group(GroupName)[source(Sender)] : true <-
    .print("Log: Leave group ", GroupName, " received from ", Sender, ".");
    !leaveOmiGroup(GroupName).


+notifyGroup(IsWellFormed)[source(Sender)] : true <-
    .print("Log: Automation Agent is notified about the group change.");

/*
 *
 */
+roles(RoleId)[source(Sender)] : true <-
    .print("Log: New role ", RoleId, " received from ", Sender, ".").

/*
 *
 */
-roles(RoleId)[source(Sender)] : true <-
    .print("Log: Leave role ", RoleId, " received from ", Sender, ".").

/* Plan for joining workspace
 *
 */
+!joinWorkspace(Room) : true <-
    .print("Join workspace ", Room,".");
    joinRoom(Room, Success).

/* Plan for leaving a workspace
 *
 */
+!leaveWorkspace(Room) : true <-
    .print("Leave workspace ", Room, ".");
    leaveRoom(Room, Success).

+!adoptRole : hasRole(Role) & group(GroupId) <-
    adoptRole(Role, GroupId, Success);
    .print("RoleId: ", Role, " Group: ", GroupId, " Success: ", Success).

+!adoptRole : true <-
    .print("Does not know RoleId and GroupId");
    .wait(5000);
    !adoptRole.

+!revokeRole : true <-
    .print("Log: Role is revoked.").

+!automate_telemetry(Mission, Scheme) : true <-
    .print(Mission, Scheme);
    commitToMission(Mission, Scheme, "automate_telemetry", Success);
    .print("It works.").

{ include("$jacamo/templates/common-cartago.asl") }
{ include("$jacamo/templates/common-moise.asl") }
{ include("$moiseJar/asl/org-rules.asl") }
