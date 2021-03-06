/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (BanRequestHandler.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.handlers

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import it.marko.banlist.listers.BanLister
import it.marko.banlist.server.RequestHandler
import org.bukkit.BanList
import org.bukkit.plugin.java.JavaPlugin

/**
 * Classe per gestire le richieste verso l'url definito in `output.path.ban`
 */
internal class BanRequestHandler : RequestHandler() {
    //istanza di BanList
    private val main: JavaPlugin = it.marko.banlist.BanList.getInstance()

    override fun onIncomingRequest(exchange: HttpExchange) {
        //creo il lister
        val banList = BanLister()
        val out = JsonObject()

        //mostrare i ban per ip?

        val showIP = main.config.getBoolean("show.ban.byIP", true)
        val showNAME = main.config.getBoolean("show.ban.byNAME", true)


        //json di ban per ip e nomi
        if (showNAME) {
            val responseByNAME = banList.getJSON(BanList.Type.NAME)
            out.add("byNAME", responseByNAME)
        }
        if (showIP) {
            val responseByIP = banList.getJSON(BanList.Type.IP)
            out.add("byIP", responseByIP)
        }

        //invio i dati
        flushData(out.toString())
    }
}