package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.data.local_db.AppDatabase
import com.example.myapplication.data.remote_db.MovieService
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.firebaseImpl.AuthRepositoryFirebase
import com.example.myapplication.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
// The module that Hilt can use to inject dependencies from external services and libraries
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson) : Retrofit {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }

    @Provides
    fun provideGson() : Gson = GsonBuilder().create()

    @Provides
    fun provideMovieService(retrofit: Retrofit) : MovieService =
        retrofit.create(MovieService::class.java)

    @Provides
    @Singleton
    fun provideLocalDataBase(@ApplicationContext appContext:Context) : AppDatabase =
        AppDatabase.getDatabase(appContext)

    @Provides
    @Singleton
    fun provideMovieDao(database: AppDatabase) = database.movieDao()

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryFirebase()
    }
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

//    @Provides
//    @Singleton
//    fun provideFirebaseFirestore(): FirebaseFirestore {
//        return FirebaseFirestore.getInstance()
//    }
}