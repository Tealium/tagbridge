utag = new (function(){
    this.o = {
        1 : {
            "sender" : {
            },
            "loader" : {
                "LOAD" : function(){}
            }
        }
    };
    this.DB = function(){};
    this.test = {
        macros : {
            "utid": 1,
            "utloaderid": 1,
            "debug": 1,
            "track_all_views": false,
            "track_all_links": false,
            "org": "016D5C175213CCA80A490D05@AdobeOrg",
            "aquisition_server": "c00.adobe.com",
            "aquisition_appid": "10a77a60192fbb628376e1b1daeeb65debf934e2c807e067ceb2963a41b165ee",
            "batch_limit": "0",
            "char_set": "UTF-8",
            "lifecycle_timeout": "300",
            "rsids": "coolApp",
            "server": "my.CoolApp.com",
            "ssl": "false",
            "offline_enabled": "true",
            "privacy_default": "optedin",
            "referrer_timeout": "5",
            "client_code": "abc",
            "target_timeout": "5",
            "remote_poi": "https://assets.adobedtm.com/staging/42a6fc9b77cd9f29082cf19b787bae75b7d1f9ca/scripts/satellite-53e0faadc2f9ed92bc00003b.json",
            "remote_messages": "https://assets.adobedtm.com/staging/42a6fc9b77cd9f29082cf19b787bae75b7d1f9ca/scripts/satellite-53e0f9e2c2f9ed92bc000032.json",
            "utgen": function () {},
            "debug_send": function () {},
            "debug_end": function () {},
            "extend": function () {}
        }
    };
    this.loader = {
        GV : function(map){return map}
    };
})();

