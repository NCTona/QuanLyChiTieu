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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpackcompose.app.features.apiService.LogAPI.SignInViewModel
import com.example.jetpackcompose.app.features.apiService.LogAPI.SignInViewModelFactory
import com.example.jetpackcompose.app.features.ReadNotificationTransaction.PostExpenseNotiTransaction
import com.example.jetpackcompose.app.features.ReadNotificationTransaction.PostIncomeNotiTransaction
import com.example.jetpackcompose.app.features.ReadNotificationTransaction.TransactionNotificationScreen
import com.example.jetpackcompose.app.features.ReadNotificationTransaction.TransactionStorage
import com.example.jetpackcompose.app.features.editFeatures.EditExpenseTransaction
import com.example.jetpackcompose.app.features.editFeatures.EditFixedExpenseTransaction
import com.example.jetpackcompose.app.features.editFeatures.EditIncomeExpenseTransaction
import com.example.jetpackcompose.app.features.editFeatures.EditIncomeTransaction
import com.example.jetpackcompose.app.screens.BudgetScreen
import com.example.jetpackcompose.app.screens.CalendarScreen
import com.example.jetpackcompose.app.screens.MainScreen
import com.example.jetpackcompose.app.screens.OtherScreen
import com.example.jetpackcompose.app.screens.anual_sceens.AnualScreen
import com.example.jetpackcompose.app.screens.anual_sceens.InputFixedTab
import com.example.jetpackcompose.app.screens.find_calendar.FindCalendarScreen
import com.example.jetpackcompose.app.screens.login_signup.SignInScreen
import com.example.jetpackcompose.app.screens.login_signup.SignUpScreen
import com.example.jetpackcompose.app.screens.login_signup.forgot_password.ForgotPasswordScreen
import com.example.jetpackcompose.app.screens.login_signup.forgot_password.OTPContent
import com.example.jetpackcompose.app.screens.login_signup.forgot_password.SetPasswordContent
import com.example.jetpackcompose.components.montserrat
import com.example.jetpackcompose.ui.theme.primaryColor
import com.example.jetpackcompose.ui.theme.textColor

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppQuanLyChiTieu(transactionStorage: TransactionStorage) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val signInViewModel: SignInViewModel = viewModel(
        factory = SignInViewModelFactory(context)
    )
    val transactions =
        transactionStorage.transactionsState.value // Lấy trạng thái danh sách giao dịch

    var showDialog by rememberSaveable { mutableStateOf(false) }
    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    val isDialogShown by remember {
        mutableStateOf(
            sharedPreferences.getBoolean(
                "is_dialog_shown",
                false
            )
        )
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

    if (showDialog) {
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

    // Kiểm tra nếu Token đã được xác nhận hay không
    if (signInViewModel.isTokenCleared()) {
        NavHost(navController = navController, startDestination = "signup") {
            composable("signup") { SignUpScreen(navController) }
            composable("signin") { SignInScreen(navController) }
            composable("forgotpassword") { ForgotPasswordScreen(navController) }
            composable(
                "setPassword/{email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType })
            )
            {
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
            composable("mainscreen") { MainScreen(navController) }
            composable("anual") { AnualScreen(navController) }
            composable("other") { OtherScreen(navController) }
            composable("inputfixedtab") { InputFixedTab(navController) }
            composable("calendar") { CalendarScreen(navController) }
            composable("budget") { BudgetScreen(navController) }
            composable("findTransaction") { FindCalendarScreen(navController) }
            composable("transactionNotification") { TransactionNotificationScreen(navController) }

            // Chỉnh sửa giao dịch (truyền transactionId)
            composable(
                "editExpense/{transactionId}?date={transactionDate}",
                arguments = listOf(
                    navArgument("transactionId") { type = NavType.IntType },
                    navArgument("transactionDate") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                // Lấy các tham số từ NavArgument
                val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: 0
                val transactionDate = backStackEntry.arguments?.getString("transactionDate") ?: ""

                // Chuyển đến màn hình chỉnh sửa chi tiêu
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
                // Lấy các tham số từ NavArgument
                val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: 0
                val transactionDate = backStackEntry.arguments?.getString("transactionDate") ?: ""

                // Chuyển đến màn hình chỉnh sửa thu nhập
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
                // Lấy các tham số từ NavArguments
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
                // Lấy các tham số từ NavArguments
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
    } else {
        NavHost(navController = navController, startDestination = "mainscreen") {
            composable("signup") { SignUpScreen(navController) }
            composable("signin") { SignInScreen(navController) }
            composable("forgotpassword") { ForgotPasswordScreen(navController) }
            composable(
                "setPassword/{email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType })
            )
            {
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
            composable("mainscreen") { MainScreen(navController) }
            composable("anual") { AnualScreen(navController) }
            composable("other") { OtherScreen(navController) }
            composable("inputfixedtab") { InputFixedTab(navController) }
            composable("calendar") { CalendarScreen(navController) }
            composable("budget") { BudgetScreen(navController) }
            composable("findTransaction") { FindCalendarScreen(navController) }
            composable("transactionNotification") { TransactionNotificationScreen(navController) }


            // Chỉnh sửa giao dịch (truyền transactionId)
            composable(
                "editExpense/{transactionId}?date={transactionDate}",
                arguments = listOf(
                    navArgument("transactionId") { type = NavType.IntType },
                    navArgument("transactionDate") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                // Lấy các tham số từ NavArgument
                val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: 0
                val transactionDate = backStackEntry.arguments?.getString("transactionDate") ?: ""

                // Chuyển đến màn hình chỉnh sửa chi tiêu
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
                // Lấy các tham số từ NavArgument
                val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: 0
                val transactionDate = backStackEntry.arguments?.getString("transactionDate") ?: ""

                // Chuyển đến màn hình chỉnh sửa thu nhập
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
                // Lấy các tham số từ NavArguments
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
                // Lấy các tham số từ NavArguments
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
    }
}




