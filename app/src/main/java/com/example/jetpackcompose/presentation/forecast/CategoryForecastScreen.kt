package com.example.jetpackcompose.presentation.forecast

import androidx.hilt.navigation.compose.hiltViewModel

import androidx.compose.foundation.background
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

    LaunchedEffect(Unit) {
        viewModel.loadForecasts()
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
                                    onClick = { navController.popBackStack() },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .offset(x = (-8).dp)
                                        .offset(y = (1).dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                                        contentDescription = "Quay lại",
                                        tint = primaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Text(
                                    text = "Dự đoán chi tiêu",
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
                                onClick = { viewModel.loadForecasts() },
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
                        "Dự đoán chi tiêu cuối tháng",
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
                            ForecastCategoryCard(forecast = forecast)
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
    val statusColor = when (forecast.status) {
        "over_budget" -> Color(0xFFE53935)
        "warning" -> Color(0xFFFF8F00)
        "safe" -> Color(0xFF43A047)
        else -> Color(0xFF757575)
    }

    val statusText = when (forecast.status) {
        "over_budget" -> "Vượt ngân sách"
        "warning" -> "Cảnh báo"
        "safe" -> "An toàn"
        "no_budget" -> "Chưa đặt ngân sách"
        else -> forecast.status
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Row 1: Category name + Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f)
                )

                // Status badge
                Text(
                    statusText,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(statusColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Current spent + Predicted
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Đã chi",
                        fontFamily = montserrat,
                        fontSize = 11.sp,
                        color = Color(0xFF888888)
                    )
                    Text(
                        "Dự kiến cuối tháng",
                        fontFamily = montserrat,
                        fontSize = 11.sp,
                        color = Color(0xFF888888)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        formatVND(forecast.current_spent),
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Color(0xFF333333)
                    )

                    Text(
                        formatVND(forecast.predicted_spending),
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = statusColor
                    )
                }
            }

            // Row 3: Budget info
            if (forecast.budget > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Ngân sách: ${formatVND(forecast.budget)}",
                        fontFamily = montserrat,
                        fontSize = 11.sp,
                        color = Color(0xFF888888)
                    )
                    Text(
                        "Sử dụng: ${forecast.budget_used_pct}%",
                        fontFamily = montserrat,
                        fontSize = 11.sp,
                        color = Color(0xFF888888)
                    )
                }
            }

            // Row 4: Suggestion
            if (forecast.suggestion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    forecast.suggestion,
                    fontFamily = montserrat,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor
                )
            }

            // Row 5: Suggested daily
            if (forecast.suggested_daily > 0) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Nên chi ~${formatVND(forecast.suggested_daily)}/ngày",
                    fontFamily = montserrat,
                    fontSize = 11.sp,
                    color = Color(0xFF1565C0)
                )
            }
        }
    }
}

private fun formatVND(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${formatter.format(amount.toLong())} VND"
}
