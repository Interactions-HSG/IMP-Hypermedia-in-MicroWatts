/* Initial beliefs and rules */
role(automation_agent).

hasRole(Role) :- role(Role) & roles(Roles) & .member(Role, Roles).

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
+get_temperature(Room)[source(User)] : true <-
    .print("Log: Measure the temperature in ", Room, ".");
    !joinWorkspace(Room);
    !adoptRole.

/* Receiving belief from a user to stop determining the temperature of a room.
 *
 */
-get_temperature(Room)[source(User)] : true <-
    .print("Log: Stop the temperature measurement in ", Room, ".");
    !leaveWorkspace(Room).

/* Receiving belief from the OMI to join a group
 * 
 */
+group(GroupName)[source(OMI)] : true <- 
    .print("Log: New group ", GroupName, " received from ", OMI, ".").
    

/* Receiving belief from the OMI to exit a group
 *
 */
-group(GroupName)[source(OMI)] : true <-
    .print("Log: Leave group ", GroupName, " received from ", OMI, ".");
    !leaveOmiGroup(GroupName).

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
    joinYggdrasil(Room, Success).

/* Plan for leaving a workspace
 *
 */
+!leaveWorkspace(Room) : true <-
    .print("Leave workspace ", Room, ".").

+!adoptRole : hasRole(Role) & group(GroupId) <-
    adoptRole(Role, GroupId, Success);
    .print("RoleId: ", Role, " Group: ", GroupId, " Success: ", Success).

+!adoptRole : true <-
    .print("Does not know RoleId and GroupId");
    .wait(5000);
    !adoptRole.

+!automate_telemetry(Mission, Scheme) : true <-
    .print(Mission, Scheme);
    commitToMission(Mission, Scheme, "automate_telemetry", Success);
    .print("It works.").
/*
// Currently hardcoded EntryURI will be used
+!findHyperMediaEnvironment : entrypoint(URI) <- 
       .print("Finding organisation");
       getEntrypoint(URI, EntrypointRepresentation, Success);
       !entrypointFound(EntrypointRepresentation, Success).


+!entrypointFound(EntrypointRepresentation, Success) : Success == true <-
       .print("Found entrypoint, checking out root workspace");
       getWorkspace("root",EntrypointRepresentation, FoundWorkspace ,RootWorkspaceUri);    // Returns true / false and the URI to the rootWorkspace
       !workspaceFound(FoundWorkspace, RootWorkspaceUri).
       
+!entrypointFound(EntrypointRepresentation, Success) : Success == false <-
       .print("Could not find entrypoint, trying again next cycle.");
       .random([10000,20000,50000],X);
       .wait(X);
       !findHyperMediaEnvironment.

*/

// Plan for if we do not find the root uri 
+!workspaceFound(FoundWorkspace, RootWorkspaceUri) : FoundWorkspace == false <-
       .print("Could not find root workspace, trying again next cycle.");
       .random([10000,20000,50000],X);
       .wait(X);
       !findHyperMediaEnvironment. 

// If we find a URI for a root workspace plan
+!workspaceFound(FoundWorkspace, RootWorkspaceUri) : FoundWorkspace == true <-
       .print("Found root workspace, joining Organisation");
       readEndpoint(RootWorkspaceUri, RootWorkspaceRepresentation);                 // get the TD representation of root workspace
       findOrganisationInWorkspace(RootWorkspaceRepresentation, FoundOrg, OrgArtifactUri);          // try to get the URI of the Org Artifact
       !organisationFound(FoundOrg, OrgArtifactUri, RootWorkspaceUri).

// plan for if we do not find the org artifact uri
+!organisationFound(FoundOrg, OrgArtifactUri, RootWorkspaceUri) : FoundOrg == false <-
       .print("Could not find organisation, trying again nect cycle.");
       .random([10000,20000,50000],X);
       .wait(X);
       !workspaceFound(true, RootWorkspaceUri).
                                              
// plan of we do get the org artifact uri
+!organisationFound(FoundOrg, OrgArtifactUri, RootWorkspaceUri) : FoundOrg == true <-
       .print("Found Organisation Artifact, trying to join Organisation.");
       readEndpoint(OrgArtifactUri, OrgRepresentation);
       joinOrganisation(OrgRepresentation,Success).

{ include("$jacamo/templates/common-cartago.asl") }
{ include("$jacamo/templates/common-moise.asl") }
{ include("$moiseJar/asl/org-rules.asl") }
