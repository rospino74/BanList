/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (JailedLister.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.listers.essentials

import com.earth2me.essentials.Essentials
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Bukkit.getServer

internal class JailedLister {
    //istanza di essentials
    private val essentials = Bukkit.getPluginManager().getPlugin("Essentials") as Essentials

    enum class Type {
        JAILED,
        JAILS
    }

    /**
     * Fornisce una lista in [JsonArray] contenente i dati della lista mutati
     *
     * @param type Cosa devo ritornare?
     * @return Ritorna un [array][JsonArray] di [oggetti json][JsonObject]
     */
    fun getJSON(type: Type): JsonArray {
        return when(type) {
            Type.JAILED -> getJailed()
            Type.JAILS -> getJails()
        }
    }

    private fun getJailed(): JsonArray {
        val out = JsonArray()

        getServer().offlinePlayers.forEach {
            val user = essentials.getOfflineUser(it.name)

            //se l'utente non è in cella continuo
            if (!user.isJailed) return@forEach

            //oggetto json
            val obj = JsonObject()

            //nome del player
            obj.addProperty("name", it.name)

            //se timeout == 0 imposto forever a true
            val timeout = user.jailTimeout
            obj.addProperty("until", timeout)
            obj.addProperty("forever", timeout == 0L)

            //nome della cella
            obj.addProperty("jail", user.jail)

            //appendo alla lista
            out.add(obj)
        }

        //ritorno la lista
        return out
    }

    private fun getJails(): JsonArray {
        val out = JsonArray()

        essentials.jails.list

        essentials.jails.list.forEach {
            val jail = essentials.jails.getJail(it)

            //oggetto json
            val obj = JsonObject()

            //nome del player
            obj.addProperty("name", it)

            //chiave location
            val location = JsonObject()

            //Coordinata x
            location.addProperty("x", jail.x)

            //Coordinata y
            location.addProperty("y", jail.y)

            //Coordinata z
            location.addProperty("z", jail.z)

            //mondo
            location.addProperty("world", jail.world!!.name)

            //aggiungo a obj
            obj.add("location", location)

            //appendo alla lista
            out.add(obj)
        }

        //ritorno la lista
        return out
    }
}
