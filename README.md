# AccelometerKotlin

This is an sample app in Kotlin for the upcoming workshop for Software Competitiveness International . 

## Purpoce of the app : 

Is to show the new programming language Kotlin and some components of the Android Platform . 

### Description of the app :

* There is a background service that contains an algorithm that detects Free fall
* Background Service notifies Main Activity that the phone made a Free Fall
* Local Broadcast receiver is the medium of the comunication between Activity and the Service 
* MainActivity is responsible to alter the UI and Make actions upon Free fall

### Description of the Algorithm of the Free Fall : 

We use the vector of the velocities X Y Z of the accelerometer .
