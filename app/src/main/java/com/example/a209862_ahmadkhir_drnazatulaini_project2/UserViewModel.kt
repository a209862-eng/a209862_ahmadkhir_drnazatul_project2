package com.example.a209862_ahmadkhir_drnazatulaini_project2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// VIEW MODEL  –  updated for Lab 5
// (Lab 5, Part 2 – Required Component 5: ViewModel calling repository methods
//                  and exposing StateFlow to UI)
//
//  Changes vs. Lab 4:
//  • Constructor now accepts AduanRepository instead of hard-coded in-memory list
//  • aduanList is collected from repository's Flow and converted to StateFlow
//  • addAduan / resolveAduan now call suspend repository methods inside coroutines
//  • Seed data is inserted once on first launch (when the table is empty)
// ─────────────────────────────────────────────────────────────────────────────
class UserViewModel(private val repository: AduanRepository) : ViewModel() {

    // ── User profile ──────────────────────────────────────────────────────────
    private val _uiState = MutableStateFlow(UserData())
    val uiState: StateFlow<UserData> = _uiState.asStateFlow()

    fun updateUserName(name: String) {
        _uiState.update { it.copy(userName = name) }
    }

    fun updateUserEmail(email: String) {
        _uiState.update { it.copy(userEmail = email) }
    }

    // ── Reward points ─────────────────────────────────────────────────────────
    private val _rewardPoints = MutableStateFlow(1_250)
    val rewardPoints: StateFlow<Int> = _rewardPoints.asStateFlow()

    // ── Aduan list  –  sourced from Room via repository Flow ──────────────────
    /**
     * stateIn converts the cold repository Flow into a hot StateFlow.
     *  • started = WhileSubscribed(5_000) keeps the Flow alive for 5 s after
     *    the last collector disappears (survives screen rotation).
     *  • initialValue = emptyList() so the UI can start rendering immediately.
     */
    val aduanList: StateFlow<List<AduanData>> =
        repository.allAduan
            .stateIn(
                scope          = viewModelScope,
                started        = SharingStarted.WhileSubscribed(5_000),
                initialValue   = emptyList()
            )

    // ── Seed data: insert defaults when the DB is empty ───────────────────────
    init {
        viewModelScope.launch {
            // Wait for the first real emission from Room
            repository.allAduan.first().let { existing ->
                if (existing.isEmpty()) {
                    val fmt = SimpleDateFormat("d MMMM yyyy", Locale("ms", "MY"))
                    listOf(
                        AduanData(0, "Sampah Tidak Dikutip",  "Taman Mesra, Kajang",  "Dalam Siasatan", "22 April 2026", true),
                        AduanData(0, "Lampu Jalan Rosak",     "Section 7, Bangi",     "Selesai",        "20 April 2026", false),
                        AduanData(0, "Longkang Tersumbat",    "Semenyih",             "Dalam Siasatan", "18 April 2026", true),
                        AduanData(0, "Papan Tanda Jatuh",     "Putrajaya",            "Selesai",        "15 April 2026", false),
                        AduanData(0, "Jalan Berlubang",       "Kajang Utama",         "Dalam Siasatan", "10 April 2026", false)
                    ).forEach { repository.insert(it) }
                }
            }
        }
    }

    /**
     * Add a new aduan submitted by the logged-in user.
     * Room will emit the updated list automatically → UI recomposes.
     */
    fun addAduan(title: String, location: String) {
        val today = SimpleDateFormat("d MMMM yyyy", Locale("ms", "MY")).format(Date())
        viewModelScope.launch {
            repository.insert(
                AduanData(
                    id       = 0,          // Room auto-generates the real id
                    title    = title,
                    location = location,
                    status   = "Dalam Siasatan",
                    date     = today,
                    isMine   = true
                )
            )
        }
        _rewardPoints.update { it + 50 }
    }

    /**
     * Mark an aduan as Selesai (resolved).
     * Delegates to the repository which calls the DAO suspend function.
     */
    fun resolveAduan(id: Int) {
        viewModelScope.launch {
            repository.resolveById(id)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// VIEW MODEL FACTORY
// Required because UserViewModel now has a constructor parameter (repository).
// Pass this factory to viewModel() so Compose/Android can instantiate it.
// ─────────────────────────────────────────────────────────────────────────────
class UserViewModelFactory(private val repository: AduanRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}