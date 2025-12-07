package com.gery711k.yettelteszt

import android.app.Application
import com.gery711k.yettelteszt.di.dataSourceModule
import com.gery711k.yettelteszt.di.networkModule
import com.gery711k.yettelteszt.di.repositoryModule
import com.gery711k.yettelteszt.di.viewModelModule
import com.gery711k.yettelteszt.ui.navigation.navigationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class DemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DemoApplication)
            modules(
                networkModule,
                dataSourceModule,
                repositoryModule,
                viewModelModule,
                navigationModule,
            )
        }

        Timber.plant(Timber.DebugTree())
    }
}