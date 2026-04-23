package com.example.jetpackcompose.presentation.forecast

import androidx.hilt.navigation.compose.hiltViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.jetpackcompose.R
import com.example.jetpackcompose.presentation.forecast.ForecastViewModel
import com.example.jetpackcompose.components.montserrat
import com.example.jetpackcompose.ui.theme.primaryColor
import com.example.jetpackcompose.ui.theme.topBarColor
import java.util.Calendar
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnDeviceForecastScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<ForecastViewModel>()

    val remainingDays = remember {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        maxDays - currentDay + 1
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth(),
                        ) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .offset(x = (-8).dp)
                                    .offset(y = (1).dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                                    contentDescription = "Quay l\u1ea1i",
                                    tint = primaryColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Text(
                                text = "D\u1ef1 \u0111o\u00e1n \u0103n u\u1ed1ng (TFLite)",
                                fontFamily = montserrat,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .offset(x = (-8).dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = topBarColor),
                    modifier = Modifier.height(50.dp)
                )
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xfff5f5f5))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tr\u1ea1ng th\u00e1i AI On-Device:",
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = viewModel.statusMessage,
                        fontFamily = montserrat,
                        color = if (viewModel.isModelReady) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.downloadModel() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        enabled = !viewModel.isLoading
                    ) {
                        Text(if (viewModel.isModelReady) "C\u1eadp nh\u1eadt Model" else "T\u1ea3i Model (.tflite)", fontFamily = montserrat)
                    }
                }
            }

            if (viewModel.isModelReady) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { viewModel.runPrediction() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                            enabled = !viewModel.isLoading
                        ) {
                            Text("D\u1ef1 \u0111o\u00e1n \u0103n u\u1ed1ng ${if (remainingDays > 0) remainingDays else ""} ng\u00e0y t\u1edbi", fontFamily = montserrat)
                        }

                        if (viewModel.isLoading) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(color = primaryColor)
                        }

                        viewModel.predictedAmount?.let { amount ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "D\u1ef1 b\u00e1o \u0103n u\u1ed1ng ${remainingDays} ng\u00e0y t\u1edbi (TFLite):",
                                fontFamily = montserrat,
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )

                            val dailyAvg = amount / 7.0
                            val predictedForRemaining = dailyAvg * remainingDays.coerceAtLeast(0)

                            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
                            Text(
                                text = "${formatter.format(predictedForRemaining.toLong())} VND",
                                fontFamily = montserrat,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "D\u1ef1 \u0111o\u00e1n chi ti\u00eau \u0103n u\u1ed1ng, \u0111\u01b0\u1ee3c t\u1ea1o tr\u1ef1c ti\u1ebfp t\u1ea1i m\u00e1y \u0111i\u1ec7n tho\u1ea1i, kh\u00f4ng c\u1ea7n n\u1ed1i m\u1ea1ng \u0111\u1ebfn server AI.",
                                fontFamily = montserrat,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
