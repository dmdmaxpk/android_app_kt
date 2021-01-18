package com.dmdmax.goonj.network.responses

class Params {
    private var key: String? = null;
    private var value: String? = null
    private var intValue = -1
    private var boolValue = false

    constructor(key: String, value: String?) {
        this.key = key
        this.value = value
    }

    constructor(key: String?, value: Int) {
        this.key = key
        intValue = value
    }

    constructor(key: String?, value: Boolean) {
        this.key = key
        boolValue = value
    }

    fun getKey(): String? {
        return key
    }

    fun getValue(): String? {
        return value
    }

    fun getIntValue(): Int {
        return intValue
    }

    fun getBoolValue(): Boolean {
        return boolValue
    }
}