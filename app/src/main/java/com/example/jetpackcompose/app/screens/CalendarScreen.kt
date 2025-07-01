package com.example.jetpackcompose.app.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jetpackcompose.app.features.apiService.TransactionAPI.GetTransactionViewModel
import com.example.jetpackcompose.app.network.TransactionResponse
import com.example.jetpackcompose.components.CustomCalendar
import com.example.jetpackcompose.components.DayIndex
import com.example.jetpackcompose.components.MessagePopup
import com.example.jetpackcompose.components.MonthPickerButton
import com.example.jetpackcompose.components.montserrat
import com.example.jetpackcompose.ui.theme.highGray
import com.example.jetpackcompose.ui.theme.primaryColor
import com.example.jetpackcompose.ui.theme.textColor
import com.example.jetpackcompose.ui.theme.topBarColor
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Calendar
import java.util.Locale

data class DailyTransaction(
    val date: String,
    val amountIncome: Long,
    val amountExpense: Long
)

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    val viewModel: GetTransactionViewModel = GetTransactionViewModel(LocalContext.current)
    // Lấy tháng và năm hiện tại làm giá trị mặc định
    val currentMonthYear = remember {
        val calendar = Calendar.getInstance()
        val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
        val year = calendar.get(Calendar.YEAR)
        "$month/$year"
    }

    val currencyFormatter = remember {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
        symbols.decimalSeparator = '.'
        symbols.groupingSeparator = ','
        val format = DecimalFormat("#,###", symbols)
        format
    }

    var selectedMonthYear by rememberSaveable { mutableStateOf(currentMonthYear) }
    var selectedDate by remember { mutableStateOf("") }
    var transactionList by remember { mutableStateOf(listOf<DailyTransaction>()) }
    var dateTransactionList by remember { mutableStateOf<Map<String, List<TransactionResponse.TransactionDetail>>>(emptyMap()) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var showPopup by remember { mutableStateOf(false) }
    val monthYear = selectedMonthYear.substring(0, 7)

    var isDataLoaded1 by remember { mutableStateOf(false) }
    var isDataLoaded2 by remember { mutableStateOf(false) }

    MessagePopup(
        showPopup = showPopup,
        successMessage = successMessage,
        errorMessage = errorMessage,
        onDismiss = { showPopup = false } // Đóng popup khi nhấn ngoài
    )

    // Khởi tạo NavHostController
    LaunchedEffect(selectedMonthYear) {
        errorMessage = ""
        successMessage = "Đang tải dữ liệu..."
        showPopup = true
        val (month, year) = monthYear.split("/").map { it.toInt() }
        Log.d("CalendarScreen", "month: $month, year: $year")
        selectedDate = ""
        viewModel.getTransactions(
            month = month,
            year = year,
            onSuccess1 = { transactions ->
                transactionList = transactions.map { transaction ->
                    DailyTransaction(
                        date = transaction.date,
                        amountIncome = transaction.amountIncome,
                        amountExpense = transaction.amountExpense
                    )
                }
                isDataLoaded1 = true
            },
            onSuccess2 = { transactions ->
                val groupedTransactions = transactions.entries.map { (date, transactionDetails) ->
                    val details = transactionDetails.map {
                        TransactionResponse.TransactionDetail(
                            categoryName = it.categoryName,
                            amount = it.amount,
                            transactionDate = it.transactionDate,
                            note = it.note,
                            type = it.type,
                            transaction_id = it.transaction_id
                        )
                    }
                    date to details
                }

                dateTransactionList = groupedTransactions.toMap().toSortedMap(reverseOrder())

                isDataLoaded2 = true
            },
            onError = { error -> errorMessage = error }
        )
    }

    LaunchedEffect(isDataLoaded1, isDataLoaded2) {
        if (isDataLoaded1 && isDataLoaded2) {
            showPopup = false
            isDataLoaded1 = false
            isDataLoaded2 = false
        }
    }

    val totalExpense = transactionList.sumOf { it.amountExpense }
    val totalIncome = transactionList.sumOf { it.amountIncome }
    val totalBalance = totalIncome - totalExpense


    val formattedBalance = buildAnnotatedString {
        append(
            if (totalBalance >= 0) {
                "+${currencyFormatter.format(totalBalance)}"
            } else {
                currencyFormatter.format(totalBalance)
            }
        )
        withStyle(style = SpanStyle(fontSize = 12.sp)) {  // Kích thước nhỏ hơn cho ký tự "₫"
            append("₫")
        }
    }

    val formattedTotalExpense = buildAnnotatedString {
        append(currencyFormatter.format(totalExpense))
        withStyle(style = SpanStyle(fontSize = 12.sp)) {  // Kích thước nhỏ hơn cho ký tự "₫"
            append("₫")
        }
    }

    val formattedTotalIncome = buildAnnotatedString {
        append(currencyFormatter.format(totalIncome))
        withStyle(style = SpanStyle(fontSize = 12.sp)) {  // Kích thước nhỏ hơn cho ký tự "₫"
            append("₫")
        }
    }

    MaterialTheme(
        typography = Typography(
            bodyLarge = TextStyle(fontWeight = FontWeight.Normal),
            titleLarge = TextStyle(fontWeight = FontWeight.Bold)
        )
    ) {
        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .padding(0.dp)
                        .fillMaxWidth()
                ) {
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

                                // Text "Lịch" căn giữa
                                androidx.compose.material3.Text(
                                    text = "Lịch",
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
                        modifier = Modifier
                            .height(50.dp)
                    )
                    // Divider ngay dưới TopAppBar
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    MonthPickerButton(onDateSelected = { month ->
                        selectedMonthYear = month
                        Log.d("CalendarScreen", "selectedMonthYear: $selectedMonthYear")
                    })
                }

                Spacer(modifier = Modifier.height(16.dp))
                Log.d("CalendarScreen", "selectedMonthYear: $transactionList")
                CustomCalendar(
                    selectedMonthYear = monthYear,
                    transactionList,
                    onDateSelected = { dateSelected ->
                        selectedDate = dateSelected
                    }
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Thu nhập",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = montserrat,
                                fontSize = 12.sp,
                            ),
                            color = textColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Text(
                            text = formattedTotalIncome,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = montserrat,
                                fontSize = 16.sp,
                            ),
                            color = Color(0xff37c8ec),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Chi tiêu",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = montserrat,
                                fontSize = 12.sp,
                            ),
                            color = textColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Text(
                            text = formattedTotalExpense,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                fontFamily = montserrat,
                            ),
                            color = primaryColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Số dư",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = montserrat,
                                fontSize = 12.sp,
                            ),
                            color = textColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Text(
                            text = formattedBalance,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = montserrat,
                                fontSize = 16.sp,
                            ),
                            color = if (totalBalance >= 0) Color(0xff37c8ec) else primaryColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontFamily = montserrat,
                        style = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Center),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(
                        color = highGray,
                        thickness = 0.7.dp
                    )
                    LazyColumn {

                        item {
                            // Truyền navController vào DayIndex
                            DayIndex(
                                dateTransactionList = dateTransactionList,
                                selectedDate = selectedDate,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}


