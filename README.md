# AccelometerKotlin

This is an sample app in Kotlin for the upcoming workshop 1 for Software Competitiveness International . 

## Purpoce of the app : 

Is to show the new programming language Kotlin and some components of the Android Platform . 

### Description of the app :

* There is a background **Service** that contains an algorithm that detects the Free fall
* The Background Service notifies **MainActivity** that the phone made a Free Fall
* The **LocalBroadcastReceiver** is the medium of the communication between **Activity** and the **Service** 
* **MainActivity** is responsible to alter the UI and make actions upon Free fall

### Description of the Algorithm of the Free Fall : 

We use the **vector** of the Velocities X Y Z of the accelerometer. 
