package com.mapex.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mapex.data.local.entity.PaisPaginacionEntity

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<PaisPaginacionEntity>)

    @Query("SELECT * FROM pais_paginacion WHERE id_contries = :id")
    suspend fun getRemoteKeysId(id: String): PaisPaginacionEntity?

    @Query("DELETE FROM pais_paginacion")
    suspend fun clearRemoteKeys()
}
