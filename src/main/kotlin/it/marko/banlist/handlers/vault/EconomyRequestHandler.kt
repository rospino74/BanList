/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (EconomyRequestHandler.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.handlers.vault

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import it.marko.banlist.BanList
import it.marko.banlist.handlers.RequestHandler
import it.marko.banlist.listers.vault.EconomyLister
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit.getServer
import org.bukkit.plugin.java.JavaPlugin
import java.io.OutputStream

class EconomyRequestHandler : RequestHandler() {
    //economy
    var e = getServer().servicesManager.getRegistration(Economy::class.java)!!.provider
    //istanza di Main
    private val main: JavaPlugin = BanList.getInstance()

    override fun handle(exchange: HttpExchange?) {
        //se exchange == null esco
        if (exchange == null)
            return

        //faccio il log
        log("Richiesta HTTP ricevuta da '${exchange.remoteAddress}', per il percorso '${exchange.requestURI}'")

        //oggetto di out
        val out = JsonObject()

        //lister
        val lister = EconomyLister(e)

        //mostro i bilanci degli utenti se attivati
        if (main.config.getBoolean("show.economy.balances")) {
            val balances = lister.getJSON(EconomyLister.Type.BALANCES)
            //aggiungo all'output
            out.add("balances", balances)
        }

        //mostro i bilanci delle banche se attivati
        if (main.config.getBoolean("show.economy.banks") && e.hasBankSupport()) {
            val banks = lister.getJSON(EconomyLister.Type.BANKS)
            //aggiungo all'output
            out.add("banks", banks)
        }

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