package com.mapex.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mapex.data.local.dao.CountryDao
import com.mapex.data.local.dao.RemoteKeysDao
import com.mapex.data.local.entity.ContriesEntity
import com.mapex.data.local.entity.PaisDetalleEntity
import com.mapex.data.local.entity.PaisPaginacionEntity

@Database(
    entities = [
        ContriesEntity::class,
        PaisDetalleEntity::class,
        PaisPaginacionEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mapex_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
