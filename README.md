# OEE Calculator 📊
OEE (Overall Equipment Effectiveness) Calculator is a simple **Android application** designed for the manufacturing industry to calculate **Availability, Performance, and Quality** and determine the **OEE percentage**.

## Features
-  Calculate OEE based on planned production time, operating time, total count, good count, and ideal cycle time.

## 🛠 Tech Stack
- **Kotlin**
- **Jetpack Compose**
- **Material 3 UI**
- **Firebase (if applicable)**

## 🔧 Installation & Setup
### 1️⃣ Prerequisites
- Android Studio **Giraffe (2023.3.1)+**
- JDK 17+
- Gradle **8+**
- An Android device or emulator running **Android 8+ (API 26+)**

### 2️⃣ Clone Repository
```sh
git clone https://github.com/timrettop/OEECalc.git
cd OEECalc
```
### 3️⃣ Open in Android Studio
1.  Open Android Studio.
2.  Click "Open an Existing Project" and select the OEECalc folder.
3.  Wait for Gradle Sync to complete

### 4️⃣ Run the App
1.  Click ▶️ **Run** in the top-right corner of the screen.

## How OEE is calculated
OEE = Availability × Performance × Quality

Where:
- Availability = (Operating Time) / (Planned Production Time)
- Performance = (Ideal Cycle Time × Total Count) / (Operating Time)
- Quality = (Good Count) / (Total Count)

## 🤝 Contributing

1.	Fork the repository.
2.	Create a new branch `git checkout -b feature-xyz`.
3.	Commit your changes `git commit -m "Added new feature"`.
4.	Push to the branch `git push origin feature-xyz`.
5.	Open a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
