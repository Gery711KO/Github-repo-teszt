package com.gery711k.yettelteszt

import android.app.Application
import com.gery711k.yettelteszt.data.di.dataSourceModule
import com.gery711k.yettelteszt.data.di.networkModule
import com.gery711k.yettelteszt.data.di.repositoryModule
import com.gery711k.yettelteszt.domain.di.useCaseModule
import com.gery711k.yettelteszt.ui.di.viewModelModule
import com.gery711k.yettelteszt.ui.di.navigationModule
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
                useCaseModule,
                viewModelModule,
                navigationModule,
            )
        }

        Timber.plant(Timber.DebugTree())
    }
}