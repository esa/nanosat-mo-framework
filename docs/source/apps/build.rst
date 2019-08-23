================
Build an NMF app
================
Now let's build our app. This process is pretty straight forward.
First, build your app by going into its root folder and calling ``mvn install``. 
Then, call SDK packaging by opening a console in the ``sdk/sdk-package`` folder and calling ``mvn install``.

That's it, our app's start scripts and properties are now residing in ``sdk/sdk-package/target/nmf-sdk-2.0.0-SNAPSHOT/home/sobel``.

You can now go ahead and start the NMF supervisor with simulator, start the CTT, connect to the supervisor, start your app, connect to your app and take some nice pictures!
