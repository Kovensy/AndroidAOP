package com.flyjingfish.android_aop_plugin.beans

data class ModifyExtendsClassJson(
    val type :String,
    val targetClassName: String,
    val modifyClassName: String,
    var used: String
)