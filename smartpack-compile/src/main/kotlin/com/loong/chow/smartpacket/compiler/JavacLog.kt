package com.loong.chow.smartpacket.compiler

import javax.lang.model.element.Element
import javax.tools.Diagnostic

class JavacLog(val element: Element) : Log {

    override fun i(msg: String) {
        Context.messenger.printMessage(Diagnostic.Kind.NOTE, msg, element)
    }

    override fun w(msg: String) {
        Context.messenger.printMessage(Diagnostic.Kind.WARNING, msg, element)
    }

    override fun e(msg: String) {
        Context.messenger.printMessage(Diagnostic.Kind.ERROR, msg, element)
    }
}