package com.centinai.app.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.centinai.app.R
import com.centinai.app.viewmodel.WebLoadViewModel

@Composable
fun SplashScreen(navController: NavController, viewModel: WebLoadViewModel) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(viewModel.isWebLoaded, viewModel.hasError) {
        Log.d("SplashScreen", "🔥 Effect triggered - isWebLoaded=${viewModel.isWebLoaded}, hasError=${viewModel.hasError}")

        if (viewModel.hasError) {
            Log.d("SplashScreen", "❌ Hay error. Navegando a error.")
            visible = false
            delay(300)
            navController.navigate("error") {
                popUpTo("splash") { inclusive = true }
            }
        } else if (viewModel.isWebLoaded) {
            Log.d("SplashScreen", "✅ Web cargada. Navegando a webview.")
            visible = false
            delay(500)
            navController.navigate("webview") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }


    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(800)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF162E34)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_centinai),
                contentDescription = "Logo CentinAI",
                modifier = Modifier.size(180.dp)
            )
        }
    }
}