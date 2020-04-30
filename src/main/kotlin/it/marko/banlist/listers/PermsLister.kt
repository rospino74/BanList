/*
 * Copyright (c) 2020 MemoryOfLife
 * This file (PermsLister.kt) and its related project (BanList) are governed by the Apache 2.0 license.
 * You may not use them except in compliance with the License which can be found at:
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package it.marko.banlist.listers

import net.milkbowl.vault.permission.Permission
import com.google.gson.JsonArray

internal class PermsLister(private val permission: Permission) {
    enum class Type {
        GROUPS,
        PERMISSIONS
    }

    fun getJSON(type: Type) : JsonArray? {
        if(type == Type.GROUPS)
            return listGroups()

        if(type == Type.PERMISSIONS)
            return listPermissions()

        return JsonArray()
    }

    private fun listPermissions(): JsonArray? {
        return null
    }

    private fun listGroups(): JsonArray {
        //array di output
        val out = JsonArray()

        //foreach per ogni gruppo
        permission.groups.forEach {
            out.add(it)
        }

        //ritorno la lista
        return out
    }
}