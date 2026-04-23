package com.example.jetpackcompose.presentation.budget

import androidx.hilt.navigation.compose.hiltViewModel

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jetpackcompose.presentation.budget.GetBudgetCategoryViewModel
import com.example.jetpackcompose.presentation.budget.PutLimitTransactionViewModel
import com.example.jetpackcompose.components.BudgetTextField
import com.example.jetpackcompose.components.MessagePopup
import com.example.jetpackcompose.components.MyButtonComponent
import com.example.jetpackcompose.components.montserrat
import com.example.jetpackcompose.data.remote.dto.LimitTransaction
import com.example.jetpackcompose.ui.theme.primaryColor
import com.example.jetpackcompose.ui.theme.textColor
import com.example.jetpackcompose.ui.theme.topBarColor

@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(navController: NavController) {
    val putViewModel = hiltViewModel<PutLimitTransactionViewModel>()

    var showPopup by remember { mutableStateOf(false) }

    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    var houseValue by remember { mutableStateOf(TextFieldValue()) }
    var foodValue by remember { mutableStateOf(TextFieldValue()) }
    var shoppingValue by remember { mutableStateOf(TextFieldValue()) }
    var movingValue by remember { mutableStateOf(TextFieldValue()) }
    var cosmeticValue by remember { mutableStateOf(TextFieldValue()) }
    var exchangingValue by remember { mutableStateOf(TextFieldValue()) }
    var medicalValue by remember { mutableStateOf(TextFieldValue()) }
    var educatingValue by remember { mutableStateOf(TextFieldValue()) }
    var saveValue by remember { mutableStateOf(TextFieldValue()) }

    val context = LocalContext.current
    val viewModel = hiltViewModel<GetBudgetCategoryViewModel>()
    val aiViewModel = hiltViewModel<com.example.jetpackcompose.presentation.forecast.CategoryForecastViewModel>()
    var isLoading by remember { mutableStateOf(true) }

    val suitableIncome = listOf(
        houseValue,
        foodValue,
        shoppingValue,
        movingValue,
        cosmeticValue,
        exchangingValue,
        medicalValue,
        educatingValue,
        saveValue
    ).sumOf { it.text.toLongOrNull() ?: 0L }

    LaunchedEffect(Unit) {
        aiViewModel.loadForecasts()
        viewModel.getBudgetTransaction(
            onError = { isLoading = false },
            onSuccess = {
                houseValue = TextFieldValue(it[0].limitExpense.toString())
                foodValue = TextFieldValue(it[1].limitExpense.toString())
                shoppingValue = TextFieldValue(it[2].limitExpense.toString())
                movingValue = TextFieldValue(it[3].limitExpense.toString())
                cosmeticValue = TextFieldValue(it[4].limitExpense.toString())
                exchangingValue = TextFieldValue(it[5].limitExpense.toString())
                medicalValue = TextFieldValue(it[6].limitExpense.toString())
                educatingValue = TextFieldValue(it[7].limitExpense.toString())
                saveValue = TextFieldValue(it[8].limitExpense.toString())
                isLoading = false
            }
        )
    }

    MessagePopup(
        showPopup = showPopup,
        successMessage = successMessage,
        errorMessage = errorMessage,
        onDismiss = { showPopup = false }
    )

    MaterialTheme {
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
                                    .padding(start = 16.dp, end = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Ngân sách",
                                    fontFamily = montserrat,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = topBarColor
                        ),
                        modifier = Modifier.height(50.dp)
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                }
            }
        ) { paddingValues ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(Color(0xfff5f5f5))
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Thiết lập ngân sách hàng tháng: ",
                        fontFamily = montserrat,
                        color = primaryColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                    listOf(
                        "Nhà ở" to houseValue,
                        "Chi phí ăn uống" to foodValue,
                        "Mua sắm quần áo" to shoppingValue,
                        "Đi lại" to movingValue,
                        "Chăm sóc sắc đẹp" to cosmeticValue,
                        "Giao lưu" to exchangingValue,
                        "Y tế" to medicalValue,
                        "Học tập" to educatingValue,
                        "Khoản tiết kiệm" to saveValue
                    ).forEach { (label, value) ->
                        val categoryId = when (label) {
                            "Nhà ở" -> 1L
                            "Chi phí ăn uống" -> 2L
                            "Mua sắm quần áo" -> 3L
                            "Đi lại" -> 4L
                            "Chăm sóc sắc đẹp" -> 5L
                            "Giao lưu" -> 6L
                            "Y tế" -> 7L
                            "Học tập" -> 8L
                            else -> -1L
                        }
                        val forecast = aiViewModel.forecasts.find { it.category_id == categoryId }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = label,
                                    fontFamily = montserrat,
                                    color = textColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(2f),
                                    lineHeight = 18.sp
                                )
                                Box(modifier = Modifier.weight(7f)) {
                                    BudgetTextField(
                                        amountState = value.text,
                                        onValueChange = { newValue ->
                                            when (label) {
                                                "Nhà ở" -> houseValue = TextFieldValue(newValue)
                                                "Chi phí ăn uống" -> foodValue = TextFieldValue(newValue)
                                                "Mua sắm quần áo" -> shoppingValue = TextFieldValue(newValue)
                                                "Đi lại" -> movingValue = TextFieldValue(newValue)
                                                "Chăm sóc sắc đẹp" -> cosmeticValue = TextFieldValue(newValue)
                                                "Giao lưu" -> exchangingValue = TextFieldValue(newValue)
                                                "Y tế" -> medicalValue = TextFieldValue(newValue)
                                                "Học tập" -> educatingValue = TextFieldValue(newValue)
                                                "Khoản tiết kiệm" -> saveValue = TextFieldValue(newValue)
                                            }
                                        },
                                        colorPercent = Color.Black
                                    )
                                }
                                Text(
                                    text = "đ",
                                    fontFamily = montserrat,
                                    color = textColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            // ===== AI Suggestion Section =====
                            if (forecast != null) {
                                val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale("vi", "VN"))
                                val predicted = forecast.predicted_spending.toLong()
                                val currentBudget = value.text.toLongOrNull() ?: 0L

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    thickness = 0.5.dp,
                                    color = Color.LightGray
                                )

                                if (predicted <= currentBudget && currentBudget > 0) {
                                    // Du doan <= ngan sach -> goi y lam tron len hang chuc nghin
                                    val roundedUp = ((predicted + 9999) / 10000) * 10000
                                    val formattedRounded = formatter.format(roundedUp)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "AI gợi ý: ${formattedRounded}đ",
                                            fontFamily = montserrat,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = primaryColor
                                        )
                                        TextButton(
                                            onClick = {
                                                val newVal = TextFieldValue(roundedUp.toString())
                                                when (label) {
                                                    "Nhà ở" -> houseValue = newVal
                                                    "Chi phí ăn uống" -> foodValue = newVal
                                                    "Mua sắm quần áo" -> shoppingValue = newVal
                                                    "Đi lại" -> movingValue = newVal
                                                    "Chăm sóc sắc đẹp" -> cosmeticValue = newVal
                                                    "Giao lưu" -> exchangingValue = newVal
                                                    "Y tế" -> medicalValue = newVal
                                                    "Học tập" -> educatingValue = newVal
                                                    "Khoản tiết kiệm" -> saveValue = newVal
                                                }
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.textButtonColors(
                                                containerColor = primaryColor,
                                                contentColor = Color.White
                                            ),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Áp dụng",
                                                fontFamily = montserrat,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                } else if (predicted > currentBudget && currentBudget > 0) {
                                    // Du doan > ngan sach -> goi y budget moi
                                    val suggestedNew = predicted
                                    val roundedSuggested = ((suggestedNew + 9999) / 10000) * 10000
                                    val formattedPredicted = formatter.format(predicted)
                                    val overAmount = predicted - currentBudget

                                    // Tinh so du tu cac category khac (loai tiet kiem)
                                    val totalSurplus = aiViewModel.forecasts
                                        .filter { it.category_id != categoryId && it.category_id != 9L && it.budget > 0 }
                                        .filter { it.predicted_spending < it.budget }
                                        .sumOf { (it.budget - it.predicted_spending).toLong() }

                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "Có khả năng vượt ${formatter.format(overAmount)}đ (dự kiến: ${formattedPredicted}đ)",
                                            fontFamily = montserrat,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFE53935)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))

                                        if (roundedSuggested <= totalSurplus + currentBudget) {
                                            // So du du de bu -> goi y budget moi + nut Ap dung
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Gợi ý: ${formatter.format(roundedSuggested)}đ",
                                                    fontFamily = montserrat,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF1565C0)
                                                )
                                                TextButton(
                                                    onClick = {
                                                        val newVal = TextFieldValue(roundedSuggested.toString())
                                                        when (label) {
                                                            "Nhà ở" -> houseValue = newVal
                                                            "Chi phí ăn uống" -> foodValue = newVal
                                                            "Mua sắm quần áo" -> shoppingValue = newVal
                                                            "Đi lại" -> movingValue = newVal
                                                            "Chăm sóc sắc đẹp" -> cosmeticValue = newVal
                                                            "Giao lưu" -> exchangingValue = newVal
                                                            "Y tế" -> medicalValue = newVal
                                                            "Học tập" -> educatingValue = newVal
                                                            "Khoản tiết kiệm" -> saveValue = newVal
                                                        }
                                                    },
                                                    shape = RoundedCornerShape(6.dp),
                                                    colors = ButtonDefaults.textButtonColors(
                                                        containerColor = Color(0xFF1565C0),
                                                        contentColor = Color.White
                                                    ),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "Áp dụng",
                                                        fontFamily = montserrat,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        } else {
                                            // Khong du so du -> khuyen han che
                                            Text(
                                                text = "Số dư cuối tháng không đủ để bù. Nên hạn chế chi tiêu danh mục này.",
                                                fontFamily = montserrat,
                                                fontSize = 11.sp,
                                                color = Color(0xFFFF8F00)
                                            )
                                        }
                                    }
                                } else {
                                    // Chua dat ngan sach -> goi y dua tren du doan
                                    val roundedUp = ((predicted + 9999) / 10000) * 10000
                                    val formattedRounded = formatter.format(roundedUp)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "AI gợi ý: ${formattedRounded}đ",
                                            fontFamily = montserrat,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = primaryColor
                                        )
                                        TextButton(
                                            onClick = {
                                                val newVal = TextFieldValue(roundedUp.toString())
                                                when (label) {
                                                    "Nhà ở" -> houseValue = newVal
                                                    "Chi phí ăn uống" -> foodValue = newVal
                                                    "Mua sắm quần áo" -> shoppingValue = newVal
                                                    "Đi lại" -> movingValue = newVal
                                                    "Chăm sóc sắc đẹp" -> cosmeticValue = newVal
                                                    "Giao lưu" -> exchangingValue = newVal
                                                    "Y tế" -> medicalValue = newVal
                                                    "Học tập" -> educatingValue = newVal
                                                    "Khoản tiết kiệm" -> saveValue = newVal
                                                }
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.textButtonColors(
                                                containerColor = primaryColor,
                                                contentColor = Color.White
                                            ),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Áp dụng",
                                                fontFamily = montserrat,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ) {
                        Text(
                            text = "Phù hợp cho khoản thu đầu vào hàng tháng: %,dđ".format(suitableIncome),
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            fontFamily = montserrat,
                            fontWeight = FontWeight.Normal,
                            color = primaryColor,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ) {
                        MyButtonComponent(
                            value = "Lưu ngân sách",
                            isLoading = false,
                            onClick = {

                                errorMessage = ""
                                successMessage = "Đang gửi dữ liệu..."
                                showPopup = true

                                val categoryLimits = listOf(
                                    LimitTransaction.CategoryLimit(1, houseValue.text.toLongOrNull() ?: 0L),
                                    LimitTransaction.CategoryLimit(2, foodValue.text.toLongOrNull() ?: 0L),
                                    LimitTransaction.CategoryLimit(3, shoppingValue.text.toLongOrNull() ?: 0L),
                                    LimitTransaction.CategoryLimit(4, movingValue.text.toLongOrNull() ?: 0L),
                                    LimitTransaction.CategoryLimit(5, cosmeticValue.text.toLongOrNull() ?: 0L),
                                    LimitTransaction.CategoryLimit(6, exchangingValue.text.toLongOrNull() ?: 0L),
                                    LimitTransaction.CategoryLimit(7, medicalValue.text.toLongOrNull() ?: 0L),
                                    LimitTransaction.CategoryLimit(8, educatingValue.text.toLongOrNull() ?: 0L),
                                    LimitTransaction.CategoryLimit(9, saveValue.text.toLongOrNull() ?: 0L)
                                )

                                val limitTransaction: LimitTransaction = LimitTransaction(categoryLimits)
                                putViewModel.addLimitTransaction(
                                    data = limitTransaction.limits,
                                     onError = {
                                         successMessage = ""
                                         errorMessage = "Có lỗi xảy ra khi gửi dữ liệu!"
                                         showPopup = true
                                     },
                                    onSuccess = {
                                        errorMessage = ""
                                        successMessage = "Gửi dữ liệu thành công!"
                                        showPopup = true
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BudgetScreenPreview() {
//    BudgetScreen()
}
