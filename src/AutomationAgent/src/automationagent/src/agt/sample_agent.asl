// Agent sample_agent in project automationagent

/* Initial beliefs and rules */
entrypoint("http://localhost:8080/").

/* Initial goals */

!start.

/* Plans */

+!start : true
    <- .print("hello world.");
       .date(Y,M,D); .time(H,Min,Sec,MilSec); // get current date & time
       +started(Y,M,D,H,Min,Sec);             // add a new belief
       !findOrganisation.


+!findOrganisation : entrypoint(URI) <- 
       .print("Finding organisation");
       readEndpoint(URI, EntrypointRepresentation);
       .print("Read entrypoint, checking out root workspace");
       getWorkspace("root",EntrypointRepresentation, RootWorkspaceUri);
       readEndpoint(RootWorkspaceUri, RootWorkspaceRepresentation);
       .print("Found root workspace, joining Organisation");
       joinOrganisationInWorkspace(RootWorkspaceRepresentation).


{ include("$jacamo/templates/common-cartago.asl") }
{ include("$jacamo/templates/common-moise.asl") }
{ include("$moiseJar/asl/org-rules.asl") }
