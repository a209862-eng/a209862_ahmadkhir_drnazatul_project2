package com.example.a209862_ahmadkhir_drnazatulaini_project2

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────────────────────────────────────
// DASHBOARD SCREEN  –  updated with Part 3 & 4 feature access cards
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun DashboardScreen(
    userViewModel : UserViewModel,
    navController : NavHostController
) {
    val uiState      by userViewModel.uiState.collectAsState()
    val aduanList    by userViewModel.aduanList.collectAsState()
    val rewardPoints by userViewModel.rewardPoints.collectAsState()

    var showSafetyDialog by remember { mutableStateOf(false) }
    var quickReportText  by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .verticalScroll(rememberScrollState())
        ) {

            // ── 1. HEADER ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF8BC34A), Color(0xFF689F38))
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 35.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    Text(
                        text       = "SELAMAT DATANG,",
                        color      = Color.Black.copy(alpha = 0.7f),
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text       = uiState.userName.uppercase(),
                        color      = Color.Black,
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Icon(
                    imageVector        = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    tint               = Color.Black,
                    modifier           = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                )
            }

            // ── 2. SEARCH ADUAN CARD ──────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-45).dp),
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Carian Aduan", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value         = "",
                        onValueChange = {},
                        placeholder   = { Text("Cari isu...", color = Color.Gray) },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedTextColor   = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick  = { navController.navigate(AppRoutes.ADUAN) },
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Text("Mula Mencari", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ── 3. REWARD POINTS CARD ─────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-30).dp),
                shape  = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))
            ) {
                Row(
                    modifier           = Modifier.padding(16.dp),
                    verticalAlignment  = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFF8BC34A))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text       = "Mata Ganjaran Wira: $rewardPoints pts",
                        color      = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── 4. QUICK ACTIONS GRID ─────────────────────────────────────────
            Row(
                modifier                = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-15).dp),
                horizontalArrangement   = Arrangement.SpaceBetween
            ) {
                QuickActionItem(Icons.Default.LocationOn, "Berdekatan") {
                    navController.navigate(AppRoutes.BERDEKATAN)
                }
                QuickActionItem(Icons.Default.Warning, "Bencana", iconTint = Color.Red) {
                    showSafetyDialog = true
                }
                QuickActionItem(Icons.Default.Home, "Teduhan") {
                    navController.navigate(AppRoutes.TEDUHAN)
                }
                QuickActionItem(Icons.Default.Face, "Wira Bot") {
                    navController.navigate(AppRoutes.WIRABOT)
                }
            }

            // ── 5. PART 3 & 4 FEATURES SECTION ───────────────────────────────
            Text(
                text       = "Ciri Baharu  🆕",
                color      = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                modifier   = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            )

            // Firebase Aduan Card
            FeatureCard(
                icon        = Icons.Default.Cloud,
                iconColor   = Color(0xFF64B5F6),
                bgColor     = Color(0xFF1A1E2E),
                title       = "Aduan Komuniti (Firebase)",
                subtitle    = "Cloud Integration • Firestore",
                description = "Kongsi aduan dengan semua pengguna secara masa nyata melalui Firebase Firestore.",
                tag         = "SDG 16 ☁️",
                tagColor    = Color(0xFF64B5F6),
                onClick     = { navController.navigate(AppRoutes.FIREBASE_ADUAN) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // News API Card
            FeatureCard(
                icon        = Icons.Default.Article,
                iconColor   = Color(0xFFFFA726),
                bgColor     = Color(0xFF2E1E0A),
                title       = "Berita SDG 16 (REST API)",
                subtitle    = "Web API • GNews",
                description = "Berita terkini berkaitan keadilan, tadbir urus & SDG 16 dari seluruh dunia.",
                tag         = "SDG 16 📰",
                tagColor    = Color(0xFFFFA726),
                onClick     = { navController.navigate(AppRoutes.NEWS_API) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // GPS Sensor Card
            FeatureCard(
                icon        = Icons.Default.MyLocation,
                iconColor   = Color(0xFF8BC34A),
                bgColor     = Color(0xFF1A2E1A),
                title       = "Aduan Lokasi GPS",
                subtitle    = "Sensor • FusedLocationProvider",
                description = "Gunakan sensor GPS untuk menentukan lokasi tepat aduan anda secara automatik.",
                tag         = "SDG 16 📍",
                tagColor    = Color(0xFF8BC34A),
                onClick     = { navController.navigate(AppRoutes.LOKASI) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── 6. QUICK REPORT CARD ──────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3A2D2D))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Hantar Laporan Baru", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value         = quickReportText,
                        onValueChange = { quickReportText = it },
                        placeholder   = { Text("Ceritakan isu anda...", color = Color.Gray) },
                        modifier      = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8BC34A),
                            focusedTextColor   = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick  = {
                            if (quickReportText.isNotBlank()) {
                                userViewModel.addAduan(quickReportText, "Tidak ditetapkan")
                                quickReportText = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E7777)),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Send, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hantar Aduan", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ── 7. ADUAN TERKINI ──────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Aduan Terkini", color = Color.White, fontWeight = FontWeight.Bold)
                TextButton(onClick = { navController.navigate(AppRoutes.ADUAN) }) {
                    Text("Lihat Semua →", color = Color(0xFF8BC34A), fontSize = 13.sp)
                }
            }

            LazyRow(
                contentPadding        = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(aduanList.take(5)) { aduan ->
                    AduanCard(title = aduan.title, location = aduan.location)
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // ── Emergency Alert Dialog ─────────────────────────────────────────────
        if (showSafetyDialog) {
            EmergencyAlertDialog(
                phoneNumber = "0173127132",
                onConfirm   = { showSafetyDialog = false },
                onDismiss   = { showSafetyDialog = false }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FEATURE CARD  –  Part 3 & 4 entry point cards on Dashboard
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun FeatureCard(
    icon        : ImageVector,
    iconColor   : Color,
    bgColor     : Color,
    title       : String,
    subtitle    : String,
    description : String,
    tag         : String,
    tagColor    : Color,
    onClick     : () -> Unit
) {
    Card(
        onClick  = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color    = iconColor.copy(alpha = 0.15f),
                shape    = RoundedCornerShape(12.dp),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(28.dp))
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = tagColor, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = Color.Gray, fontSize = 11.sp, lineHeight = 15.sp)
            }

            Icon(Icons.Default.ArrowForward, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        }

        Surface(
            color    = tagColor.copy(alpha = 0.1f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            shape    = RoundedCornerShape(6.dp)
        ) {
            Text(
                tag,
                color    = tagColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// QUICK ACTION ITEM
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun QuickActionItem(
    icon     : ImageVector,
    label    : String,
    iconTint : Color = Color(0xFF8BC34A),
    onClick  : () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick  = onClick,
            modifier = Modifier
                .size(64.dp)
                .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
        ) {
            Icon(icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(28.dp))
        }
        Text(label, color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ADUAN CARD  (expandable)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AduanCard(title: String, location: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        onClick  = { expanded = !expanded },
        modifier = Modifier
            .width(200.dp)
            .animateContentSize(),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title,    color = Color.White, fontWeight = FontWeight.Bold)
            Text(location, color = Color.Gray,  fontSize   = 12.sp)
            if (expanded) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Status: Dalam Siasatan", color = Color(0xFF8BC34A), fontSize = 11.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EMERGENCY ALERT DIALOG
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun EmergencyAlertDialog(
    phoneNumber : String,
    onConfirm   : () -> Unit,
    onDismiss   : () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Color(0xFF2C2C2E),
        shape            = RoundedCornerShape(28.dp),
        icon = {
            Icon(
                imageVector        = Icons.Default.Warning,
                contentDescription = null,
                tint               = Color(0xFFD32F2F),
                modifier           = Modifier.size(60.dp)
            )
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text          = "ALERT!",
                    color         = Color(0xFFD32F2F),
                    fontWeight    = FontWeight.ExtraBold,
                    fontSize      = 24.sp,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text       = "Mesej bantuan ini akan dihantar kepada Bahagian Keselamatan Area Berdekatan",
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    textAlign  = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        },
        text = {
            Column(
                modifier            = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors   = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
                    border   = BorderStroke(1.dp, Color.Gray),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier          = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Phone, null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(phoneNumber, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("Anda Pasti?", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        confirmButton = {
            Button(
                onClick  = onConfirm,
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(50.dp),
                shape    = RoundedCornerShape(12.dp)
            ) { Text("Ya", color = Color.White, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            OutlinedButton(
                onClick  = onDismiss,
                border   = BorderStroke(1.dp, Color(0xFF5C9AD2)),
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(50.dp),
                shape    = RoundedCornerShape(12.dp)
            ) { Text("Tidak", color = Color(0xFF5C9AD2), fontWeight = FontWeight.Bold) }
        }
    )
}