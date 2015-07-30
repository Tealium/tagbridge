# JavaScript API

> In progress

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

utag.mobile.remote_api.response.google_dfp[0] = function(code, message) {
	console.log(code);
	console.log(message)
};


window.open('tealium://google_dfp?request=' + encodeURIComponent(JSON.stringify({
    config : {
        response_id : 0
    }, 
    payload : {
        ad_sizes : [ "BANNER" ], 
		ad_unit_id : "/6499/example/banner", 
		bottom : 1, 
		right : 1
    }
})), '_self');
```

* ad_unit_id (String) **Required**
* ad_sizes (Array[String]) **Required**
 * BANNER
 * LARGE_BANNER
 * MEDIUM_RECTANGLE
 * FULL_BANNER
 * LEADERBOARD
 * SMART_BANNER
* left (int in dip) *Optional*
* top (int in dip) *Optional*
* bottom (int in dip) *Optional*
* right (int in dip) *Optional*
