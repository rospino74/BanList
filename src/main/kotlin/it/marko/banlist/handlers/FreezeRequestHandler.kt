/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (FreezeRequestHandler.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.handlers

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import it.marko.banlist.listers.FreezeLister
import java.io.OutputStream

/**
 * Classe per gestire le richieste verso l'url definito in `output.path.freeze`
 */
internal class FreezeRequestHandler : RequestHandler() {
    override fun onIncomingRequest(exchange: HttpExchange) {
        //creo il lister
        val freezeList = FreezeLister()

        //slvo in un oggetto JSON
        val out = JsonObject()
        out.add("freeze", freezeList.getJSON())


        flushData(out.toString())
    }
}