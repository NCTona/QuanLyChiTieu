package com.example.jetpackcompose.components

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcompose.R
import com.example.jetpackcompose.app.screens.Category
import com.example.jetpackcompose.app.screens.DailyTransaction
import com.example.jetpackcompose.ui.theme.saturdayColor
import com.example.jetpackcompose.ui.theme.sundayColor
import com.example.jetpackcompose.ui.theme.textColorPrimary
import com.example.jetpackcompose.ui.theme.bgColor
import com.example.jetpackcompose.ui.theme.bgItemColor
import com.example.jetpackcompose.ui.theme.primaryColor
import com.example.jetpackcompose.ui.theme.secondaryColor
import com.example.jetpackcompose.ui.theme.componentShapes
import com.example.jetpackcompose.ui.theme.highGray
import com.example.jetpackcompose.ui.theme.textColor
import java.lang.StrictMath.PI
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.sin


val montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)

@Composable
fun NormalTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .heightIn(min = 32.dp)
            .fillMaxWidth(),
        style = TextStyle(
            fontSize = 18.sp,
            fontFamily = montserrat,
            fontWeight = FontWeight.Normal,
        ),
        color = textColor,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun HeadingTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .heightIn(min = 48.dp)
            .fillMaxWidth(),
        style = TextStyle(
            fontSize = 28.sp,
            fontFamily = montserrat,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal,
        ),
        color = textColorPrimary,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun MyTextFieldComponent(
    value: String,
    onValueChange: (String) -> Unit,
    labelValue: String,
    painterResource: Painter,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    isPassword: Boolean = false
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val isFocused = remember { mutableStateOf(false) }
    val isPasswordVisible = remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(componentShapes.small)
            .onFocusChanged { focusState ->
                isFocused.value = focusState.isFocused
            },
        shape = RoundedCornerShape(10.dp),
        label = {
            Text(
                text = labelValue,
                fontFamily = montserrat,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = if (isFocused.value) primaryColor else Color.LightGray
            )
        },
        value = value,
        onValueChange = onValueChange,
        visualTransformation = if (isPassword && !isPasswordVisible.value) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = primaryColor,
            focusedLabelColor = primaryColor,
            unfocusedLabelColor = Color.LightGray,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = primaryColor,
            textColor = textColor,
            backgroundColor = bgColor
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()  // Clear focus from the text field
                keyboardController?.hide()  // Hide the keyboard
            }
        ),
        leadingIcon = {
            Icon(
                painter = painterResource,
                contentDescription = "Leading icon for $labelValue",
                tint = highGray
            )
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                    Icon(
                        painter = painterResource(
                            if (isPasswordVisible.value) R.drawable.baseline_key_24 else R.drawable.baseline_key_off_24
                        ),
                        contentDescription = if (isPasswordVisible.value) "Hide password" else "Show password",
                        tint = highGray
                    )
                }
            }
        } else null
    )
}


@Composable
fun PasswordTextFieldComponent(
    value: String,
    onValueChange: (String) -> Unit,
    labelValue: String,
    painterResource: Painter
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val isFocused = remember { mutableStateOf(false) }
    val passwordVisibility = remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(componentShapes.small)
            .onFocusChanged { focusState ->
                isFocused.value = focusState.isFocused
            },
        shape = RoundedCornerShape(10.dp),
        label = {
            Text(
                text = labelValue,
                fontFamily = montserrat,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = if (isFocused.value) primaryColor else Color.LightGray
            )
        },
        value = value,
        onValueChange = onValueChange,
        visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = primaryColor,
            focusedLabelColor = primaryColor,
            unfocusedLabelColor = Color.LightGray,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = primaryColor,
            textColor = textColor,
            backgroundColor = bgColor
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        ),
        leadingIcon = {
            Icon(
                painter = painterResource,
                contentDescription = "Leading icon for $labelValue",
                tint = highGray
            )
        },
        trailingIcon = {
            val icon = if (passwordVisibility.value) {
                painterResource(R.drawable.outline_visibility_off_24)
            } else {
                painterResource(R.drawable.outline_visibility)
            }
            val description = if (passwordVisibility.value) {
                "Ẩn mật khẩu"
            } else {
                "Hiện mật khẩu"
            }
            IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
                Icon(
                    painter = icon,
                    contentDescription = description,
                    tint = highGray
                )
            }
        }
    )
}


