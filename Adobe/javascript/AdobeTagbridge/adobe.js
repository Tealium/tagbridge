
//~~tv:template.20140819
//~~tc:

// check if mobile utiltilies do not exist or are an older version and update
utag.init_mobile={"version":{"version":"1.3.1"},"notes":"added serial queue","init":function(){utag.mobile=this.version;utag.mobile.remote_api={ready:true,response:{},queue:new Array(),api_builder:function(){return{init:function(command_id,name,u){name=name+"_"+new Date().getTime()+ +Math.floor((Math.random()*2000)+1);if(!u.data[command_id]){u.data[command_id]={}}
    u.data[command_id].base_url="tealium://"+command_id+"?request=";u.data[command_id][name]={};u.data[command_id][name]["config"]={};u.data[command_id][name]["payload"]={};this.command_id=command_id;if(command_id=="_http"){this.url=function(url){u.data[command_id][name]["payload"]["url"]=url;return this;};this.method=function(method){this.method=method;return this;};this.authenticate=function(username,password){u.data[command_id][name]["payload"]["authenticate"]={"username":username,"password":password}
        return this;};this.add_header=function(key_or_object,value){add_object("headers",key_or_object,value);return this;};this.add_parameter=function(key_or_object,value){add_object("parameters",key_or_object,value);return this;};var add_object=function(type,key_or_object,value){if(typeof key_or_object=="object"){if(!u.data[command_id][name]["payload"][type]){u.data[command_id][name]["payload"][type]=key_or_object;}else{for(var key in key_or_object){u.data[command_id][name]["payload"][type][key]=key_or_object[key];}}}else{if(!u.data[command_id][name]["payload"][type]){u.data[command_id][name]["payload"][type]={}}
        u.data[command_id][name]["payload"][type][key_or_object]=value;}};}
    if(command_id=="_push"){this.project_number=function(project_number){u.data[command_id][name]["payload"]["project_number"]=project_number;return this;}}
    if(command_id=="_http"||command_id=="_push"){this.add_body=function(string_key_or_object,value){if(typeof string_key_or_object=="string"&&!value){u.data[command_id][name]["payload"]["body"]=string_key_or_object;}else if(typeof string_key_or_object=="object"){add_object("body",string_key_or_object);}else{if(!u.data[command_id][name]["payload"]["body"])u.data[command_id][name]["payload"]["body"]={};u.data[command_id][name]["payload"]["body"][string_key_or_object]=value;}
        return this;};}
    this.callback=function(callback){this.callback_function=callback;return this;};this.response_id=function(response_id){u.data[command_id][name]["config"]["response_id"]=(response_id)?name+"_"+response_id:name;u.data[command_id][name]["response_id_set"]=function(){u.data[command_id][name]["config"]["response_id"]=name;};return this;};this.trigger=function(){this.response_id();if(u.data[command_id][name].response_id_set){u.data[command_id][name].response_id_set();}
        if(u.data[command_id][name]["payload"]["body"]&&typeof u.data[command_id][name]["payload"]["body"]=="object"&&u.data[command_id][name]["payload"]["headers"]&&u.data[command_id][name]["payload"]["headers"]["Content-Type"]&&u.data[command_id][name]["payload"]["headers"]["Content-Type"]!="application/x-www-form-urlencoded"){u.data[command_id][name]["payload"]["body"]=JSON.stringify(u.data[command_id][name]["payload"]["body"]);}
        if(!utag.mobile.remote_api.response[command_id]){utag.mobile.remote_api.response[command_id]={}}
        if(this.payload&&this.custom=="module"){u.data[command_id][name]["payload"]=this.payload;}
        if(this.payload&&this.custom=="command"){for(var argument in this.payload){u.data[command_id][name]["payload"][argument]=this.payload[argument];}}
        if(this.method){u.data[command_id][name]["payload"]["method"]=this.method;}
        var remote_api=utag.mobile.remote_api;var response_id=u.data[command_id][name].config.response_id;remote_api.response[command_id][response_id]=function(code,body){if(remote_api.queue.length>0){var next_call=remote_api.queue.shift();next_call();if(remote_api.queue.length<1){remote_api.ready=true;}}
        else{remote_api.ready=true;}};if(this.callback_function){var cf=this.callback_function;remote_api.response[command_id][response_id]=function(code,body){try{cf(code,body);}
        catch(e){}
            if(remote_api.queue.length>0){var next_call=remote_api.queue.shift();next_call();if(remote_api.queue.length<1){remote_api.ready=true;}}
            else{remote_api.ready=true;}}}
        if(remote_api.ready){remote_api.ready=false;var current_call=u.data[command_id].base_url+encodeURIComponent(JSON.stringify(u.data[command_id][name]));window.open(current_call,"_self");}
        else{var queued_command=u.data[command_id].base_url+encodeURIComponent(JSON.stringify(u.data[command_id][name]));remote_api.queue.push(function(){window.open(queued_command,"_self");});}}
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
if(!utag.mobile||(utag.mobile&&!utag.mobile.version)){utag.init_mobile.init();}
else{var old_mobile_version=utag.mobile.version.split(".");var new_mobile_version=utag.init_mobile.version.version.split(".");var oa=parseInt(old_mobile_version[0]);var na=parseInt(new_mobile_version[0]);var ob=parseInt(old_mobile_version[1]);var nb=parseInt(new_mobile_version[1]);var oc=parseInt(old_mobile_version[2]);var nc=parseInt(new_mobile_version[2]);if(na>oa||(na==oa&&nb>ob)||(na==oa&&nb==ob&&nc>oc)){utag.init_mobile.init();}}



//tealium tagBridge for adobe cloud native mobile SDK - utag.sender.template ut4.0.##UTVERSION##, Copyright ##UTYEAR## Tealium.com Inc. All Rights Reserved.
(function(){

    var template = function (id, loader) {
        try {
            var macros = utag.macros[loader][id];
            var u = {};
            utag.o[loader].sender[id] = u;

            u.ev = {
                "view": 1,
                "link": 1
            };

            u.initialized = false;

            u.tlmAdobe = new function () {
                var adobe = function () {
                    var adobe = new utag.mobile.remote_api.api_builder();
                    adobe.init("adobe", "adobe cloud", u);
                    return adobe;
                };
                this.pause_collecting_lifecycle_data = function () {
                    adobe().is_custom_module().method("pause_collecting_lifecycle_data").trigger();
                };
                this.keep_lifecycle_session_alive = function () {
                    adobe().is_custom_module().method("keep_lifecycle_session_alive").trigger();
                };
                this.collect_lifecycle_data = function () {
                    var parseObject = function (json, nested) {
                        for (var key in json) {
                            var value = json[key];
                            if ((typeof value) == "string") {
                                if (!value) {
                                    delete json[key];
                                }
                            }
                            else if ((typeof value) == "object" && !nested) {
                                if (!Object.keys(value).length) {
                                    delete json[key]
                                }
                                else {
                                    json[key] = parseObject(value, true);
                                }
                            }
                        }
                        return json;
                    };
                    var config_data = {};
                    if (typeof u.data.config_json === 'object') {
                        config_data = parseObject(u.data.config_json)
                    }
                    if (config_data.target.timeout) {
                        config_data.target.timeout = parseInt(config_data.target.timeout);
                    }
                    ;
                    if (config_data.analytics.ssl) {
                        config_data.analytics.ssl = (config_data.analytics.ssl === 'true');
                    }
                    ;
                    if (config_data.analytics.offlineEnabled) {
                        config_data.analytics.offlineEnabled = (config_data.analytics.offlineEnabled === 'true');
                    }
                    ;
                    if (config_data.analytics.lifecycleTimeout) {
                        config_data.analytics.lifecycleTimeout = parseInt(config_data.analytics.lifecycleTimeout);
                    }
                    ;
                    if (config_data.analytics.batchLimit) {
                        config_data.analytics.batchLimit = parseInt(config_data.analytics.batchLimit);
                    }
                    ;
                    if (config_data.analytics.referrerTimeout) {
                        config_data.analytics.referrerTimeout = parseInt(config_data.analytics.referrerTimeout);
                    }
                    ;
                    config_data = JSON.stringify(config_data);
                    var _adobe = adobe();
                    _adobe.is_custom_module().method("collect_lifecycle_data");
                    if(!!Object.keys(u.data.config_json).length){
                        _adobe.add_argument("config_json", config_data);
                    }
                    _adobe.trigger();
                };
                this.track_state = function (state_name, data) {
                    adobe().is_custom_module().method("track_state").add_argument("state_name", state_name).add_argument("custom_data", data).trigger();
                };
                this.track_action = function (action_name, data) {
                    adobe().is_custom_module().method("track_action").add_argument("action_name", action_name).add_argument("custom_data", data).trigger();
                };
                this.track_location = function (latitude, longitude, poi, data) {
                    if (poi && (JSON.parse(poi)instanceof Array)) {
                        JSON.parse(poi).forEach(function (element) {
                            if (element.length == 4) {
                                var distance = distance_in_meters(latitude, longitude, element[1], element[2]);
                                if (distance <= element[3]) {
                                    context_data["a.loc.poi"] = element[0];
                                    context_data["a.loc.dist"] = distance + "";
                                }
                            }
                        });
                    }
                    adobe().is_custom_module().method("track_location").add_argument("latitude", latitude).add_argument("longitude", longitude).add_argument("custom_data", data).trigger();
                }
                this.track_lifetime_value = function (amount, data) {
                    adobe().is_custom_module().method("track_lifetime_value_increase").add_argument("amount", amount).add_argument("custom_data", data).callback(function (response_code, response_body) {
                        if ((/^2\d*/).test(response_code) && typeof(Storage) !== "undefined" && response_body != "") {
                            localStorage.setItem("adobe_total_lifetime_value", response_body);
                        }
                    }).trigger();
                }
                this.timed_action_start = function (action_name, data) {
                    adobe().is_custom_module().method("track_timed_action_start").add_argument("action", action_name).add_argument("custom_data", data).trigger();
                };
                this.timed_action_update = function (action_name, data) {
                    adobe().is_custom_module().method("track_timed_action_update").add_argument("action", action_name).add_argument("custom_data", data).trigger();
                };
                this.timed_action_end = function (action_name, data) {
                    var _adobe = adobe();
                    _adobe.is_custom_module().method("track_timed_action_end").add_argument("action", action_name).add_argument("custom_data", data);
                    if (!(/^null$|^$/i).test(mapped_data.timed_action_callback)) {
                        _adobe.callback(function (response_code, response_body) {
                            if (typeof JSON.parse(response_body) == "object") {
                                var r = JSON.parse(response_body);
                                var in_app_duration = r.in_app_duration;
                                var total_duration = r.total_duration;
                                var data = r.data;
                                window[mapped_data.timed_action_callback](in_app_duration, total_duration, data);
                            }
                        });
                    }
                    _adobe.trigger();
                };
                this.track_beacon = function (beacon_uuid, data) {
                    var _adobe = adobe();
                    _adobe.is_custom_module().method("track_beacon").add_argument("custom_data", data);
                    if (beacon_uuid) {
                        _adobe.add_argument("proximity_uuid", beacon_uuid);
                    }
                    _adobe.trigger();
                }
                this.track_beacon_clear = function () {
                    adobe().is_custom_module().method("tracking_clear_current_beacon").trigger();
                }
            };

            macros.utgen();

            u.send = function (a, b) {
                if (u.ev[a] || u.ev.all !== undefined) {

                    macros.debug_send();

                    var c, d, e, f;

                    u.data = {
                        "debug": macros.debug,
                        "track_all_views": macros.track_all_views,
                        "track_all_links": macros.track_all_links,
                        "config_json": {
                            "version": "1.0",
                            "marketingCloud": {
                              "org": macros.org
                            },
                            "aquisition" : {
                                "server": macros.aquisition_server,
                                "appid": macros.aquisition_appid,
                            },
                            "analytics": {
                                "batchLimit": macros.batch_limit,
                                "charset": macros.char_set,
                                "lifecycleTimeout": macros.lifecycle_timeout,
                                "rsids": macros.rsids,
                                "server": macros.server,
                                "ssl": macros.ssl,
                                "offlineEnabled": macros.offline_enabled,
                                "privacyDefault": macros.privacy_default,
                                "referrerTimeout": macros.referrer_timeout,
                                "poi": [

                                ]
                            },
                            "target": {
                                "clientCode": macros.client_code,
                                "timeout": macros.target_timeout
                            },
                            "messages": [

                            ],
                            "remotes": {
                                "analytics.poi": macros.remote_poi,
                                "messages": macros.remote_messages
                            }
                        },
                        "mapped_data": {}
                    };

                    macros.extend();

                    u.event = [];
                    u.addEvent = function (v) {
                        if (typeof v == "string") {
                            u.event.push(v);
                        }
                    }

                    for (d in utag.loader.GV(u.map)) {
                        if (b[d] !== undefined && b[d] !== "") {
                            e = u.map[d].split(",");
                            for (f = 0; f < e.length; f++) {
                                u.data.mapped_data[e[f]] = b[d];
                            }
                        }
                        else {
                            c = d.split(":");
                            if (c.length == 2 && b[c[0]] == c[1]) {
                                if (u.map[d] != "") {
                                    u.addEvent(u.map[d])
                                }
                            }
                        }
                    }

                    var tlmAdobe = u.tlmAdobe;

                    var mapped_data = u.data.mapped_data;
    // create context data object based on mappings "context.{CONTEXT VAR}"
                    var context_data = {};
                    for (key in mapped_data) {
                        if (( /^context\./i ).test(key)) {
                            var newKey = key.replace(/^context\./, "");
                            context_data[newKey] =  mapped_data[key];
                        }
                        switch(key){
                            case 'org':
                                u.data.config_json.org = mapped_data[key];
                                break;
                            case 'aquisition_server':
                                u.data.config_json.aquisition.server = mapped_data[key];
                                break;
                            case 'aquisition_appid':
                                u.data.config_json.aquisition.appid = mapped_data[key];
                                break;
                            case 'batch_limit':
                                u.data.config_json.analytics.batchLimit = mapped_data[key];
                                break;
                            case 'char_set':
                                u.data.config_json.analytics.charset = mapped_data[key];
                                break;
                            case 'lifecycle_timeout':
                                u.data.config_json.analytics.lifecycleTimeout = mapped_data[key];
                                break;
                            case 'rsids':
                                u.data.config_json.analytics.rsids = mapped_data[key];
                                break;
                            case 'server':
                                u.data.config_json.analytics.server = mapped_data[key];
                                break;
                            case 'ssl':
                                u.data.config_json.analytics.ssl = mapped_data[key];
                                break;
                            case 'offline_enabled':
                                u.data.config_json.analytics.offlineEnabled = mapped_data[key];
                                break;
                            case 'privacy_default':
                                u.data.config_json.analytics.privacyDefault = mapped_data[key];
                                break;
                            case 'referrer_timeout':
                                u.data.config_json.analytics.referrerTimeout = mapped_data[key];
                                break;
                            case 'poi':
                                u.data.config_json.analytics.poi = mapped_data[key];
                                break;
                            case 'client_code':
                                u.data.config_json.target.clientCode = mapped_data[key];
                                break;
                            case 'target_timeout':
                                u.data.config_json.target.timeout = mapped_data[key];
                                break;
                            case 'messages':
                                var messagesArray = JSON.parse( mapped_data[key] );
                                if (Array.isArray(messagesArray)){
                                    u.data.config_json.messages = messagesArray;
                                }
                                break;
                            case 'remote_poi':
                                u.data.config_json.remotes['analytics.poi'] = mapped_data[key];
                                break;
                            case 'remote_messages':
                                u.data.config_json.remotes.messages = mapped_data[key];
                                break;

                            default:

                        }
                    };

    // enrich data layer with total lifetime value
                    if (typeof(Storage) !== "undefined" && !(/^null$|^$/i).test(localStorage.getItem("adobe_total_lifetime_value"))) {
                        b.adobe_total_lifetime_value = localStorage.getItem("adobe_total_lifetime_value") + "";
                    }


    // distance calculator
                    var distance_in_meters = function (lat1, lon1, lat2, lon2) {
                        function deg2rad(deg) {
                            return deg * (Math.PI / 180)
                        }

                        var R = 6371; // Radius of the earth in km
                        var dLat = deg2rad(lat2 - lat1);  // deg2rad below
                        var dLon = deg2rad(lon2 - lon1);
                        var a =
                            Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                            Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                            Math.sin(dLon / 2) * Math.sin(dLon / 2);
                        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                        var d = (R * c) * 1000; // Distance in meters
                        return d;
                    }


    // evaluate events ----------------------------------------------------------


    // check if lifecycle launch
                    if (b.lifecycle_type && (/launch/i).test(b.lifecycle_type)) {
                        tlmAdobe.collect_lifecycle_data();
                    }

    // check if lifecycle wake
                    if (b.lifecycle_type && (/wake/i).test(b.lifecycle_type)) {
                        // add custom launch/wake functions here

                    }

    // check if lifecycle sleep or close
                    if (b.lifecycle_type && (/sleep|terminate/i).test(b.lifecycle_type)) {
                        // add sleep/terminate functions here
                        if ((/android/i).test(b.platform) && (/sleep/i).test(b.lifecycle_type)) {
                            tlmAdobe.pause_collecting_lifecycle_data();
                        }
                        else if ((/ios/i).test(b.platform) && (/sleep/i).test(b.lifecycle_type)) {
                            tlmAdobe.keep_lifecycle_session_alive();
                        }
                    }

    // location event
                    if (mapped_data.latitude && mapped_data.longitude) {
                        tlmAdobe.track_location(mapped_data.latitude, mapped_data.longitude, mapped_data.poi, context_data);
                    }


    // view events
                    // if track_state is mapped
                    if (u.event.indexOf("track_state") >= 0) {
                        tlmAdobe.track_state(mapped_data.state_name, context_data);
                    } // if track_state not mapped fire on all views
                    else if (a == "view" && u.data.track_all_views && mapped_data.state_name) {
                        tlmAdobe.track_state(mapped_data.state_name, context_data);
                    }

    // link events
                    // if track_action is mapped
                    if (u.event.indexOf("track_action") >= 0) {
                        tlmAdobe.track_action(mapped_data.action_name, context_data);
                    } // if track_action not mapped fire on all links
                    else if (a == "link" && u.data.track_all_links && mapped_data.action_name && !b.lifecycle_type) {
                        tlmAdobe.track_action(mapped_data.action_name, context_data);
                    }

    // track lifetime value increase
                    if (mapped_data.lifetime_value) {
                        tlmAdobe.track_lifetime_value(mapped_data.lifetime_value, context_data);
                    }

// timed actions
                    if (!!mapped_data.timed_action_name) {
                        if (!!u.event["timed_action_start"]) {
                            tlmAdobe.timed_action_start(mapped_data.timed_action_name, context_data);
                        }
                        if (!!u.event["timed_action_update"]) {
                            tlmAdobe.timed_action_update(mapped_data.timed_action_name, context_data);
                        }
                        if (!!u.event["timed_action_end"]) {
                            tlmAdobe.timed_action_end(mapped_data.timed_action_name, context_data);
                        }
                    }

// track beacons
                    if (!!u.event["track_beacon"] || (/^did_range_beacons$/).test(b.beacon_event))  {
                        if (!!mapped_data.beacon_uuid) {
                            tlmAdobe.track_beacon(mapped_data.beacon_uuid, context_data);
                        }
                        else {
                            tlmAdobe.track_beacon(mapped_data.beacon_uuid, null);
                        }
                    } // cleartrack beacons
                    if (!!u.event["track_beacon_clear"] || (/^clear_beacon$/).test(b.beacon_event)) {
                        tlmAdobe.track_beacon_clear();
                    }


                     macros.debug_end();
                }
            };
            utag.o[loader].loader.LOAD(id);
        } catch (error) {
            utag.DB(error);
        }
    };


    var init_macros = {
        "utid": "##UTID##",
        "utloaderid": "##UTLOADERID##",
        "debug": "##UTVARconfig_debug##",
        "track_all_views": "##UTVARconfig_trackviews##",
        "track_all_links": "##UTVARconfig_tracklinks##",
        "org": "##UTVARconfig_org##",
        "aquisition_server": "##UTVARconfig_aquisition_server##",
        "aquisition_appid": "##UTVARconfig_aquisition_appid##",
        "batch_limit": "##UTVARconfig_batch_limit##",
        "char_set": "##UTVARconfig_char_set##",
        "lifecycle_timeout": "##UTVARconfig_lifecycle_timeout##",
        "rsids": "##UTVARconfig_rsids##",
        "server": "##UTVARconfig_server##",
        "ssl": "##UTVARconfig_ssl##",
        "offline_enabled": "##UTVARconfig_offline_enabled##",
        "privacy_default": "##UTVARconfig_privacy_default##",
        "referrer_timeout": "##UTVARconfig_referrer_timeout##",
        "client_code": "##UTVARconfig_client_code##",
        "target_timeout": "##UTVARconfig_target_timeout##",
        "remote_poi": "##UTVARconfig_remote_poi##",
        "remote_messages": "##UTVARconfig_remote_messages##",
        "debug_send": function () {
            //##UTENABLEDEBUG##utag.DB("send:##UTID##");
        },
        "debug_end": function(){
            //##UTENABLEDEBUG##utag.DB("send:##UTID##:COMPLETE");
        },
        "utgen": function () {
            u = utag.o[this.utloaderid].sender[this.utid];
            //##UTGEN##
        },
        "extend": function () {
            u = utag.o[this.utloaderid].sender[this.utid];
            //##UTEXTEND##
        }
    };
    if (utag.test){
        for(var key in utag.test.macros){
            var value = utag.test.macros[key];
            init_macros[key] = value;
        }
    }
    utag.macros = utag.macros || {};
    utag.macros[init_macros.utid] = utag.macros[init_macros.utid] || {};
    utag.macros[init_macros.utid][init_macros.utloaderid] = init_macros;
    template(init_macros.utid, init_macros.utloaderid);

})();

