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

utag.mobile.remote_api.response.google_dfp.get_ads = function(status, message) {
	console.log("get_ad_unit_ids: " + status + "; " + message);
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

*Example*

```javascript
window.open('tealium://google_dfp?request=' + encodeURIComponent(JSON.stringify({
    config : {
        response_id : "create_banner_ad"
    }, 
    payload : {
		command : "create_banner_ad",
        banner_ad_sizes : [ "SMART_BANNER" ], 
		ad_unit_id : "/6499/example/banner", 
		banner_anchor : "BOTTOM", 	
		ad_id : "mainview_bottom_banner_ad"
    }
})), '_self');
```

## create_interstitial_ad

* ad_unit_id (String) **Required**
* ad_sizes (Array[String]) **Required**
 * "BANNER"
 * "LARGE_BANNER"
 * "MEDIUM_RECTANGLE"
 * "FULL_BANNER"
 * "LEADERBOARD"
 * "SMART_BANNER"
* andchor (String) **Required**
 * "top" 
 * "bottom"
* id (String) *Optional*
 * Any string

*Example*

```javascript
window.open('tealium://google_dfp?request=' + encodeURIComponent(JSON.stringify({
    config : {
        response_id : "create_ad"
    }, 
    payload : {
		command : "create_ad",
        ad_sizes : [ "SMART_BANNER" ], 
		ad_unit_id : "/6499/example/banner", 
		anchor : "bottom", 	
    }
})), '_self');
```

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
		ad_id : "mainview_bottom_banner_ad"
    }
})), '_self');
```



