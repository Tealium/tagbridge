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
        left: 0, 
		bottom:	0,
		right: 0,
		height:	100
    }
})), '_self');

window.open('tealium://google_dfp?request=' + encodeURIComponent(JSON.stringify({
    config : {
        response_id : 0
    }, 
    payload : {
		top: 0,
		right: 0,
		height:	100,
		width: 100
    }
})), '_self');
```

* left
* top
* bottom
* right
* width 
* height