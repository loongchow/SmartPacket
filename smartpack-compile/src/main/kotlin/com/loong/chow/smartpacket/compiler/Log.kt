package com.loong.chow.smartpacket.compiler

interface Log {
    fun i(msg: String)
    fun w(msg: String)
    fun e(msg: String)
}