/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (PermsRequestHandler.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.handlers

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import it.marko.banlist.listers.PermsLister
import it.marko.banlist.listers.PermsLister.Type
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit.getServer
import java.io.OutputStream

class PermsRequestHandler : RequestHandler() {
    //permissions
    var p = getServer().servicesManager.getRegistration(Permission::class.java)!!.provider

    override fun handle(exchange: HttpExchange?) {
        //se exchange == null esco
        if (exchange == null)
            return

        //faccio il log
        log("Richiesta HTTP ricevuta da '${exchange.remoteAddress}', per il percorso '${exchange.requestURI}'")

        //oggetto di out
        val out = JsonObject()

        //lister
        val list = PermsLister(p).getJSON(Type.GROUPS)

        //aggiungo all'output
        out.add("groups", list)

        //imposto gli header per consentire le richieste AJAX
        exchange.responseHeaders?.set("Content-Type", "application/json; charset=UTF-8")
        exchange.responseHeaders?.set("Access-Control-Allow-Origin", "*")
        exchange.sendResponseHeaders(200, out.toString().toByteArray().size.toLong())

        //apro l'outputstream
        val os: OutputStream? = exchange.responseBody
        os?.write(out.toString().toByteArray())
        os?.close()
    }
}