# Picture Processor NMF App



NMF App that allows to:

	- start and stop an external process
	- provide to the external process environment variables and input arguments 
	- set a timeout in order to force the automatic termination of the external process
	- redirect the external process standard output and standard error to a file



## Prerequisites:

Python is a pre-requisite since the process launched by this demo will be instantiated from a python script. The App can be easily modified to run other processes.



## MO Actions:

The App exposes two MO Actions: "*TakeAndProcessPicture*" and "*DestroyProcess*".



### TakeAndProcessPicture

When this action is triggered, the App takes a picture through the Platform Camera Service. The picture is then placed within the "*picture*" folder and processed by the script "*imageEditor.py*". The script will produce a copy of the original picture and will print the current date and time upon it.  



The standard output and standard error of the process will be logged in a file within the "*logs*" folder. The log file name will have the following format: "picture-processor-\<processed-picture-file-name\>.log"



When the external process terminates the App will publish a MO Parameter whose name have the following format: "Process Request ID: \<Action Request ID\> exitCode: \<exitCode\>". 



#### Arguments:

1. <u>Minimum Process Duration in seconds</u>: this is the first action argument and can be used to simulate a long running process. The default value is 0. This value will be passed to the process through an environment variable.
2. <u>Maximum Process Duration in seconds:</u> this is the second action argument and can be used to set a timeout after which the process is killed. The default value is -1, which is interpreted as "*infinity*"  



### DestroyProcess:

When this action is triggered, the App will attempt to terminate the process having the provided "Action Request ID"



The "Action Request ID" is printed on the console of the "Apps Launcher Service" of the Supervisor whenever the "*TakeAndProcessPicture*" action is triggered.

Example:

```
INFO: Process 1614083521462 is starting. It will last at least 0s and at most 30000ms 
```



#### Arguments:

1. <u>Action Request ID</u>: the action request ID of the "*TakeAndProcessPicture*" Action that triggered the external process.



