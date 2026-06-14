package com.example.a209862_ahmadkhir_drnazatulaini_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// ─────────────────────────────────────────────────────────────────────────────
// DATA MODEL  –  News article from REST API
// ─────────────────────────────────────────────────────────────────────────────
data class NewsArticle(
    val title       : String,
    val description : String,
    val url         : String,
    val source      : String,
    val publishedAt : String
)

// ─────────────────────────────────────────────────────────────────────────────
// API HELPER  –  fetches SDG 16 / justice news from GNews public REST API
// (https://gnews.io  –  free tier, no auth needed for limited requests)
//
//  Fallback: if network is unavailable, returns curated static articles so
//  the screen always shows something meaningful during a demo.
// ─────────────────────────────────────────────────────────────────────────────
suspend fun fetchJusticeNews(): List<NewsArticle> = withContext(Dispatchers.IO) {
    try {
        // GNews free public endpoint – justice & governance related
        val apiKey = "YOUR_GNEWS_API_KEY"   // ← replace with your free key from gnews.io
        val query  = "justice+governance+SDG16"
        val urlStr = "https://gnews.io/api/v4/search?q=$query&lang=en&max=10&apikey=$apiKey"

        val connection = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            requestMethod  = "GET"
            connectTimeout = 8000
            readTimeout    = 8000
        }

        if (connection.responseCode == 200) {
            val body   = connection.inputStream.bufferedReader().readText()
            val json   = JSONObject(body)
            val array  = json.getJSONArray("articles")
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                NewsArticle(
                    title       = obj.optString("title",       "Tiada tajuk"),
                    description = obj.optString("description", "Tiada penerangan"),
                    url         = obj.optString("url",         ""),
                    source      = obj.optJSONObject("source")?.optString("name", "Tidak diketahui") ?: "Tidak diketahui",
                    publishedAt = obj.optString("publishedAt", "").take(10)
                )
            }
        } else {
            fallbackNews()
        }
    } catch (e: Exception) {
        fallbackNews()
    }
}

// Static fallback articles – shown when API key is not set or network fails
private fun fallbackNews(): List<NewsArticle> = listOf(
    NewsArticle(
        "Malaysia Perkasa Sistem Keadilan Digital",
        "Kerajaan Malaysia melancarkan sistem pelaporan jenayah dalam talian bagi meningkatkan akses keadilan kepada rakyat, selaras dengan matlamat SDG 16.",
        "https://www.malaysia.gov.my",
        "Bernama",
        "2025-04-20"
    ),
    NewsArticle(
        "SDG 16: Peace, Justice and Strong Institutions",
        "UN reports progress on SDG 16 with 140+ countries adopting open government initiatives and digital public services to increase transparency.",
        "https://sdgs.un.org/goals/goal16",
        "United Nations",
        "2025-04-15"
    ),
    NewsArticle(
        "Transparansi Kerajaan Tempatan di Malaysia",
        "Laporan SUHAKAM menunjukkan peningkatan 34% dalam aduan kes rasuah yang diselesaikan pada 2024, hasil daripada platform e-aduan komuniti.",
        "https://www.suhakam.org.my",
        "SUHAKAM",
        "2025-04-10"
    ),
    NewsArticle(
        "Community Reporting Apps Reduce Crime by 18%",
        "A study by UNDP shows that mobile community reporting platforms in Southeast Asia have reduced petty crime rates by up to 18% in urban areas.",
        "https://www.undp.org",
        "UNDP",
        "2025-04-05"
    ),
    NewsArticle(
        "E-Aduan: Revolusi Penyampaian Perkhidmatan Awam",
        "Platform e-Aduan Malaysia mencatatkan lebih 200,000 laporan komuniti dalam tempoh setahun, dengan 78% diselesaikan dalam masa 7 hari.",
        "https://www.malaysia.gov.my",
        "MyGov",
        "2025-03-28"
    ),
    NewsArticle(
        "Ketidaksamaan Akses Keadilan: Cabaran SDG 16",
        "Laporan PBB mendedahkan bahawa lebih 5 bilion manusia masih tidak mempunyai akses kepada sistem keadilan yang adil dan telus.",
        "https://sdgs.un.org",
        "UN News",
        "2025-03-20"
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// NEWS API SCREEN
// Part 3 – Data from the Internet (Web API / REST API)
//
//  • Fetches live SDG 16 / justice news from GNews REST API
//  • Uses Kotlin coroutines (LaunchedEffect) for async network call
//  • Shows loading state, error handling, and article list
//  • Users can tap articles to open in browser (LocalUriHandler)
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsApiScreen(onNavigateBack: () -> Unit) {

    var articles     by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    var isLoading    by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTab  by remember { mutableIntStateOf(0) }

    val uriHandler = LocalUriHandler.current

    // ── Fetch on first composition ────────────────────────────────────────────
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            articles  = fetchJusticeNews()
        } catch (e: Exception) {
            errorMessage = "Ralat: ${e.message}"
            articles     = fallbackNews()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Berita SDG 16", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
                        Text("Keadilan & Tadbir Urus", color = Color.Black.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isLoading = true
                        errorMessage = null
                    }) {
                        Icon(Icons.Default.Refresh, "Muat semula", tint = Color.Black)
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
        ) {

            // ── API info banner ───────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1E2E))
            ) {
                Row(
                    modifier          = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, null, tint = Color(0xFF64B5F6), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "Data daripada GNews",
                            color      = Color(0xFF64B5F6),
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Berita terkini berkaitan SDG 16 dari seluruh dunia",
                            color    = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // ── Loading state ─────────────────────────────────────────────────
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF8BC34A))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Memuatkan berita daripada API...", color = Color.Gray, fontSize = 13.sp)
                        Text("GNews REST API", color = Color.Gray.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }
            } else {

                // ── Error banner (non-fatal – fallback articles shown) ─────────
                errorMessage?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1A1A))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFFFA726), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "API tidak dapat dihubungi. Menunjukkan artikel contoh.",
                                color    = Color(0xFFFFA726),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // ── Articles count ─────────────────────────────────────────────
                Text(
                    "${articles.size} artikel ditemui",
                    color    = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                // ── Article list ───────────────────────────────────────────────
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(articles) { article ->
                        NewsArticleCard(
                            article   = article,
                            onTap     = {
                                if (article.url.startsWith("http")) {
                                    try { uriHandler.openUri(article.url) } catch (_: Exception) {}
                                }
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NEWS ARTICLE CARD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun NewsArticleCard(article: NewsArticle, onTap: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Source & date row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF8BC34A).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        article.source,
                        modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color      = Color(0xFF8BC34A),
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(article.publishedAt, color = Color.Gray, fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text       = article.title,
                color      = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text     = article.description,
                color    = Color.Gray,
                fontSize = 12.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // SDG 16 tag
                Surface(
                    color = Color(0xFF1A3A5C),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "SDG 16 🏛️",
                        modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color      = Color(0xFF64B5F6),
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Baca lebih lanjut", color = Color(0xFF8BC34A), fontSize = 11.sp)
                    Icon(Icons.Default.ArrowForward, null, tint = Color(0xFF8BC34A), modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}