package com.vote4tech.domicilio2.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vote4tech.domicilio2.data.local.dao.CandidatoDao
import com.vote4tech.domicilio2.data.local.dao.CiudadanoLocalDao
import com.vote4tech.domicilio2.data.local.dao.EleccionDao
import com.vote4tech.domicilio2.data.local.dao.VotoLocalDao
import com.vote4tech.domicilio2.data.local.entity.CandidatoLocalEntity
import com.vote4tech.domicilio2.data.local.entity.CiudadanoLocalEntity
import com.vote4tech.domicilio2.data.local.entity.EleccionLocalEntity
import com.vote4tech.domicilio2.data.local.entity.VotoLocalEntity

@Database(
    entities = [
        EleccionLocalEntity::class,
        CandidatoLocalEntity::class,
        CiudadanoLocalEntity::class,
        VotoLocalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DomicilioDatabase : RoomDatabase() {
    abstract fun eleccionDao(): EleccionDao
    abstract fun candidatoDao(): CandidatoDao
    abstract fun ciudadanoLocalDao(): CiudadanoLocalDao
    abstract fun votoLocalDao(): VotoLocalDao

    companion object {
        @Volatile private var INSTANCE: DomicilioDatabase? = null

        fun getInstance(context: Context): DomicilioDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, DomicilioDatabase::class.java, "domicilio_db")
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
