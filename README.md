# Android System Monitor for AOSP Settings

A system monitoring tool integrated into Android Settings app for LineageOS/AOSP ROMs.

## Features

- **File System Monitor** - View internal and external storage usage
- **Process Monitor** - List all running apps with memory usage  
- **Resource Monitor** - Real-time CPU, Memory, Battery, and Temperature

## Screenshots

Settings Entry → System Monitor Dashboard → Three Tabs (File System | Processes | Resources)

## Requirements

- LineageOS 20 (Android 13) or compatible AOSP source code
- Build environment setup (Ubuntu 20.04+, 16GB RAM, 250GB storage)
- Device with unlocked bootloader

## Installation

### 1. Copy Files to AOSP Settings

```bash
cd ~/AOSP/lineage-20.0  # Your AOSP source directory

# Copy Java files
cp -r systemmonitor packages/apps/Settings/src/com/android/settings/

# Copy resource files
cp -r layout/* packages/apps/Settings/res/layout/
cp -r drawable/* packages/apps/Settings/res/drawable/
cp -r menu/* packages/apps/Settings/res/menu/
cp -r xml/* packages/apps/Settings/res/xml/
```

### 2. Update Android.bp

Edit `packages/apps/Settings/Android.bp` and add:

```
static_libs: [
    "androidx.viewpager2_viewpager2",
    "androidx.recyclerview_recyclerview",
],
```

### 3. Update AndroidManifest.xml

Edit `packages/apps/Settings/AndroidManifest.xml`:

**Add permissions (before `<application>` tag):**
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_SUPERUSER" />
```

**Add Activity (inside `<application>` tag):**
```xml
<activity
    android:name=".systemmonitor.SystemMonitorActivity"
    android:label="@string/system_monitor_title"
    android:theme="@android:style/Theme.DeviceDefault.Settings"
    android:exported="true" />
```

### 4. Add Strings

Add all strings from `values/strings.xml` to `packages/apps/Settings/res/values/strings.xml`

### 5. Update Top Level Settings

Edit `packages/apps/Settings/res/xml/top_level_settings.xml` and add the preference entry (see included `xml/top_level_settings.xml` for reference).

## Building

```bash
# Navigate to AOSP source
cd ~/AOSP/lineage-20.0

# Setup environment
source build/envsetup.sh
lunch lineage_<your_device>-userdebug

# Build Settings app only
mmm packages/apps/Settings
```

## Installing

```bash
# Connect device via USB
adb root
adb remount

# Push Settings APK
adb push out/target/product/<device>/system/priv-app/Settings/Settings.apk /system/priv-app/Settings/

# Reboot device
adb reboot
```

## Usage

1. Open Settings app
2. Scroll to "System Monitor"
3. Tap to open
4. View three tabs: File System, Processes, Resources
5. Use refresh button to update data

## File Structure

```
AndroidTaskManager/
├── systemmonitor/              # Java source files (7 files)
├── layout/                     # UI layouts (5 files)
├── drawable/                   # Icons (3 files)
├── menu/                       # Menu resource (1 file)
├── values/                     # Strings
├── xml/                        # Preferences
├── Android.bp                  # Build config snippet
└── AndroidManifest.xml         # Manifest snippet
```

## How It Works

- **Settings Integration**: PreferenceController adds entry to main Settings page
- **Dashboard Fragment**: Launches the System Monitor Activity
- **Main Activity**: Manages three tabs using ViewPager2
- **Fragments**: Each tab is a separate Fragment that monitors specific data
- **Data Collection**: Uses Android system APIs (StatFs, ActivityManager, etc.)
- **Background Threading**: Data collection runs in background to avoid UI freeze

## Troubleshooting

**Build Error: "Cannot find ViewPager2"**
- Add `androidx.viewpager2_viewpager2` to Android.bp

**Build Error: "RecyclerView not found"**  
- Add `androidx.recyclerview_recyclerview` to Android.bp

**System Monitor not showing in Settings**
- Check top_level_settings.xml integration
- Verify strings are added correctly

**App crashes on launch**
- Verify Activity declaration in AndroidManifest.xml
- Check theme is `Theme.DeviceDefault.Settings`

**External storage shows "N/A"**
- Normal if device has no SD card
- Check storage permissions if SD card exists

## Contributing

Feel free to fork and submit pull requests!

Ideas for contributions:
- Network traffic monitoring
- Process kill functionality
- Historical data graphs
- Export data feature

## License

Apache License 2.0

## Credits

Built for LineageOS 20 (Android 13)

YouTube Tutorial: [Yet to publish]

---

⭐ Star this repo if you found it helpful!