package com.intec.t2o.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.intec.t2o.components.GoBackButton
import com.intec.t2o.components.NumericPad
import com.intec.t2o.navigation.AppScreens
import com.intec.t2o.robotinterface.RobotManager
import com.intec.t2o.viewmodels.MqttViewModel
import com.intec.t2o.viewmodels.NumericPanelViewModel

@Composable
fun NumericPanelScreen(
    navController: NavController,
    numericPanelViewModel: NumericPanelViewModel,
    mqttViewModel: MqttViewModel,
    robotManager: RobotManager
) {
    Log.d("Current Screen", "NumericPanelScreen")
    
    val shouldCheckCode = remember { mutableStateOf(false) }
    val isCodeCorrect by numericPanelViewModel.isCodeCorrect.collectAsState()

    LaunchedEffect(shouldCheckCode.value) {
        if (shouldCheckCode.value) {
            numericPanelViewModel.checkForTaskExecution()
            shouldCheckCode.value = false
        }
    }

    LaunchedEffect(isCodeCorrect) {
        if (isCodeCorrect) {
            navController.navigate(AppScreens.MeetingScreen.route)
        }
    }

    FuturisticGradientBackground {
        NumericPad(
            numericPanelViewModel = numericPanelViewModel,
            onClick = { shouldCheckCode.value = true },
            titleText = "Por favor, introduce el código que se te ha proporcionado"
        )
        GoBackButton(onClick = { mqttViewModel.returnToPosition(mqttViewModel.returnDestination.value!!) })
    }
}