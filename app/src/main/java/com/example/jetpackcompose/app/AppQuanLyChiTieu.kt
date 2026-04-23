package com.example.jetpackcompose.app

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpackcompose.data.local.TokenStorage
import com.example.jetpackcompose.app.features.readNotificationTransaction.PostExpenseNotiTransaction
import com.example.jetpackcompose.app.features.readNotificationTransaction.PostIncomeNotiTransaction
import com.example.jetpackcompose.app.features.readNotificationTransaction.TransactionNotificationScreen
import com.example.jetpackcompose.app.features.readNotificationTransaction.TransactionStorage
import com.example.jetpackcompose.presentation.transaction.edit.EditExpenseTransaction
import com.example.jetpackcompose.presentation.transaction.edit.EditFixedExpenseTransaction
import com.example.jetpackcompose.presentation.transaction.edit.EditIncomeExpenseTransaction
import com.example.jetpackcompose.presentation.transaction.edit.EditIncomeTransaction
import com.example.jetpackcompose.presentation.budget.BudgetScreen
import com.example.jetpackcompose.presentation.transaction.list.CalendarScreen
import com.example.jetpackcompose.presentation.common.MainScreen
import com.example.jetpackcompose.presentation.settings.OtherScreen
import com.example.jetpackcompose.presentation.transaction.fixed.AnualScreen
import com.example.jetpackcompose.presentation.transaction.fixed.InputFixedTab
import com.example.jetpackcompose.presentation.transaction.list.FindCalendarScreen
import com.example.jetpackcompose.presentation.auth.SignInScreen
import com.example.jetpackcompose.presentation.auth.SignInViewModel
import com.example.jetpackcompose.presentation.auth.SignUpScreen
import com.example.jetpackcompose.presentation.auth.ForgotPasswordScreen
import com.example.jetpackcompose.presentation.auth.OTPContent
import com.example.jetpackcompose.presentation.auth.SetPasswordContent
import com.example.jetpackcompose.presentation.forecast.ForecastScreen
import com.example.jetpackcompose.presentation.forecast.OnDeviceForecastScreen
import com.example.jetpackcompose.presentation.forecast.CategoryForecastScreen
import com.example.jetpackcompose.presentation.forecast.AnomalyScreen
import com.example.jetpackcompose.presentation.forecast.SpendingAlertScreen
import com.example.jetpackcompose.components.montserrat
import com.example.jetpackcompose.ui.theme.primaryColor
import com.example.jetpackcompose.ui.theme.textColor
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppQuanLyChiTieu(transactionStorage: TransactionStorage) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val signInViewModel: SignInViewModel = hiltViewModel()
    val transactions =
        transactionStorage.transactionsState.value

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showLogOutDialog by rememberSaveable { mutableStateOf(false) }

    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    val isDialogShown by remember {
        mutableStateOf(
            sharedPreferences.getBoolean(
                "is_dialog_shown",
                false
            )
        )
    }
    var isLoggedIn = TokenStorage(context).isLoggedIn()
    var isRefreshTokenExpired = TokenStorage(context).isRefreshTokenExpired(context)

    LaunchedEffect(Unit) {
        while (true) {
            isRefreshTokenExpired =
                TokenStorage(context).isRefreshTokenExpired(context)
            delay(30_000) // check mỗi 30s
        }
    }

    LaunchedEffect(transactions, isDialogShown) {
        Log.d("AppQuanLyChiTieu", "Transactions: $transactions")
        Log.d("AppQuanLyChiTieu", "isDialogShown: $isDialogShown")
        if (!transactionStorage.isEmpty()) {
            // Chỉ điều hướng nếu có giao dịch
            navController.navigate("transactionNotification")
            sharedPreferences.edit().putBoolean("is_dialog_shown", true).apply()
        } else {
            if (!isDialogShown) {
                showDialog = true
                // Đặt lại trạng thái hiển thị thông báo
                sharedPreferences.edit().putBoolean("is_dialog_shown", true).apply()
            }
        }
    }

    LaunchedEffect (isRefreshTokenExpired) {
        if (isRefreshTokenExpired) {
            showLogOutDialog = true
        }
    }


    if (showDialog && isLoggedIn) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    "Giao dịch biến động số dư",
                    fontFamily = montserrat,
                    color = Color(0xff222222),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    "Có giao dịch từ biến động số dư mới được nhận, bạn có muốn thêm không?",
                    fontFamily = montserrat,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate("transactionNotification")
                }) {
                    Text(
                        "OK",
                        fontFamily = montserrat,
                        color = primaryColor
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    // Không cần lưu lại trạng thái "bỏ qua" vì muốn dialog hiển thị lại khi mở lại app
                }) {
                    Text(
                        "Bỏ qua",
                        color = Color.Gray,
                        fontFamily = montserrat
                    )
                }
            }
        )
    }

    if (showLogOutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogOutDialog = false
                TokenStorage(context).clear()
                navController.navigate("signin")
            },
            title = {
                Text(
                    "Phiên đăng nhập đã hết hạn",
                    fontFamily = montserrat,
                    color = Color(0xff222222),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    "Vui lòng đăng nhập lại.",
                    fontFamily = montserrat,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogOutDialog = false
                    TokenStorage(context).clear()
                    navController.navigate("signin")
                }) {
                    Text(
                        "OK",
                        fontFamily = montserrat,
                        color = primaryColor
                    )
                }
            }
        )
    }

    val startDestination = if (!isLoggedIn) "signup" else "mainscreen"

    NavHost(navController = navController, startDestination = startDestination) {
        addAllRoutes(navController)
    }
}

