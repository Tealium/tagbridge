# Brief 

This module incorporates device location tracking into TagBridge. The command id for this module is ```_location```. 

## Methods

* [get_location](#get_location)
* [cancel](#cancel)

## Methods Detail

### get_location

Get the next update from the device's location service. If the timeout is reached, an outdated/previous location or null will be returned. Otherwise a stringified JSON object with the keys ```latitude``` and ```longitude``` will be returned. If multiple queries are made before and a location update occurs before any timeout, the same location will be returned to both callbacks. 

```javascript
// if null the callback will look like:
utag.mobile.remote_api.response['_location'][responseId](204);

// otherwise
utag.mobile.remote_api.response['_location'][responseId](200, "{\"latitude\":\"0\",\"longitude\":\"0\"}");
```

Arguments:

* timeout : long (Android &amp; iOS)
 * Required
* min_distance : float/double (Android &amp; iOS)
 * Optional
* provider : array or string (Android) 
 * Required
* min_time : long (Android &amp; iOS)
 * Optional
* desired_accuracy : int (iOS)
 * Required
* activity_type : int (iOS)
 * Optional

Direct call: 

```javascript
window.open('tealium://_location?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	}, 
	payload : {
		method : 'get_location',
		arguments : {
			// (long) *required
			// time in ms before returning a cached or null last position.
			timeout : 60000 
			// (decimal (float android, double iOS)) *optional (default 0 android) 
			// minimum distance between location updates, in meters
			min_distance : 500,			
			// (array or string) *required, Android-only.
			// "gps", "network", "passive"
			provider : ["gps"],
			// (long) *optional Android-only. Default 0.
			// minimum time interval between location updates, in milliseconds
			min_time : 0,
			// (int), *required, iOS-only.
			// kCLLocationAccuracyBestForNavigation = 1, kCLLocationAccuracyBest = 2, 
			// kCLLocationAccuracyNearestTenMeters = 3, kCLLocationAccuracyHundredMeters = 4, 
			// kCLLocationAccuracyKilometer = 5, kCLLocationAccuracyThreeKilometers = 6
			desired_accuracy : 1,
			// (int), *optional, iOS-only.
			//  CLActivityTypeOther = 1, CLActivityTypeAutomotiveNavigation = 2
			//  CLActivityTypeFitness = 3, CLActivityTypeOtherNavigation = 4
			activity_type : 1
		}
	}
})), '_self');
```

#### Additional Android Notes

Note the following providers require the following permissions: 

* "gps" - ACCESS_FINE_LOCATION
* "network" - ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION
* "passive" - ACCESS_FINE_LOCATION

If a provider is attempted without the appropriate permission declared by the app, the TagBridge callback will receive a status 555 accompanied by a stringified stack trace looking something like: 

```
java.lang.SecurityException: "network" location provider requires ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission.
	at android.os.Parcel.readException(Parcel.java:1465)
	at android.os.Parcel.readException(Parcel.java:1419)
	at android.location.ILocationManager$Stub$Proxy.requestLocationUpdates(ILocationManager.java:540)
	at android.location.LocationManager.requestLocationUpdates(LocationManager.java:860)
	at android.location.LocationManager.requestLocationUpdates(LocationManager.java:454)
	at com.tealium.location.LocationRemoteCommand.start(LocationRemoteCommand.java:38)
	at com.tealium.location.LocationRemoteCommand.onInvoke(LocationRemoteCommand.java:76)
	at com.tealium.library.RemoteCommand.a(Unknown Source)
	at com.tealium.library.ah.a(Unknown Source)
	...
```

### cancel

This will prompt the module to cancel checking for updates. If the ```response_ids``` key is specified, only the response_ids specified will be cancelled. Otherwise all queued requests will be removed. 

Arguments: 

* response_ids : array (Android &amp; iOS)
 * Optional; 
 * When specified, its element response_ids will be cancelled. 
 * When absent, all queued requests will perform their "timeout" operation.

Direct call: 

```javascript
window.open('tealium://_location?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	}, 
	payload : {
		method : 'cancel'
		arguments : {
			// (array of strings) *optional; if not specified all queued are removed.
			response_ids : []
		}
	}
})), '_self');
```