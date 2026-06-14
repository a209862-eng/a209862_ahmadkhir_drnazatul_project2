package com.example.a209862_ahmadkhir_drnazatulaini_project2

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// LOKASI SCREEN
// Part 3 – Sensor Integration (GPS / Location Sensor)
//
//  • Uses Android FusedLocationProviderClient (GPS + Network + Cell tower fusion)
//  • Requests ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION at runtime
//  • Reverse geocodes coordinates → human-readable address via Geocoder
//  • Auto-fills the location field so user can attach GPS to an Aduan
//  • Ties into UserViewModel.addAduan() → Room Database (Local Persistence)
//
//  SDG 16 relevance: accurate location tagging improves government response
//  time for community issues and ensures justice is served at the right place.
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")  // permission is checked at runtime below
@Composable
fun LokasiScreen(
    onNavigateBack : () -> Unit,
    userViewModel  : UserViewModel
) {
    val context = LocalContext.current

    // ── State ─────────────────────────────────────────────────────────────────
    var latitude        by remember { mutableStateOf<Double?>(null) }
    var longitude       by remember { mutableStateOf<Double?>(null) }
    var address         by remember { mutableStateOf("") }
    var isLocating      by remember { mutableStateOf(false) }
    var locationError   by remember { mutableStateOf<String?>(null) }
    var permissionGiven by remember { mutableStateOf(false) }
    var showAduanDialog by remember { mutableStateOf(false) }
    var aduanTitle      by remember { mutableStateOf("") }
    var showSuccess     by remember { mutableStateOf(false) }

    // ── Fused location client ─────────────────────────────────────────────────
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // ── Permission launcher ───────────────────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            permissionGiven = true
            // Auto-fetch location after permission granted
            isLocating    = true
            locationError = null
            val cancelToken = CancellationTokenSource()
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancelToken.token)
                .addOnSuccessListener { loc: Location? ->
                    isLocating = false
                    if (loc != null) {
                        latitude  = loc.latitude
                        longitude = loc.longitude
                        // Reverse geocode
                        try {
                            @Suppress("DEPRECATION")
                            val geocoder  = Geocoder(context, Locale("ms", "MY"))
                            val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                            address = if (!addresses.isNullOrEmpty()) {
                                val a = addresses[0]
                                listOfNotNull(
                                    a.thoroughfare,
                                    a.subLocality,
                                    a.locality,
                                    a.adminArea
                                ).joinToString(", ")
                            } else {
                                "${loc.latitude.format(5)}, ${loc.longitude.format(5)}"
                            }
                        } catch (_: Exception) {
                            address = "${loc.latitude.format(5)}, ${loc.longitude.format(5)}"
                        }
                    } else {
                        locationError = "Lokasi tidak dapat diperoleh. Cuba semula."
                    }
                }
                .addOnFailureListener { e ->
                    isLocating    = false
                    locationError = "Gagal mendapatkan lokasi: ${e.message}"
                }
        } else {
            locationError = "Kebenaran lokasi ditolak. Sila benarkan akses lokasi dalam tetapan."
        }
    }

    // ── Helper: request location ──────────────────────────────────────────────
    fun requestLocation() {
        isLocating    = true
        locationError = null
        val cancelToken = CancellationTokenSource()
        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancelToken.token)
            .addOnSuccessListener { loc: Location? ->
                isLocating = false
                if (loc != null) {
                    latitude  = loc.latitude
                    longitude = loc.longitude
                    try {
                        @Suppress("DEPRECATION")
                        val geocoder  = Geocoder(context, Locale("ms", "MY"))
                        val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                        address = if (!addresses.isNullOrEmpty()) {
                            val a = addresses[0]
                            listOfNotNull(
                                a.thoroughfare,
                                a.subLocality,
                                a.locality,
                                a.adminArea
                            ).joinToString(", ")
                        } else {
                            "${loc.latitude.format(5)}, ${loc.longitude.format(5)}"
                        }
                    } catch (_: Exception) {
                        address = "${loc.latitude.format(5)}, ${loc.longitude.format(5)}"
                    }
                } else {
                    locationError = "Lokasi tidak dapat diperoleh. Cuba semula."
                }
            }
            .addOnFailureListener { e ->
                isLocating    = false
                locationError = "Gagal: ${e.message}"
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Aduan Lokasi GPS", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
                        Text("Sensor GPS Aktif", color = Color.Black.copy(alpha = 0.7f), fontSize = 11.sp)
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
        containerColor = Color(0xFF121212)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // ── GPS Icon + animated ring ──────────────────────────────────────
            Box(contentAlignment = Alignment.Center) {
                if (isLocating) {
                    CircularProgressIndicator(
                        color    = Color(0xFF8BC34A),
                        modifier = Modifier.size(110.dp),
                        strokeWidth = 3.dp
                    )
                }
                Box(
                    modifier         = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            if (latitude != null)
                                Brush.radialGradient(listOf(Color(0xFF8BC34A), Color(0xFF558B2F)))
                            else
                                Brush.radialGradient(listOf(Color(0xFF2A2A2A), Color(0xFF1A1A1A)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        tint     = if (latitude != null) Color.Black else Color(0xFF8BC34A),
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text       = if (isLocating) "Mencari lokasi anda..."
                else if (latitude != null) "Lokasi Ditemui ✓"
                else "Sensor GPS",
                color      = if (latitude != null) Color(0xFF8BC34A) else Color.White,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text     = "FusedLocationProviderClient (GPS + Network)",
                color    = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Coordinates card ──────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text("Maklumat GPS", color = Color(0xFF8BC34A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color.White.copy(alpha = 0.08f))

                    CoordRow("Latitud",  latitude?.format(6)  ?: "—")
                    Spacer(modifier = Modifier.height(8.dp))
                    CoordRow("Longitud", longitude?.format(6) ?: "—")

                    if (address.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Home, null, tint = Color(0xFF8BC34A), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Alamat (Reverse Geocode)", color = Color.Gray, fontSize = 11.sp)
                                Text(address, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Error ─────────────────────────────────────────────────────────
            locationError?.let { err ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3A1A1A)),
                    shape  = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(err, color = Color.Red, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Get Location button ───────────────────────────────────────────
            Button(
                onClick  = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    if (permissionGiven) requestLocation()
                },
                enabled  = !isLocating,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                shape    = RoundedCornerShape(14.dp)
            ) {
                if (isLocating) {
                    CircularProgressIndicator(
                        color       = Color.Black,
                        modifier    = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Mencari...", color = Color.Black, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.MyLocation, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (latitude == null) "Dapatkan Lokasi GPS" else "Kemas Kini Lokasi",
                        color      = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── Submit aduan with GPS button (shown after location acquired) ──
            if (latitude != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick  = { showAduanDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(52.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5631)),
                    shape    = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Send, null, tint = Color(0xFF8BC34A), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hantar Aduan dengan Lokasi GPS", color = Color(0xFF8BC34A), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── SDG 16 explanation card ───────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A1A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🏛️ Kaitan dengan SDG 16", color = Color(0xFF8BC34A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Data GPS yang tepat membantu pihak berkuasa mengesan dan menangani isu komuniti dengan lebih cepat. " +
                                "Ini menyokong SDG 16 — Keamanan, Keadilan dan Institusi yang Kukuh — " +
                                "melalui penyampaian perkhidmatan awam yang lebih telus dan efisien.",
                        color      = Color.Gray,
                        fontSize   = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // ── Submit Aduan with GPS Dialog ──────────────────────────────────────────
    if (showAduanDialog) {
        AlertDialog(
            onDismissRequest = { showAduanDialog = false },
            containerColor   = Color(0xFF1E1E1E),
            shape            = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color(0xFF8BC34A), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aduan + Lokasi GPS", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value         = aduanTitle,
                        onValueChange = { aduanTitle = it },
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
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2E1A)),
                        shape  = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier          = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, null, tint = Color(0xFF8BC34A), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(address.ifBlank { "GPS: ${"%.5f".format(latitude)}, ${"%.5f".format(longitude)}" },
                                color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick  = {
                        if (aduanTitle.isNotBlank()) {
                            val loc = address.ifBlank { "GPS: ${"%.5f".format(latitude)}, ${"%.5f".format(longitude)}" }
                            userViewModel.addAduan(aduanTitle, loc)
                            aduanTitle    = ""
                            showAduanDialog = false
                            showSuccess     = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                    shape  = RoundedCornerShape(10.dp)
                ) {
                    Text("Hantar ke Room DB", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAduanDialog = false }, shape = RoundedCornerShape(10.dp)) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }

    // ── Success Snackbar-style dialog ─────────────────────────────────────────
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            containerColor   = Color(0xFF1A2E1A),
            shape            = RoundedCornerShape(16.dp),
            title = { Text("✓ Berjaya!", color = Color(0xFF8BC34A), fontWeight = FontWeight.Bold) },
            text  = {
                Text(
                    "Aduan dengan lokasi GPS telah disimpan ke Room Database tempatan.",
                    color      = Color.White,
                    fontSize   = 13.sp,
                    textAlign  = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { showSuccess = false },
                    colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                    shape   = RoundedCornerShape(10.dp)
                ) { Text("OK", color = Color.Black, fontWeight = FontWeight.Bold) }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// HELPERS
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CoordRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Gray,  fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)