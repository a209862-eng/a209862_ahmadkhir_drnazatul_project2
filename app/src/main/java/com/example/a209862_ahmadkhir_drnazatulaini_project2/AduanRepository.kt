package com.example.a209862_ahmadkhir_drnazatulaini_project2

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ─────────────────────────────────────────────────────────────────────────────
// REPOSITORY
// (Lab 5, Part 2 – Required Component 4: Repository connecting ViewModel ↔ DAO)
//
//  The repository is the single source of truth for aduan data.
//  It hides the Room implementation details from the ViewModel so the
//  ViewModel never imports Room directly.
// ─────────────────────────────────────────────────────────────────────────────
class AduanRepository(private val dao: AduanDao) {

    /**
     * All aduan as a Flow<List<AduanData>>.
     * Room emits a new list every time a row is inserted, updated, or deleted.
     * The ViewModel collects this Flow via StateFlow so the UI reacts instantly.
     */
    val allAduan: Flow<List<AduanData>> =
        dao.getAll().map { entities -> entities.map { it.toAduanData() } }

    /** Persist a new aduan (must be called from a coroutine / suspend context). */
    suspend fun insert(aduan: AduanData) {
        dao.insert(aduan.toEntity())
    }

    /** Persist a status change (e.g. "Selesai"). */
    suspend fun update(aduan: AduanData) {
        dao.update(aduan.toEntity())
    }

    /** Mark one aduan as Selesai by its primary key. */
    suspend fun resolveById(id: Int) {
        dao.resolveById(id)
    }

    /** Remove a single aduan permanently. */
    suspend fun delete(aduan: AduanData) {
        dao.delete(aduan.toEntity())
    }
}
