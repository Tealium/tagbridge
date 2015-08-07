# JavaScript API

> In progress

## Setup JS

```javascript

if(!utag.mobile) {
	utag.mobile = {};
}

if(!utag.mobile.remote_api) {
		utag.mobile.remote_api = {};
}

if(!utag.mobile.remote_api.response) {
	utag.mobile.remote_api.response = {};
}

if(!utag.mobile.remote_api.response.google_dfp) {
	utag.mobile.remote_api.response.google_dfp = {};
}

// Response methods

utag.mobile.remote_api.response.google_dfp.create_banner_ad = function(status, message) {
	console.log("create_banner_ad: " + status + "; " + message);
};

utag.mobile.remote_api.response.google_dfp.create_interstitial_ad = function(status, message) {
	console.log("create_interstitial_ad: " + status + "; " + message);
};

utag.mobile.remote_api.response.google_dfp.show_interstitial_ad = function(status, message) {
	console.log("create_interstitial_ad: " + status + "; " + message);
};

utag.mobile.remote_api.response.google_dfp.get_ads = function(status, message) {
	console.log("get_ads: " + status + "; " + message);
};

utag.mobile.remote_api.response.google_dfp.remove_ad = function(status, message) {
	console.log("remove_ad: " + status + "; " + message);
};
```

> All payload parameters must have the **command** key with a corresponding command name (String) value.

## create_banner_ad

* ad_unit_id (String) **Required**
* banner_ad_sizes (Array[String]) **Required**
 * "BANNER"
 * "LARGE_BANNER"
 * "MEDIUM_RECTANGLE"
 * "FULL_BANNER"
 * "LEADERBOARD"
 * "SMART_BANNER"
* banner_anchor (String) **Required**
 * "TOP" 
 * "BOTTOM"
* ad_id (String) *Optional*
 * Any string
* custom_targeting (Object) *Optional*
 * key-value (String, Array)
* keywords (Array) *Optional* 
* category_exclusions (Array) *Optional* 
* request_agent (String) *Optional* 
* location (Object) *Optional* 
 * latitude (double)
 * longitude (double)
* gender (String) *Optional* 
 * "MALE"
 * "FEMALE"
 * "UNKNOWN"
* birthday (long) *Optional* 
* tag_for_child_directed_treatment (boolean) *Optional* 
* manual_impressions (boolean) *Optional* 
* publisher_provided_id (String) *Optional* 

*Example*

```javascript
window.open('tealium://google_dfp?request=' + encodeURIComponent(JSON.stringify({
    config : {
        response_id : "create_banner_ad"
    }, 
    payload : {
		command : "create_banner_ad",
        banner_ad_sizes : [ "SMART_BANNER", "BANNER" ], 
		ad_unit_id : "/6499/example/banner", 
		banner_anchor : "BOTTOM", 	
		ad_id : "mainview_bottom_banner_ad", 
		custom_targeting : {
			target0 : "people", 
			target1 : [ "groups", "of", "people" ]
		},
		keywords : [ "alpha", "beta" ],
		category_exclusions : [ "colors", "numbers" ],
		request_agent : "request_agent",
		location : {
			"latitude" : 32.906231,
			"longitude" : -117.237921
		},
		gender : "FEMALE",
		birthday : 599817600000,
		tag_for_child_directed_treatment : false,
		manual_impressions : false,
		publisher_provided_id : "publisher_provided_id"
    }
})), '_self');
```

## create_interstitial_ad

Interstitial ads are not shown right away and need time to load. The status can be queried by ```get_ads```. Once loaded, call ```show_interstitial_ad``` to show this ad.

* ad_unit_id (String) **Required**
* id (String) *Optional*
 * Any string
* custom_targeting (Object) *Optional*
 * key-value (String, Array)
* keywords (Array) *Optional* 
* category_exclusions (Array) *Optional* 
* request_agent (String) *Optional* 
* location (Object) *Optional* 
 * latitude (double)
 * longitude (double)
* gender (String) *Optional* 
 * "MALE"
 * "FEMALE"
 * "UNKNOWN"
* birthday (long) *Optional* 
* tag_for_child_directed_treatment (boolean) *Optional* 
* manual_impressions (boolean) *Optional* 
* publisher_provided_id (String) *Optional* 
* test_devices (Array) *Optional*
 * "DEVICE_ID_EMULATOR" for all emulators

*Example*

```javascript
window.open('tealium://google_dfp?request=' + encodeURIComponent(JSON.stringify({
    config : {
        response_id : "create_interstitial_ad"
    }, 
    payload : {
		command : "create_interstitial_ad",
		ad_id : "mainview_interstitial_ad",
		ad_unit_id : "/6499/example/interstitial", 
		custom_targeting : {
			target0 : "people", 
			target1 : [ "groups", "of", "people" ]
		},
		keywords : [ "alpha", "beta" ],
		category_exclusions : [ "colors", "numbers" ],
		request_agent : "request_agent",
		location : {
			"latitude" : 32.906231,
			"longitude" : -117.237921
		},
		gender : "FEMALE",
		birthday : 599817600000,
		tag_for_child_directed_treatment : false,
		manual_impressions : false,
		publisher_provided_id : "publisher_provided_id", 
		test_devices : [ "DEVICE_ID_EMULATOR" ]
    }
})), '_self');
```

## show_interstitial_ad

Show an existing interstitial ad already created by ```create_interstitial_ad```.

* command (String) **Required**
* ad_unit_id (String) **Required**
* ad_id (String) *Optional*

*Example*

window.open('tealium://google_dfp?request=' + encodeURIComponent(JSON.stringify({
    config : {
        response_id : "show_interstitial_ad"
    }, 
    payload : {
		command : "show_interstitial_ad",
		ad_unit_id : "/6499/example/interstitial", 
		ad_id : "mainview_interstitial_ad"
    }
})), '_self');


## get_ads

Get a list of visible ad unit ids, a stringified json array of string unit ids is delivered to the callback.

*Example*

```javascript
window.open('tealium://google_dfp?request=' + encodeURIComponent(JSON.stringify({
    config : {
        response_id : "get_ads"
    }, 
    payload : {
		command : "get_ads"
    }
})), '_self');
```

## remove_ad

Remove a visible ad from by its unit id.

* ad_unit_id (String) *Optional*
* ad_id (String) *Optional*

*Example*

```javascript
window.open('tealium://google_dfp?request=' + encodeURIComponent(JSON.stringify({
    config : {
        response_id : "remove_ad"
    }, 
    payload : {
		command : "remove_ad",
		ad_id : "mainview_interstitial_ad"
    }
})), '_self');
```



