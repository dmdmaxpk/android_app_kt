package com.dmdmax.goonj.models

import java.io.Serializable

class PackageModel: Serializable {
    lateinit var id: String;
    lateinit var name: String;
    lateinit var price: String;
    lateinit var text: String;
    lateinit var slug: String
    lateinit var desc: String
    lateinit var paywallId: String
    lateinit var serviceId: String
    var default: Boolean = false;

    constructor(){

    }

    constructor(id: String, name: String, price: String, desc: String, slug: String, default: Boolean, serviceId: String){
        this.id = id;
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.text = desc;
        this.slug = slug;
        this.default = default;
        this.serviceId = serviceId;
    }
}