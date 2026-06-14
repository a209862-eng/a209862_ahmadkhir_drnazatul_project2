package com.example.a209862_ahmadkhir_drnazatulaini_project2

// ─────────────────────────────────────────────────────────────────────────────
// DATA MODEL  –  single source of truth held inside UserViewModel
// ─────────────────────────────────────────────────────────────────────────────

data class UserData(
    val userName  : String = "GUEST USER",
    val userEmail : String = ""
)

// Aduan (complaint) item stored in ViewModel state
data class AduanData(
    val id       : Int,
    val title    : String,
    val location : String,
    val status   : String = "Dalam Siasatan",
    val date     : String,
    val isMine   : Boolean = true
)