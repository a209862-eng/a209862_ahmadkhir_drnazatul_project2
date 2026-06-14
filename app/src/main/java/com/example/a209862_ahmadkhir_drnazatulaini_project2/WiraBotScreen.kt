package com.example.a209862_ahmadkhir_drnazatulaini_project2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// DATA MODEL
// ─────────────────────────────────────────────────────────────────────────────
data class ChatMessage(
    val text  : String,
    val isBot : Boolean
)

// ─────────────────────────────────────────────────────────────────────────────
// WIRABOT SCREEN  –  crash fix: replaced NavHostController parameter with
// a plain () -> Unit lambda.  Passing a NavHostController directly and calling
// popBackStack() inside composition (not inside a click handler) caused
// IllegalStateException.  With a lambda the call only fires on user tap.
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WiraBotScreen(onNavigateBack: () -> Unit) {

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                "Selamat datang ke Wira Bot! Saya boleh membantu anda tentang aduan komuniti, " +
                        "inisiatif berdekatan, dan bantuan kecemasan. Apa yang boleh saya bantu?",
                isBot = true
            )
        )
    }

    var inputText        by remember { mutableStateOf("") }
    val listState             = rememberLazyListState()
    val coroutineScope        = rememberCoroutineScope()

    val botReplies = listOf(
        "Terima kasih atas soalan anda! Untuk membuat aduan, pergi ke tab 'Aduan' dan tekan 'Tambah Aduan Baru'.",
        "Berdasarkan maklumat komuniti setempat, terdapat beberapa inisiatif aktif di kawasan anda. Sila semak skrin 'Berdekatan' untuk maklumat lanjut.",
        "Dalam situasi kecemasan, sila hubungi talian kecemasan 999 atau pergi ke pusat teduhan berdekatan.",
        "Saya faham kebimbangan anda. Aduan anda akan disemak oleh pihak berkuasa tempatan dalam masa 3-5 hari bekerja.",
        "Untuk melihat status aduan anda, pergi ke tab 'Aduan' dan pilih 'Aduan Saya'."
    )

    val suggestions = listOf(
        "Cara buat aduan",
        "Kecemasan berdekatan",
        "Status aduan saya",
        "Inisiatif komuniti"
    )

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        messages.add(ChatMessage(text, isBot = false))
        inputText = ""
        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
            kotlinx.coroutines.delay(700)
            messages.add(ChatMessage(botReplies.random(), isBot = true))
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {

        // ── 1. TOP BAR ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF8BC34A), Color(0xFF558B2F))
                    )
                )
                .padding(top = 48.dp, bottom = 20.dp, start = 8.dp, end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // ── Back button: uses the lambda, safe inside onClick ─────────────
            IconButton(
                onClick  = onNavigateBack,          // ← safe: runs on tap only
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint               = Color.Black
                )
            }

            Row(
                modifier              = Modifier.align(Alignment.Center),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier         = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("W", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
                Column {
                    Text("Wira Bot", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Pembantu Komuniti AI", fontSize = 11.sp, color = Color.Black.copy(alpha = 0.7f))
                }
            }
        }

        // ── 2. CHAT AREA ──────────────────────────────────────────────────────
        LazyColumn(
            state               = listState,
            modifier            = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding      = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { message ->
                WiraChatBubble(message)
            }

            // Suggestion chips
            item {
                FlowRow(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                ) {
                    suggestions.forEach { suggestion ->
                        SuggestionChip(
                            onClick = { sendMessage(suggestion) },
                            label   = {
                                Text(suggestion, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                            },
                            colors  = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color(0xFF1E1E1E)
                            ),
                            border  = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.4f)),
                            shape   = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }
        }

        // ── 3. INPUT ROW ──────────────────────────────────────────────────────
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0A0A))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value         = inputText,
                onValueChange = { inputText = it },
                placeholder   = { Text("Tanya Wira Bot...", color = Color.Gray) },
                modifier      = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape         = RoundedCornerShape(28.dp),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor   = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    focusedTextColor        = Color.White,
                    unfocusedTextColor      = Color.White,
                    focusedBorderColor      = Color(0xFF8BC34A),
                    unfocusedBorderColor    = Color.Gray
                )
            )

            Spacer(modifier = Modifier.width(10.dp))

            FloatingActionButton(
                onClick        = { sendMessage(inputText) },
                containerColor = Color(0xFF8BC34A),
                contentColor   = Color.Black,
                shape          = CircleShape,
                modifier       = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Send, "Hantar", modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CHAT BUBBLE
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun WiraChatBubble(message: ChatMessage) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isBot) Arrangement.Start else Arrangement.End,
        verticalAlignment     = Alignment.Bottom
    ) {
        if (message.isBot) {
            Box(
                modifier         = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2E4D21)),
                contentAlignment = Alignment.Center
            ) {
                Text("W", color = Color(0xFF8BC34A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color    = if (message.isBot) Color(0xFF1E3A1E) else Color(0xFF2E4A0F),
            shape    = RoundedCornerShape(
                topStart    = 16.dp,
                topEnd      = 16.dp,
                bottomEnd   = if (message.isBot) 16.dp else 4.dp,
                bottomStart = if (message.isBot) 4.dp  else 16.dp
            ),
            modifier = Modifier.widthIn(max = 270.dp)
        ) {
            Text(
                text       = message.text,
                modifier   = Modifier.padding(12.dp),
                color      = Color.White,
                fontSize   = 14.sp,
                lineHeight = 20.sp
            )
        }

        if (!message.isBot) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier         = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text("U", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}