@Composable
fun CheckboxComponent(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Cách đều trên dưới
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = primaryColor,
                uncheckedColor = Color.Gray,
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(8.dp)) // Tạo khoảng cách giữa checkbox và text
        Text(
            text = text,
            fontFamily = montserrat, // Đảm bảo khai báo `monsterrat`
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = Color(0xFF777777),
            modifier = Modifier.weight(1f) // Tự động chiếm không gian còn lại
        )
    }
}


@Composable
fun DrawBottomLine(height: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .drawBehind {
                val strokeWidth = 0.8.dp.toPx()
                val y = size.height / 2
                drawLine(
                    color = Color.LightGray,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
    )
}

//Tạo layout với LazyColumn
@Composable
fun CategoriesGrid(
    categories: List<Category>,
    buttonColor: Color,
    selectedCategory: Category?,
    column: Int,
    onCategorySelected: (Category) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(column),
        contentPadding = PaddingValues(4.dp), // Loại bỏ padding xung quanh lưới
    ) {
        items(categories) { category ->
            CategoryItem(
                column = column,
                category = category,
                buttonColor = buttonColor,
                isSelected = (category == selectedCategory),
                percentage = category.percentage,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun CategoryItem(
    column: Int,
    category: Category,
    buttonColor: Color,
    isSelected: Boolean,
    percentage: Float,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) buttonColor else Color.Transparent

    // State to hold the size of the Box
    var boxHeight by remember { mutableFloatStateOf(0f) }

    // Tạo Animatable để điều khiển offset cho sóng
    val waveOffset = remember { Animatable(0f) }

    // Tạo hiệu ứng sóng uốn lượn
    LaunchedEffect(key1 = true) {
        waveOffset.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2500, // Tăng durationMillis để làm chậm animation
                    easing = LinearEasing // Sử dụng LinearEasing để chuyển động mượt mà
                ),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    val adjustedPercentage = if (percentage < 0.001f) -0.1f else percentage

    val waveYOffset by animateFloatAsState(
        targetValue = boxHeight * (1 - adjustedPercentage - 0.04f),
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = ""
    )

    val height = if (column == 2) 120.dp else 90.dp
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .padding(4.dp)
            .border(
                BorderStroke(2.dp, borderColor),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.White)
            .onSizeChanged { size ->
                // Lưu chiều cao của Box khi thay đổi kích thước
                boxHeight = size.height.toFloat()
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val waveHeight = size.height * 0.04f // Giảm waveHeight để sóng nhỏ hơn
            val waveLength = size.width * 0.8f // Giảm waveLength để sóng dày hơn
            val waveY = waveYOffset // Vị trí sóng thay đổi theo hiệu ứng từ animateFloatAsState
            val offset = waveOffset.value * waveLength // Di chuyển sóng theo phương ngang

            // Vẽ sóng uốn lượn
            val wavePath = Path().apply {
                moveTo(0f, waveY)
                for (x in 0..size.width.toInt() step 20) {
                    val y =
                        waveY + waveHeight * sin(2.0f * PI.toFloat() * (x.toFloat() + offset) / waveLength)
                    lineTo(x.toFloat(), y)
                }
                lineTo(size.width + 50f, size.height)
                lineTo(0f, size.height)
                close()
            }

            // Vẽ nền
            drawRect(
                color = if (percentage < 0.001f) Color(0xFFF6DADA) else bgItemColor,
                size = size
            )

            // Vẽ path sóng
            drawPath(
                path = wavePath,
                color = if (percentage < 0.1f) Color(0xFFFF7A7A) else Color(0xFFB3E5FC) // Thực ra là 2 màu giống nhau
            )
        }

        // Hiển thị icon và tên danh mục
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.align(Alignment.Center)
        ) {
            // Icon
            IconButton(
                onClick = { onClick() },
                colors = IconButtonDefaults.iconButtonColors(contentColor = category.iconColor),
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 2.dp)
                    .size(32.dp)
            ) {
                androidx.compose.material3.Icon(
                    painter = category.iconPainter(),
                    contentDescription = category.name
                )
            }

            // Tên danh mục
            androidx.compose.material3.Text(
                category.name,
                color = Color.Gray,
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}


@Composable
fun MyButtonComponent(value: String, onClick: () -> Unit, isLoading: Boolean) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(top = 16.dp),
        enabled = !isLoading // Disable button while loading
    ) {
        if (isLoading) {
            // Hiển thị CircularProgressIndicator khi đang loading
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            // Hiển thị text bình thường khi không loading
            Text(
                value,
                color = Color.White,
                fontFamily = montserrat,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}


@Composable
fun ClickableTextComponent(value: String, onClick: () -> Unit) {
    Text(
        value,
        color = secondaryColor,
        fontFamily = com.example.jetpackcompose.components.montserrat,
        fontWeight = FontWeight.Light,
        fontSize = 10.sp,
        modifier = Modifier
            .padding(top = 5.dp)
            .clickable(onClick = onClick)
    )
}

fun formatNumber(input: String): String {
    return input.replace(",", "").toLongOrNull()?.let {
        String.format(Locale.US, "%,d", it) // Sử dụng Locale.US để đảm bảo định dạng đúng
    } ?: ""
}

@Composable
fun NumberTextField(amountState: String, onValueChange: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = FocusRequester()
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = formatNumber(amountState),
        onValueChange = { newInput ->
            if (newInput == "0" && amountState.isEmpty()) {
                // do nothing to block the first '0'
            } else {
                val filteredInput = newInput.filter { it.isDigit() }
                onValueChange(
                    if (filteredInput.isNotEmpty() && filteredInput != "0") {
                        filteredInput
                    } else if (filteredInput == "0" && amountState.isNotEmpty()) {
                        filteredInput
                    } else {
                        ""
                    }
                )
            }
        },
                singleLine = true,
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .height(30.dp)
            .width(220.dp)
            .background(Color(0xFFe7e7e7), shape = componentShapes.small)
            .border(
                1.dp,
                if (isFocused) primaryColor else Color.Transparent,
                componentShapes.small,
            )
            .padding(horizontal = 8.dp),
        textStyle = TextStyle(
            textAlign = TextAlign.Start,
            fontSize = 20.sp,
            fontFamily = montserrat,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()  // Clear focus from the text field
                keyboardController?.hide()  // Hide the keyboard
            }
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (amountState.isEmpty()) {
                    Text(
                        if (isFocused) "" else "0",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = montserrat,
                        fontSize = 20.sp,
                        style = LocalTextStyle.current
                    )
                }
                innerTextField()
            }
        },
    )
}

@Preview
@Composable
fun PreviewNumberTextField() {
    NumberTextField(amountState = "", onValueChange = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteTextField(textState: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var textNote by remember { mutableStateOf("") }
    androidx.compose.material3.OutlinedTextField(
        modifier = Modifier
            .width(250.dp),
        value = textState,
        onValueChange = onValueChange,
        colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = primaryColor
        ),
        placeholder = {
            Text(
                "Chưa nhập vào",
                color = Color.LightGray,
                fontFamily = montserrat,
            )
        },
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontFamily = montserrat,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                textNote = textState.toString()
                focusManager.clearFocus()  // Clear focus from the text field
                keyboardController?.hide()  // Hide the keyboard
            }
        ),
    )
}

