
# Adobe TagBridge Module API Reference

This is the API method refense for the Adobe TagBridge Module it assumes the [ReadMe](../Adobe/) for this module has been read.

All methods utilize the command id ```adobe```. The payload of this method supports two keys, the required key ```method``` and the sometimes optional key ```arguments```. ```method``` specifies the module method, if missing a status 400 will be returned to the callback. ```arguments``` will be required for some functions, but not for all; additionally, it's value is a JSON object of key-value pairs specific to the method it is being delivered to.

Each method has example code utilizing a [Tealium Mobile Utilities](#mobile-utilities) javascrpt helper used while building out tags to handle the request construction.

## Methods

* [set_privacy_status](#set_privacy_status)
* [set_user_identifier](#set_user_identifier)
* [collect_lifecycle_data](#collect_lifecycle_data)
* [pause_collecting_lifecycle_data](#pause_collecting_lifecycle_data) (Android only)
* [keep_lifecycle_session_alive](#keep_lifecycle_session_alive) (iOS only)
* [track_state](#track_state)
* [track_action](#track_action)
* [track_action_from_background](#track_action_from_background)
* [track_lifetime_value_increase](#track_lifetime_value_increase)
* [track_location](#track_location)
* [track_beacon](#track_beacon)
* [tracking_clear_current_beacon](#tracking_clear_current_beacon)
* [track_timed_action_start](#track_timed_action_start)
* [track_timed_action_update](#track_timed_action_update)
* [track_timed_action_end](#track_timed_action_end)
* [tracking_send_queued_hits](#tracking_send_queued_hits)
* [tracking_clear_queue](#tracking_clear_queue)
* [media_close](#media_close)
* [media_play](#media_play)
* [media_complete](#media_complete)
* [media_stop](#media_stop)
* [media_click](#media_click)
* [media_track](#media_track)




---

### set_privacy_status

Arguments:

* privacy_status : string

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "set_privacy_status",
		arguments : {
			// (integer) *required
			// Value: 1 = OptIn, 2 = OptOut, 3 = Unknown
			privacy_status : privacyStatus
		}
	}
})), '_self');
```

Mobile Utilities call:

```javascript
var testPrivacyStatusCommand = utag.mobile.remote_api.api_builder();
        
testPrivacyStatusCommand.init("adobe", "Adobe Set Pivacy Status", utag);
testPrivacyStatusCommand.is_custom_module()
	.method("set_privacy_status")
		.add_argument("privacy_status","1");
testPrivacyStatusCommand.trigger();
``` 

iOS equivalent:

```objective-c
[ADBMobile setPrivacyStatus:ADBMobilePrivacyStatusOptIn];
```

Android equivalent:

```java
Config.setPrivacyStatus(MobilePrivacyStatus.MOBILE_PRIVACY_STATUS_OPT_IN);
```

[top](#adobe-tagbridge-module-api-reference)

### set_user_identifier

Arguments:

* identifier : string

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "set_user_identifier",
		arguments : {
			// (string) *required
			identifier : "bob the builder"
		}
	}
})), '_self');
```

Mobile Utilities call:

```javascript
var testUserIdentifierCommand = utag.mobile.remote_api.api_builder();
        
testUserIdentifierCommand.init("adobe", "Adobe Set User Identifier", utag);
testUserIdentifierCommand.is_custom_module()
	.method("set_user_identifier")
		.add_argument("identifier","bob the builder");
testUserIdentifierCommand.trigger();
``` 

iOS equivalent:

```objective-c
[ADBMobile setPrivacyStatus:ADBMobilePrivacyStatusOptIn];
```

Android equivalent:

```java

String identifier; //...

Config.setUserIdentifier(identifier);
```

[top](#adobe-tagbridge-module-api-reference)

### collect_lifecycle_data

Arguments:

* none

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "collect_lifecycle_data",
	}
})), '_self');
```

Mobile Utilities call:

```javascript
var testLifecycleCommand = utag.mobile.remote_api.api_builder();
testLifecycleCommand.init("adobe", "Adobe Collect Lifecycle Data", utag);
testLifecycleCommand.is_custom_module()
	.method("collect_lifecycle_data");
testLifecycleCommand.trigger();
``` 

iOS equivalent:

```objective-c
[ADBMobile collectLifecycleData];
```

Android equivalent:

```java
Config.collectLifecycleData();
```

### pause_collecting_lifecycle_data

Note: Android Only

Arguments:

* none

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "pause_collecting_lifecycle_data",
	}
})), '_self');
```

Mobile Utilities call:

```javascript
var testLifecycleCommand = utag.mobile.remote_api.api_builder();
testLifecycleCommand.init("adobe", "Adobe Pause Collecting Lifecycle Data", utag);
testLifecycleCommand.is_custom_module()
	.method("pause_collecting_lifecycle_data");
testLifecycleCommand.trigger();
``` 

Android equivalent:

```java
Config.pauseCollectingLifecycleData();
```

### keep_lifecycle_session_alive

Note: iOS Only

Arguments:

* none

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "keep_lifecycle_session_alive",
	}
})), '_self');
```

Mobile Utilities call:

```javascript
var testLifecycleCommand = utag.mobile.remote_api.api_builder();
testLifecycleCommand.init("adobe", "Adobe Pause Collecting Lifecycle Data", utag);
testLifecycleCommand.is_custom_module()
	.method("keep_lifecycle_session_alive");
testLifecycleCommand.trigger();
``` 

iOS equivalent:

```objective-c
[ADBMobile keepLifecycleSessionAlive];
```

[top](#adobe-tagbridge-module-api-reference)
	
### track_state

Arguments:

* state_name : string
* custom_data : object

Direct call:
	
```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "track_state",
		arguments : {
			// (string) *required
			state_name : stateName,
			// (JSON object) *optional
			custom_data : {}
		}
	}
})), '_self');
```

Mobile Utilities call:

```javascript
var testTrackStateCommand = utag.mobile.remote_api.api_builder();
testTrackStateCommand.init("adobe", "Adobe Track State", utag);
testTrackStateCommand.is_custom_module()
	.method("track_state")
		.add_argument("state_name","view_state_converted")
		.add_argument("custom_data",{"custom_key":"custom_value"});
testTrackStateCommand.trigger();
``` 

iOS equivalent:

```objective-c
NSString *stateName         = @"view_state_converted"
NSDictionary *customData    = @{ @"custom_key" : @"custom_value" };
    
[ADBMobile trackState:stateName data:customData];
```

Android equivalent:

```java
String state;
Map<String, Object> customData;

//...

Analytics.trackState(state, customData);
```

[top](#adobe-tagbridge-module-api-reference)

### track_action

Arguments:

* action_name : string
* custom_data : object

Direct call:
		
```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "track_action",
		arguments : {
			// (string) *required
			action_name : actionName,
			// (JSON object) *optional
			custom_data : {}
		}
	}
})), '_self');
```		

Mobile Utilities call:

```javascript
var testTrackActionCommand = utag.mobile.remote_api.api_builder();
testTrackActionCommand.init("adobe", "Adobe Track Action", utag);
testTrackActionCommand.is_custom_module()
	.method("track_action")
		.add_argument("action_name","my_action_name")
		.add_argument("custom_data",{"custom_key":"custom_value"});
