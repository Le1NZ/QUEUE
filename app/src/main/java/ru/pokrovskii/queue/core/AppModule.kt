package ru.pokrovskii.queue.core

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.pokrovskii.queue.data.api.UserApi
import ru.pokrovskii.queue.data.dataBase.UserDAO
import ru.pokrovskii.queue.data.dataBase.UserDataBase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideUserDataBase(app: Application): UserDataBase {
        return Room.databaseBuilder(
            app,
            UserDataBase::class.java,
            UserDataBase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserApi(): UserApi {
        return Retrofit.Builder().baseUrl(UserApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build().create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserDao(userDataBase: UserDataBase): UserDAO {
        return userDataBase.userDAO
    }

}