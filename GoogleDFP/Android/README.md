# Tealium Android Google DFP TagBridge Module

This module abstracts the functionality of [Google's DoubleClick for Publishers](https://developers.google.com/mobile-ads-sdk/docs/dfp/android/banner?hl=en) into a platform-independant, TagBridge-driven, implementation.

## Getting started

### 1. Dependencies

Both the Tealium Library and Google Service's Ads dependencies will need to be added to the ```build.gradle``` file:

```
dependencies {
    // ...
    compile 'com.google.android.gms:play-services-ads:7.5.0'
    compile files('libs/tealium.4.1.3c.jar') // Can be Compact or Full version
}
```

### 2. Source

Copy the contents of ```Source``` into your app's ```src/main/java``` directory.

### 3. Add to Initialization

Add the module to Tealium's initialization call:

```java
Tealium.initialize(Tealium.Config.create(application, "tealiummobile", "demo", "dev")
    .addRemoteCommand(new GoogleDFPRemoteCommand(application)));
```