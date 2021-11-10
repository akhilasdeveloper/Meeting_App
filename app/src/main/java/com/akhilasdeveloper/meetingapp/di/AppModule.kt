package com.akhilasdeveloper.meetingapp.di

import com.google.api.client.json.jackson2.JacksonFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideJacksonBuilder(): JacksonFactory {
        return JacksonFactory.getDefaultInstance()
    }

}
