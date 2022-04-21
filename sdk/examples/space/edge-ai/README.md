# Edge AI NMF App



NMF App that allows to:

	- Run AI for Earth Observation
	- Takes advantage of the AI service part of the NanoSat MO Framework
	- Stores the processed tiles in a folder



## Prerequisites:

The Edge AI App takes advantage of the AI service of the NanoSat MO Framework therefore the App needs to be running on a NMF system with an implementation of the AI service, in particular, to an edge computing hardware such as the Intel Movidius device which has been space-qualified with ESA's Phi-Sat-1 mission.



## MO Actions:

The App exposes two MO Actions: "*AI_Start*" and "*AI_Cancel*".



### AI_Start

When this action is triggered, the App will run inference using the AI service.


When the external process terminates the App will publish a MO Parameter whose name have the following format: "Process Request ID: \<Action Request ID\> exitCode: \<exitCode\>". 



### AI_Cancel:

When this action is triggered, the App will attempt to terminate the process having the provided "Action Request ID"


The "Action Request ID" is printed on the console of the "Apps Launcher Service" of the Supervisor whenever the "*AI_Start*" action is triggered.

Example:

```
INFO: Process 1614083521462 is starting. It will last at least 0 seconds and at most 30000 ms 
```



#### Arguments:

1. <u>Action Request ID</u>: the action request ID of the "*AI_Start*" Action that triggered the external process.



