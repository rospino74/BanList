/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (OnlineRequestHandler.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.handlers

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import it.marko.banlist.listers.PlayerLister
import it.marko.banlist.server.RequestHandler

internal class OnlinePlayersRequestHandler : RequestHandler() {
    override fun onIncomingRequest(httpExchange: HttpExchange) {
        val out = JsonObject()

        //json di player online
        out.add("online", PlayerLister().getJSON(PlayerLister.Type.ONLINE))

        //invio i dati
        flushData(out.toString())
    }
}