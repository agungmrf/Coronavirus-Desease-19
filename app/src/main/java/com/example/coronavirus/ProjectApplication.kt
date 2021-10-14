package com.example.coronavirus

import android.app.Application
import android.content.Context
import com.example.coronavirus.di.AppComponent
import com.example.coronavirus.di.AppModule
import com.example.coronavirus.di.NetworkModule
import com.example.coronavirus.utils.LocalizationUtil
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class ProjectApplication : Application() {
    companion object {
        lateinit var instance : ProjectApplication
            private set
    }
    lateinit var appComponent : AppComponent

    override fun onCreate() {
        super.onCreate()
        instance = this

        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .networkModule(NetworkModule("https://api.covid19api.com/"))
            .build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(
            LocalizationUtil.wrapContext(base!!)
        )
    }
}