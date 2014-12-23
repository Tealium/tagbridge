# TagBridge Specification

The contents of this directory are the various modules built utilizing TagBridge. 

## Brief

This document specifies the format of a TagBridge request, and how the native implementations should respond to a request. 

## Table of Contents

* [Request Specification](#request-specification)
* [Meta Keys](#meta-keys)
 * [config](#config)
 * [payload](#payload)
* [Response Specification](#response-specification)
* [Response Statuses](#response-statuses)
 * [200](#200)
 * [400](#400)
 * [404](#404)
 * [555](#555)
* [Reserved Remote Commands](#reserved-remote-commands)
 * [_http](#_http)
 * [_push](#_push)
 * [_mobilecompanion](#_mobilecompanion)


## Request Specification

A request is a native command invocation and is identical to a web-request but instead of say ```http://tealium.com``` it would look like ```tealium://foo?request="%7B%22config%22%3A%7B%22response_id%22%3A%2212345%22%7D%7D"```.

| Component | Description | Specification | Example |
| --------- | ----------- | ---------- | ------- |
| Protocol | Name of the protocol. | Must be ```tealium``` | ```tealium://``` |
| Address | Command Id | dom-acceptable: must match the regex ```^[\w-]*$```. | ```command-id``` |
| QueryString-key | Single key which provides callback info and payload. | must be ```request``` | ```?request=``` |
| QueryString-value | A url-encoded JSON object consisting of meta-keys and objects. | Valid URL-encoded stringified JSON Object | ```"%7B%22config%22%3A%7B%22response_id%22%3A%2212345%22%7D%7D"``` |
| [Meta-key](#meta-keys) | Key specifying TagBridge response.  | Predefined keys only. | [config](#config) &amp; [payload](#payload) |

## Meta Keys

These keys instruct the native TagBridge components how to perform and respond to a given request.

### config
These keys provide TagBridge with additional info in order to complete the request. Entries belong in the table below. 

* *optional*
* **type**: JSON object

##### Header Names

| Name | Type | Description | 
| ---- | ---- | ----------- |
| response_id | string | The index of the JavaScript callback. |

### payload
If defined, this JSON object will be passed as the argument to the native implementation of the command, else an empty JSON object will be provided. 

* *optional*
* **type**: JSON object


## Response Specification

Per request, the TagBridge tag will create the following callback: 

```javascript
utag.mobile.remote_api.response[command_id][response_id] = function(status, message) {
    //...
}
```

* command_id
 * **type**: string
 * The TagBridge command name, will match the address in the request.
* response_id
 * **type**: string
 * [Header Key](#config), should be unique per request.
* [status](#response-statuses)
 * **type**: integer/number
 * Status response from the native TagBridge system. 
* message
 * *optional*
 * **type**: string
 * Can be anything returned by a TagBridge command. 
 
## Response Statuses

##### 200

The command id was found, and processed as expected.

##### 400

The JSON object value for the [payload](#payload) [meta-key](#meta-key) was not what the native command expected. 

##### 404

No command was found matching the given command id.

##### 555

A native exception was thrown, a stringified stack trace is passed as the message body.

## Reserved Remote Commands

These commands will be available on both iOS and Android starting version 4.1.

* [_http](#_http)
* [_push](#_push)
* [_mobilecompanion](#_mobilecompanion)

---

#### _http

Perform a http operation bypassing a CORS violation. 

**Required Keys**

* url
* method

**Returned Parameters**

* status
 * Server provided http status code (usually [200](#200))
 * [400](#400) for a malformed argument. 
 * [555](#555) for a native runtime error. 
* message
 * Response if available, otherwise if [400](#400) or [555](#555), the error message is provided.

**Example**

```javascript
var response_id = new Date().getTime();

window.open('tealium://_http?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : response_id
	}, 
	payload : {
		authenticate : {
			username : '<username>',
			password : '<password>'
		}, // http://username:password@url...
		url : '<url>',
		headers : {
			'<header>' : '<value>'
		},
		parameters : {
			'<someKey>' : '<someValue>'
		},// http://url.com?someKey=someValue...
		body : {
			'<someKey>' : '<someValue>'
		}, // Or String, thought if a given JSON the structure will be converted into a form submission.
		method : '<POST/GET/PUT>'
	}
})), '_self');
```

---

#### _push

Register to receive push notifications.

**Required Keys**

* project_number (Android only)

**Returned Parameters**

* status
 * [200](#200) if successfully retrieved.
 * [400](#400) for a malformed argument.
 * [555](#555) for a native runtime error. 
* message
 * Push token if available, otherwise if [400](#400) or [555](#555), the error message is provided.

**Example**

```javascript
var response_id = new Date().getTime();

window.open('tealium://_push?request=' + encodeURIComponent(JSON.stringify({
	config : {
		response_id : response_id
	}, 
	payload : {
		project_number : '<project number>' // Provided by the Google Developers Console
	}
})), '_self');
```

#### _mobilecompanion

Launch Mobile Companion (if unlocked by the Mobile Publish Settings).

**Returned Parameters**

* status
 * [200](#200) successfully launched.
 * [555](#555) Mobile Companion is not enabled by the Mobile Publish Settings.
* message
 * null.

**Example**

```javascript
window.open('tealium://_mobilecompanion', '_self');
```
