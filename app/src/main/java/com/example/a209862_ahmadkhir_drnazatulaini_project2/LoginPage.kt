package com.example.a209862_ahmadkhir_drnazatulaini_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────────────
// LOGIN PAGE
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LoginPage(
    onLoginSuccess : () -> Unit,
    onSignUpClick  : () -> Unit,
    userViewModel  : UserViewModel
) {
    val uiState by userViewModel.uiState.collectAsState()
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1C1E))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ── Logo ──────────────────────────────────────────────────────────────
        Icon(
            imageVector        = Icons.Default.Computer,
            contentDescription = "Tech Savvy Logo",
            tint               = Color(0xFF8BC34A),
            modifier           = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("TECH",  color = Color(0xFF8BC34A), fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("SAVVY", color = Color(0xFF8BC34A), fontSize = 20.sp)
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text       = "Log Masuk",
            color      = Color.White,
            fontSize   = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(24.dp))

        // ── Email field ───────────────────────────────────────────────────────
        OutlinedTextField(
            value         = uiState.userEmail,
            onValueChange = { userViewModel.updateUserEmail(it) },
            label         = { Text("Nama/Email", color = Color.Gray) },
            leadingIcon   = { Icon(Icons.Default.Email, null, tint = Color.Gray) },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true,
            shape         = RoundedCornerShape(12.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Color(0xFF8BC34A),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor    = Color(0xFF8BC34A),
                cursorColor          = Color(0xFF8BC34A),
                focusedTextColor     = Color.White,
                unfocusedTextColor   = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // ── Password field ────────────────────────────────────────────────────
        OutlinedTextField(
            value                  = password,
            onValueChange          = { password = it },
            label                  = { Text("Kata Laluan", color = Color.Gray) },
            leadingIcon            = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
            modifier               = Modifier.fillMaxWidth(),
            singleLine             = true,
            visualTransformation   = PasswordVisualTransformation(),
            shape                  = RoundedCornerShape(12.dp),
            colors                 = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = Color(0xFF8BC34A),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor    = Color(0xFF8BC34A),
                cursorColor          = Color(0xFF8BC34A),
                focusedTextColor     = Color.White,
                unfocusedTextColor   = Color.White
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        // ── Login button ──────────────────────────────────────────────────────
        Button(
            onClick  = {
                // Set a default display name derived from the email when name is blank
                if (uiState.userName == "GUEST USER" && uiState.userEmail.isNotBlank()) {
                    userViewModel.updateUserName(
                        uiState.userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
                    )
                }
                onLoginSuccess()
            },
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Text("Log Masuk", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Atau log masuk dengan", color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        // ── Social login icons ────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SocialLoginIconVector(Icons.Default.AccountCircle)
            SocialLoginIconVector(Icons.Default.Share)
            SocialLoginIconVector(Icons.Default.Sms)
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onSignUpClick) {
            Text("Belum ada akaun? Daftar Sekarang", color = Color(0xFF8BC34A))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SOCIAL LOGIN ICON  –  reusable circular button
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SocialLoginIconVector(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        modifier = Modifier.size(48.dp),
        shape    = CircleShape,
        color    = Color(0xFF2C2D30),
        onClick  = { }
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = Color.White,
            modifier           = Modifier.padding(12.dp)
        )
    }
}