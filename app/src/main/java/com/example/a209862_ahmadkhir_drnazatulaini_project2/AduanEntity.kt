package com.example.a209862_ahmadkhir_drnazatulaini_project2  // ← must match your project package

import androidx.room.Entity
import androidx.room.PrimaryKey

// ─────────────────────────────────────────────────────────────────────────────
// ROOM ENTITY
// This file MUST exist in the same package as AduanDao.kt
// (Lab 5, Part 2 – Required Component 1: Entity)
// ─────────────────────────────────────────────────────────────────────────────
@Entity(tableName = "aduan_table")
data class AduanEntity(
    @PrimaryKey(autoGenerate = true)
    val id       : Int    = 0,
    val title    : String,
    val location : String,
    val status   : String  = "Dalam Siasatan",
    val date     : String,
    val isMine   : Boolean = true
)

// ─────────────────────────────────────────────────────────────────────────────
// EXTENSION FUNCTIONS  –  convert between Room entity and UI data class
// ─────────────────────────────────────────────────────────────────────────────

fun AduanEntity.toAduanData() = AduanData(
    id       = id,
    title    = title,
    location = location,
    status   = status,
    date     = date,
    isMine   = isMine
)

fun AduanData.toEntity() = AduanEntity(
    id       = id,
    title    = title,
    location = location,
    status   = status,
    date     = date,
    isMine   = isMine
)