testTrackActionCommand.trigger();
``` 

iOS equivalent:

```objective-c
NSString *actionName        = @"my_action_name";
NSDictionary *customData    = @{ @"custom_key" : @"custom_value" };

[ADBMobile trackAction:actionName data:customData];
```

Android equivalent:

```java
String action;
Map<String, Object> contextData;

//...

Analytics.trackAction(action, contextData);
```

[top](#adobe-tagbridge-module-api-reference)

### track_action_from_background

Arguments:

* action_name : string
* custom_data : object
	
Direct call:
		
```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "track_action_from_background",
		arguments : {
			// (string) *required
			action_name : actionName,
			// (JSON object) *optional
			custom_data : {}
		}
	}
})), '_self');		
```
		
Mobile Utilities call:

```javascript
var testTrackActionInBackgroundCommand = utag.mobile.remote_api.api_builder();
testTrackActionInBackgroundCommand.init("adobe", "Adobe Track Action from Background", utag);
testTrackActionInBackgroundCommand.is_custom_module()
	.method("track_action_from_background")
		.add_argument("action_name","my_track_action_backgrounded")
		.add_argument("custom_data",{"custom_key":"custom_value"});
testTrackActionInBackgroundCommand.trigger();
```

iOS equivalent:

```objective-c
NSString *actionName        = @"my_track_action_backgrounded";
NSDictionary *customData    = @{ @"custom_key" : @"custom_value" };

[ADBMobile trackAction:actionName data:customData];
```

Android equivalent:

```java
String action;
Map<String, Object> contextData;

//...

Analytics.trackAction(action, contextData);
```

[top](#adobe-tagbridge-module-api-reference)

### track_lifetime_value_increase

Arguments:

* ammount : string containing decimal value

Returns: JSON Object with Key/Value pairs:

* lifetime_value: string / double

```javascript
{
	lifetime_value: "30.9"
}
```
 	

Direct call:
			
```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "track_lifetime_value_increase",
		arguments : {
			// (decimal) *required
			amount : "10.30",
			// (JSON object) *optional
			custom_data : {}
		}
	}
})), '_self');				
```			

Mobile Utilities call:

```javascript
var testTrackLifetimeValueIncreaseCommand = utag.mobile.remote_api.api_builder();
testTrackLifetimeValueIncreaseCommand.init("adobe", "Adobe Track Lifetime Value Increase", utag);
testTrackLifetimeValueIncreaseCommand.is_custom_module()
	.method("track_lifetime_value_increase")
		.add_argument("ammount","10.30")
		.add_argument("custom_data",{"custom_key":"custom_value"});
