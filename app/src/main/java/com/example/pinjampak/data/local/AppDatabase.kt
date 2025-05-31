package com.example.pinjampak.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pinjampak.data.local.dao.ProfileDao
import com.example.pinjampak.data.local.entity.CustomerProfileEntity
import com.example.pinjampak.data.local.entity.UserProfileEntity

@Database(
    entities = [UserProfileEntity::class, CustomerProfileEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}
