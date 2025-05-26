package com.centinai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.centinai.app.ui.screens.ErrorScreen
import com.centinai.app.ui.screens.SplashScreen
import com.centinai.app.ui.screens.WebViewScreen
import com.centinai.app.ui.theme.CentinAIAppTheme
import com.centinai.app.viewmodel.WebLoadViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CentinAIAppTheme {
                val navController = rememberNavController()
                val viewModel: WebLoadViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "webview",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash") { SplashScreen(navController = navController, viewModel = viewModel) }
                        composable("webview") { WebViewScreen(viewModel = viewModel) }
                        composable("error") {
                            ErrorScreen(
                                message = viewModel.errorMessage ?: "Error al cargar la aplicación",
                                onRetry = {
                                    viewModel.reset()
                                    navController.navigate("splash") {
                                        popUpTo("error") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}