testTrackLifetimeValueIncreaseCommand.trigger();
``` 

iOS equivalent:

```objective-c
NSString *rawAmmount        = @"10.30";
    
NSDecimalNumber *ammount    = [NSDecimalNumber decimalNumberWithString:rawAmmount];
NSDictionary *customData    = @{ @"custom_key" : @"custom_value" };

[ADBMobile trackLifetimeValueIncrease:ammount data:customData];
```

Android equivalent:

```java
BigDecimal amount;
HashMap<String, Object> contextData;

//...

Analytics.trackLifetimeValueIncrease(amount, contextData);
```

[top](#adobe-tagbridge-module-api-reference)

###track_location

Arguments:

* latitude : string of decimal
* longitude : string of decimal
* custom_data : object

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "track_location",
		arguments : {
			// (string) *required
			latitude : "32.906407",
			// (string) *required
			longitude : "-117.237921",
			// (JSON object) *optional
			custom_data : {}
		}
	}
})), '_self');	
```

Mobile Utilities call:

```javascript
var testTrackLocationCommand = utag.mobile.remote_api.api_builder();
testTrackLocationCommand.init("adobe", "Adobe Track Location", utag);
testTrackLocationCommand.is_custom_module()
	.method("track_location")
		.add_argument("latitude","32.906407")
		.add_argument("longitude","-117.237921")
		.add_argument("custom_data",{"custom_key":"custom_value"});
testTrackLocationCommand.trigger();
``` 

iOS equivalent:

```objective-c

CLLocationDegrees latitudeValue		= [@"32.906407" doubleValue];
CLLocationDegrees longitudeValue 	= [@"-117.237921" doubleValue];

CLLocation *location = [[CLLocation alloc] initWithLatitude:latitudeValue
                                      			  longitude:longitudeValue];
    

NSDictionary *customData    = @{ @"custom_key" : @"custom_value" };;

[ADBMobile trackLocation:location data:customData];
```

Android equivalent:

```java
Location loc;
Map<String, Object> contextData;

//...

Analytics.trackLocation(loc, contextData);
```

[top](#adobe-tagbridge-module-api-reference)

###track_beacon

Arguments:

* beacon_uuid : string of UUID should match proximity UUID for a CLBeacon class store in a tagBridge instance's ivar;
* custom_data : object
			

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "track_beacon",
		arguments : {
			// (string) *required
			proximity_uuid : "123141414sdsdf51",
			// (JSON object) *optional
			custom_data : {},
			// Major number of the beacon (such as store number)
			// (string) *required-android
			major : "1234",
			// Minor number of the beacon (such as a unique number within a store)
			// (string) *required-android
			minor : "123456789",
			// (int) *required-android 
			// 0 unknown, 1 immediate, 2 near, 3 far. 
			proximity : 2
		}
	}
})), '_self');	
```

Mobile Utilities call:

```javascript
var testTrackBeaconCommand = utag.mobile.remote_api.api_builder();
testTrackBeaconCommand.init("adobe", "Adobe Track Beacon", utag);
testTrackBeaconCommand.is_custom_module()
	.method("track_beacon")
		.add_argument("proximity_uuid","123141414sdsdf51")
		.add_argument("custom_data",{"custom_key":"custom_value"})
		.add_argument("major", "1234")
		.add_argument("minor", "123456789")
		.add_argument("proximity", 2);
testTrackBeaconCommand.trigger();
``` 

iOS equivalent:

```objective-c
CLBeacon *beacon 			= self.beacons[@"123141414sdsdf51"];

NSDictionary *customData    = @{ @"custom_key" : @"custom_value" };

if (beacon) {
	[ADBMobile trackBeacon:beacon data:customData];
}			
```

Android equivalent:

```java
String uuid;
String major;
String minor;
Map<String, Object> contextData;

//...

Analytics.trackBeacon(uuid, major, minor, BEACON_PROXIMITY.PROXIMITY_NEAR, contextData);
```

[top](#adobe-tagbridge-module-api-reference)

###tracking_clear_current_beacon

Arguments:

* none

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "tracking_clear_current_beacon"
	}
})), '_self');	
```

Mobile Utilities call:

