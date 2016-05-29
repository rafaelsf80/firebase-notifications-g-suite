# Notifications from a Google Form to Android devices using Firebase #
Android app to demonstrate how to send [topic notifications](https://firebase.google.com/docs/notifications/android/console-topics#set_up_the_sdk) using Firebase Cloud Messaging (formerly Google Cloud Messaging).
Instead of a 3rd-party messaging server, a Google Form is used to generate and send the notification.

The apk receives notifications from a Google Form, and makes use of Apps Script to trigger en event to send a notification, as explained [in this video, min 18:00](https://www.youtube.com/watch?v=RSgMEtRl0sw).

Notifications can be used by enterprises of any vertical to send urgent information to their employees. 

Learn more use cases and how to use Google Apps at Work [here](https://apps.google.com/learning-center/use-at-work/)


## Usage

1) Compile and launch the apk. Make sure you add a google-services.json file of your Firebase project

2) Create a Google Form from scratch. At least on of the fields should be the group (topic). Make sure the topic names are the same in both the Android app (values/strings.xml) and the form

3) Following [this video](https://www.youtube.com/watch?v=RSgMEtRl0sw), create an Apps Script to send the notification. You will need an API key of your Firebase project


## Messaging Server

The backend is just a [Google Form](https://www.google.es/intl/es/forms/about/), hosted on Google Drive. A sample screenshot ban be found below.


## Dependencies

The following libraries must be included for proper compilation and execution:

```groovy  
    compile 'com.android.support:appcompat-v7:23.4.0'
    compile 'com.google.android.gms:play-services-gcm:9.0.0'
    compile 'com.google.firebase:firebase-messaging:9.0.0'
```


## Screenshots

Main activity and Google Form:

<img src="https://raw.githubusercontent.com/rafaelsf80/firebase-notifications-for-work/master/screenshots/main.png" alt="alt text" width="100" height="200">
<img src="https://raw.githubusercontent.com/rafaelsf80/firebase-notifications-for-work/master/screenshots/form.png" alt="alt text" width="100" height="200">
