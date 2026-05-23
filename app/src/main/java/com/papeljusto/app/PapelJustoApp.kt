package com.papeljusto.app

import android.app.Application

class PapelJustoApp : Application()
{
    lateinit var container: AppContainer
        private set

    override fun onCreate()
    {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}