@SuppressLint("DiscouragedApi")
@Composable
fun MonthPickerButton(onDateSelected: (String) -> Unit) {
    var dateText by remember { mutableStateOf("") }
    var dateRangeText by remember { mutableStateOf("") }
    var showMonthPicker by remember { mutableStateOf(false) }
    val calendar = remember { mutableStateOf(Calendar.getInstance()) }

    val monthYearFormat = SimpleDateFormat("MM/yyyy", Locale("vi", "VN"))

    fun updateDateText() {
        dateText = monthYearFormat.format(calendar.value.time)

        // Lấy ngày đầu tháng và cuối tháng
        val firstDay = calendar.value.clone() as Calendar
        val lastDay = calendar.value.clone() as Calendar
        firstDay.set(Calendar.DAY_OF_MONTH, 1)
        lastDay.set(Calendar.DAY_OF_MONTH, calendar.value.getActualMaximum(Calendar.DAY_OF_MONTH))

        // Format khoảng ngày
        val dayMonthFormat = SimpleDateFormat("d/M", Locale("vi", "VN"))
        dateRangeText = "(${dayMonthFormat.format(firstDay.time)} - ${dayMonthFormat.format(lastDay.time)})"

        onDateSelected("$dateText $dateRangeText")
    }

    LaunchedEffect(key1 = Unit) {
        updateDateText()
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
                calendar.value.add(Calendar.MONTH, -1)
                updateDateText()
            },
            modifier = Modifier
                .weight(1f)
                .size(20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                modifier = Modifier.size(14.dp),
                contentDescription = "Previous Month",
                tint = Color(0xFF444444)
            )
        }
        Button(
            onClick = { showMonthPicker = true },
            shape = componentShapes.small,
            modifier = Modifier
                .height(30.dp)
                .weight(8f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE7E7E7)),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dateText,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = montserrat,
                    fontSize = 16.sp,
                    color = textColor,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = dateRangeText,
                    fontWeight = FontWeight.Light,
                    fontFamily = montserrat,
                    fontSize = 12.sp,
                    color = textColor
                )
            }
        }
        IconButton(
            onClick = {
                calendar.value.add(Calendar.MONTH, +1)
                updateDateText()
            },
            modifier = Modifier
                .weight(1f)
                .size(20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_arrow_forward_ios_24),
                contentDescription = "Next Month",
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF444444)
            )
        }
    }

    if (showMonthPicker) {
        MonthPickerDialog(
            currentYear = calendar.value.get(Calendar.YEAR),
            currentMonth = calendar.value.get(Calendar.MONTH),
            onDismiss = { showMonthPicker = false },
            onMonthYearSelected = { selectedMonth, selectedYear ->
                calendar.value.set(Calendar.YEAR, selectedYear)
                calendar.value.set(Calendar.MONTH, selectedMonth)
                updateDateText()
                showMonthPicker = false
            }
        )
    }
}


