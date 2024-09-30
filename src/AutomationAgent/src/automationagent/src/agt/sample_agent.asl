// Agent sample_agent in project automationagent

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true
    <- .print("hello world.");
       .date(Y,M,D); .time(H,Min,Sec,MilSec); // get current date & time
       +started(Y,M,D,H,Min,Sec);             // add a new belief
       findOrganisation(Org).


+findOrganisation(Org) : true
    <- readEndpoint(Response);
       readTD(Response).


{ include("$jacamo/templates/common-cartago.asl") }
{ include("$jacamo/templates/common-moise.asl") }
{ include("$moiseJar/asl/org-rules.asl") }
