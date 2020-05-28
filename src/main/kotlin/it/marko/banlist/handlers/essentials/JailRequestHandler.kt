/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (JailRequestHandler.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.handlers.essentials

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import org.bukkit.plugin.Plugin
import it.marko.banlist.BanList
import it.marko.banlist.server.RequestHandler
import it.marko.banlist.listers.essentials.JailedLister

/**
 * Classe per gestire le richieste verso l'url definito in `output.path.essentials.jail`
 */
internal class JailRequestHandler : RequestHandler() {
    private val banList = BanList.getInstance() as Plugin

    override fun onIncomingRequest(exchange: HttpExchange) {
        val jailedList = JailedLister()

        //creo il json di ritorno
        val out = JsonObject()

        if(banList.config.getBoolean("show.essentials.Jail.jailed"))
            out.add("jailed", jailedList.getJSON(JailedLister.Type.JAILED))

        if(banList.config.getBoolean("show.essentials.Jail.jails"))
            out.add("jails", jailedList.getJSON(JailedLister.Type.JAILS))

        flushData(out.toString())
    }
}