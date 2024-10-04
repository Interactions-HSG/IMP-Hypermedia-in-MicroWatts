// Agent sample_agent in project automationagent

/* Initial beliefs and rules */
entrypoint("http://yggdrasil:8080/").

/* Initial goals */

!start.

/* Plans */

+!start : true
    <- .print("hello world.");
       .date(Y,M,D); .time(H,Min,Sec,MilSec); // get current date & time
       +started(Y,M,D,H,Min,Sec);             // add a new belief
       !findHyperMediaEnvironment.                     // First thing Automation Agent will do is join Organisation


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
       !findOrganisation.


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
