/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (PermsLister.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.listers

import net.milkbowl.vault.permission.Permission
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.bukkit.Bukkit.getServer

internal class PermsLister(private val permission: Permission) {
    enum class Type {
        GROUPS,
        PERMISSIONS
    }

    fun getJSON(type: Type) : JsonObject {
        if(type == Type.GROUPS)
            return listGroups()

        if(type == Type.PERMISSIONS)
            return listPermissions()

        return JsonObject()
    }

    private fun listPermissions(): JsonObject {
        return JsonObject()
    }

    private fun listGroups(): JsonObject {
        //array di output
        val out = JsonObject()

        //foreach per ogni gruppo
        permission.groups.forEach {
            //array di output pe singolo permesso
            val permOut = JsonArray()

            //ciclo per inserire i player nei gruppi
            getServer().offlinePlayers.forEach { op ->
                if(!permission.playerInGroup(null, op, it))
                    permOut.add(op.name)
            }

            //salvo nella lista di output
            out.add(it, permOut)
        }

        //ritorno la lista
        return out
    }
}