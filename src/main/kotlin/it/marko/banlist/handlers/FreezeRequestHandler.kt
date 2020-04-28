/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (FreezeRequestHandler.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.handlers

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import it.marko.banlist.listers.FreezeLister
import org.bukkit.BanList
import java.io.OutputStream

/**
 * Classe per gestire le richieste verso l'url definito in `output.path.freeze`
 */
internal class FreezeRequestHandler : HttpHandler {
    override fun handle(exchange: HttpExchange?) {
        val freezeList = FreezeLister()

        //slvo in un oggetto JSON
        val out = JsonObject()
        out.add("freeze", freezeList.getJSON())


        //imposto gli header per consentire le richieste AJAX
        exchange?.responseHeaders?.set("Content-Type", "application/json; charset=UTF-8")
        exchange?.responseHeaders?.set("Access-Control-Allow-Origin", "*")
        exchange?.sendResponseHeaders(200, out.toString().toByteArray().size.toLong())

        //apro l'outputstream
        val os: OutputStream? = exchange?.responseBody
        os?.write(out.toString().toByteArray())
        os?.close()
    }
}