/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (RequestHandler.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import org.bukkit.BanList
import java.io.OutputStream

class RequestHandler : HttpHandler {
    override fun handle(exchange: HttpExchange?) {
        val banList = BanLister()
        val out = JsonObject()

        //mostrare i ban per ip?
        val showIP = Main.getInstance().config.getBoolean("show.byIP", true)
        val showNAME = Main.getInstance().config.getBoolean("show.byNAME", true)

        //json di ban per ip e nomi
        if (showNAME) {
            val responseByNAME = banList.getJSON(BanList.Type.NAME)
            out.add("byNAME", responseByNAME)
        }
        if (showIP) {
            val responseByIP = banList.getJSON(BanList.Type.IP)
            out.add("byIP", responseByIP)
        }


        //imposto la risposta
        exchange?.responseHeaders?.set("Content-Type", "application/json; charset=UTF-8")
        exchange?.responseHeaders?.set("Access-Control-Allow-Origin", "*")
        exchange?.sendResponseHeaders(200, out.toString().toByteArray().size.toLong())
        val os: OutputStream? = exchange?.responseBody
        os?.write(out.toString().toByteArray())
        os?.close()
    }
}