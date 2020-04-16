/*
 * Copyright (c) 2020MemoryOfLife
 * This file (BanLister.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.bukkit.BanList
import org.bukkit.Bukkit

class BanLister {
    fun getJSON(type: BanList.Type): JsonArray {
        val out = JsonArray()
        val banList = Bukkit.getBanList(type).banEntries

        //itero banList
        banList.forEach { e ->
            run {
                val obj = JsonObject()

                //nome
                obj.addProperty("name", e.target)

                //se expiration == null imposto forever a true
                if (e.expiration == null) {
                    obj.addProperty("until", 0)
                    obj.addProperty("forever", true)
                } else {
                    obj.addProperty("until", e.expiration!!.time)
                    obj.addProperty("forever", false)
                }

                //data di creazione
                obj.addProperty("created", e.created.time)

                //nome admin
                when (e.source) {
                    "Plugin", "Server", "Console" -> obj.addProperty("admin", "Server")
                    else -> obj.addProperty("admin", e.source)
                }

                //motivo del ban
                obj.addProperty("reason", e.reason)

                //aggiungo alla lista
                out.add(obj)
            }
        }

        return out
    }
}