utag.init_mobile = {
    "version": {"version": "1.3.1"}, "notes": "added serial queue", "init": function () {
        utag.mobile = this.version;
        utag.mobile.remote_api = {
            ready: true, response: {}, queue: new Array(), api_builder: function () {
                return {
                    init: function (command_id, name, u) {
                        name = name + "_" + new Date().getTime() + +Math.floor((Math.random() * 2000) + 1);
                        if (!u.data[command_id]) {
                            u.data[command_id] = {}
                        }
                        u.data[command_id].base_url = "tealium://" + command_id + "?request=";
                        u.data[command_id][name] = {};
                        u.data[command_id][name]["config"] = {};
                        u.data[command_id][name]["payload"] = {};
                        this.command_id = command_id;
                        if (command_id == "_http") {
                            this.url = function (url) {
                                u.data[command_id][name]["payload"]["url"] = url;
                                return this;
                            };
                            this.method = function (method) {
                                this.method = method;
                                return this;
                            };
                            this.authenticate = function (username, password) {
                                u.data[command_id][name]["payload"]["authenticate"] = {
                                    "username": username,
                                    "password": password
                                }
                                return this;
                            };
                            this.add_header = function (key_or_object, value) {
                                add_object("headers", key_or_object, value);
                                return this;
                            };
                            this.add_parameter = function (key_or_object, value) {
                                add_object("parameters", key_or_object, value);
                                return this;
                            };
                            var add_object = function (type, key_or_object, value) {
                                if (typeof key_or_object == "object") {
                                    if (!u.data[command_id][name]["payload"][type]) {
                                        u.data[command_id][name]["payload"][type] = key_or_object;
                                    } else {
                                        for (var key in key_or_object) {
                                            u.data[command_id][name]["payload"][type][key] = key_or_object[key];
                                        }
                                    }
                                } else {
                                    if (!u.data[command_id][name]["payload"][type]) {
                                        u.data[command_id][name]["payload"][type] = {}
                                    }
                                    u.data[command_id][name]["payload"][type][key_or_object] = value;
                                }
                            };
                        }
                        if (command_id == "_push") {
                            this.project_number = function (project_number) {
                                u.data[command_id][name]["payload"]["project_number"] = project_number;
                                return this;
                            }
                        }
                        if (command_id == "_http" || command_id == "_push") {
                            this.add_body = function (string_key_or_object, value) {
                                if (typeof string_key_or_object == "string" && !value) {
                                    u.data[command_id][name]["payload"]["body"] = string_key_or_object;
                                } else if (typeof string_key_or_object == "object") {
                                    add_object("body", string_key_or_object);
                                } else {
                                    if (!u.data[command_id][name]["payload"]["body"])u.data[command_id][name]["payload"]["body"] = {};
                                    u.data[command_id][name]["payload"]["body"][string_key_or_object] = value;
                                }
                                return this;
                            };
                        }
                        this.callback = function (callback) {
                            this.callback_function = callback;
                            return this;
                        };
                        this.response_id = function (response_id) {
                            u.data[command_id][name]["config"]["response_id"] = (response_id) ? name + "_" + response_id : name;
                            u.data[command_id][name]["response_id_set"] = function () {
                                u.data[command_id][name]["config"]["response_id"] = name;
                            };
                            return this;
                        };
                        this.trigger = function () {
                            this.response_id();
                            if (u.data[command_id][name].response_id_set) {
                                u.data[command_id][name].response_id_set();
                            }
                            if (u.data[command_id][name]["payload"]["body"] && typeof u.data[command_id][name]["payload"]["body"] == "object" && u.data[command_id][name]["payload"]["headers"] && u.data[command_id][name]["payload"]["headers"]["Content-Type"] && u.data[command_id][name]["payload"]["headers"]["Content-Type"] != "application/x-www-form-urlencoded") {
                                u.data[command_id][name]["payload"]["body"] = JSON.stringify(u.data[command_id][name]["payload"]["body"]);
                            }
                            if (!utag.mobile.remote_api.response[command_id]) {
                                utag.mobile.remote_api.response[command_id] = {}
                            }
                            if (this.payload && this.custom == "module") {
                                u.data[command_id][name]["payload"] = this.payload;
                            }
                            if (this.payload && this.custom == "command") {
                                for (var argument in this.payload) {
                                    u.data[command_id][name]["payload"][argument] = this.payload[argument];
                                }
                            }
                            if (this.method) {
                                u.data[command_id][name]["payload"]["method"] = this.method;
                            }
                            var remote_api = utag.mobile.remote_api;
                            var response_id = u.data[command_id][name].config.response_id;
                            remote_api.response[command_id][response_id] = function (code, body) {
                                if (remote_api.queue.length > 0) {
                                    var next_call = remote_api.queue.shift();
                                    next_call();
                                    if (remote_api.queue.length < 1) {
                                        remote_api.ready = true;
                                    }
                                }
                                else {
                                    remote_api.ready = true;
                                }
                            };
                            if (this.callback_function) {
                                var cf = this.callback_function;
                                remote_api.response[command_id][response_id] = function (code, body) {
                                    try {
                                        cf(code, body);
                                    }
                                    catch (e) {
                                    }
                                    if (remote_api.queue.length > 0) {
                                        var next_call = remote_api.queue.shift();
                                        next_call();
                                        if (remote_api.queue.length < 1) {
                                            remote_api.ready = true;
                                        }
                                    }
                                    else {
                                        remote_api.ready = true;
                                    }
                                }
                            }
                            if (remote_api.ready) {
                                remote_api.ready = false;
                                var current_call = u.data[command_id].base_url + encodeURIComponent(JSON.stringify(u.data[command_id][name]));
                                //setTimeout(function () {
                                    window.open(current_call, "_self");
                                //}, 100 + Math.floor((Math.random() * 200) + 1));
                            }
                            else {
                                var queued_command = u.data[command_id].base_url + encodeURIComponent(JSON.stringify(u.data[command_id][name]));
                                remote_api.queue.push(function () {
                                    window.open(queued_command, "_self");
                                });
                            }
                        }
                        return this;
                    }, add_argument: function (argument, value) {
                        if (!value) {
                            value = ""
                        }
                        if (this.custom && this.custom == "module") {
                            if (!this.payload) {
                                this.payload = {"arguments": {}};
                            }
                            this.payload["arguments"][argument] = value;
                        }
                        else if (this.custom && this.custom == "command") {
                            if (!this.payload) {
                                this.payload = {};
                            }
                            this.payload[argument] = value;
                        }
                        else {
                            this[argument] = value;
                        }
                        return this;
                    }, is_custom_module: function () {
                        this.custom = "module";
                        this.method = function (method_name) {
                            if (method_name) {
                                this.method = method_name;
                            }
                            else {
                                this.method = function (method_name) {
                                    this.method = method_name;
                                }
                            }
                            return this;
                        }
                        if (this.payload) {
                            delete this.payload;
                        }
                        return this;
                    }, is_custom_command: function () {
                        this.custom = "command";
                        if (this.payload) {
                            delete this.payload;
                        }
                        return this;
                    }
                }
            }
        };
    }
}
if (!utag.mobile || (utag.mobile && !utag.mobile.version)) {
    utag.init_mobile.init();
}
else {
    var old_mobile_version = utag.mobile.version.split(".");
    var new_mobile_version = utag.init_mobile.version.version.split(".");
    var oa = parseInt(old_mobile_version[0]);
    var na = parseInt(new_mobile_version[0]);
    var ob = parseInt(old_mobile_version[1]);
    var nb = parseInt(new_mobile_version[1]);
    var oc = parseInt(old_mobile_version[2]);
    var nc = parseInt(new_mobile_version[2]);
    if (na > oa || (na == oa && nb > ob) || (na == oa && nb == ob && nc > oc)) {
        utag.init_mobile.init();
    }
}