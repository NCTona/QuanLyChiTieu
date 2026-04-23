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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavHostController
import com.example.jetpackcompose.R
import com.example.jetpackcompose.presentation.forecast.AIDashboardViewModel
import com.example.jetpackcompose.data.remote.dto.CategoryForecastResponse
import com.example.jetpackcompose.components.montserrat
import com.example.jetpackcompose.ui.theme.primaryColor
import com.example.jetpackcompose.ui.theme.topBarColor
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<AIDashboardViewModel>()

    LaunchedEffect(Unit) {
        viewModel.loadSummary()
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
                                    .fillMaxWidth(),
                            ) {
                                IconButton(
                                    onClick = { navController.popBackStack("mainscreen", inclusive = false) },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .offset(x = (-8).dp)
                                        .offset(y = (1).dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                                        contentDescription = "Quay lai",
                                        tint = primaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Text(
                                    text = "Phân tích AI",
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
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = topBarColor
                        ),
                        modifier = Modifier.height(50.dp)
                    )
                    HorizontalDivider(
                        color = Color.LightGray,
                        thickness = 1.dp
                    )
                }
            }
        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xfff5f5f5))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ===== Loading =====
                if (viewModel.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = primaryColor)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Đang phân tích dữ liệu AI...",
                                    fontFamily = montserrat,
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                // ===== Error =====
                viewModel.errorMessage?.let { error ->
                    item {
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
                                Text(error, fontFamily = montserrat, fontSize = 12.sp, color = Color(0xFF666666))
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.loadSummary() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Thử lại", fontFamily = montserrat, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // ===== Dashboard Content =====
                viewModel.summary?.let { summary ->

                    // Section 1: Du doan chi tieu (On-device TFLite)
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("on_device_forecast") }
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Dự báo xu hướng ăn uống",
                                        fontFamily = montserrat,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        "Dự đoán chi tiêu ăn uống tuần tới bằng AI TFLite trực tiếp trên điện thoại",
                                        fontFamily = montserrat,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }

                    // Section 2: Anomaly Badge (IForest)
                    if (summary.anomaly_count > 0) {
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { navController.navigate("anomaly_detect") }
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Badge(containerColor = Color(0xFFD32F2F)) {
                                        Text(
                                            "${summary.anomaly_count}",
                                            fontFamily = montserrat,
                                            fontSize = 12.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Phát hiện khoản chi bất thường",
                                            fontFamily = montserrat,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = Color(0xFFC62828)
                                        )
                                        summary.top_anomalies.firstOrNull()?.let { firstAnomaly ->
                                            Text(
                                                firstAnomaly.message,
                                                fontFamily = montserrat,
                                                fontSize = 11.sp,
                                                color = Color(0xFF666666),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Section 3: Category Forecasts (LightGBM)
                    if (summary.category_forecasts.isNotEmpty()) {
                        item {
                            Text(
                                "Dự đoán theo danh mục (tháng này)",
                                fontFamily = montserrat,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        val topCategories = summary.category_forecasts.take(3)
                        items(topCategories) { forecast ->
                            DashboardCategoryCard(forecast = forecast)
                        }

                        if (summary.category_forecasts.size > 3) {
                            item {
                                TextButton(
                                    onClick = { navController.navigate("category_forecast") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Xem tất cả ${summary.category_forecasts.size} danh mục ->",
                                        fontFamily = montserrat,
                                        fontSize = 13.sp,
                                        color = primaryColor
                                    )
                                }
                            }
                        }
                    }

                    // Section 4: Spending Alerts (Pattern)
                    if (summary.upcoming_alerts.isNotEmpty()) {
                        item {
                            Text(
                                "Cảnh báo chi tiêu sắp tới",
                                fontFamily = montserrat,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        items(summary.upcoming_alerts) { alert ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text("Ngày", fontSize = 14.sp, fontFamily = montserrat, fontWeight = FontWeight.Bold, color = Color(0xFFFF8F00))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Ngày ${alert.day_of_month} - ${alert.category_name}",
                                            fontFamily = montserrat,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF333333)
                                        )
                                        Text(
                                            alert.message,
                                            fontFamily = montserrat,
                                            fontSize = 11.sp,
                                            color = Color(0xFF666666),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Section 5: Info Card
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "Về các model AI",
                                    fontFamily = montserrat,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    "- TFLite (On-device): Dự đoán chi tiêu ăn uống tuần tới dựa trên thói quen cá nhân\n" +
                                            "- LightGBM: Dự đoán chi tiêu tháng này theo từng danh mục\n" +
                                            "- Isolation Forest: Phát hiện giao dịch bất thường",
                                    fontFamily = montserrat,
                                    fontSize = 11.sp,
                                    color = Color(0xFF555555),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardCategoryCard(forecast: CategoryForecastResponse) {
    val statusColor = when (forecast.status) {
        "over_budget" -> Color(0xFFE53935)
        "warning" -> Color(0xFFFF8F00)
        "safe" -> Color(0xFF43A047)
        else -> Color(0xFF757575)
    }
    val statusText = when (forecast.status) {
        "over_budget" -> "Vượt NS"
        "warning" -> "Cảnh báo"
        "safe" -> "An toàn"
        "no_budget" -> "Chưa đặt"
        else -> forecast.status
    }

    val categoryName = when (forecast.category_id) {
        1L -> "Chi phí nhà ở"
        2L -> "Ăn uống"
        3L -> "Mua sắm quần áo"
        4L -> "Đi lại"
        5L -> "Chăm sóc sắc đẹp"
        6L -> "Giao lưu"
        7L -> "Y tế"
        8L -> "Học tập"
        else -> "Danh mục #${forecast.category_id}"
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    categoryName,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF333333)
                )
                Text(
                    "Đã chi: ${formatCurrency(forecast.current_spent)}",
                    fontFamily = montserrat,
                    fontSize = 11.sp,
                    color = Color(0xFF888888)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatCurrency(forecast.predicted_spending),
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = statusColor
                )
                Text(
                    statusText,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = statusColor
                )
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${formatter.format(amount.toLong())} VND"
}
