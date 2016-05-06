Syncano & Google Cloud Messaging
================================

This example demonstrates how to use Syncano to send GCM messages to your app.

In below tutorial, we assume you already have a dedicated Syncano instance created.
Prepare your Syncano instance name, further called INSTANCE and Syncano account API key, further called ACCOUNT_KEY.

1. Register your app for GCM service in Google
----------------------------------------------
There is a wizard provided by Google to do it.
https://developers.google.com/mobile/add?platform=android&cntapi=gcm

As a result you should have:
- Server API Key
- google-services.json file

You should see your app added here:
https://console.developers.google.com
but you don't have to go there. You can manage it there, create new api key etc.

2. Set GCM server API key on Syncano
------------------------------------
Send it using simple http request.
Use your instance name and account key. You can pass your gcm api key as a development key, production key or both, it depends how you want to use them. Set it for both if you're not sure yet.
```bash
curl -X "PATCH" "https://api.syncano.io/v1.1/instances/<INSTANCE>/push_notifications/gcm/config/" -H "X-API-KEY: <ACCOUNT_KEY>" -H "Content-Type: application/json" -d '{"production_api_key":"<PRODUCTION_KEY>","development_api_key":"<DEVELOPMENT_KEY>"}'
```

If using Windows, you can use other tool than curl to make a http request, maybe ["Postman"](https://www.getpostman.com/).

3. Configure your app
---------------------
- Copy your google-services.json to your projects folder. If you used this code, you should override a file in app directoy.
- Set your Syncano account key and instance name in `app/gradle.properties` file.

4. Run your app
---------------
- Run it. After running an app, it will receive GCM registration id and register it on Syncano. It's also written to logcat.
- Send GCM messages to chosen registration ids.
- Check if messages come to your Android notifications bar.
To send GCM message:
```bash
curl -X "POST" "https://api.syncano.io/v1.1/instances/<INSTANCE>/push_notifications/gcm/messages/" -H "X-API-KEY: <ACCOUNT_KEY>" -H "Content-Type: application/json" -d '{ "content": { "environment": "development", "registration_ids":["<REGISTRATION_ID>"], "data": {"message":"sample message"} } }'
```
You can also list all registered devices using:
```bash
curl -X "GET" "https://api.syncano.io/v1.1/instances/<INSTANCE>/push_notifications/gcm/devices/" -H "X-API-KEY: <ACCOUNT_KEY>"
```

Api keys used in this example
-----------------------------
- Syncano Account API key - It is an administrator key, that can make any changes in his syncano instances.
- GCM Server API key - Key that is necessary to communicate with GCM servers, used by Syncano, not an app.
- Registration id / token - It's an id given by GCM to specific app on specific device. Using this ids you can send messages to chosen devices.

Syncano related code in the app
-------------------------------
gradle.properties
```gradle
syncano_api_key="<APP_API_KEY>"
syncano_instance="<INSTANCE>"
```

build.gradle
```gradle
dependencies {
    compile 'io.syncano:library:4.1.0'
}
```

SampleApplication.java - Initiating Syncano singleton
```java
new SyncanoBuilder().androidContext(getApplicationContext()).apiKey(BuildConfig.SYNCANO_API_KEY)
        .instanceName(BuildConfig.SYNCANO_INSTANCE_NAME).setAsGlobalInstance(true).useLoggedUserStorage(true)
        .build();
```

GetGCMTokenTask.java - Registering device registration id
```java
Syncano.getInstance().registerPushDevice(new PushDevice(token)).send();
```


More information
----------------
- GCM app example from Google
https://github.com/googlesamples/google-services/tree/master/android/gcm
- For questions not related to Syncano, but to GCM itself, check here
https://developers.google.com/cloud-messaging/
- Syncano docs
http://docs.syncano.io/
- Syncano API reference
http://docs.syncano.io/v0.1.1
- Syncano GUI
https://dashboard.syncano.io/
