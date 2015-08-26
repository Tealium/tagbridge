# Tealium Android Google DFP TagBridge Integration

The Tealium TagBridge integration abstracts the functionality of [Google's DoubleClick for Publishers](https://developers.google.com/mobile-ads-sdk/docs/dfp/android/banner?hl=en) into platform-independant code controllable by TiQ.

* [Getting Started](#getting-started)
 * [1. Dependencies](#1-dependencies)
 * [2. Add Source](#2-add-source)
 * [3. Add to Initialization](#3-add-to-initialization)
* [Rotations](#rotations)

## Getting Started

### 1. Dependencies

Both the Tealium Library and Google Service's Ads dependencies will need to be added to the ```build.gradle``` file:

```
dependencies {
    // ...
    compile 'com.google.android.gms:play-services-ads:7.5.0'
    compile files('libs/tealium.4.1.3c.jar') // Can be Compact or Full version
}
```

### 2. Add Source

Copy the contents of ```Source``` into your app's ```src/main/java``` directory.

### 3. Add to Initialization

Add the module to Tealium's initialization call:

```java
Tealium.initialize(Tealium.Config.create(application, "tealiummobile", "demo", "dev")
    .addRemoteCommand(new GoogleDFPRemoteCommand(application)));
```

## Rotations

If the Activity is declared in the ```AndroidManifest.xml``` with the ```android:configChanges="orientation"``` attribute, ads will persist accross rotations. Otherwise, the rotation is a "new" activity and any desired ads will need to be injected.

