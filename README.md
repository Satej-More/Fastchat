# Fastchat 🚀

Fastchat is a modern, real-time Android messaging application built with Java and Firebase. It features a unique **End-to-End Encryption (E2E)** system powered by Python integration via Chaquopy.

## ✨ Features

- **Real-time Messaging**: Instant message delivery using Firebase Firestore and Realtime Database.
- **User Authentication**: Secure Sign-In and Sign-Up flows via Firebase Auth.
- **Advanced Encryption**: Custom E2E encryption/decryption using a multi-layer cipher (Vigenere + Polybius) implemented in Python.
- **Push Notifications**: Stay updated with Firebase Cloud Messaging (FCM).
- **Responsive Design**: Uses scalable DP/SP units to ensure a consistent look across all screen sizes.
- **Image Support**: Profile and chat image handling with RoundedImageView.

## 🛠️ Tech Stack

- **Frontend**: Android (Java), XML
- **Backend**: Firebase (Auth, Firestore, Realtime DB, Storage, FCM)
- **Logic Integration**: Chaquopy (Python 3.12)
- **Networking**: Retrofit, Scalars Converter
- **UI Libraries**: Material Components, RoundedImageView, SDP/SSP for responsiveness.

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Koala or newer recommended)
- **JDK 17** or higher
- **Python 3.12** installed on your local machine
- A physical Android device or Emulator (API 21+)

### Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/fastchat.git
   ```

2. **Firebase Setup**:
   - Create a project on the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android App with the package name `com.example.fastchat`.
   - Download the `google-services.json` file and place it in the `app/` directory.
   - Enable **Email/Password Auth**, **Firestore**, and **Realtime Database** in your Firebase project.

3. **Configure Python Path**:
   In `app/build.gradle`, ensure the `buildPython` path matches your local Python installation:
   ```gradle
   python {
       buildPython "C:/Path/To/Your/Python/python.exe"
   }
   ```

4. **Build & Run**:
   Sync the project with Gradle files and run it on your device.

## 🔒 Security (E2E Encryption)
Fastchat uses a custom hybrid encryption logic found in `src/main/python/FInal_Project.py`. It combines:
1. **Vigenere Cipher**: For initial character shifts based on a secure keyword.
2. **Polybius Square**: For numerical encoding of the cipher text.
3. **Custom Character Mapping**: To handle special characters and digits securely.

## 📦 Dependencies
- `firebase-bom:32.7.2`
- `chaquopy:15.0.0`
- `retrofit:2.9.0`
- `roundedimageview:2.3.0`
- `sdp-android:1.1.0`

---
*Created with ❤️ for more secure communication.*
