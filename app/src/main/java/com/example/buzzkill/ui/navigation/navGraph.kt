package com.example.buzzkill.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.buzzkill.ui.screens.home.homeScreen

sealed class screen(val route: String){
    object home: screen("home")
    object rulebuilder: screen("ruleBuilder?ruleId={ruleId}"){
        fun createRoute(ruleId: Long? = null) = if(ruleId != null){
            "ruleBUilder?ruleId=$ruleId"
        }else{
            "ruleBuilder"
        }
    }

    object history: screen("history")
    object setting: screen("setting")

}

@Composable
fun navGraph(navController: NavHostController = rememberNavController()){
    NavHost(
        navController = navController,
        startDestination = screen.home.route
    ){
        composable(screen.home.route) {
            homeScreen(
                onAddRule = { navController.navigate(screen.rulebuilder.createRoute()) },
                onEditRule = { ruleId -> navController.navigate(screen.rulebuilder.createRoute(ruleId)) },
                onOpenHistory = { navController.navigate(screen.history.route) },
                onOpenSettings = { navController.navigate((screen.setting.route)) }
            )
        }

        composable(
            route = screen.rulebuilder.route,
            arguments = listOf(
                navArgument("ruleId"){
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            ruleBuilderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(screen.history.route) {
            historyScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(screen.setting.route) {
            settingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}