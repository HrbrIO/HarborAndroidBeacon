# BeaconSampleAndroid

Getting started with Harbor Beacon on Android:

- [Setup](#setup) - Get setup
- [Beacon Sample](#BeaconSample) - Run the Beacon Sample app

## Setup

This project uses/requires:
 - Android Studio.
 - SDK Version API 23: Android 6.0 (Marshmallow)
 - Google Play Services

If you haven't used Harbor before, welcome! You'll need to [Sign up for a Harbor account](https://www.hrbr.io/try-hrbr) first.

1. Open `BeaconSampleAndroid` from Android Studio.

## BeaconSample

### Running the Beacon Sample

To get started with the Quickstart application follow these steps:

1. Open this `BeaconSampleAndroid` in Android Studio

<img width="700px" src="images/samples/xcode_beacon_sample.png"/>

2. Copy your organization API Key from the [API Keys page](https://cloud.hrbr.io/#!/account/apikeys).

<img width="700px" src="images/samples/copy_organization_api_key.png"/>

3. Paste your organization API Key from the earlier step in the `Info.plist`.

<img width="700px" src="images/samples/xcode_api_key.png"/>

4. Copy your **AppVersionID** from the specific [Applications page](https://cloud.hrbr.io/#!/apps/list).

<img width="700px" src="images/samples/copy_appversionid.png"/>

5. Paste your **AppVersionID** in the `HarborLogger.swift`.

<img width="700px" src="images/samples/xcode_appversionid.png"/>

6. Copy your **Beacon Version ID** from the specific [Edit Beacon page](https://cloud.hrbr.io/#!/apps/list) for the Beacons for your Application.

<img width="700px" src="images/samples/copy_beacon_version_id.png"/>

7. Paste your **Beacon Version ID** in the `HarborLogger.swift`.

<img width="700px" src="images/samples/xcode_beaconversionid.png"/>

8. Run the Beacon Sample app on your iOS device or simulator.

<img width="700px" src="images/samples/home_screen.png"/>

9. Once you have your app running, you should beacons on the [Developer View page](https://cloud.hrbr.io/#!/apps/list) for your Application.!

<img width="700px" src="images/samples/click_developer_view.png"/>

### Beacon Messages

|Message Type|Message Data|Description|
|---|---|---|
|APP_START_MSG|N/A||
|APP_BG_MSG|N/A||
|APP_FG_MSG|N/A||
|APP_KILL_MSG|N/A||
|HEARTBEAT|N/A||
|SCREEN_VIEW|`{  "screen" : screenName } `||
|SCREEN_DWELL|`{ "screen": screenName, "time" : timeIntervalSince1970 }`||
|GEOLOCATION|`{ "latitude" : latitude, "longitude" : longitude, "altitude" : altitudeMeters, "hz_accuracy" : horizontalAccuracyMeters, "vrt_accuracy" : verticalAccuracyMeters `||


### Beacon Functions

-`HarborLogger.appStart()`
-`HarborLogger.appBackground()`
-`HarborLogger.appForeground()`
-`HarborLogger.appKill()`
-`HarborLogger.startHeartbeat()`
-`HarborLogger.stopHeartbeat()`
-`HarborLogger.logLocation(location)`
-`HarborLogger.startScreenDwell(viewName)`
-`HarborLogger.stopScreenDwell(viewName)`

## License

This project is licensed under the [Apache License, Version 2.0](https://github.com/HrbrIO/BeaconSampleAndroid/blob/master/LICENSE)