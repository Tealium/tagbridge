/*
 * windowStub - Used to simulate calls to window.open and test assertions
 * done      Function to signal test is complete
 * callback  Function containing assertions to evaluate
 */
windowStub = function(done, callback){
    this.document = new (function(){})();

    this.tagBridgeCall = [];    // To store uri requests for evaluation in assertions

    this.open = function(uri, window){
        if (window != "_self"){
            this.tagBridgeCall.push(false);
        }
        else{
            this.tagBridgeCall.push(uri);
        }
        if (callback) {
            callback(done); // Evaluate assertion for this test
        }
    };
};

/*
 * simulateAppCallback - Used to simulate native app eval of callback method
 *      callback method is also used to call next request in the serial queue
 * REQUIRED to test
 */
simulateAppCallback = function(callBackArgs){
    // simulate the native callback
    callBackArgs = callBackArgs || {};
    var response_code = callBackArgs.code;
    var response_body = callBackArgs.body;
    var response = utag.mobile.remote_api.response.adobe;
    for (var key in response) break;
    var callback = response[key];
    delete response[key];
    if(callback) callback(response_code, response_body);
};

testURI = function (uri, done, callBackArgs) {
    var result = window.tagBridgeCall.shift();      // get the URI tagBridge attempted
    var regx = (/cloud_.*%22%7D%2C%22payload/i);    // define pattern to remove unique id
    result = result.replace(regx, "cloud_%22%7D%2C%22payload"); // remove unique id
    // expected request URI
    var expectedURI = uri;
    // if result doesnt match expected request FAIL
    if (result !== expectedURI) {
        setTimeout(function(){simulateAppCallback();}, 10); // Needs to call async to not hold queue or block the fail
        throw new Error("tagBridge API incorrect for: 'collect lifecycle data'\n#Expected: " + expectedURI + "\n#Actual!!: " + result);
    }
    done();  // signal test is done (needed for async processing of tagBridge requests)
    if(!callBackArgs) {callBackArgs = {"code":"200","body":""};}
    simulateAppCallback(callBackArgs);  // Simulate native app callback to release the queue
};

Storage = true;
localStorage = {
    "setItem" : function(key, value){
        if (Storage === true) {Storage = {}}
        Storage[key] = value;
    },
    "getItem" : function(key){
        if (Storage === true) {
            return null;
        }
        else {
            return Storage[key] || null;
        }
    }
};
