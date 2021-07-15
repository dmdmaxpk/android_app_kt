package com.dmdmax.goonj.events

class MessageEvent {
    var name: String;
    var value: Any?;

    object EventNames{
        var NETWORK_CONNECTED = "network_connected";
    }

    constructor(name: String, value: Any?){
        this.name = name;
        this.value = value;
    }
}