```javascript
var testTrackingClearBeaconCommand = utag.mobile.remote_api.api_builder();
testTrackingClearBeaconCommand.init("adobe", "Adobe Clear Current Beacon", utag);
testTrackingClearBeaconCommand.is_custom_module()
	.method("tracking_clear_current_beacon");
testTrackingClearBeaconCommand.trigger();
``` 

iOS equivalent:

```objective-c
[ADBMobile trackingClearCurrentBeacon];
```

Android equivalent:

```java
Analytics.clearBeacon();
```

[top](#adobe-tagbridge-module-api-reference)

###track_timed_action_start

Arguments:

* action : string
* custom_data : object

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "track_timed_action_start",
		arguments : {
			// (string) *required
			action : "time to checkout",
			// (JSON object) *optional
			custom_data : {}
		}
	}
})), '_self');	
```

Mobile Utilities call:

```javascript
var testTrackTimedActionStartCommand = utag.mobile.remote_api.api_builder();
testTrackTimedActionStartCommand.init("adobe", "Adobe Track Timed Action Start", utag);
testTrackTimedActionStartCommand.is_custom_module()
	.method("track_timed_action_start")
		.add_argument("action","time to checkout")
		.add_argument("custom_data",{"custom_key":"custom_value"});
testTrackTimedActionStartCommand.trigger();
``` 

iOS equivalent:

```objective-c
 NSString *action            = @"time to checkout";
 NSDictionary *customData    = @{ @"custom_key" : @"custom_value" };

 [ADBMobile trackTimedActionStart:action data:customData];
```

Android equivalent:

```java
String action;
Map<String, Object> contextData;

//...

Analytics.trackTimedActionStart(action, contextData);
```

[top](#adobe-tagbridge-module-api-reference)

###track_timed_action_update

Arguments:

* action : string
* custom_data : object

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "track_timed_action_update",
		arguments : {
			// (string) *required
			action : "time to checkout",
			// (JSON object) *optional
			custom_data : {}
		}
	}
})), '_self');	
```

Mobile Utilities call:

```javascript
var testTrackTimedActionUpdateCommand = utag.mobile.remote_api.api_builder();
testTrackTimedActionUpdateCommand.init("adobe", "Adobe Track Timed Action Update", utag);
testTrackTimedActionUpdateCommand.is_custom_module()
	.method("track_timed_action_update")
		.add_argument("action","time to checkout")
		.add_argument("custom_data",{"custom_key":"custom_value"});
testTrackTimedActionUpdateCommand.trigger();
``` 

iOS equivalent:

```objective-c
NSString *action            = @"time to checkout";
NSDictionary *customData    = @{ @"custom_key" : @"custom_value" };

[ADBMobile trackTimedActionUpdate:action data:customData];
```

Android equivalent:

```java
String action;
Map<String, Object> contextData;

//...

Analytics.trackTimedActionUpdate(action, contextData);
```

[top](#adobe-tagbridge-module-api-reference)

###track_timed_action_end

Arguments:

* action : string
* custom_data : object
* should_send_event : string of true | false
			

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "track_timed_action_end",
		arguments : {
			// (string) *required
			action : "time to checkout",
			// (JSON object) *optional
			custom_data : {},
			// (string "bool") *optional
			should_send_event : "false"
		}
	}
})), '_self');	
```

Mobile Utilities call:

```javascript
var testTrackTimedActionEndCommand = utag.mobile.remote_api.api_builder();
testTrackTimedActionEndCommand.init("adobe", "Adobe Track Timed Action End", utag);
testTrackTimedActionEndCommand.is_custom_module()
	.method("track_timed_action_end")
		.add_argument("action","time to checkout")
		.add_argument("custom_data",{"custom_key":"custom_value"})
		.add_argument("should_send_event", "false");
testTrackTimedActionEndCommand.trigger();

``` 

iOS equivalent:

```objective-c
NSString *action            = @"time to checkout";
NSDictionary *customData    = @{ @"custom_key" : @"custom_value" };
BOOL shouldSend 			= YES;

[ADBMobile trackTimedActionEnd:action logic:^BOOL(NSTimeInterval inAppDuration, NSTimeInterval totalDuration, NSMutableDictionary *data) {
    
    if (customData) {
        [data addEntriesFromDictionary:customData];
    }
    // do something with custom data
    
    // put any custom handling here
    return shouldSend;
}];

```

Android equivalent:

```java
String action;
final Map<String, Object> contextData;
final boolean shouldSend;

//...

TimedActionBlock<Boolean> block = new TimedActionBlock<Boolean>() {
	@Override
	public Boolean call(long inAppDuration, long totalDuration, Map<String, Object> customData) {
		customData.putAll(contextData);
		return shouldSend;
	}
};

