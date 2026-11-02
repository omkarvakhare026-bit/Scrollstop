# ScrollStop — War on Endless Scrolling 🛑⚡

> **A native Android weapon engineered for high-performance discipline against mindless Instagram Reels and YouTube Shorts scrolling.**

Targeted specifically for **Redmi 12 5G** (and all Android devices running API 26+ / Android 8.0 to Android 14+).

---

## 🏗️ Architecture & Technology Stack

- **Platform:** Native Android (Kotlin 1.9.24, Jetpack Compose, Material 3 Dark Theme)
- **Engine:** Android `AccessibilityService` (`ScrollStopAccessibilityService`)
- **Database:** Room Database 2.6.1 (`ScrollStopDatabase`)
  - `DailyStatEntity`: Today's reels, Instagram vs YouTube split, reminder count, stops vs continues.
  - `ReelScrollEventEntity`: Raw timestamped log of transitions with matched node IDs and confidence rating.
  - `MotivationalItemEntity`: Pre-seeded warrior reality checks + user custom uploaded images & quotes.
- **Preferences:** Jetpack DataStore Preferences (`UserPreferencesRepository`).
- **Overlay Interventions:** Standalone `WindowManager` overlay (`TYPE_APPLICATION_OVERLAY`) with fallback to translucent fullscreen Activity (`ReminderOverlayActivity`).
- **Image Storage:** Private internal storage (`files/motivation_images/`) to prevent permission expiration and maintain offline permanence.
- **CI / Build:** GitHub Actions (`.github/workflows/build-apk.yml`) with automated Gradle debug APK compilation and artifact delivery.

---

## 🚀 How to Build & Download the APK (Cloud Build)

Because you do not need to install the heavy Android Studio IDE or local SDKs on your computer, your repository is configured with a automated GitHub Actions workflow.

### Step 1: Push Code to your GitHub Repository
```bash
git init
git add .
git commit -m "feat: complete ScrollStop native Android application"
git branch -M main
git remote add origin https://github.com/<YOUR_USERNAME>/<YOUR_REPO_NAME>.git
git push -u origin main
```

### Step 2: Automatic Compilation
1. Open your repository on GitHub in your phone or PC browser.
2. Click the **"Actions"** tab at the top.
3. You will see **"Build ScrollStop Debug APK"** running automatically.
4. Wait approximately 2–3 minutes for the green checkmark (`Assemble Debug APK`).

### Step 3: Download the APK
1. Click on the completed workflow run.
2. Scroll to the bottom under **"Artifacts"**.
3. Download **`ScrollStop-Debug-APK`** (contains `app-debug.apk`).
4. Transfer to or open directly on your **Redmi 12 5G**.

---

## 📱 Redmi 12 5G (MIUI / HyperOS) Setup & Permissions

Xiaomi devices (MIUI 14 and HyperOS) feature aggressive battery killers that suppress background services and floating overlays. ScrollStop includes a dedicated **Redmi 12 5G setup wizard** right inside the app under the **Permissions** tab:

1. **Accessibility Service ("ScrollStop Reel Guard"):**
   - Tap **"Grant Accessibility"** in ScrollStop.
   - Tap **Downloaded apps** > **ScrollStop Reel Guard**.
   - Turn **ON** the switch and accept the security prompt.
2. **Display Over Other Apps (Overlay):**
   - Tap **"Grant Overlay Permission"** in ScrollStop.
   - Toggle **Allow display over other apps**.
3. **MIUI Specific Adjustments (Crucial for Redmi 12 5G):**
   - **Autostart:** Open the Permissions tab in ScrollStop > tap **"1. Enable Autostart in Security"** > Toggle ON for ScrollStop.
   - **Pop-up Windows in Background:** Tap **"2. Allow Pop-up Windows in Background"** > tap **Other permissions** > Allow **"Display pop-up windows while running in the background"**.
   - **Battery Saver:** Tap **"3. Battery Saver: Set to 'No Restrictions'"** > Select **No restrictions**.

---

## 🧪 Testing Reel Detection & The 5-Reel Reminder

### 1. Instant Overlay Test:
- Open ScrollStop > On the **Dashboard**, tap **"TEST STRICT INTERVENTION OVERLAY"**.
- The menacing dark red alert will immediately take over the screen, trigger harsh vibration, display your motivational quote/image, and present the two decisive choices:
  - **"I'M GETTING BACK TO WORK"** (Closes Instagram/YouTube, navigates immediately to the Home screen, and logs a STOP victory).
  - **"CONTINUE SCROLLING"** (Dismisses overlay and increments your continuation tally).

### 2. Live Verification in Detection Lab:
- Go to the **Detection Lab** tab in ScrollStop.
- Split-screen or switch between ScrollStop and Instagram / YouTube.
- Scroll through Reels or Shorts:
  - Confirmed transitions will show green **`CONFIRMED REEL`** tags with extracted author/title identifiers.
  - Scroll gestures will show amber **`INFERRED REEL`** tags.
  - Regular feed posts are automatically filtered with **`IGNORED (Feed or Non-Reel Window)`**.
- Once your reel count hits **5**, the strict intervention overlay triggers automatically.

---

## 🎨 Motivation Gallery

- Go to the **Motivation** tab.
- Tap **"ADD"** to select any photo from your phone's gallery (e.g., your dream physique, dream car, family, exam target, bank balance).
- Add your custom reality check text.
- Choose whether the app displays your **Fixed Active Motivation** or **Random Selection** on each 5-reel alert.
- Stored permanently in app-private storage.
