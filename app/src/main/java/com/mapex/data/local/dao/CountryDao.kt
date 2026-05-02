package com.mapex.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.mapex.data.local.entity.ContriesEntity
import com.mapex.data.local.entity.PaisDetalleEntity
import kotlinx.coroutines.flow.Flow

import androidx.paging.PagingSource

@Dao
interface CountryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<ContriesEntity>)

    @Query("SELECT * FROM contries ORDER BY nombre_comun ASC")
    fun getAllCountries(): Flow<List<ContriesEntity>>

    @Query("SELECT * FROM contries ORDER BY nombre_comun ASC")
    fun getPagedCountries(): PagingSource<Int, ContriesEntity>

    @Query("SELECT * FROM contries WHERE nombre_comun LIKE :query OR id_contries LIKE :query ORDER BY nombre_comun ASC")
    fun searchPagedCountries(query: String): PagingSource<Int, ContriesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountryDetail(detail: PaisDetalleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountryDetails(details: List<PaisDetalleEntity>)

    @Transaction
    @Query("SELECT * FROM contries WHERE id_contries = :id")
    suspend fun getCountryDetail(id: String): ContriesWithDetail?

    @Transaction
    @Query("SELECT * FROM contries")
    suspend fun getAllCountriesWithDetail(): List<ContriesWithDetail>

    @Transaction
    suspend fun insertAllWithDetails(countries: List<ContriesEntity>, details: List<PaisDetalleEntity>) {
        insertCountries(countries)
        insertCountryDetails(details)
    }

    @Query("DELETE FROM contries")
    suspend fun clearAllCountries()
}

data class ContriesWithDetail(
    @Embedded val country: ContriesEntity,
    @Relation(
        parentColumn = "id_contries",
        entityColumn = "id_contries"
    )
    val detail: PaisDetalleEntity?
)