Analytics.trackTimedActionEnd(action, block);
```

[top](#adobe-tagbridge-module-api-reference)

###tracking_send_queued_hits

Arguments:

* none

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "tracking_send_queued_hits"
	}
})), '_self');
```

Mobile Utilities call:

```javascript
var testTrackingSendQueuedHitsCommand = utag.mobile.remote_api.api_builder();
testTrackingSendQueuedHitsCommand.init("adobe", "Adobe Tracking Send Queued Hits", utag);
testTrackingSendQueuedHitsCommand.is_custom_module()
	.method("tracking_send_queued_hits");
testTrackingSendQueuedHitsCommand.trigger();
``` 

iOS equivalent:

```objective-c
[ADBMobile trackingSendQueuedHits];
```

Android equivalent:

```java
Analytics.sendQueuedHits();
```

[top](#adobe-tagbridge-module-api-reference)

###tracking_clear_queue

Arguments:

* none

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "tracking_clear_queue"
	}
})), '_self');	
```

Mobile Utilities call:

```javascript
var testTrackingClearQueueCommand = utag.mobile.remote_api.api_builder();
testTrackingClearQueueCommand.init("adobe", "Adobe Tracking Clear Queue", utag);
testTrackingClearQueueCommand.is_custom_module()
	.method("tracking_clear_queue");
testTrackingClearQueueCommand.trigger();
``` 

iOS equivalent:

```objective-c
[ADBMobile trackingClearQueue];
```

Android equivalent:

```java
Analytics.clearQueue();
```

[top](#adobe-tagbridge-module-api-reference)

###media_close

Arguments:

* name : string

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "media_close",
		arguments : {
			// (string) *required
			name : "Star Wars Ep. VII"
		}
	}
})), '_self');	
```

Mobile Utilities call:

```javascript
var testMediaCloseCommand = utag.mobile.remote_api.api_builder();
testMediaCloseCommand.init("adobe", "Adobe Media Close", utag);
testMediaCloseCommand.is_custom_module()
	.method("media_close")
		.add_argument("name","Star Wars Ep. VII");
testMediaCloseCommand.trigger();
``` 

iOS equivalent:

```objective-c
NSString *name = @"Star Wars Ep. VII";

[ADBMobile mediaClose:name];
```

Android equivalent:

```java
String name;

//...

Media.close(name);
```

[top](#adobe-tagbridge-module-api-reference)

###media_play

Arguments:

* name : string
* offset : string - doubleValue

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "media_play",
		arguments : {
			// (string) *required
			name : "Star Wars Ep. VII",
			// (stirng "double")
			offset : "0.30"
		}
	}
})), '_self');	

```

Mobile Utilities call:

```javascript
var testMediaPlayCommand = utag.mobile.remote_api.api_builder();
testMediaPlayCommand.init("adobe", "Adobe Media Play", utag);
testMediaPlayCommand.is_custom_module()
	.method("media_play")
		.add_argument("name", "Star Wars Ep. VII")
		.add_argument("offset", "0.30");
testMediaPlayCommand.trigger();	

``` 

iOS equivalent:

```objective-c
NSString *name 		= @"Star Wars Ep. VII";
NSString *offset 	= @"0.30";

double offsetValue 	= 0.0;

if (offset) {
    offsetValue = [offset doubleValue];
}
[ADBMobile mediaPlay:name offset:offsetValue];
```

Android equivalent:

```java
String name;
double offset;

//...

Media.play(name, offset);
```

[top](#adobe-tagbridge-module-api-reference)

###media_complete

Arguments:

* name : string
* offset : string - doubleValue

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "media_complete",
		arguments : {
			// (string) *required
			name : "Star Wars Ep. VII",
			// (stirng "double")
			offset : "7300.70"
		}
	}
})), '_self');	
```

Mobile Utilities call:

```javascript
var testMediaCompleteCommand = utag.mobile.remote_api.api_builder();
testMediaCompleteCommand.init("adobe", "Adobe Media Complete", utag);
testMediaCompleteCommand.is_custom_module()
	.method("media_complete")
		.add_argument("name", "Star Wars Ep. VII")
		.add_argument("offset", "7300.70");
testMediaCompleteCommand.trigger();	
``` 

iOS equivalent:

```objective-c
NSString *name 		= @"Star Wars Ep. VII";
NSString *offset 	= @"7300.70";

double offsetValue 	= 0.0;

if (offset) {
    offsetValue = [offset doubleValue];
}
[ADBMobile mediaComplete:name offset:offsetValue];
```

Android equivalent:

```java
String name;
double offset;

//...

Media.complete(name, offset);
```

