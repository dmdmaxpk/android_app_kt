package com.dmdmax.goonj.models

import java.io.Serializable

class Paywall: Serializable {

    lateinit var id: String;
    lateinit var name: String;
    lateinit var slug: String;
    lateinit var desc: String;
    lateinit var packages: ArrayList<PackageModel>;
    var active: Boolean = false;

    var mSelectedPackage: PackageModel? = null;

    constructor(){

    }

    constructor(id: String, name: String, slug: String){
        this.id = id;
        this.name = name;
        this.slug = slug;
    }
}