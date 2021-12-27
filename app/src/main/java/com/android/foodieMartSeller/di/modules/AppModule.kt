package com.android.foodieMartSeller.di.modules

import com.android.foodieMartSeller.di.modules.app.*
import dagger.Module

@Module(
    includes = [
        ActivityBindingModule::class,
        MainActivityBindingModule::class,
        NetworkModule::class,
        ServiceBindingModule::class,
        AppViewModelModule::class
    ]
)
class AppModule {


}