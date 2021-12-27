package com.android.foodieMartSeller.di.modules.app

import android.app.Application
import com.android.foodieMartSeller.network.factory.FitnessTrakarServiceFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(application: Application): Retrofit {
        return FitnessTrakarServiceFactory(application).create()
    }
}
