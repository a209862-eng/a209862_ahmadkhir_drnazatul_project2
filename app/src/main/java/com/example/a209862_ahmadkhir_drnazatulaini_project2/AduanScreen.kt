package com.example.a209862_ahmadkhir_drnazatulaini_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────────────
// ADUAN SCREEN  (Part 3: displays items added via ViewModel)
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AduanScreen(userViewModel: UserViewModel) {

    // ── Observe shared ViewModel state ────────────────────────────────────────
    val allAduan by userViewModel.aduanList.collectAsState()

    var selectedCategory by remember { mutableStateOf("Semua") }
    var searchQuery      by remember { mutableStateOf("") }

    // ── Add-aduan dialog state ────────────────────────────────────────────────
    var showAddDialog     by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var newTitle          by remember { mutableStateOf("") }
    var newLocation       by remember { mutableStateOf("") }

    // ── Filter logic ──────────────────────────────────────────────────────────
    val filteredAduan = remember(selectedCategory, searchQuery, allAduan) {
        allAduan
            .filter { aduan ->
                when (selectedCategory) {
                    "Aduan Saya"       -> aduan.isMine
                    "Dalam Siasatan"   -> aduan.status == "Dalam Siasatan"
                    "Selesai"          -> aduan.status == "Selesai"
                    else               -> true
                }
            }
            .filter { aduan ->
                searchQuery.isBlank() ||
                        aduan.title.contains(searchQuery, ignoreCase = true) ||
                        aduan.location.contains(searchQuery, ignoreCase = true)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {

        // ── Top App Bar ───────────────────────────────────────────────────────
        CenterAlignedTopAppBar(
            title = {
                Text(
                    "Senarai Aduan",
                    color      = Color.White,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFF1E1E1E)
            )
        )

        // ── Search Field ──────────────────────────────────────────────────────
        OutlinedTextField(
            value         = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder   = { Text("Cari aduan...", color = Color.Gray) },
            leadingIcon   = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            modifier      = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            shape         = RoundedCornerShape(12.dp),
            singleLine    = true,
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Color(0xFF8BC34A),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor     = Color.White,
                unfocusedTextColor   = Color.White,
                cursorColor          = Color(0xFF8BC34A)
            )
        )

        // ── Category Filter Chips ─────────────────────────────────────────────
        val categories = listOf("Semua", "Aduan Saya", "Dalam Siasatan", "Selesai")
        LazyRow(
            contentPadding        = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier              = Modifier.padding(bottom = 8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick  = { selectedCategory = category },
                    label    = { Text(category) },
                    colors   = FilterChipDefaults.filterChipColors(
                        containerColor          = Color(0xFF1E1E1E),
                        labelColor              = Color.Gray,
                        selectedContainerColor  = Color(0xFF8BC34A),
                        selectedLabelColor      = Color.Black
                    ),
                    border   = null
                )
            }
        }

        // ── Add Aduan Button ──────────────────────────────────────────────────
        Button(
            onClick  = { showAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Text("+ Tambah Aduan Baru", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        // ── Aduan List ────────────────────────────────────────────────────────
        LazyColumn(
            modifier              = Modifier.fillMaxSize(),
            contentPadding        = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement   = Arrangement.spacedBy(16.dp)
        ) {
            if (filteredAduan.isEmpty()) {
                item {
                    Box(
                        modifier         = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tiada aduan dalam kategori ini.", color = Color.Gray)
                    }
                }
            } else {
                items(filteredAduan, key = { it.id }) { aduan ->
                    AduanListItem(
                        title    = aduan.title,
                        location = aduan.location,
                        status   = aduan.status,
                        date     = aduan.date,
                        isMine   = aduan.isMine,
                        onResolve = {
                            if (aduan.status != "Selesai") userViewModel.resolveAduan(aduan.id)
                        }
                    )
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
                Text("Aduan Baru", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value         = newTitle,
                        onValueChange = { newTitle = it },
                        label         = { Text("Tajuk Aduan", color = Color.Gray) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        shape         = RoundedCornerShape(10.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Color(0xFF8BC34A),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor     = Color.White,
                            unfocusedTextColor   = Color.White,
                            cursorColor          = Color(0xFF8BC34A),
                            focusedLabelColor    = Color(0xFF8BC34A)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value         = newLocation,
                        onValueChange = { newLocation = it },
                        label         = { Text("Lokasi", color = Color.Gray) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        shape         = RoundedCornerShape(10.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Color(0xFF8BC34A),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor     = Color.White,
                            unfocusedTextColor   = Color.White,
                            cursorColor          = Color(0xFF8BC34A),
                            focusedLabelColor    = Color(0xFF8BC34A)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick  = {
                        if (newTitle.isNotBlank() && newLocation.isNotBlank()) {
                            userViewModel.addAduan(title = newTitle, location = newLocation)
                            newTitle      = ""
                            newLocation   = ""
                            showAddDialog = false
                            showSuccessDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                    shape  = RoundedCornerShape(10.dp)
                ) { Text("Hantar", color = Color.Black, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddDialog = false },
                    shape   = RoundedCornerShape(10.dp)
                ) { Text("Batal", color = Color.Gray) }
            }
        )
    }

    // ── Wedding Card Success Dialog ───────────────────────────────────────────
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            containerColor   = Color(0xFF1A1208),
            shape            = RoundedCornerShape(20.dp),
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text          = "✦  ✦  ✦",
                        color         = Color(0xFFB8973A),
                        fontSize      = 18.sp,
                        letterSpacing = 6.sp,
                        textAlign     = TextAlign.Center,
                        modifier      = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider(
                        modifier  = Modifier.padding(vertical = 10.dp),
                        color     = Color(0xFFB8973A).copy(alpha = 0.5f)
                    )
                    Text(
                        text          = "NOTIFIKASI RASMI",
                        color         = Color(0xFFA88C4A),
                        fontSize      = 11.sp,
                        letterSpacing = 2.sp,
                        textAlign     = TextAlign.Center,
                        modifier      = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text       = "Aduan Sudah Ditambah",
                        color      = Color(0xFFE8C96A),
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Serif,
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider(
                        modifier  = Modifier.padding(vertical = 10.dp),
                        color     = Color(0xFFB8973A).copy(alpha = 0.5f)
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text       = "Aduan anda telah berjaya direkodkan.\nKami akan menyiasat dengan segera\ndan maklum balas akan diberikan.",
                        color      = Color(0xFFC9AA60),
                        fontSize   = 14.sp,
                        textAlign  = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier   = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text      = "— ✦ —",
                        color     = Color(0xFFB8973A),
                        fontSize  = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick  = { showSuccessDialog = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFB8973A)),
                    shape    = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        text       = "Tutup",
                        color      = Color(0xFF1A1208),
                        fontWeight = FontWeight.Medium,
                        fontSize   = 15.sp
                    )
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ADUAN LIST ITEM CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AduanListItem(
    title     : String,
    location  : String,
    status    : String,
    date      : String,
    isMine    : Boolean,
    onResolve : () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = title,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    modifier   = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = if (status == "Selesai")
                        Color(0xFF8BC34A).copy(alpha = 0.2f)
                    else
                        Color(0xFFFFC107).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text       = status,
                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color      = if (status == "Selesai") Color(0xFF8BC34A) else Color(0xFFFFC107),
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector        = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint               = Color.Gray,
                    modifier           = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(location, color = Color.Gray, fontSize = 12.sp)
            }

            if (isMine) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFF8BC34A).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "Aduan Saya",
                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        color      = Color(0xFF8BC34A),
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(
                modifier  = Modifier.padding(vertical = 10.dp),
                color     = Color.White.copy(alpha = 0.08f)
            )

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text     = "Tarikh: $date",
                    color    = Color.Gray.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
                // Allow user to mark their own aduan as resolved
                if (isMine && status != "Selesai") {
                    TextButton(
                        onClick        = onResolve,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text("Tandakan Selesai", color = Color(0xFF8BC34A), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}