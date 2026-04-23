# Quan Ly Chi Tieu — Android App

Ung dung Android quan ly chi tieu ca nhan voi Jetpack Compose, tich hop AI du doan chi tieu tren thiet bi (on-device TFLite).

## Tech Stack

| Thanh phan | Cong nghe |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM |
| DI | Hilt (Dagger) |
| Network | Retrofit 2 + OkHttp (SSL pinning) |
| AI On-device | TensorFlow Lite 2.14 |
| Navigation | Navigation Compose 2.8 |
| Security | EncryptedSharedPreferences + Tink |
| Background | WorkManager + NotificationListenerService |
| Build | Gradle KTS (compileSdk 35) |
| Min SDK | 24 (Android 7.0) |

## Yeu cau

- **Android Studio** Hedgehog tro len
- **JDK** 17+
- **Android SDK** 35
- **Backend server** dang chay (Spring Boot)

## Cai dat

### 1. Clone repo

```bash
git clone https://github.com/NCTona/QuanLyChiTieu.git
cd QuanLyChiTieu
```

### 2. Cau hinh backend URL

Sua `BaseURL` trong `app/src/main/java/com/example/jetpackcompose/data/remote/ApiService.kt`:

```kotlin
object BaseURL {
    const val baseUrl = "https://your-server-url"
}
```

### 3. Cau hinh SSL Certificate

Dat certificate cua backend vao `app/src/main/res/xml/network_security_config.xml` de cho phep ket noi HTTPS.

### 4. Tao Release Keystore

```bash
keytool -genkeypair -alias release_key_alias -keyalg RSA -keysize 2048 \
  -storetype JKS -keystore release_keystore.jks -validity 3650
```

Cap nhat duong dan keystore trong `app/build.gradle.kts`:

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

Hoac mo project trong Android Studio va nhan **Run**.

## Tinh nang chinh

### Quan ly giao dich
- Them/sua/xoa giao dich thu chi
- Tim kiem giao dich theo ghi chu, danh muc, so tien
- Giao dich co dinh (dinh ky hang thang)
- Doc notification ngan hang tu dong tao giao dich

### Ngan sach
- Dat han muc chi tieu theo danh muc
- Theo doi han muc con lai

### Bao cao
- Bao cao thu/chi theo thang
- Bieu do truc quan (pie chart, bar chart)

### AI Forecast (tich hop MLOps)
- Du doan chi tieu theo danh muc (LightGBM — server)
- Phat hien giao dich bat thuong (Isolation Forest — server)
- Canh bao ngay chi cao (thong ke — server)
- Tong hop AI dashboard (3 model trong 1 man hinh)
- Du doan chi tieu tuan toi (LSTM — on-device TFLite)

### Bao mat
- JWT Authentication (access + refresh token)
- EncryptedSharedPreferences (token storage)
- SSL/TLS pinning
- Anti-tampering (SecurityGuard: kiem tra chu ky APK, package name, debugger)

## Cau truc du an

```
app/src/main/java/com/example/jetpackcompose/
├── MainActivity.kt                  # Entry point
├── QuanLyChiTieuApp.kt             # Hilt Application class
├── app/
│   ├── AppQuanLyChiTieu.kt         # App navigation & routing
│   └── features/
│       └── readNotificationTransaction/
│           ├── ReadTransactionNoti.kt           # NotificationListenerService
│           ├── PostExpenseNotiTransaction.kt     # Tu dong tao chi tu noti
│           ├── PostIncomeNotiTransaction.kt      # Tu dong tao thu tu noti
│           ├── TransactionNotificationScreen.kt  # UI xac nhan giao dich
│           └── RestartServiceReceiver.kt         # BroadcastReceiver
├── data/
│   ├── local/
│   │   ├── TokenStorage.kt          # EncryptedSharedPreferences
│   │   └── tflite/
│   │       └── ExpenseForecastHelper.kt  # LSTM on-device inference
│   └── remote/
│       ├── ApiService.kt            # Retrofit API definitions
│       ├── AuthInterceptor.kt       # Tu dong gan JWT token
│       ├── TokenAuthenticator.kt    # Tu dong refresh token
│       ├── RetrofitProvider.kt      # Retrofit factory
│       ├── UnsafeOkHttpClient.kt    # OkHttp client builder
│       └── dto/                     # Data Transfer Objects
├── di/
│   ├── NetworkModule.kt             # Hilt DI (Retrofit, ApiService)
│   ├── StorageModule.kt             # Hilt DI (TokenStorage)
│   └── AuthApi.kt                   # Qualifier cho Auth API
├── presentation/
│   ├── auth/                        # Dang nhap, dang ky, OTP, forgot password
│   ├── transaction/                 # CRUD giao dich
│   │   ├── input/                   # Man hinh nhap giao dich
│   │   ├── edit/                    # Man hinh sua giao dich
│   │   ├── list/                    # Danh sach giao dich
│   │   └── fixed/                   # Giao dich co dinh
│   ├── budget/                      # Han muc ngan sach
│   ├── report/                      # Bao cao thu chi
│   ├── forecast/                    # AI dashboard & predictions
│   │   ├── ForecastScreen.kt        # AI tong hop dashboard
│   │   ├── CategoryForecastScreen.kt# Du doan theo danh muc
│   │   ├── AnomalyScreen.kt        # Phat hien bat thuong
│   │   ├── SpendingAlertScreen.kt   # Canh bao chi cao
│   │   └── OnDeviceForecastScreen.kt# LSTM on-device
│   ├── settings/                    # Cai dat ung dung
│   ├── navigation/                  # Bottom navigation bar
│   └── common/                      # Man hinh dung chung
├── components/
│   ├── ChartAndDialogComponents.kt  # Bieu do & dialog
│   ├── FormComponents.kt           # Form input components
│   └── LayoutComponents.kt         # Layout dung chung
├── security/
│   └── SecurityGuard.kt            # Anti-tampering protection
└── ui/
    └── theme/                       # Material 3 theme
```

## Ket noi voi he thong

App nay hoat dong cung:
- **Spring Boot Backend** — xu ly API, luu tru du lieu, chay AI predictions phia server
- **MLOps Server** — train va serve model AI (LightGBM, Isolation Forest, LSTM)

```
Android App  ──HTTPS──>  Spring Boot Backend  ──HTTP──>  MLOps FastAPI
     │                         │
     │                         ├── MySQL (du lieu giao dich)
     │                         └── Upload/Download TFLite model
     │
     └── TFLite (on-device LSTM inference)
```

## Bao mat

- **JWT** — Access token + Refresh token tu dong
- **EncryptedSharedPreferences** — Luu token an toan (Tink AES)
- **SSL Pinning** — Chi ket noi server tin cay
- **SecurityGuard** — Kiem tra chu ky APK, chong debug, chong clone
- **Network Security Config** — Cau hinh certificate trust
