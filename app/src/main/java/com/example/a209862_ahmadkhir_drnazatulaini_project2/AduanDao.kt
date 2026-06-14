@file:Suppress("AndroidUnresolvedRoomSqlReference")

package com.example.a209862_ahmadkhir_drnazatulaini_project2

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ─────────────────────────────────────────────────────────────────────────────
// DAO  –  Data Access Object
// (Lab 5, Part 2 – Required Component 2: DAO Interface)
//
//  • insert()      – add a new aduan row
//  • getAll()      – observe ALL rows as a Flow (auto-updates UI on change)
//  • update()      – update status (e.g. mark as Selesai)
//  • delete()      – remove a single aduan
//  • deleteAll()   – wipe the table (useful for tests / reset)
// ─────────────────────────────────────────────────────────────────────────────
@Dao
interface AduanDao {

    /** Insert a new aduan.  OnConflict REPLACE so editing works too. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(aduan: AduanEntity)

    /** Return all aduan rows as a cold Flow ordered newest-first. */
    @Query("SELECT * FROM aduan_table ORDER BY id DESC")
    fun getAll(): Flow<List<AduanEntity>>

    /** Update an existing row (e.g. change status to "Selesai"). */
    @Update
    suspend fun update(aduan: AduanEntity)

    /** Delete a single row. */
    @Delete
    suspend fun delete(aduan: AduanEntity)

    /** Convenience query: mark one row as "Selesai" by its id. */
    @Query("UPDATE aduan_table SET status = 'Selesai' WHERE id = :id")
    suspend fun resolveById(id: Int)

    /** Drop all rows (useful for testing or full reset). */
    @Query("DELETE FROM aduan_table")
    suspend fun deleteAll()
}