[top](#adobe-tagbridge-module-api-reference)

###media_stop

Arguments:

* name : string
* offset : string - doubleValue

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "media_stop",
		arguments : {
			// (string) *required
			name : "Star Wars Ep. VII",
			// (stirng "double")
			offset : "200.70"
		}
	}
})), '_self');	
```

Mobile Utilities call:

```javascript
var testMediaStopCommand = utag.mobile.remote_api.api_builder();
testMediaStopCommand.init("adobe", "Adobe Media Stop", utag);
testMediaStopCommand.is_custom_module()
	.method("media_stop")
		.add_argument("name", "Star Wars Ep. VII")
		.add_argument("offset", "200.70");
testMediaStopCommand.trigger();	
``` 

iOS equivalent:

```objective-c
NSString *name 		= @"Star Wars Ep. VII";
NSString *offset 	= @"200.70";

double offsetValue 	= 0.0;

if (offset) {
    offsetValue = [offset doubleValue];
}
[ADBMobile mediaStop:name offset:offsetValue];
```

Android equivalent:

```java
String name;
double offset;

//...

Media.stop(name, offset);
```

[top](#adobe-tagbridge-module-api-reference)

###media_click

Arguments:

* name : string
* offset : string - doubleValue

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "media_click",
		arguments : {
			// (string) *required
			name : "Star Wars Ep. VII",
			// (string "double")
			offset : "400.42"
		}
	}
})), '_self');	
```

Mobile Utilities call:

```javascript
var testMediaClickCommand = utag.mobile.remote_api.api_builder();
testMediaClickCommand.init("adobe", "Adobe Media Click", utag);
testMediaClickCommand.is_custom_module()
	.method("media_click")
		.add_argument("name", "Star Wars Ep. VII")
		.add_argument("offset", "400.42");
testMediaClickCommand.trigger();
``` 

iOS equivalent:

```objective-c
NSString *name 		= @"Star Wars Ep. VII";
NSString *offset 	= @"400.42";

double offsetValue 	= 0.0;

if (offset) {
    offsetValue = [offset doubleValue];
}
[ADBMobile mediaClick:name offset:offsetValue];
```

Android equivalent:

```java
String name;
double offset;

//...

Media.click(name, offset);
```

