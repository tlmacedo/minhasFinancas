package br.com.tlmacedo.minhasfinancas

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import br.com.tlmacedo.minhasfinancas.ui.navigation.NavGraph
import br.com.tlmacedo.minhasfinancas.ui.theme.MinhasFinancasTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Atividade principal da aplicação.
 * 
 * Esta é a porta de entrada do aplicativo (Entry Point). Utiliza [AndroidEntryPoint] 
 * para permitir a injeção de dependências pelo Hilt. A classe estende [FragmentActivity]
 * para suportar funcionalidades como o gerenciador de biometria.
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    
    /**
     * Ponto de inicialização da atividade.
     * 
     * Configura a interface de usuário utilizando Jetpack Compose, define o tema
     * global da aplicação e inicializa o grafo de navegação principal.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilita a exibição de ponta a ponta (edge-to-edge) para uma UI moderna
        enableEdgeToEdge()
        
        setContent {
            MinhasFinancasTheme {
                // Surface é o container base que utiliza a cor de fundo do tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Inicializa o controlador de navegação e o grafo de telas
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}
