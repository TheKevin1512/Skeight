package com.kevindom.skeight.di

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kevindom.skeight.App
import com.kevindom.skeight.firebase.*
import com.kevindom.skeight.storage.Preferences

open class DefaultDependencyInjector {

    private lateinit var kodein: Kodein

    fun create(app: App): Kodein {
        kodein = Kodein {

            bind<Context>("appContext") with instance(app)

            bind<Preferences>() with singleton { Preferences(context = instance("appContext")) }

            bind<MessageManager>() with singleton { MessageManager(context = instance("appContext")) }

            bind<UserManager>() with singleton { UserManager() }

            bind<RoomManager>() with singleton { RoomManager(context = instance("appContext")) }

            bind<Gson>() with singleton { GsonBuilder().setPrettyPrinting().create() }

            bind<StorageManager>() with singleton { StorageManager() }

            bind<AnalyticsManager>() with singleton { AnalyticsManager(context = instance("appContext")) }
        }
        return kodein
    }
}