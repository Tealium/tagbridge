
describe('Adobe TagBridge Template Test', function() {

    beforeEach(function(done){ // reset objects before each test
        require("./../mock/utag.stub.js");
        require("../mobile_utilities_commands.js");
        require("./../mock/support.functions.js");
        require("../adobe.js");

        u = utag.o[1].sender[1];    // define u
        u.map = {};     // define mappings
        b = {};         // define test data
        done();
        /* use these to trigger tag processing in test
        u.send("view", b);
        u.send("link", b);
        */
    });

/*
*@Test
*/
    describe('Should init Adobe SDK and collect lifecycle on:', function(done) {
        //after(function () {
        //    simulateAppCallback();
        //});
        it('launch', function (done) {
            callNumber = 1; // two calls should be made
            testResults = null;

            var testAssertion = function (done) {
                testURI(
                    "tealium://adobe?request=%7B%22config%22%3A%7B%22response_id%22%3A%22adobe%20cloud_%22%7D%2C%22payload%22%3A%7B%22arguments%22%3A%7B%22config_json%22%3A%22%7B%5C%22version%5C%22%3A%5C%221.0%5C%22%2C%5C%22marketingCloud%5C%22%3A%7B%5C%22org%5C%22%3A%5C%22016D5C175213CCA80A490D05%40AdobeOrg%5C%22%7D%2C%5C%22aquisition%5C%22%3A%7B%5C%22server%5C%22%3A%5C%22c00.adobe.com%5C%22%2C%5C%22appid%5C%22%3A%5C%2210a77a60192fbb628376e1b1daeeb65debf934e2c807e067ceb2963a41b165ee%5C%22%7D%2C%5C%22analytics%5C%22%3A%7B%5C%22batchLimit%5C%22%3A0%2C%5C%22charset%5C%22%3A%5C%22UTF-8%5C%22%2C%5C%22lifecycleTimeout%5C%22%3A300%2C%5C%22rsids%5C%22%3A%5C%22coolApp%5C%22%2C%5C%22server%5C%22%3A%5C%22my.CoolApp.com%5C%22%2C%5C%22ssl%5C%22%3Afalse%2C%5C%22offlineEnabled%5C%22%3Atrue%2C%5C%22privacyDefault%5C%22%3A%5C%22optedin%5C%22%2C%5C%22referrerTimeout%5C%22%3A5%2C%5C%22poi%5C%22%3A%5B%5D%7D%2C%5C%22target%5C%22%3A%7B%5C%22clientCode%5C%22%3A%5C%22abc%5C%22%2C%5C%22timeout%5C%22%3A5%7D%2C%5C%22remotes%5C%22%3A%7B%5C%22analytics.poi%5C%22%3A%5C%22https%3A%2F%2Fassets.adobedtm.com%2Fstaging%2F42a6fc9b77cd9f29082cf19b787bae75b7d1f9ca%2Fscripts%2Fsatellite-53e0faadc2f9ed92bc00003b.json%5C%22%2C%5C%22messages%5C%22%3A%5C%22https%3A%2F%2Fassets.adobedtm.com%2Fstaging%2F42a6fc9b77cd9f29082cf19b787bae75b7d1f9ca%2Fscripts%2Fsatellite-53e0f9e2c2f9ed92bc000032.json%5C%22%7D%7D%22%7D%2C%22method%22%3A%22collect_lifecycle_data%22%7D%7D",
                    done
                );
            };

            window = new windowStub(done, testAssertion);   // create window stub and pass completion and assertion callbacks

            /*
             * Define simulated data layer here
             * b.key = "value";
             */
            b.lifecycle_type = "launch";

            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */

            // call the main send function as view
            u.send("view", b);

            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });
    });


/*
*@Test
*/
    describe('Lifecycle Session (sleep)', function(done) {
        //after(function () {
        //    simulateAppCallback();
        //});
        it('iOS keep session alive', function (done) {
            /*
             * testAssertion - Function callback block to send to window
             *       define assertions here
             */
            var testAssertion = function(){
                testURI(
                    "tealium://adobe?request=%7B%22config%22%3A%7B%22response_id%22%3A%22adobe%20cloud_%22%7D%2C%22payload%22%3A%7B%22method%22%3A%22keep_lifecycle_session_alive%22%7D%7D",
                    done
                );
            };

            window = new windowStub(done, testAssertion);   // create window stub and pass completion and assertion callbacks

            /*
             * Define simulated data layer here
             * b.key = "value";
             */
            b.lifecycle_type = "sleep";
            b.platform = "ios";

            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */

            // call the main send function as view
            u.send("view", b);

            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });

        it('Android pause collection', function (done) {
            /*
             * testAssertion - Function callback block to send to window
             *       define assertions here
             */
            var testAssertion = function(){
                testURI(
                    "tealium://adobe?request=%7B%22config%22%3A%7B%22response_id%22%3A%22adobe%20cloud_%22%7D%2C%22payload%22%3A%7B%22method%22%3A%22pause_collecting_lifecycle_data%22%7D%7D",
                done
                );
            };

            window = new windowStub(done, testAssertion);   // create window stub and pass completion and assertion callbacks

            /*
             * Define simulated data layer here
             * b.key = "value";
             */
            b.lifecycle_type = "sleep";
            b.platform = "android";

            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */

            // call the main send function as view
            u.send("view", b);

            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });
    });

/*
*@Test
*/
    describe('Track Location ', function(done) {
        //after(function () {
        //    simulateAppCallback();
        //});
        it('Lat and Long', function (done) {
            /*
             * testAssertion - Function callback block to send to window
             *       define assertions here
             */
            var testAssertion = function(){
                testURI(
                    "tealium://adobe?request=%7B%22config%22%3A%7B%22response_id%22%3A%22adobe%20cloud_%22%7D%2C%22payload%22%3A%7B%22arguments%22%3A%7B%22latitude%22%3A%22100%22%2C%22longitude%22%3A%22100%22%2C%22custom_data%22%3A%7B%7D%7D%2C%22method%22%3A%22track_location%22%7D%7D",
                    done
                );
            };

            window = new windowStub(done, testAssertion);   // create window stub and pass completion and assertion callbacks

            /*
             * Define simulated data layer here
             * b.key = "value";
             */
            b.latitude = "100";
            b.longitude = "100";

            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */
            u.map.latitude = "latitude";
            u.map.longitude = "longitude";

            // call the main send function as view
            u.send("view", b);

            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });

        it('Lat and Long w/ data', function (done) {
            /*
             * testAssertion - Function callback block to send to window
             *       define assertions here
             */
            var testAssertion = function(){
                testURI(
                    "tealium://adobe?request=%7B%22config%22%3A%7B%22response_id%22%3A%22adobe%20cloud_%22%7D%2C%22payload%22%3A%7B%22arguments%22%3A%7B%22latitude%22%3A%22100%22%2C%22longitude%22%3A%22100%22%2C%22custom_data%22%3A%7B%22location.data%22%3A%22value%22%7D%7D%2C%22method%22%3A%22track_location%22%7D%7D",
                    done
                );
            };

            window = new windowStub(done, testAssertion);   // create window stub and pass completion and assertion callbacks

            /*
             * Define simulated data layer here
             * b.key = "value";
             */
            b.latitude = "100";
            b.longitude = "100";
            b.custom_data = "value";

            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */
            u.map.latitude = "latitude";
            u.map.longitude = "longitude";
            u.map.custom_data = "context.location.data";

            // call the main send function as view
            u.send("view", b);

            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });
    });

/*
*@Test
*/
    describe('Track State (view) ', function(done) {
        //after(function () {
        //    simulateAppCallback();
        //});
        it('all views', function (done) {
            /*
             * testAssertion - Function callback block to send to window
             *       define assertions here
             */
            var testAssertion = function(){
                testURI(
                    "tealium://adobe?request=%7B%22config%22%3A%7B%22response_id%22%3A%22adobe%20cloud_%22%7D%2C%22payload%22%3A%7B%22arguments%22%3A%7B%22state_name%22%3A%22test%20view%22%2C%22custom_data%22%3A%7B%7D%7D%2C%22method%22%3A%22track_state%22%7D%7D",
                    done
                );
            };

            window = new windowStub(done, testAssertion);   // create window stub and pass completion and assertion callbacks

            /*
             * Define simulated data layer here
             * b.key = "value";
             */
            b.state_name = "test view";

            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */
            u.map.state_name = "state_name";

            // call the main send function as view
            utag.macros[1][1]["track_all_views"] = true;
            u.send("view", b);
            utag.macros[1][1]["track_all_views"] = false;

            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });

        it('all views w/data', function (done) {
            /*
             * testAssertion - Function callback block to send to window
             *       define assertions here
             */
            var testAssertion = function(){
                testURI(
                    "tealium://adobe?request=%7B%22config%22%3A%7B%22response_id%22%3A%22adobe%20cloud_%22%7D%2C%22payload%22%3A%7B%22arguments%22%3A%7B%22state_name%22%3A%22test%20view%22%2C%22custom_data%22%3A%7B%22state.data%22%3A%22value%22%7D%7D%2C%22method%22%3A%22track_state%22%7D%7D",
                    done
                );
            };

            window = new windowStub(done, testAssertion);   // create window stub and pass completion and assertion callbacks

            /*
             * Define simulated data layer here
             * b.key = "value";
             */
            b.state_name = "test view";
            b.custom_data = "value";

            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */
            u.map.state_name = "state_name";
            u.map.custom_data = "context.state.data";

            // call the main send function as view
            utag.macros[1][1]["track_all_views"] = true;
            u.send("view", b);
            utag.macros[1][1]["track_all_views"] = false;

            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });

        it('Track State from mapped value', function (done) {
            /*
             * testAssertion - Function callback block to send to window
             *       define assertions here
             */
            var testAssertion = function(){
                testURI(
                    "tealium://adobe?request=%7B%22config%22%3A%7B%22response_id%22%3A%22adobe%20cloud_%22%7D%2C%22payload%22%3A%7B%22arguments%22%3A%7B%22state_name%22%3A%22test%20page%22%2C%22custom_data%22%3A%7B%7D%7D%2C%22method%22%3A%22track_state%22%7D%7D",
                    done
                );
            };

            window = new windowStub(done, testAssertion);   // create window stub and pass completion and assertion callbacks

            /*
             * Define simulated data layer here
             * b.key = "value";
             */
            b.track_state = "true";
            b.page_name = "test page";
            //b.custom_data = "value";

            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */
            u.map["track_state:true"] = "track_state";
            u.map.page_name = "state_name";
            //u.map.custom_data = "context.location.data";

            // call the main send function as view
            u.send("view", b);

            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });
    });

/*
*@Test
*/
    describe('Track Action (link) ', function(done) {
        //after(function () {
        //    simulateAppCallback();
        //});
        it('all links', function (done) {
            /*
             * testAssertion - Function callback block to send to window
             *       define assertions here
             */
            var testAssertion = function(){
                testURI(
                    "tealium://adobe?request=%7B%22config%22%3A%7B%22response_id%22%3A%22adobe%20cloud_%22%7D%2C%22payload%22%3A%7B%22arguments%22%3A%7B%22action_name%22%3A%22test%20action%22%2C%22custom_data%22%3A%7B%7D%7D%2C%22method%22%3A%22track_action%22%7D%7D",
                    done
                );
            };

            window = new windowStub(done, testAssertion);   // create window stub and pass completion and assertion callbacks

            /*
             * Define simulated data layer here
             * b.key = "value";
             */
            b.action_name = "test action";

            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */
            u.map.action_name = "action_name";

            // call the main send function as view
            utag.macros[1][1]["track_all_links"] = true;
            u.send("link", b);
            utag.macros[1][1]["track_all_links"] = false;

            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });

        it('all links w/data', function (done) {
            /*
             * testAssertion - Function callback block to send to window
             *       define assertions here
             */
            var testAssertion = function(){
                testURI(
                    "tealium://adobe?request=%7B%22config%22%3A%7B%22response_id%22%3A%22adobe%20cloud_%22%7D%2C%22payload%22%3A%7B%22arguments%22%3A%7B%22action_name%22%3A%22test%20link%22%2C%22custom_data%22%3A%7B%22action.data%22%3A%22value%22%7D%7D%2C%22method%22%3A%22track_action%22%7D%7D",
                    done
                );
            };

            window = new windowStub(done, testAssertion);   // create window stub and pass completion and assertion callbacks

            /*
             * Define simulated data layer here
             * b.key = "value";
             */
            b.action_name = "test link";
            b.custom_data = "value";

            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */
            u.map.action_name = "action_name";
            u.map.custom_data = "context.action.data";

            // call the main send function as view
            utag.macros[1][1]["track_all_links"] = true;
            u.send("link", b);
            utag.macros[1][1]["track_all_links"] = false;

            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });

        it('Track Action from mapped value', function (done) {
            /*
             * testAssertion - Function callback block to send to window
             *       define assertions here
             */
            var testAssertion = function(){
                testURI(
                    "tealium://adobe?request=%7B%22config%22%3A%7B%22response_id%22%3A%22adobe%20cloud_%22%7D%2C%22payload%22%3A%7B%22arguments%22%3A%7B%22action_name%22%3A%22test%20action%22%2C%22custom_data%22%3A%7B%7D%7D%2C%22method%22%3A%22track_action%22%7D%7D",
                    done
                );
            };

            window = new windowStub(done, testAssertion);   // create window stub and pass completion and assertion callbacks

            /*
             * Define simulated data layer here
             * b.key = "value";
             */
            b.track_action = "true";
            b.action_name = "test action";
            //b.custom_data = "value";

            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */
            u.map["track_action:true"] = "track_action";
            u.map.action_name = "action_name";
            //u.map.custom_data = "context.location.data";

            // call the main send function as view
            u.send("view", b);

            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });
    });


/*
*@Test
*/
    describe('Lifetime value ', function(done) {
        it('check localStorage is null', function (done) {

            window = new windowStub();   // create window stub

            /*
             * Define simulated data layer here
             * b.key = "value";
             */


            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */


            // call the main send function as view
            utag.test.macros.track_all_links = true;
            u.send("link", b);
            utag.test.macros.track_all_links = false;
            if (b.adobe_total_lifetime_value){
                throw new Error("adobe_total_lifetime_value is initialized in localStorage before being set");
            }
            done();
            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });

        it('track lifetime value', function (done) {
            /*
             * testAssertion - Function callback block to send to window
             *       define assertions here
             */
            var testAssertion = function(){
                testURI(
                    "tealium://adobe?request=%7B%22config%22%3A%7B%22response_id%22%3A%22adobe%20cloud_%22%7D%2C%22payload%22%3A%7B%22arguments%22%3A%7B%22amount%22%3A%221%22%2C%22custom_data%22%3A%7B%7D%7D%2C%22method%22%3A%22track_lifetime_value_increase%22%7D%7D",
                    done,
                    {
                        "code":"200",
                        "body":"1"
                    }
                );
            };

            window = new windowStub(done, testAssertion);   // create window stub and pass completion and assertion callbacks

            /*
             * Define simulated data layer here
             * b.key = "value";
             */
            b.add_lifetime_value = "1";

            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */
            u.map.add_lifetime_value = "lifetime_value";

            // call the main send function as view
            utag.test.macros.track_all_links = true;
            u.send("link", b);
            utag.test.macros.track_all_links = false;

            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });

        it('check localStorage has lifetime value', function (done) {

            window = new windowStub();   // create window stub

            /*
             * Define simulated data layer here
             * b.key = "value";
             */


            /*
             * Define simulated mappings here
             * u.map.key = "value";
             */


            // call the main send function as view
            utag.test.macros.track_all_links = true;
            u.send("link", b);
            utag.test.macros.track_all_links = false;

            // sync method to evaluate if callback was success
            simulateAppCallback({
                "code":"200",
                "body":"1"
            });
            if (!localStorage.getItem("adobe_total_lifetime_value")){
                throw new Error("adobe_total_lifetime_value is not initialized in localStorage after being set");
            }
            else if(localStorage.getItem("adobe_total_lifetime_value") != "1"){
                throw new Error("value of adobe_total_lifetime_value in localStorage is not '1'");
            }
            done();
            // call the main send function as link
            //u.send("link", b);

            //u.send("link", b);
        });

    });


});