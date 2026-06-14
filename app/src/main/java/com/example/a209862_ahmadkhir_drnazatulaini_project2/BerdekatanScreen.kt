package com.example.a209862_ahmadkhir_drnazatulaini_project2


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────────────
// DATA MODEL  –  community initiative
// ─────────────────────────────────────────────────────────────────────────────
data class CommunityInitiative(
    val id        : Int,
    val title     : String,
    val date      : String,
    val time      : String,
    val status    : String,
    val location  : String = "",
    val organiser : String = ""
)

// ─────────────────────────────────────────────────────────────────────────────
// BERDEKATAN SCREEN  –  full-screen with own back button
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BerdekatanScreen(
    onNavigateBack : () -> Unit,
    userViewModel  : UserViewModel      // kept for future ViewModel integration
) {
    // ── Local state for the initiative list ───────────────────────────────────
    var initiatives by remember {
        mutableStateOf(
            listOf(
                CommunityInitiative(1, "Gotong Royong",       "Ahad, 1 Okt",   "9.00 pg - 12.00 tgh", "Dalam proses",      "Taman Mesra, Kajang", "JKKK Taman Mesra"),
                CommunityInitiative(2, "Pasar Tani Mingguan", "Sabtu, 30 Sept","7.00 pg - 1.00 ptg",  "Baru Dimulakan",    "Section 7, Bangi",    "Persatuan Penduduk"),
                CommunityInitiative(3, "Kelas Komuniti",      "Jumaat, 5 Okt", "2.00 ptg - 5.00 ptg", "Akan Datang",       "Dewan Komuniti",      "Jabatan Pelajaran")
            )
        )
    }

    // ── Add-initiative dialog state ───────────────────────────────────────────
    var showDialog   by remember { mutableStateOf(false) }
    var newTitle     by remember { mutableStateOf("") }
    var newDate      by remember { mutableStateOf("") }
    var newTime      by remember { mutableStateOf("") }
    var newLocation  by remember { mutableStateOf("") }
    var newOrganiser by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Inisiatif Komuniti",
                            fontWeight = FontWeight.Bold,
                            color      = Color.Black,
                            fontSize   = 16.sp
                        )
                        Text(
                            "Berdekatan",
                            fontWeight = FontWeight.Bold,
                            color      = Color.Black,
                            fontSize   = 16.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint               = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFA5C978))
            )
        },
        containerColor     = Color(0xFF121212),
        floatingActionButton = {
            FloatingActionButton(
                onClick         = { showDialog = true },
                containerColor  = Color(0xFFA5C978)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Inisiatif", tint = Color.Black)
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier        = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding  = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary row
            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "${initiatives.size} Inisiatif Aktif",
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                    Surface(
                        color = Color(0xFFA5C978).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Kawasan Saya",
                            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color      = Color(0xFFA5C978),
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            items(initiatives, key = { it.id }) { initiative ->
                InitiativeCard(initiative = initiative)
            }
        }
    }

    // ── Add Initiative Dialog ─────────────────────────────────────────────────
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor   = Color(0xFF1E1E1E),
            shape            = RoundedCornerShape(20.dp),
            title = {
                Text("Inisiatif Baru", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column {
                    listOf(
                        Triple("Nama Inisiatif", newTitle)    { v: String -> newTitle    = v },
                        Triple("Tarikh",         newDate)     { v: String -> newDate     = v },
                        Triple("Masa",           newTime)     { v: String -> newTime     = v },
                        Triple("Lokasi",         newLocation) { v: String -> newLocation = v },
                        Triple("Penganjur",      newOrganiser){ v: String -> newOrganiser= v }
                    ).forEach { (label, value, onChange) ->
                        OutlinedTextField(
                            value         = value,
                            onValueChange = onChange,
                            label         = { Text(label, color = Color.Gray) },
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            shape         = RoundedCornerShape(10.dp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = Color(0xFFA5C978),
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor     = Color.White,
                                unfocusedTextColor   = Color.White,
                                cursorColor          = Color(0xFFA5C978),
                                focusedLabelColor    = Color(0xFFA5C978)
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            val newId = (initiatives.maxOfOrNull { it.id } ?: 0) + 1
                            initiatives = initiatives + CommunityInitiative(
                                id        = newId,
                                title     = newTitle,
                                date      = newDate,
                                time      = newTime,
                                status    = "Baru Dimulakan",
                                location  = newLocation,
                                organiser = newOrganiser
                            )
                            // reset
                            newTitle     = ""; newDate = ""; newTime = ""
                            newLocation  = ""; newOrganiser = ""
                            showDialog   = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA5C978)),
                    shape  = RoundedCornerShape(10.dp)
                ) { Text("Tambah", color = Color.Black, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDialog = false },
                    shape   = RoundedCornerShape(10.dp)
                ) { Text("Batal", color = Color.Gray) }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// INITIATIVE CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun InitiativeCard(initiative: CommunityInitiative) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon placeholder
            Box(
                modifier         = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2A3A1A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = if (initiative.title.contains("Gotong")) "🧹"
                    else if (initiative.title.contains("Pasar")) "🛒"
                    else "🌿",
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(initiative.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                if (initiative.date.isNotBlank()) {
                    Text("(${initiative.date})", color = Color.Gray, fontSize = 12.sp)
                }
                if (initiative.time.isNotBlank()) {
                    Text(initiative.time, color = Color.Gray, fontSize = 12.sp)
                }
                if (initiative.location.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                        Text(" ${initiative.location}", color = Color.Gray, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = Color(0xFFA5C978).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text       = initiative.status,
                        color      = Color(0xFFA5C978),
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}