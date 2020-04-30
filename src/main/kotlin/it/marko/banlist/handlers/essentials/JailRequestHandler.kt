/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (JailRequestHandler.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.handlers.essentials

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import it.marko.banlist.handlers.RequestHandler
import it.marko.banlist.listers.essentials.JailedLister
import it.marko.banlist.listers.essentials.MuteLister
import java.io.OutputStream

/**
 * Classe per gestire le richieste verso l'url definito in `output.mute.path`
 */
internal class JailRequestHandler : RequestHandler() {
    override fun onIncomingRequest(exchange: HttpExchange) {
        val jailedList = JailedLister()

        //creo il json di ritorno
        val out = JsonObject()
        out.add("jailed", jailedList.getJSON())

        flushData(out.toString())
    }
}