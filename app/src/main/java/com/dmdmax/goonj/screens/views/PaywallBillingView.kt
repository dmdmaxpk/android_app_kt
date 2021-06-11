package com.dmdmax.goonj.screens.views

interface PaywallBillingView {

    fun fetchPackages();
    fun processBilling(source: String);

}