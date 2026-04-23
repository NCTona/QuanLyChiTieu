# Quản Lý Chi Tiêu — Android App

Ứng dụng Android quản lý chi tiêu cá nhân với Jetpack Compose, tích hợp AI dự đoán chi tiêu trên thiết bị (on-device TFLite).

## Tech Stack

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Kiến trúc | MVVM |
| DI | Hilt (Dagger) |
| Mạng | Retrofit 2 + OkHttp (SSL pinning) |
| AI On-device | TensorFlow Lite 2.14 |
| Điều hướng | Navigation Compose 2.8 |
| Bảo mật | EncryptedSharedPreferences + Tink |
| Nền | WorkManager + NotificationListenerService |
| Build | Gradle KTS (compileSdk 35) |
| Min SDK | 24 (Android 7.0) |

## Yêu cầu

- **Android Studio** Hedgehog trở lên
- **JDK** 17+
- **Android SDK** 35
- **Backend server** đang chạy (Spring Boot)

## Cài đặt

### 1. Clone repo

```bash
git clone https://github.com/NCTona/QuanLyChiTieu.git
cd QuanLyChiTieu
```

### 2. Cấu hình backend URL

Sửa `BaseURL` trong `app/src/main/java/com/example/jetpackcompose/data/remote/ApiService.kt`:

```kotlin
object BaseURL {
    const val baseUrl = "https://your-server-url"
}
```

### 3. Cấu hình SSL Certificate

Đặt certificate của backend vào `app/src/main/res/xml/network_security_config.xml` để cho phép kết nối HTTPS.

### 4. Tạo Release Keystore

```bash
keytool -genkeypair -alias release_key_alias -keyalg RSA -keysize 2048 \
  -storetype JKS -keystore release_keystore.jks -validity 3650
```

Cập nhật đường dẫn keystore trong `app/build.gradle.kts`:

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("path/to/release_keystore.jks")
        storePassword = "your_password"
        keyAlias = "release_key_alias"
        keyPassword = "your_password"
    }
}
```

### 5. Build & Run

```bash
# Windows
.\gradlew.bat assembleDebug

# Linux/macOS
./gradlew assembleDebug
```

Hoặc mở project trong Android Studio và nhấn **Run**.

## Tính năng chính

### Quản lý giao dịch
- Thêm/sửa/xóa giao dịch thu chi
- Tìm kiếm giao dịch theo ghi chú, danh mục, số tiền
- Giao dịch cố định (định kỳ hàng tháng)
- Đọc notification ngân hàng tự động tạo giao dịch

### Ngân sách
- Đặt hạn mức chi tiêu theo danh mục
- Theo dõi hạn mức còn lại

### Báo cáo
- Báo cáo thu/chi theo tháng
- Biểu đồ trực quan (pie chart, bar chart)

### AI Forecast (tích hợp MLOps)
- Dự đoán chi tiêu theo danh mục (LightGBM — server)
- Phát hiện giao dịch bất thường (Isolation Forest — server)
- Cảnh báo ngày chi cao (thống kê — server)
- Tổng hợp AI dashboard (3 model trong 1 màn hình)
- Dự đoán chi tiêu tuần tới (LSTM — on-device TFLite)

### Bảo mật
- JWT Authentication (access + refresh token)
- EncryptedSharedPreferences (lưu token mã hóa)
- SSL/TLS pinning
- Chống giả mạo (SecurityGuard: kiểm tra chữ ký APK, package name, debugger)

## Cấu trúc dự án

```
app/src/main/java/com/example/jetpackcompose/
├── MainActivity.kt                  # Điểm khởi chạy
├── QuanLyChiTieuApp.kt             # Hilt Application class
├── app/
│   ├── AppQuanLyChiTieu.kt         # Điều hướng & routing
│   └── features/
│       └── readNotificationTransaction/
│           ├── ReadTransactionNoti.kt           # NotificationListenerService
│           ├── PostExpenseNotiTransaction.kt     # Tự động tạo chi từ noti
│           ├── PostIncomeNotiTransaction.kt      # Tự động tạo thu từ noti
│           ├── TransactionNotificationScreen.kt  # UI xác nhận giao dịch
│           └── RestartServiceReceiver.kt         # BroadcastReceiver
├── data/
│   ├── local/
│   │   ├── TokenStorage.kt          # EncryptedSharedPreferences
│   │   └── tflite/
│   │       └── ExpenseForecastHelper.kt  # LSTM suy luận trên thiết bị
│   └── remote/
│       ├── ApiService.kt            # Định nghĩa API (Retrofit)
│       ├── AuthInterceptor.kt       # Tự động gắn JWT token
│       ├── TokenAuthenticator.kt    # Tự động refresh token
│       ├── RetrofitProvider.kt      # Khởi tạo Retrofit
│       ├── UnsafeOkHttpClient.kt    # OkHttp client builder
│       └── dto/                     # Các đối tượng truyền dữ liệu
├── di/
│   ├── NetworkModule.kt             # Hilt DI (Retrofit, ApiService)
│   ├── StorageModule.kt             # Hilt DI (TokenStorage)
│   └── AuthApi.kt                   # Qualifier cho Auth API
├── presentation/
│   ├── auth/                        # Đăng nhập, đăng ký, OTP, quên mật khẩu
│   ├── transaction/                 # CRUD giao dịch
│   │   ├── input/                   # Màn hình nhập giao dịch
│   │   ├── edit/                    # Màn hình sửa giao dịch
│   │   ├── list/                    # Danh sách giao dịch
│   │   └── fixed/                   # Giao dịch cố định
│   ├── budget/                      # Hạn mức ngân sách
│   ├── report/                      # Báo cáo thu chi
│   ├── forecast/                    # AI dashboard & dự đoán
│   │   ├── ForecastScreen.kt        # AI tổng hợp dashboard
│   │   ├── CategoryForecastScreen.kt# Dự đoán theo danh mục
│   │   ├── AnomalyScreen.kt        # Phát hiện bất thường
│   │   ├── SpendingAlertScreen.kt   # Cảnh báo chi cao
│   │   └── OnDeviceForecastScreen.kt# LSTM on-device
│   ├── settings/                    # Cài đặt ứng dụng
│   ├── navigation/                  # Thanh điều hướng dưới
│   └── common/                      # Màn hình dùng chung
├── components/
│   ├── ChartAndDialogComponents.kt  # Biểu đồ & hộp thoại
│   ├── FormComponents.kt           # Thành phần nhập liệu
│   └── LayoutComponents.kt         # Bố cục dùng chung
├── security/
│   └── SecurityGuard.kt            # Chống giả mạo ứng dụng
└── ui/
    └── theme/                       # Chủ đề Material 3
```

## Kết nối với hệ thống

Ứng dụng này hoạt động cùng:
- **Spring Boot Backend** — xử lý API, lưu trữ dữ liệu, chạy AI predictions phía server
- **MLOps Server** — train và serve model AI (LightGBM, Isolation Forest, LSTM)

```
Android App  ──HTTPS──>  Spring Boot Backend  ──HTTP──>  MLOps FastAPI
     │                         │
     │                         ├── MySQL (dữ liệu giao dịch)
     │                         └── Upload/Download TFLite model
     │
     └── TFLite (suy luận LSTM trên thiết bị)
```

## Bảo mật

- **JWT** — Access token + Refresh token tự động
- **EncryptedSharedPreferences** — Lưu token an toàn (Tink AES)
- **SSL Pinning** — Chỉ kết nối server tin cậy
- **SecurityGuard** — Kiểm tra chữ ký APK, chống debug, chống clone
- **Network Security Config** — Cấu hình certificate trust
