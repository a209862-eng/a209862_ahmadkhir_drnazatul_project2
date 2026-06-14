package com.example.a209862_ahmadkhir_drnazatulaini_project2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────────────
// SIGN UP PAGE
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SignUpPage(
    onSignUpSuccess : () -> Unit,
    onLoginClick    : () -> Unit,
    userViewModel   : UserViewModel
) {
    val uiState     by userViewModel.uiState.collectAsState()
    var password    by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

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
            text       = "Daftar Akaun",
            color      = Color.White,
            fontSize   = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(24.dp))

        // ── Full name ─────────────────────────────────────────────────────────
        OutlinedTextField(
            value         = uiState.userName,
            onValueChange = { userViewModel.updateUserName(it) },
            label         = { Text("Nama Penuh", color = Color.Gray) },
            leadingIcon   = { Icon(Icons.Default.Person, null, tint = Color.Gray) },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true,
            shape         = RoundedCornerShape(12.dp),
            colors        = signUpFieldColors()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // ── Email ─────────────────────────────────────────────────────────────
        OutlinedTextField(
            value         = uiState.userEmail,
            onValueChange = { userViewModel.updateUserEmail(it) },
            label         = { Text("Email", color = Color.Gray) },
            leadingIcon   = { Icon(Icons.Default.Email, null, tint = Color.Gray) },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true,
            shape         = RoundedCornerShape(12.dp),
            colors        = signUpFieldColors()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // ── Phone ─────────────────────────────────────────────────────────────
        OutlinedTextField(
            value         = phoneNumber,
            onValueChange = { phoneNumber = it },
            label         = { Text("No. Telefon", color = Color.Gray) },
            leadingIcon   = { Icon(Icons.Default.Phone, null, tint = Color.Gray) },
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true,
            shape         = RoundedCornerShape(12.dp),
            colors        = signUpFieldColors()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // ── Password ──────────────────────────────────────────────────────────
        OutlinedTextField(
            value                = password,
            onValueChange        = { password = it },
            label                = { Text("Kata Laluan", color = Color.Gray) },
            leadingIcon          = { Icon(Icons.Default.Lock, null, tint = Color.Gray) },
            modifier             = Modifier.fillMaxWidth(),
            singleLine           = true,
            visualTransformation = PasswordVisualTransformation(),
            shape                = RoundedCornerShape(12.dp),
            colors               = signUpFieldColors()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // ── Register button ───────────────────────────────────────────────────
        Button(
            onClick  = { onSignUpSuccess() },
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Text("Daftar & Mula", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onLoginClick) {
            Text("Sudah ada akaun? Log Masuk", color = Color(0xFF8BC34A))
        }
    }
}

// ── Helper to avoid repeating identical color blocks ─────────────────────────
@Composable
private fun signUpFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Color(0xFF8BC34A),
    unfocusedBorderColor = Color.Gray,
    focusedLabelColor    = Color(0xFF8BC34A),
    cursorColor          = Color(0xFF8BC34A),
    focusedTextColor     = Color.White,
    unfocusedTextColor   = Color.White
)