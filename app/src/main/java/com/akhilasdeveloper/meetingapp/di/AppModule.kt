package com.akhilasdeveloper.meetingapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.akhilasdeveloper.meetingapp.ColorDatas
import com.akhilasdeveloper.meetingapp.GenerateMeetingRooms
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun provideColorDatas(@ApplicationContext context: Context): ColorDatas {
        return ColorDatas(context)
    }

    @Singleton
    @Provides
    fun provideGenerateMeetingRooms(@ApplicationContext context: Context, gson: Gson): GenerateMeetingRooms {
        return GenerateMeetingRooms(context,gson)
    }

}