@Preview
@Composable
fun PreviewMonthPickerButton() {
    MonthPickerButton(onDateSelected = {})
}


@SuppressLint("UnusedBoxWithConstraintsScope", "DefaultLocale")
@Composable
fun CustomCalendar(
    selectedMonthYear: String,
    transactionList: List<DailyTransaction>,
    onDateSelected: (String) -> Unit // Callback để trả về ngày đã chọn
) {
    val calendar = Calendar.getInstance()
    val currencyFormatter = remember {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
        symbols.decimalSeparator = '.'
        symbols.groupingSeparator = ','
        val format = DecimalFormat("#,###", symbols)
        format
    }

    // Tách tháng và năm từ chuỗi truyền vào
    val parts = selectedMonthYear.split("/")
    val month = parts[0].toInt() - 1 // Tháng trong Calendar là từ 0 đến 11
    val year = parts[1].toInt()

    // Thiết lập tháng và năm cho Calendar
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    // Tính toán số ngày trong tháng và các ngày cần hiển thị
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek =
        (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Điều chỉnh ngày đầu tuần để bắt đầu từ Thứ Hai

    // Tính toán số ngày trong tháng sau
    calendar.add(Calendar.MONTH, 1)
    val daysInNextMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    calendar.add(Calendar.MONTH, -1) // Quay lại tháng hiện tại

    val days = mutableListOf<String>()
    val leadingEmptyDays = firstDayOfWeek // Số ngày trống trước khi bắt đầu tháng
    for (i in 1..leadingEmptyDays) {
        days.add("") // Thêm ô trống cho các ngày của tháng trước
    }

    // Thêm ngày trong tháng hiện tại
    for (i in 1..daysInMonth) {
        days.add(i.toString())
    }

    // Thêm ngày của tháng sau vào cuối lịch
    val trailingEmptyDays =
        (7 - (days.size % 7)) % 7 // Tính số ô trống cần thêm sau ngày cuối của tháng hiện tại
    for (i in 1..trailingEmptyDays) {
        days.add("") // Thêm ô trống cho các ngày của tháng sau
    }

    // Header cho lịch (Các ngày trong tuần)
    val daysOfWeek = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

    // Tính số hàng của lịch (5 hoặc 6 hàng)
    val rows = days.chunked(7) // Chia thành các hàng, mỗi hàng 7 ngày
    val calendarHeight =
        if (rows.size == 6) 230.dp else 200.dp // Nếu có 6 hàng, chiều cao là 230.dp

    // Trạng thái cho ngày được chọn
    val selectedDate = remember { mutableStateOf("") }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(calendarHeight)
            .background(Color.White)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Hàng các ngày trong tuần
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f) // Mỗi ngày trong tuần có cùng trọng số
                            .height(10.dp)
                            .background(Color(0xFFe1e1e1))
                            .border(0.25.dp, Color(0xFFd4d4d4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontWeight = FontWeight.Normal,
                            fontSize = 8.sp,
                            fontFamily = montserrat,
                            color = if (day == "CN") sundayColor else if (day == "T7") saturdayColor else textColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Lưới ngày
            rows.forEachIndexed { _, week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    week.forEachIndexed { columnIndex, day ->
                        Box(
                            modifier = Modifier
                                .weight(1f) // Mỗi ô ngày có cùng trọng số
                                .height(35.dp)
                                .background(
                                    when {
                                        day.isEmpty() -> Color(0xfff1f1f1) // Màu nền cho ngày của tháng trước và tháng sau
                                        day == selectedDate.value -> Color(0xFFF8E6D6) // Màu nền cho ngày được chọn
                                        else -> Color.White
                                    }
                                )
                                .border(0.25.dp, Color(0xFFd4d4d4))
                                .clickable {
                                    // Thực hiện hành động khi người dùng chọn ngày
                                    if (day.isNotEmpty()) {
                                        val formattedDay = String.format("%02d", day.toInt())
                                        val selectedDay = "$year-${month + 1}-$formattedDay"
                                        selectedDate.value = selectedDay
                                        onDateSelected(selectedDay) // Trả về ngày đã chọn qua callback
                                    }
                                },
                            contentAlignment = Alignment.TopStart
                        ) {
                            Column(
                                modifier = Modifier.padding(4.dp)
                            ) {
                                if (day.isNotEmpty()) {
                                    // Hiển thị ngày
                                    Text(
                                        text = day,
                                        color = when (columnIndex) {
                                            6 -> sundayColor // Chủ nhật
                                            5 -> saturdayColor // Thứ 7
                                            else -> Color.Black // Các ngày trong tuần
                                        },
                                        fontFamily = montserrat,
                                        fontSize = 8.sp,
                                        textAlign = TextAlign.Start
                                    )

                                    // Chuyển ngày thành định dạng yyyy-MM-dd để so sánh
                                    val formattedDay = String.format("%02d", day.toInt())
                                    val formattedMonth = String.format("%02d", month + 1)
                                    val transactionDate = "$year-${formattedMonth}-$formattedDay"

                                    // Kiểm tra xem ngày có giao dịch không và hiển thị amountIncome và amountExpense
                                    val transaction =
                                        transactionList.find { it.date == transactionDate }
                                    Log.d("CustomCalendar", "transaction: $transaction")
                                    transaction?.let {
                                        // Hiển thị amountIncome nếu không bằng 0
                                        if (it.amountIncome > 0) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                Text(
                                                    text = currencyFormatter.format(it.amountIncome),
                                                    color = Color(0xff37c8ec),
                                                    fontSize = 7.sp,
                                                    textAlign = TextAlign.End,
                                                    fontFamily = montserrat
                                                )
                                            }
                                        }
                                        // Hiển thị amountExpense nếu không bằng 0
                                        if (it.amountExpense > 0) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                Text(
                                                    text = currencyFormatter.format(it.amountExpense),
                                                    color = primaryColor,
                                                    fontSize = 7.sp,
                                                    textAlign = TextAlign.End,
                                                    fontFamily = montserrat
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryProgress(
    categoryName: String,
    transactionNote: String,
    transactionAmount: Long,
    transactionType: String,
    categoryPercent: Float
) {
    val currencyFormatter = remember {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
        symbols.decimalSeparator = '.'
        symbols.groupingSeparator = ','

        val format = DecimalFormat("#,###", symbols)
        format
    }

    val amountText = buildAnnotatedString {
        append(currencyFormatter.format(transactionAmount))
        withStyle(style = SpanStyle(fontSize = 10.sp)) {  // Kích thước nhỏ hơn cho ký tự "₫"
            append("₫")
        }
    }

    // Danh sách các Category
    val categories = listOf(
        Category(
            1,
            "Chi phí nhà ở",
            { painterResource(R.drawable.outline_home_work_24) },
            Color(0xFFB40300),
            1.00f
        ),
        Category(
            2,
            "Ăn uống",
            { painterResource(R.drawable.outline_ramen_dining_24) },
            Color(0xFF911294),
            1.00f
        ),
        Category(
            3,
            "Mua sắm quần áo",
            { painterResource(R.drawable.clothes) },
            Color(0xFF0C326E),
            1.00f
        ),
        Category(
            4,
            "Đi lại",
            { painterResource(R.drawable.outline_train_24) },
            Color(0xFF126AB6),
            1.00f
        ),
        Category(
            5,
            "Chăm sóc sắc đẹp",
            { painterResource(R.drawable.outline_cosmetic) },
            Color(0xFF0D96DA),
            1.00f
        ),
        Category(
            6,
            "Giao lưu",
            { painterResource(R.drawable.entertainment) },
            Color(0xFF4DB218),
            1.00f
        ),
        Category(
            7,
            "Y tế",
            { painterResource(R.drawable.outline_health_and_safety_24) },
            Color(0xFFD5CC00),
            1.00f
        ),
        Category(
            8,
            "Học tập",
            { painterResource(R.drawable.outline_education) },
            Color(0xFFEE9305),
            1.00f
        ),
        Category(
            10,
            "Tiền lương",
            { painterResource(R.drawable.salary) },
            Color(0xFFfb791d),
            1.00f
        ),
        Category(
            11,
            "Tiền thưởng",
            { painterResource(R.drawable.baseline_card_giftcard_24) },
            Color(0xFF37c166),
            1.00f
        ),
        Category(
            12,
            "Thu nhập phụ",
            { painterResource(R.drawable.secondary) },
            Color(0xFFf95aa9),
            1.00f
        ),
        Category(13, "Trợ cấp", { painterResource(R.drawable.subsidy) }, Color(0xFF0000FF), 1.00f)
    )

    // Tìm Category phù hợp với categoryName
    val category = categories.find { it.name == categoryName }

    // Nếu tìm thấy Category, hiển thị icon và tên
    category?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth(if (it.id < 10) 0.95f else 1f)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(
                    painter = it.iconPainter(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                    tint = it.iconColor
                )

                Text(
                    text = it.name,
                    fontFamily = montserrat,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (transactionNote.isNotEmpty()) {
                    Text(
                        text = "(${transactionNote})",
                        fontFamily = montserrat,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Text(
                    text = amountText,
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Bold,
                    color = if (transactionType == "expense") primaryColor else Color(0xff37c8ec),
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "(${String.format("%.2f", categoryPercent * 100)}%)",
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Light,
                    fontSize = 10.sp,
                    color = if (transactionType == "expense") primaryColor else Color(0xff37c8ec),
                    textAlign = TextAlign.End
                )

            }


            Log.d("CategoryProgress", "name: ${categoryName}, process: ${categoryPercent}")
            // Thanh tiến trình
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(top = 2.dp)
            ) {
//              Nền của thanh tiến trình
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        size = size ,
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                }
                // Tiến trình chính theo tỷ lệ
                Canvas(modifier = Modifier
                    .fillMaxWidth(categoryPercent)
                    .height(8.dp)
                ) {
                    val progressColor = if (categoryPercent > 1f) {
                        Brush.linearGradient(
                            colors = listOf(
                                it.iconColor.copy(alpha = 0.8f),
                                it.iconColor.copy(alpha = 1f)
                            )
                        )
                    } else {
                        SolidColor(it.iconColor.copy(alpha = 0.8f))
                    }
                    drawRoundRect(
                        brush = progressColor,
                        size = size,
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                }
            }
        }
    }
}









