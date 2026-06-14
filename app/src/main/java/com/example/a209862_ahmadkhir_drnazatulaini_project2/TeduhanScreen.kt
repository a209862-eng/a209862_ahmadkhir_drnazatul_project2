package com.example.a209862_ahmadkhir_drnazatulaini_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
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
// TEDUHAN SCREEN  –  community shelter & assistance
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun TeduhanScreen() {

    val teduhanList = remember {
        listOf(
            TeduhanItem("Pusat Teduhan Berdekatan",  "03-2329 3999",  "Taman Mesra, Kajang"),
            TeduhanItem("Balai Raya Komuniti",        "03-8737 2063",  "Bangi Utama"),
            TeduhanItem("Pusat Bantuan Bencana",      "03-2027 3063",  "Putrajaya"),
            TeduhanItem("Hospital Kajang",            "03-8736 5000",  "Kajang, Selangor"),
            TeduhanItem("Balai Polis Semenyih",       "03-8724 2222",  "Semenyih")
        )
    }

    var mapVisible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        // ── Green Header Card ─────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape  = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF8BC34A))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text       = "Teduhan Komuniti\n& Bantuan",
                    color      = Color.Black,
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // ── Map Toggle ────────────────────────────────────────────────
                Surface(
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Peta Lokasi Teduhan", color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked         = mapVisible,
                            onCheckedChange = { mapVisible = it },
                            colors          = SwitchDefaults.colors(
                                checkedThumbColor  = Color(0xFF8BC34A),
                                checkedTrackColor  = Color.DarkGray
                            )
                        )
                    }
                }
            }
        }

        // ── Map Placeholder ───────────────────────────────────────────────────
        if (mapVisible) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .padding(horizontal = 16.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector        = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint               = Color(0xFF8BC34A),
                            modifier           = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Peta Lokasi Teduhan",
                            color    = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            "${teduhanList.size} lokasi berdekatan",
                            color    = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Location List ─────────────────────────────────────────────────────
        Text(
            text     = "Senarai Lokasi",
            color    = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(items = teduhanList) { item ->
                TeduhanItemCard(item = item)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DATA MODEL
// ─────────────────────────────────────────────────────────────────────────────
data class TeduhanItem(
    val title    : String,
    val contact  : String,
    val location : String
)

// ─────────────────────────────────────────────────────────────────────────────
// TEDUHAN ITEM CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun TeduhanItemCard(item: TeduhanItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier              = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(" ${item.location}", color = Color.Gray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Call, null, tint = Color(0xFF8BC34A), modifier = Modifier.size(14.dp))
                    Text(" ${item.contact}", color = Color(0xFF8BC34A), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            // Map thumbnail placeholder
            Box(
                modifier         = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Place, null, tint = Color(0xFF8BC34A), modifier = Modifier.size(28.dp))
            }
        }
    }
}