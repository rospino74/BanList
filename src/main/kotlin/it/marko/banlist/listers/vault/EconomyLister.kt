/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (EconomyLister.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.listers.vault

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit.getServer

internal class EconomyLister(private val economy: Economy) {
    enum class Type {
        BALANCES,
        BANKS
    }

    fun getJSON(type: Type): JsonArray {
        return when (type) {
            Type.BALANCES -> listBalances()
            Type.BANKS -> listBanks()
        }
    }

    private fun listBalances() : JsonArray {
        //array di output
        val out = JsonArray()

        getServer().offlinePlayers.forEach {
            //oggetto di out
            val balance = JsonObject()

            //imposto il nome
            balance.addProperty("name", it.name)

            //imposto cifra
            balance.addProperty("balance", economy.getBalance(it))

            //appendo ad out
            out.add(balance)
        }

        //ritorno la lista
        return out
    }

    private fun listBanks() : JsonArray {
        //array di output
        val out = JsonArray()

        economy.banks.forEach {
            //oggetto di out
            val bank = JsonObject()

            //imposto il nome
            bank.addProperty("name", it)

            //imposto cifra
            bank.addProperty("balance", economy.bankBalance(it).balance)

            //appendo ad out
            out.add(bank)
        }

        //ritorno la lista
        return out
    }
}