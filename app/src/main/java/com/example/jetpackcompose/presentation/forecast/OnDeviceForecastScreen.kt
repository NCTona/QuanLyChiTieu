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

    // Tính số ngày còn lại trong tháng (giống server)
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
                                .background(color = Color(0xfff5f5f5))
                                .height(50.dp)
                                .fillMaxSize()
                                .padding(end = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Dự đoán cá nhân (TFLite)",
                                fontFamily = montserrat,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                                contentDescription = "Quay lại",
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
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
            // Tình trạng Model
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Trạng thái AI On-Device:",
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
                        Text(if (viewModel.isModelReady) "Cập nhật Model" else "Tải Model (.tflite)", fontFamily = montserrat)
                    }
                }
            }

            // Chạy dự đoán
            if (viewModel.isModelReady) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { viewModel.runPrediction() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                            enabled = !viewModel.isLoading
                        ) {
                            Text("Dự đoán ${if (remainingDays > 0) remainingDays else ""} ngày tới", fontFamily = montserrat)
                        }

                        if (viewModel.isLoading) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(color = primaryColor)
                        }

                        viewModel.predictedAmount?.let { amount ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Kết quả dự báo ${remainingDays} ngày (TFLite):",
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
                                text = "Dự đoán này được tạo trực tiếp tại máy điện thoại, không cần nối mạng đến server AI.",
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
