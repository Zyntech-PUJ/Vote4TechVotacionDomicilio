package com.votacion.domicilio.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.votacion.domicilio.data.local.dao.*
import com.votacion.domicilio.data.local.entity.*

@Database(
    entities = [
        VotoDraftEntity::class,
        VotoLocalEntity::class,
        EleccionLocalEntity::class,
        CandidatoLocalEntity::class,
        CiudadanoLocalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DomicilioDatabase : RoomDatabase() {

    abstract fun votoDraftDao(): VotoDraftDao
    abstract fun votoLocalDao(): VotoLocalDao
    abstract fun eleccionDao(): EleccionDao
    abstract fun candidatoDao(): CandidatoDao
    abstract fun ciudadanoLocalDao(): CiudadanoLocalDao

    companion object {
        @Volatile private var INSTANCE: DomicilioDatabase? = null

        fun getInstance(context: Context): DomicilioDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DomicilioDatabase::class.java,
                    "domicilio_db"
                ).build().also { INSTANCE = it }
            }
    }
}
