package com.example.pinjampak.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pinjampak.data.local.entity.CustomerProfileEntity
import com.example.pinjampak.data.local.entity.UserProfileEntity

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomerProfile(customerProfile: CustomerProfileEntity)

    @Query("SELECT * FROM user_profile WHERE username = :username")
    suspend fun getUserProfile(username: String): UserProfileEntity?

    @Query("SELECT * FROM customer_profile WHERE username = :username")
    suspend fun getCustomerProfile(username: String): CustomerProfileEntity?
}
