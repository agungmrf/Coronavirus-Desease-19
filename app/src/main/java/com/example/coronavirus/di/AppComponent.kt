package com.example.coronavirus.di

import com.example.coronavirus.features.onboarding.viewmodel.OnboardingViewModel
import com.example.coronavirus.features.main.viewmodel.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {
    fun inject(viewmodel: MainViewModel)
    fun inject(viewmodel: OnboardingViewModel)
}