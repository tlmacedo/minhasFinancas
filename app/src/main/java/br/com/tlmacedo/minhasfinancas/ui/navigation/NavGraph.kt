package br.com.tlmacedo.minhasfinancas.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.tlmacedo.minhasfinancas.auth.AuthState
import br.com.tlmacedo.minhasfinancas.ui.screens.HomeScreen
import br.com.tlmacedo.minhasfinancas.ui.screens.auth.LoginScreen
import br.com.tlmacedo.minhasfinancas.ui.screens.auth.RegisterScreen
import br.com.tlmacedo.minhasfinancas.ui.screens.contas.AddEditContaScreen
import br.com.tlmacedo.minhasfinancas.ui.screens.contas.ContasScreen
import br.com.tlmacedo.minhasfinancas.ui.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Contas : Screen("contas")
    object AddConta : Screen("contas/add")
    object EditConta : Screen("contas/edit/{contaId}") {
        fun createRoute(contaId: Long) = "contas/edit/$contaId"
    }
    object ContaTransacoes : Screen("contas/{contaId}/transacoes") {
        fun createRoute(contaId: Long) = "contas/$contaId/transacoes"
    }
    object Eventos : Screen("eventos")
    object AddEvento : Screen("eventos/add")
    object Categorias : Screen("categorias")
    object Relatorios : Screen("relatorios")
    object Configuracoes : Screen("configuracoes")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    
    // Determinar tela inicial baseado no estado de autenticação
    val startDestination = when (authState) {
        is AuthState.Authenticated -> Screen.Home.route
        AuthState.NeedSetup -> Screen.Register.route
        AuthState.NeedLogin -> Screen.Login.route
        else -> Screen.Login.route
    }
    
    // Observar mudanças de estado para navegação
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
            AuthState.NeedLogin -> {
                if (navController.currentDestination?.route != Screen.Login.route &&
                    navController.currentDestination?.route != Screen.Register.route) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> {}
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                viewModel = authViewModel
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = authViewModel
            )
        }
        
        // Main
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToContas = { navController.navigate(Screen.Contas.route) },
                onNavigateToEventos = { navController.navigate(Screen.Eventos.route) },
                onNavigateToCategorias = { navController.navigate(Screen.Categorias.route) },
                onNavigateToRelatorios = { navController.navigate(Screen.Relatorios.route) },
                onNavigateToConfiguracoes = { navController.navigate(Screen.Configuracoes.route) }
            )
        }
        
        composable(Screen.Contas.route) {
            ContasScreen(
                onNavigateToAddConta = { navController.navigate(Screen.AddConta.route) },
                onNavigateToEditConta = { contaId -> 
                    navController.navigate(Screen.EditConta.createRoute(contaId))
                },
                onNavigateToContaTransacoes = { contaId ->
                    navController.navigate(Screen.ContaTransacoes.createRoute(contaId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AddConta.route) {
            AddEditContaScreen(
                contaId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.EditConta.route,
            arguments = listOf(navArgument("contaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val contaId = backStackEntry.arguments?.getLong("contaId")
            AddEditContaScreen(
                contaId = contaId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.ContaTransacoes.route,
            arguments = listOf(navArgument("contaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val contaId = backStackEntry.arguments?.getLong("contaId") ?: 0L
            PlaceholderScreen(
                title = "Transações da Conta",
                message = "Em construção...\nFiltro por conta ID: $contaId",
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Eventos.route) {
            PlaceholderScreen(
                title = "Transações", 
                message = "Em construção...",
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Categorias.route) {
            PlaceholderScreen(
                title = "Categorias", 
                message = "Em construção...",
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Relatorios.route) {
            PlaceholderScreen(
                title = "Relatórios", 
                message = "Em construção...",
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Configuracoes.route) {
            PlaceholderScreen(
                title = "Configurações", 
                message = "Em construção...",
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScreen(
    title: String, 
    message: String = "Em construção...",
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
