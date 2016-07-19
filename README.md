# Serverless notifications to Android devices using Firebase and Google Apps #
Android app to demonstrate how to send [topic notifications](https://firebase.google.com/docs/notifications/android/console-topics#set_up_the_sdk) using **Firebase Cloud Messaging** (formerly Google Cloud Messaging).
Instead of a 3rd-party messaging server, a Google Form is used to generate and send the notification. No messaging server required.

The apk receives notifications from a Google Form, and makes use of Apps Script to trigger en event to send a notification, as explained [in this video, min 18:00](https://www.youtube.com/watch?v=RSgMEtRl0sw).

Notifications can be used by enterprises of any vertical to send urgent information to their employees. 

Learn more use cases and how to use Google Apps at Work [here](https://apps.google.com/learning-center/use-at-work/)


## Usage

1) Compile and launch the Android app. Make sure you properly add a **google-services.json** file of your Firebase project

2) Create a **Google Form** from scratch. At least on of the fields should be the group (topic). Make sure the topic names are the same in both the Android app (values/strings.xml) and the Google Form.

3) Following [this video, min 18:00](https://www.youtube.com/watch?v=RSgMEtRl0sw), create an **Apps Script** to send the notification in the responses spreadsheet of the form. See code below. 
You will need an API key of your Firebase project inside the script.


## Serverless messaging

The backend is just a [Google Form](https://www.google.es/intl/es/forms/about/), hosted on Google Drive. Messages are sent to the group selected in the form. A sample form screenshot can be found below.
The following code should be added to the spreadsheet responses (replace your API key accordingly):

```javascript

function onOpen() {
  var sheet = SpreadsheetApp.getActive();
  // trigger to send notification every time a form answer is submitted
  ScriptApp.newTrigger("sendMessage")
   .forSpreadsheet(sheet)
   .onFormSubmit()
   .create();
}

function sendMessage() {
  var sheet = SpreadsheetApp.getActiveSheet();
  var lastRow = sheet.getLastRow();   // Row to process
  
  // Fetch the range of columns 2 and 3 (in this case, single cell of last row)
  var msgRange = sheet.getRange(lastRow, 2, 1, 1); 
  var groupRange = sheet.getRange(lastRow, 3, 1, 1);
  // Fetch values for the row in the Range.
  var msg = msgRange.getValue();
  var topic = groupRange.getValue();  
  
  var apiKey = 'YOUR_API_KEY'; // **** REPLACE WITH YOUR API_KEY ****

  // As per doc, RegEx compliant topic must be: /topics/[a-zA-Z0-9-_.#%]+
  var regex_compliant_topic = topic.replace(/[-\s]+/g, '_');
  Logger.log(regex_compliant_topic);
  Logger.log(msg); 
  
  var payload = {'to' : "/topics/" + regex_compliant_topic,
                 'data' : {
                   'message' : msg
                 }};
  
  var urlFetchOptions =  {'contentType' : 'application/json',
                          'headers' : {'Authorization' : 'key=' + apiKey},
                          'method' : 'post',
                          'payload' : JSON.stringify(payload)};
  
  var gcmUrl = 'https://fcm.googleapis.com/fcm/send';
  var response = UrlFetchApp.fetch(gcmUrl,urlFetchOptions).getContentText()
  
  Logger.log(response); // for testing purposes. Improve error handling here
}

```

## Dependencies

The following libraries must be included for proper compilation and execution:

```groovy  
    compile 'com.android.support:appcompat-v7:23.4.0'
    compile 'com.google.android.gms:play-services-gcm:9.0.0'
    compile 'com.google.firebase:firebase-messaging:9.0.0'
```


## Screenshots

Main activity and Google Form:

<img src="https://raw.githubusercontent.com/rafaelsf80/firebase-notifications-for-work/master/app/screenshots/main.png" alt="alt text" width="100" height="200">
<img src="https://raw.githubusercontent.com/rafaelsf80/firebase-notifications-for-work/master/app/screenshots/form.png" alt="alt text" width="200" height="300">
