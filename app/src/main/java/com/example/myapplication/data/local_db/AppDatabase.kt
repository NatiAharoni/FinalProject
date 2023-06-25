package com.example.myapplication.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.models.Movie
// Setting up a Room database with a single table represented by the Movie entity.
// It provides a singleton pattern to ensure that only one instance of the database exists
// throughout the application's lifecycle.
@Database(entities = [Movie::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract fun movieDao(): MovieDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "movies_db"
                )
                    .fallbackToDestructiveMigration().build().also {
                        instance = it
                    }
            }
        }
    }

}