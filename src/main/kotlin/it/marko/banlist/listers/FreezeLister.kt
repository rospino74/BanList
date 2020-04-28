/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (FreezeLister.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.listers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import it.marko.freezer.Freezer
import org.bukkit.Bukkit

/**
 * Classe che permette di avere in un formato diverso tutti player freezati
 */
internal class FreezeLister {
    private var f = Bukkit.getPluginManager().getPlugin("Freezer") as Freezer

    /**
     * Fornisce una lista in [JsonArray] contenente i dati di tutti player freezati
     *
     * @return Ritorna un [array][JsonArray] di [oggetti json][JsonObject]
     */
    fun getJSON(): JsonArray {
        val out = JsonArray()

        val players = f.getFreezedPlayers()

        return out
    }
}