package com.example.a209862_ahmadkhir_drnazatulaini_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// DATA MODEL  –  Community Aduan stored in Firestore
// ─────────────────────────────────────────────────────────────────────────────
data class FirebaseAduan(
    val id          : String = "",
    val title       : String = "",
    val location    : String = "",
    val status      : String = "Dalam Siasatan",
    val date        : String = "",
    val submittedBy : String = ""
)

// ─────────────────────────────────────────────────────────────────────────────
// FIREBASE ADUAN SCREEN
// Part 3 – Cloud Integration (Firebase Firestore)
//
//  • Reads community-wide aduan from Firestore in real-time (addSnapshotListener)
//  • Users can submit new aduan which are pushed to Firestore
//  • Demonstrates cloud sync: data is visible across all devices instantly
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirebaseAduanScreen(
    onNavigateBack : () -> Unit,
    userViewModel  : UserViewModel
) {
    val uiState by userViewModel.uiState.collectAsState()

    // ── Firestore instance ────────────────────────────────────────────────────
    val db = remember { FirebaseFirestore.getInstance() }

    // ── State ─────────────────────────────────────────────────────────────────
    var communityAduan  by remember { mutableStateOf<List<FirebaseAduan>>(emptyList()) }
    var isLoading       by remember { mutableStateOf(true) }
    var errorMessage    by remember { mutableStateOf<String?>(null) }
    var showAddDialog   by remember { mutableStateOf(false) }
    var newTitle        by remember { mutableStateOf("") }
    var newLocation     by remember { mutableStateOf("") }
    var isSaving        by remember { mutableStateOf(false) }

    // ── Real-time Firestore listener ──────────────────────────────────────────
    // addSnapshotListener fires immediately with cached data, then on every
    // change in the "community_aduan" collection.
    DisposableEffect(Unit) {
        var registration: ListenerRegistration? = null
        try {
            registration = db.collection("community_aduan")
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    isLoading = false
                    if (error != null) {
                        errorMessage = "Gagal memuatkan data: ${error.message}"
                        return@addSnapshotListener
                    }
                    communityAduan = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(FirebaseAduan::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
                }
        } catch (e: Exception) {
            isLoading    = false
            errorMessage = "Firebase tidak tersedia: ${e.message}"
        }

        onDispose { registration?.remove() }
    }

    // ── Helper: push new aduan to Firestore ───────────────────────────────────
    fun submitToFirestore() {
        if (newTitle.isBlank() || newLocation.isBlank()) return
        isSaving = true
        val today = SimpleDateFormat("d MMMM yyyy", Locale("ms", "MY")).format(Date())
        val aduan = hashMapOf(
            "title"       to newTitle,
            "location"    to newLocation,
            "status"      to "Dalam Siasatan",
            "date"        to today,
            "submittedBy" to uiState.userName
        )
        db.collection("community_aduan")
            .add(aduan)
            .addOnSuccessListener {
                isSaving    = false
                newTitle    = ""
                newLocation = ""
                showAddDialog = false
            }
            .addOnFailureListener { e ->
                isSaving     = false
                errorMessage = "Gagal menyimpan: ${e.message}"
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Aduan Komuniti", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
                        Text("Data Masa Nyata ", color = Color.Black.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF8BC34A))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { showAddDialog = true },
                containerColor = Color(0xFF8BC34A)
            ) {
                Icon(Icons.Default.Add, "Tambah", tint = Color.Black)
            }
        },
        containerColor = Color(0xFF121212)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            // ── Firebase sync status banner ───────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E1A))
            ) {
                Row(
                    modifier          = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Cloud,
                        null,
                        tint     = Color(0xFF8BC34A),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Disinkronkan dengan Firebase Firestore",
                            color      = Color(0xFF8BC34A),
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Data dikongsi semua pengguna secara masa nyata",
                            color    = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // ── Error message ─────────────────────────────────────────────────
            errorMessage?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3A1A1A))
                ) {
                    Row(
                        modifier          = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(msg, color = Color.Red, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ── Loading / list ────────────────────────────────────────────────
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF8BC34A))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Memuatkan dari ...", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (communityAduan.isEmpty()) {
                        item {
                            Box(
                                modifier         = Modifier
                                    .fillParentMaxSize()
                                    .padding(top = 60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Cloud,
                                        null,
                                        tint     = Color.Gray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Tiada aduan komuniti lagi.", color = Color.Gray)
                                    Text("Jadilah yang pertama!", color = Color(0xFF8BC34A), fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        item {
                            Text(
                                "${communityAduan.size} Aduan Komuniti",
                                color      = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 15.sp,
                                modifier   = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        items(communityAduan, key = { it.id }) { aduan ->
                            FirebaseAduanCard(aduan = aduan)
                        }
                    }
                }
            }
        }
    }

    // ── Add Aduan Dialog ──────────────────────────────────────────────────────
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor   = Color(0xFF1E1E1E),
            shape            = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Cloud, null, tint = Color(0xFF8BC34A), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kongsi Aduan Komuniti", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        "Aduan ini akan dikongsi kepada semua pengguna melalui Google.",
                        color    = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value         = newTitle,
                        onValueChange = { newTitle = it },
                        label         = { Text("Tajuk Aduan", color = Color.Gray) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        shape         = RoundedCornerShape(10.dp),
                        colors        = firebaseFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value         = newLocation,
                        onValueChange = { newLocation = it },
                        label         = { Text("Lokasi", color = Color.Gray) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        shape         = RoundedCornerShape(10.dp),
                        colors        = firebaseFieldColors()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Dihantar oleh: ${uiState.userName}",
                        color    = Color(0xFF8BC34A),
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick  = { submitToFirestore() },
                    enabled  = !isSaving && newTitle.isNotBlank() && newLocation.isNotBlank(),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                    shape    = RoundedCornerShape(10.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color    = Color.Black,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Hantar", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDialog = false }, shape = RoundedCornerShape(10.dp)) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FIREBASE ADUAN CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun FirebaseAduanCard(aduan: FirebaseAduan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    aduan.title,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    modifier   = Modifier.weight(1f)
                )
                Surface(
                    color = if (aduan.status == "Selesai")
                        Color(0xFF8BC34A).copy(alpha = 0.2f)
                    else Color(0xFFFFC107).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        aduan.status,
                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        color      = if (aduan.status == "Selesai") Color(0xFF8BC34A) else Color(0xFFFFC107),
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                Text(" ${aduan.location}", color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Text(" ${aduan.submittedBy}", color = Color.Gray, fontSize = 11.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Cloud, null, tint = Color(0xFF8BC34A), modifier = Modifier.size(11.dp))
                    Text(" Firebase", color = Color(0xFF8BC34A), fontSize = 10.sp)
                }
            }

            if (aduan.date.isNotBlank()) {
                Text("Tarikh: ${aduan.date}", color = Color.Gray.copy(alpha = 0.7f), fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun firebaseFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Color(0xFF8BC34A),
    unfocusedBorderColor = Color.Gray,
    focusedTextColor     = Color.White,
    unfocusedTextColor   = Color.White,
    cursorColor          = Color(0xFF8BC34A),
    focusedLabelColor    = Color(0xFF8BC34A)
)