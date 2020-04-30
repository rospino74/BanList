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
import org.bukkit.BanEntry
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getServer
import org.bukkit.entity.Player

/**
 * Classe che permette di avere in un formato diverso tutte le [BanEntry] fornite da [Bukkit.getBanList]
 */
internal class JailedLister {
    //istanza di essentials
    private val essentials = Bukkit.getPluginManager().getPlugin("Essentials") as Essentials

    /**
     * Fornisce una lista in [JsonArray] contenente i dati della lista mutati
     *
     * @return Ritorna un [array][JsonArray] di [oggetti json][JsonObject]
     */
    fun getJSON(): JsonArray {
        val out = JsonArray()

        getServer().offlinePlayers.forEach {
            val user = essentials.getOfflineUser(it.name)

            //se l'utente non è in cella continuo
            if(!user.isJailed) return@forEach

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
}