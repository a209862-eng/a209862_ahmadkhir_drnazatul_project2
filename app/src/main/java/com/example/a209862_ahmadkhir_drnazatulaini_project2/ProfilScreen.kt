package com.example.a209862_ahmadkhir_drnazatulaini_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────────────────────────────────────
// PROFILE SCREEN  –  reads & writes shared ViewModel state
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ProfileScreen(
    userViewModel : UserViewModel,
    navController : NavHostController
) {
    val uiState      by userViewModel.uiState.collectAsState()
    val aduanList    by userViewModel.aduanList.collectAsState()
    val rewardPoints by userViewModel.rewardPoints.collectAsState()

    // ── Edit profile dialog state ─────────────────────────────────────────────
    var showEditDialog by remember { mutableStateOf(false) }
    var editName       by remember { mutableStateOf(uiState.userName) }
    var editEmail      by remember { mutableStateOf(uiState.userEmail) }

    // Sync local edit fields when uiState changes
    LaunchedEffect(uiState) {
        editName  = uiState.userName
        editEmail = uiState.userEmail
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .verticalScroll(rememberScrollState())
    ) {

        // ── 1. GRADIENT HEADER ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF8BC34A), Color(0xFF558B2F))
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier          = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape    = CircleShape,
                    color    = Color.Black.copy(alpha = 0.2f)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier           = Modifier.padding(16.dp),
                        tint               = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text("Profil", color = Color.Black.copy(alpha = 0.7f), fontSize = 14.sp)
                    Text(
                        uiState.userName,
                        color      = Color.Black,
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Wira Level 5", color = Color.Black.copy(alpha = 0.7f), fontSize = 14.sp)
                }
            }

            // Edit icon
            IconButton(
                onClick  = { showEditDialog = true },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Edit, "Sunting", tint = Color.Black)
            }
        }

        // ── 2. STATS ROW  (data comes from ViewModel) ─────────────────────────
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ProfileStatItem(rewardPoints.toString(), "Mata Ganjaran")
            ProfileStatItem(aduanList.count { it.isMine }.toString(), "Aduan Saya")
            ProfileStatItem(aduanList.count { it.status == "Selesai" }.toString(), "Selesai")
        }

        // ── 3. EMAIL DISPLAY ──────────────────────────────────────────────────
        if (uiState.userEmail.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Row(
                    modifier          = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Email, null, tint = Color(0xFF8BC34A), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(uiState.userEmail, color = Color.White, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ── 4. MENU LIST ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            ProfileMenuItem(Icons.Default.Person,      "Komuniti Saya")       { }
            ProfileMenuItem(Icons.Default.Settings,    "Tetapan Apl")         { }
            ProfileMenuItem(Icons.Default.Call,        "No. Telefon")         { }
            ProfileMenuItem(Icons.Default.Star,      "Bahasa")              { }
            ProfileMenuItem(Icons.Default.Edit,        "Sunting Profil")      { showEditDialog = true }
            ProfileMenuItem(Icons.Default.List, "Sejarah Laporan")     { navController.navigate(AppRoutes.ADUAN) }
            ProfileMenuItem(Icons.Default.Info,        "Tentang Aplikasi")    { }
            Spacer(modifier = Modifier.height(20.dp))

            // ── Logout ────────────────────────────────────────────────────────
            Button(
                onClick  = {
                    // Reset ViewModel state on logout
                    userViewModel.updateUserName("GUEST USER")
                    userViewModel.updateUserEmail("")
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.12f)),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Keluar", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    // ── Edit Profile Dialog ───────────────────────────────────────────────────
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor   = Color(0xFF1E1E1E),
            shape            = RoundedCornerShape(20.dp),
            title = {
                Text("Sunting Profil", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value         = editName,
                        onValueChange = { editName = it },
                        label         = { Text("Nama", color = Color.Gray) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        shape         = RoundedCornerShape(10.dp),
                        colors        = profileFieldColors()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value         = editEmail,
                        onValueChange = { editEmail = it },
                        label         = { Text("Email", color = Color.Gray) },
                        modifier      = Modifier.fillMaxWidth(),
                        singleLine    = true,
                        shape         = RoundedCornerShape(10.dp),
                        colors        = profileFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick  = {
                        userViewModel.updateUserName(editName)
                        userViewModel.updateUserEmail(editEmail)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                    shape  = RoundedCornerShape(10.dp)
                ) { Text("Simpan", color = Color.Black, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEditDialog = false },
                    shape   = RoundedCornerShape(10.dp)
                ) { Text("Batal", color = Color.Gray) }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STAT ITEM
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ProfileStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White,  fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.Gray,   fontSize = 12.sp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MENU ITEM ROW
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ProfileMenuItem(
    icon    : ImageVector,
    title   : String,
    onClick : () -> Unit
) {
    Column {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, fontSize = 15.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SHARED FIELD COLORS
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun profileFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Color(0xFF8BC34A),
    unfocusedBorderColor = Color.Gray,
    focusedTextColor     = Color.White,
    unfocusedTextColor   = Color.White,
    cursorColor          = Color(0xFF8BC34A),
    focusedLabelColor    = Color(0xFF8BC34A)
)