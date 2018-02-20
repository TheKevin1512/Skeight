package com.kevindom.skeight

import android.app.Application
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.lazy
import com.kevindom.skeight.di.DefaultDependencyInjector

class App : Application(), KodeinAware {

    override val kodein: Kodein by Kodein.lazy {
        extend(DefaultDependencyInjector().create(this@App), allowOverride = true)
    }
}