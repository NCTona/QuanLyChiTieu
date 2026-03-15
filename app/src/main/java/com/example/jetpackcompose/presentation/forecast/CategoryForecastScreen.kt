package com.example.jetpackcompose.presentation.forecast

import androidx.hilt.navigation.compose.hiltViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.jetpackcompose.data.remote.dto.CategoryForecastResponse
import com.example.jetpackcompose.presentation.forecast.CategoryForecastViewModel
import com.example.jetpackcompose.components.montserrat
import com.example.jetpackcompose.ui.theme.primaryColor
import com.example.jetpackcompose.ui.theme.topBarColor
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryForecastScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<CategoryForecastViewModel>()

    // Auto-load khi vao man hinh
    LaunchedEffect(Unit) {
        viewModel.loadForecasts(normalized = false)
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Box(
                                modifier = Modifier
                                    .height(50.dp)
                                    .fillMaxWidth()
                                    .padding(end = 16.dp), // Thêm padding bù cho nút back
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Xu hướng chi tiêu",
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
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = topBarColor
                        ),
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ===== Loading =====
                if (viewModel.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = primaryColor)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Đang phân tích dữ liệu...",
                                fontFamily = montserrat,
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // ===== Error =====
                viewModel.errorMessage?.let { error ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Lỗi kết nối",
                                fontFamily = montserrat,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFFC62828)
                            )
                            Text(
                                error,
                                fontFamily = montserrat,
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.loadForecasts(normalized = false) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                             Text("Thử lại", fontFamily = montserrat, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // ===== Forecast Cards =====
                if (viewModel.forecasts.isNotEmpty()) {
                    Text(
                        "Dự đoán chi tiêu tháng này",
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF333333)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(viewModel.forecasts) { forecast ->
                            ForecastCategoryCard(
                                forecast = forecast
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun ForecastCategoryCard(
    forecast: CategoryForecastResponse
) {
    val trendText = when (forecast.trend) {
        "increasing" -> {
            if (forecast.change_percent > 100) "Tăng đột biến"
            else if (forecast.change_percent > 30) "Tăng đáng kể"
            else "Có xu hướng tăng"
        }
        "decreasing" -> {
            if (forecast.change_percent < -50) "Giảm rất mạnh"
            else if (forecast.change_percent < -20) "Giảm đáng kể"
            else "Có xu hướng giảm"
        }
        else -> "Duy trì ổn định"
    }
    
    val trendColor = when (forecast.trend) {
        "increasing" -> Color(0xFFE53935)
        "decreasing" -> Color(0xFF43A047)
        else -> Color(0xFF1565C0)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Category info
            Column(modifier = Modifier.weight(1f)) {
                val categoryName = when (forecast.category_id) {
                    1L -> "Chi phí nhà ở"
                    2L -> "Ăn uống"
                    3L -> "Mua sắm quần áo"
                    4L -> "Đi lại"
                    5L -> "Chăm sóc sắc đẹp"
                    6L -> "Giao lưu"
                    7L -> "Y tế"
                    8L -> "Học tập"
                    else -> "Danh mục khác (#${forecast.category_id})"
                }
                
                Text(
                    categoryName,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Tháng trước: ${formatVND(forecast.current_spending)}",
                    fontFamily = montserrat,
                    fontSize = 12.sp,
                    color = Color(0xFF888888)
                )
            }

            // Right: Prediction + Trend
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatVND(forecast.predicted_spending),
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1B5E20)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    trendText,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = trendColor
                )
            }
        }
    }
}

private fun formatVND(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${formatter.format(amount.toLong())} VND"
}