/**
 * Extension function chứa toàn bộ navigation routes.
 * Tránh duplicate giữa nhánh đã đăng nhập và chưa đăng nhập.
 */
private fun NavGraphBuilder.addAllRoutes(navController: NavHostController) {
    // Auth
    composable("signup") { SignUpScreen(navController) }
    composable("signin") { SignInScreen(navController) }
    composable("forgotpassword") { ForgotPasswordScreen(navController) }
    composable(
        "setPassword/{email}",
        arguments = listOf(navArgument("email") { type = NavType.StringType })
    ) {
        val email = it.arguments?.getString("email") ?: ""
        SetPasswordContent(navController, email)
    }
    composable(
        "otp/{email}",
        arguments = listOf(navArgument("email") { type = NavType.StringType })
    ) {
        val email = it.arguments?.getString("email") ?: ""
        OTPContent(navController, email)
    }

    // Main
    composable("mainscreen") { MainScreen(navController) }
    composable("anual") { AnualScreen(navController) }
    composable("other") { OtherScreen(navController) }
    composable("inputfixedtab") { InputFixedTab(navController) }
    composable("calendar") { CalendarScreen(navController) }
    composable("budget") { BudgetScreen(navController) }
    composable("findTransaction") { FindCalendarScreen(navController) }
    composable("transactionNotification") { TransactionNotificationScreen(navController) }

    // AI / Forecast
    composable("forecast") { ForecastScreen(navController) }
    composable("on_device_forecast") { OnDeviceForecastScreen(navController) }
    composable("category_forecast") { CategoryForecastScreen(navController) }
    composable("anomaly_detect") { AnomalyScreen(navController) }
    composable("spending_alert") { SpendingAlertScreen(navController) }

    // Edit transactions
    composable(
        "editExpense/{transactionId}?date={transactionDate}",
        arguments = listOf(
            navArgument("transactionId") { type = NavType.IntType },
            navArgument("transactionDate") { type = NavType.StringType; nullable = true }
        )
    ) { backStackEntry ->
        val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: 0
        val transactionDate = backStackEntry.arguments?.getString("transactionDate") ?: ""
        EditExpenseTransaction(
            navController = navController,
            transactionId = transactionId,
            transactionDate = transactionDate
        )
    }

    composable(
        "editIncome/{transactionId}?date={transactionDate}",
        arguments = listOf(
            navArgument("transactionId") { type = NavType.IntType },
            navArgument("transactionDate") { type = NavType.StringType; nullable = true }
        )
    ) { backStackEntry ->
        val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: 0
        val transactionDate = backStackEntry.arguments?.getString("transactionDate") ?: ""
        EditIncomeTransaction(
            navController = navController,
            transactionId = transactionId,
            transactionDate = transactionDate
        )
    }

    composable(
        "editFixedExpense/{fixedTransactionId}?startDate={startDate}&endDate={endDate}",
        arguments = listOf(
            navArgument("fixedTransactionId") { type = NavType.IntType },
            navArgument("startDate") { type = NavType.StringType; nullable = true },
            navArgument("endDate") { type = NavType.StringType; nullable = true }
        )
    ) { backStackEntry ->
        val fixedTransactionId = backStackEntry.arguments?.getInt("fixedTransactionId") ?: 0
        val startDate = backStackEntry.arguments?.getString("startDate")
        val endDate = backStackEntry.arguments?.getString("endDate")
        EditFixedExpenseTransaction(
            navController = navController,
            fixedTransactionId = fixedTransactionId,
            startDate = startDate,
            endDate = endDate
        )
    }

    composable(
        "editFixedIncome/{fixedTransactionId}?startDate={startDate}&endDate={endDate}",
        arguments = listOf(
            navArgument("fixedTransactionId") { type = NavType.IntType },
            navArgument("startDate") { type = NavType.StringType; nullable = true },
            navArgument("endDate") { type = NavType.StringType; nullable = true }
        )
    ) { backStackEntry ->
        val fixedTransactionId = backStackEntry.arguments?.getInt("fixedTransactionId") ?: 0
        val startDate = backStackEntry.arguments?.getString("startDate")
        val endDate = backStackEntry.arguments?.getString("endDate")
        EditIncomeExpenseTransaction(
            navController = navController,
            fixedTransactionId = fixedTransactionId,
            startDate = startDate,
            endDate = endDate
        )
    }

    // Notification transactions
    composable("postExpenseNotiTransaction/{amount}/{selectedDate}/{index}") { backStackEntry ->
        val amount = backStackEntry.arguments?.getString("amount")?.toLongOrNull() ?: 0L
        val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
        val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
        PostExpenseNotiTransaction(navController, amount, selectedDate, index)
    }
    composable("postIncomeNotiTransaction/{amount}/{selectedDate}/{index}") { backStackEntry ->
        val amount = backStackEntry.arguments?.getString("amount")?.toLongOrNull() ?: 0L
        val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
        val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
        PostIncomeNotiTransaction(navController, amount, selectedDate, index)
    }
}