[top](#adobe-tagbridge-module-api-reference)

###media_track

Arguments:

* name : string
* custom_data : object

Direct call:

```javascript
window.open('tealium://adobe?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : responseId
	},
	payload : {
		// (string) *required
		method : "media_track",
		arguments : {
			// (string) *required
			action : "time to checkout",
			// (JSON object) *optional
			custom_data : {}
		}
	}
})), '_self');	
```

Mobile Utilities call:

```javascript
var testMediaTrackCommand = utag.mobile.remote_api.api_builder();
testMediaTrackCommand.init("adobe", "Adobe Media Track", utag);
testMediaTrackCommand.is_custom_module()
	.method("media_click")
		.add_argument("name", "Star Wars Ep. VII")
		.add_argument("custom_data",{"custom_key":"custom_value"});
testMediaTrackCommand.trigger();
``` 

iOS equivalent:

```objective-c
 NSString *action            = @"Star Wars Ep. VII";
 NSDictionary *customData    = @{ @"custom_key" : @"custom_value" };

[ADBMobile mediaTrack:name data:customData];
```

Android equivalent:

```java
String name;
Map<String, Object> data;

//...

Media.track(name, data);
```

[top](#adobe-tagbridge-module-api-reference)


---

### Mobile Utilities

```javascript
// check if mobile utiltilies do not exist or are an older version and update
utag.init_mobile={"version":{"version":"1.2.0"},"init":function(){utag.mobile=this.version;utag.mobile.remote_api={response:{},api_builder:function(){return{init:function(command_id,name,u){if(!u.data[command_id]){u.data[command_id]={}}
u.data[command_id].base_url="tealium://"+command_id+"?request=";u.data[command_id][name]={};u.data[command_id][name]["config"]={};u.data[command_id][name]["payload"]={};this.command_id=command_id;if(command_id=="_http"){this.url=function(url){u.data[command_id][name]["payload"]["url"]=url;return this;};this.method=function(method){this.method=method;return this;};this.authenticate=function(username,password){u.data[command_id][name]["payload"]["authenticate"]={"username":username,"password":password}
return this;};this.add_header=function(key_or_object,value){add_object("headers",key_or_object,value);return this;};this.add_parameter=function(key_or_object,value){add_object("parameters",key_or_object,value);return this;};var add_object=function(type,key_or_object,value){if(typeof key_or_object=="object"){if(!u.data[command_id][name]["payload"][type]){u.data[command_id][name]["payload"][type]=key_or_object;}else{for(var key in key_or_object){u.data[command_id][name]["payload"][type][key]=key_or_object[key];}}}else{if(!u.data[command_id][name]["payload"][type]){u.data[command_id][name]["payload"][type]={}}
u.data[command_id][name]["payload"][type][key_or_object]=value;}};}
if(command_id=="_push"){this.project_number=function(project_number){u.data[command_id][name]["payload"]["project_number"]=project_number;return this;}}
if(command_id=="_http"||command_id=="_push"){this.add_body=function(string_key_or_object,value){if(typeof string_key_or_object=="string"&&!value){u.data[command_id][name]["payload"]["body"]=string_key_or_object;}else if(typeof string_key_or_object=="object"){add_object("body",string_key_or_object);}else{if(!u.data[command_id][name]["payload"]["body"])u.data[command_id][name]["payload"]["body"]={};u.data[command_id][name]["payload"]["body"][string_key_or_object]=value;}
return this;};}
this.callback=function(callback){this.callback_function=callback;this.response_id();return this;};this.response_id=function(response_id){u.data[command_id][name]["config"]["response_id"]=(response_id)?name+"_"+response_id:name;u.data[command_id][name]["response_id_set"]=function(){u.data[command_id][name]["config"]["response_id"]=name+"_"+new Date().getTime()+ +Math.floor((Math.random()*2000)+1);};return this;};this.trigger=function(){if(u.data[command_id][name].response_id_set){u.data[command_id][name].response_id_set();}
if(u.data[command_id][name]["payload"]["body"]&&typeof u.data[command_id][name]["payload"]["body"]=="object"&&u.data[command_id][name]["payload"]["headers"]&&u.data[command_id][name]["payload"]["headers"]["Content-Type"]&&u.data[command_id][name]["payload"]["headers"]["Content-Type"]!="application/x-www-form-urlencoded"){u.data[command_id][name]["payload"]["body"]=JSON.stringify(u.data[command_id][name]["payload"]["body"]);}
if(!utag.mobile.remote_api.response[command_id]){utag.mobile.remote_api.response[command_id]={}}
if(this.payload&&this.custom=="module"){u.data[command_id][name]["payload"]=this.payload;}
if(this.payload&&this.custom=="command"){for(var argument in this.payload){u.data[command_id][name]["payload"][argument]=this.payload[argument];}}
if(this.method){u.data[command_id][name]["payload"]["method"]=this.method;}
utag.mobile.remote_api.response[command_id][u.data[command_id][name]["payload"].response_id]=this.callback_function?this.callback_function:function(code,body){};setTimeout(function(){window.open(u.data[command_id].base_url+encodeURIComponent(JSON.stringify(u.data[command_id][name])),"_self");},100+Math.floor((Math.random()*200)+1));}
return this;},add_argument:function(argument,value){if(!value){value=""}
if(this.custom&&this.custom=="module"){if(!this.payload){this.payload={"arguments":{}};}
this.payload["arguments"][argument]=value;}
else if(this.custom&&this.custom=="command"){if(!this.payload){this.payload={};}
this.payload[argument]=value;}
else{this[argument]=value;}
return this;},is_custom_module:function(){this.custom="module";this.method=function(method_name){if(method_name){this.method=method_name;}
else{this.method=function(method_name){this.method=method_name;}}
return this;}
if(this.payload){delete this.payload;}
return this;},is_custom_command:function(){this.custom="command";if(this.payload){delete this.payload;}
return this;}}}};}}
if(!utag.mobile||(utag.mobile&&!utag.mobile.version)){utag.init_mobile.init();}else{var old_mobile_version=utag.mobile.version.split(".");var new_mobile_version=utag.init_mobile.version.version.split(".");var oa=parseInt(old_mobile_version[0]);var na=parseInt(new_mobile_version[0]);var ob=parseInt(old_mobile_version[1]);var nb=parseInt(new_mobile_version[1]);var oc=parseInt(old_mobile_version[2]);var nc=parseInt(new_mobile_version[2]);if(na>oa||(na==oa&&nb>ob)||(na==oa&&nb==ob&&nc>oc)){utag.init_mobile.init();}}

```

[top](#adobe-tagbridge-module-api-reference)

---

### Mobile Utilities

```javascript
// check if mobile utiltilies do not exist or are an older version and update
utag.init_mobile={"version":{"version":"1.2.0"},"init":function(){utag.mobile=this.version;utag.mobile.remote_api={response:{},api_builder:function(){return{init:function(command_id,name,u){if(!u.data[command_id]){u.data[command_id]={}}
u.data[command_id].base_url="tealium://"+command_id+"?request=";u.data[command_id][name]={};u.data[command_id][name]["config"]={};u.data[command_id][name]["payload"]={};this.command_id=command_id;if(command_id=="_http"){this.url=function(url){u.data[command_id][name]["payload"]["url"]=url;return this;};this.method=function(method){this.method=method;return this;};this.authenticate=function(username,password){u.data[command_id][name]["payload"]["authenticate"]={"username":username,"password":password}
return this;};this.add_header=function(key_or_object,value){add_object("headers",key_or_object,value);return this;};this.add_parameter=function(key_or_object,value){add_object("parameters",key_or_object,value);return this;};var add_object=function(type,key_or_object,value){if(typeof key_or_object=="object"){if(!u.data[command_id][name]["payload"][type]){u.data[command_id][name]["payload"][type]=key_or_object;}else{for(var key in key_or_object){u.data[command_id][name]["payload"][type][key]=key_or_object[key];}}}else{if(!u.data[command_id][name]["payload"][type]){u.data[command_id][name]["payload"][type]={}}
u.data[command_id][name]["payload"][type][key_or_object]=value;}};}
if(command_id=="_push"){this.project_number=function(project_number){u.data[command_id][name]["payload"]["project_number"]=project_number;return this;}}
if(command_id=="_http"||command_id=="_push"){this.add_body=function(string_key_or_object,value){if(typeof string_key_or_object=="string"&&!value){u.data[command_id][name]["payload"]["body"]=string_key_or_object;}else if(typeof string_key_or_object=="object"){add_object("body",string_key_or_object);}else{if(!u.data[command_id][name]["payload"]["body"])u.data[command_id][name]["payload"]["body"]={};u.data[command_id][name]["payload"]["body"][string_key_or_object]=value;}
return this;};}
this.callback=function(callback){this.callback_function=callback;this.response_id();return this;};this.response_id=function(response_id){u.data[command_id][name]["config"]["response_id"]=(response_id)?name+"_"+response_id:name;u.data[command_id][name]["response_id_set"]=function(){u.data[command_id][name]["config"]["response_id"]=name+"_"+new Date().getTime()+ +Math.floor((Math.random()*2000)+1);};return this;};this.trigger=function(){if(u.data[command_id][name].response_id_set){u.data[command_id][name].response_id_set();}
if(u.data[command_id][name]["payload"]["body"]&&typeof u.data[command_id][name]["payload"]["body"]=="object"&&u.data[command_id][name]["payload"]["headers"]&&u.data[command_id][name]["payload"]["headers"]["Content-Type"]&&u.data[command_id][name]["payload"]["headers"]["Content-Type"]!="application/x-www-form-urlencoded"){u.data[command_id][name]["payload"]["body"]=JSON.stringify(u.data[command_id][name]["payload"]["body"]);}
if(!utag.mobile.remote_api.response[command_id]){utag.mobile.remote_api.response[command_id]={}}
if(this.payload&&this.custom=="module"){u.data[command_id][name]["payload"]=this.payload;}
if(this.payload&&this.custom=="command"){for(var argument in this.payload){u.data[command_id][name]["payload"][argument]=this.payload[argument];}}
if(this.method){u.data[command_id][name]["payload"]["method"]=this.method;}
utag.mobile.remote_api.response[command_id][u.data[command_id][name]["payload"].response_id]=this.callback_function?this.callback_function:function(code,body){};setTimeout(function(){window.open(u.data[command_id].base_url+encodeURIComponent(JSON.stringify(u.data[command_id][name])),"_self");},100+Math.floor((Math.random()*200)+1));}
return this;},add_argument:function(argument,value){if(!value){value=""}
if(this.custom&&this.custom=="module"){if(!this.payload){this.payload={"arguments":{}};}
this.payload["arguments"][argument]=value;}
else if(this.custom&&this.custom=="command"){if(!this.payload){this.payload={};}
this.payload[argument]=value;}
else{this[argument]=value;}
return this;},is_custom_module:function(){this.custom="module";this.method=function(method_name){if(method_name){this.method=method_name;}
else{this.method=function(method_name){this.method=method_name;}}
return this;}
if(this.payload){delete this.payload;}
return this;},is_custom_command:function(){this.custom="command";if(this.payload){delete this.payload;}
return this;}}}};}}
if(!utag.mobile||(utag.mobile&&!utag.mobile.version)){utag.init_mobile.init();}else{var old_mobile_version=utag.mobile.version.split(".");var new_mobile_version=utag.init_mobile.version.version.split(".");var oa=parseInt(old_mobile_version[0]);var na=parseInt(new_mobile_version[0]);var ob=parseInt(old_mobile_version[1]);var nb=parseInt(new_mobile_version[1]);var oc=parseInt(old_mobile_version[2]);var nc=parseInt(new_mobile_version[2]);if(na>oa||(na==oa&&nb>ob)||(na==oa&&nb==ob&&nc>oc)){utag.init_mobile.init();}}

```

[top](#adobe-tagbridge-module-api-reference)
