package ru.practicum.sprint12koh5

import android.app.Application
import java.lang.Exception

class Sprint12Application : Application() {

    lateinit var cart: Cart

    override fun onCreate() {
        super.onCreate()
        application = this
        cart = Cart(this)
    }

    companion object {
        lateinit var application: Sprint12Application
    }

}