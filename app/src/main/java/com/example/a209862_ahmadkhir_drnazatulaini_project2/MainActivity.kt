package com.example.a209862_ahmadkhir_drnazatulaini_project2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// ─────────────────────────────────────────────────────────────────────────────
// ROUTE CONSTANTS  –  updated with Part 3 & 4 routes
// ─────────────────────────────────────────────────────────────────────────────
object AppRoutes {
    const val LOGIN      = "login"
    const val SIGNUP     = "signup"
    const val MAIN       = "main"
    const val ADUAN      = "aduan"
    const val PROFIL     = "profil"
    const val BERDEKATAN = "berdekatan"
    const val TEDUHAN    = "teduhan"
    const val WIRABOT    = "wirabot"

    // ── Part 3 & 4 new routes ─────────────────────────────────────────────────
    const val FIREBASE_ADUAN = "firebase_aduan"   // Cloud Integration (Firestore)
    const val NEWS_API       = "news_api"          // Web API (GNews REST API)
    const val LOKASI         = "lokasi"            // Sensor (GPS)
}

// ─────────────────────────────────────────────────────────────────────────────
// MAIN ACTIVITY
// ─────────────────────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        android.util.Log.d("FIREBASE_TEST", "Firebase apps: ${com.google.firebase.FirebaseApp.getApps(this).size}")


        val database   = AppDatabase.getDatabase(applicationContext)
        val repository = AduanRepository(database.aduanDao())
        val factory    = UserViewModelFactory(repository)

        setContent {
            AppTheme {
                val navController                = rememberNavController()
                val userViewModel: UserViewModel = viewModel(factory = factory)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = Color(0xFF121212)
                ) {
                    AppNavHost(navController = navController, userViewModel = userViewModel)
                }
            }
        }
    }
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary    = Color(0xFF8BC34A),
            background = Color(0xFF121212),
            surface    = Color(0xFF1E1E1E)
        ),
        content = content
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// NAV HOST  –  includes all Part 3 & 4 destinations
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AppNavHost(
    navController : NavHostController,
    userViewModel : UserViewModel
) {
    NavHost(
        navController    = navController,
        startDestination = AppRoutes.LOGIN
    ) {
        // ── Auth ───────────────────────────────────────────────────────────────
        composable(AppRoutes.LOGIN) {
            LoginPage(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate(AppRoutes.SIGNUP) },
                userViewModel = userViewModel
            )
        }

        composable(AppRoutes.SIGNUP) {
            SignUpPage(
                onSignUpSuccess = {
                    navController.navigate(AppRoutes.MAIN) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onLoginClick  = { navController.popBackStack() },
                userViewModel = userViewModel
            )
        }

        // ── Bottom-nav scaffold screens ────────────────────────────────────────
        composable(AppRoutes.MAIN) {
            MainScaffold(navController = navController, userViewModel = userViewModel, currentRoute = AppRoutes.MAIN)
        }
        composable(AppRoutes.ADUAN) {
            MainScaffold(navController = navController, userViewModel = userViewModel, currentRoute = AppRoutes.ADUAN)
        }
        composable(AppRoutes.PROFIL) {
            MainScaffold(navController = navController, userViewModel = userViewModel, currentRoute = AppRoutes.PROFIL)
        }
        composable(AppRoutes.TEDUHAN) {
            MainScaffold(navController = navController, userViewModel = userViewModel, currentRoute = AppRoutes.TEDUHAN)
        }

        // ── Full-screen routes (own top bar, no bottom nav) ────────────────────
        composable(AppRoutes.BERDEKATAN) {
            BerdekatanScreen(
                onNavigateBack = { navController.popBackStack() },
                userViewModel  = userViewModel
            )
        }

        composable(AppRoutes.WIRABOT) {
            WiraBotScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ── PART 3 & 4: New screens ────────────────────────────────────────────

        // Part 3A: Cloud Integration — Firebase Firestore community aduan
        composable(AppRoutes.FIREBASE_ADUAN) {
            FirebaseAduanScreen(
                onNavigateBack = { navController.popBackStack() },
                userViewModel  = userViewModel
            )
        }

        // Part 3B: Web API — GNews REST API for SDG 16 news
        composable(AppRoutes.NEWS_API) {
            NewsApiScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Part 3C: Sensor — GPS Location sensor
        composable(AppRoutes.LOKASI) {
            LokasiScreen(
                onNavigateBack = { navController.popBackStack() },
                userViewModel  = userViewModel
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SHARED SCAFFOLD
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MainScaffold(
    navController : NavHostController,
    userViewModel : UserViewModel,
    currentRoute  : String
) {
    Scaffold(
        containerColor = Color(0xFF121212),
        bottomBar      = { AppBottomNavigation(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (currentRoute) {
                AppRoutes.MAIN    -> DashboardScreen(userViewModel = userViewModel, navController = navController)
                AppRoutes.ADUAN   -> AduanScreen(userViewModel = userViewModel)
                AppRoutes.PROFIL  -> ProfileScreen(userViewModel = userViewModel, navController = navController)
                AppRoutes.TEDUHAN -> TeduhanScreen()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BOTTOM NAVIGATION BAR
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AppBottomNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color(0xFF1E1E1E)) {
        val items = listOf(
            Triple(AppRoutes.MAIN,    Icons.Default.Home,   "Utama"),
            Triple(AppRoutes.ADUAN,   Icons.Default.List,   "Aduan"),
            Triple(AppRoutes.TEDUHAN, Icons.Default.Home,   "Teduhan"),
            Triple(AppRoutes.PROFIL,  Icons.Default.Person, "Profil")
        )
        items.forEach { (route, icon, label) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick  = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(AppRoutes.MAIN) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                },
                icon  = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Color(0xFF8BC34A),
                    selectedTextColor   = Color(0xFF8BC34A),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor      = Color(0xFF2A2A2A)
                )
            )
        }
    }
}