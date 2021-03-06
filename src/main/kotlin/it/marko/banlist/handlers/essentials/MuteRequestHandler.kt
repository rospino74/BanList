/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (MuteRequestHandler.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.handlers.essentials

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import it.marko.banlist.server.RequestHandler
import it.marko.banlist.listers.essentials.MuteLister

/**
 * Classe per gestire le richieste verso l'url definito in `output.mute.path`
 */
internal class MuteRequestHandler : RequestHandler() {
    override fun onIncomingRequest(exchange: HttpExchange) {
        //creo il lister
        val muteList = MuteLister()

        //creo il json di ritorno
        val out = JsonObject()
        out.add("mute", muteList.getJSON())

        //invio i dati
        flushData(out.toString())
    }
}