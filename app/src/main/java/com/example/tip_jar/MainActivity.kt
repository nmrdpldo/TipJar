package com.example.tip_jar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tip_jar.ui.composables.SavedPaymentsScreen
import com.example.tip_jar.ui.composables.TipJarMainScreen
import com.example.tip_jar.ui.theme.TipJarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipJarTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "tipjar_main_screen")
                {
                    composable("tipjar_main_screen"){
                        TipJarMainScreen(navController)
                    }
                    composable(
                        "saved_payments_screen",
                        enterTransition = {
                          fadeIn(
                              animationSpec = tween(
                                  500,
                                  easing = LinearEasing
                              )
                          ) + slideIntoContainer(
                              animationSpec = tween(
                                  500,
                                  easing = EaseIn
                              ),
                              towards = AnimatedContentTransitionScope.SlideDirection.Start
                          )
                        },
                        exitTransition = {
                            fadeOut(
                                animationSpec = tween(
                                    500,
                                    easing = LinearEasing
                                )
                            ) + slideOutOfContainer(
                                animationSpec = tween(
                                    500,
                                    easing = EaseOut
                                ),
                                towards = AnimatedContentTransitionScope.SlideDirection.End
                            )
                        }){
                        SavedPaymentsScreen(navController)
                    }
                }
            }
        }
    }
}