# App Signing Configuration Guide

## What is App Signing?

App signing is the process of digitally signing your Android app with a certificate. This is **REQUIRED** to publish your app to Google Play Store.

### Why is it needed?
- **Identity**: Proves the app is from you (the developer)
- **Security**: Prevents tampering and ensures updates come from you
- **Google Play Requirement**: You cannot publish without signing your app
- **Updates**: Google Play uses the signature to verify updates are from the same developer

## How App Signing Works

1. **Keystore File**: A secure file (`.jks` or `.keystore`) that contains your signing certificate
2. **Private Key**: The key used to sign your app (stored in the keystore)
3. **Certificate**: Proof of your identity (also in the keystore)

**⚠️ CRITICAL WARNING**: If you lose your keystore or forget the password, you **CANNOT** update your app on Google Play. You would have to publish it as a completely new app. **Keep your keystore safe!**

## Setup Steps

### Step 1: Generate a Keystore File

Run this command in your terminal (you'll be asked for a password - remember it!):

```bash
keytool -genkey -v -keystore week-number-widget-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias week-number-widget
```

**Important details to provide:**
- **Keystore password**: Create a strong password (save this!)
- **Key password**: Use the same password or create another (save this too!)
- **Your name**: Your name or company name
- **Organizational Unit**: Optional (e.g., "Development")
- **Organization**: Your company name (or personal name)
- **City**: Your city
- **State**: Your state/province
- **Country Code**: Two-letter code (e.g., US, GB, CA)

**Example:**
```
Enter keystore password: [Create and remember this!]
Re-enter new password: [Re-enter same password]
What is your first and last name?
  [Unknown]: John Doe
What is the name of your organizational unit?
  [Unknown]: Development
What is the name of your organization?
  [Unknown]: My Company
What is the name of your City or Locality?
  [Unknown]: New York
What is the name of your State or Province?
  [Unknown]: NY
What is the two-letter country code for this unit?
  [Unknown]: US
Is CN=John Doe, OU=Development, O=My Company, L=New York, ST=NY, C=US correct?
  [no]: yes
```

This will create a file called `week-number-widget-release.jks` in your current directory.

### Step 2: Move Keystore to Safe Location

Move the keystore file to your app directory:
```bash
mv week-number-widget-release.jks app/
```

### Step 3: Store Credentials Securely

Create a `keystore.properties` file (this file is gitignored - see `.gitignore`):
```properties
storePassword=your_keystore_password_here
keyPassword=your_key_password_here
keyAlias=week-number-widget
storeFile=week-number-widget-release.jks
```

**⚠️ IMPORTANT**: Never commit `keystore.properties` or `.jks` files to version control!

### Step 4: Update build.gradle

The `build.gradle` file has been configured to:
1. Load credentials from `keystore.properties`
2. Use the keystore for signing release builds
3. Keep credentials secure (not hardcoded)

## Security Best Practices

1. **Backup your keystore**: Store it in multiple secure locations (encrypted USB, cloud storage with encryption, password manager)
2. **Never commit to git**: Add to `.gitignore` and never push to repositories
3. **Strong passwords**: Use complex passwords for both keystore and key
4. **Store passwords separately**: Keep keystore file and password in different secure locations
5. **Document but don't expose**: Write down the keystore location and password in a secure password manager

## What Happens If You Lose Your Keystore?

If you lose your keystore file or forget the password:
- ❌ You **cannot** update your existing app on Google Play
- ❌ You **cannot** restore it - the private key cannot be recreated
- ✅ You **can** publish a new app with a different package name
- ✅ Google Play App Signing can help (see below)

## Google Play App Signing (Recommended)

Google Play offers "Google Play App Signing" which:
- Stores your signing key securely in Google's infrastructure
- Allows you to request key reset if lost
- Provides additional security
- Is recommended for most apps

When you upload your app to Google Play Console, you can opt into this service. It's highly recommended!

## Testing Your Signed App

After setting up signing:

1. Build a release APK:
   ```bash
   ./gradlew assembleRelease
   ```

2. The signed APK will be in: `app/build/outputs/apk/release/app-release.apk`

3. Install it on a device to test:
   ```bash
   adb install app/build/outputs/apk/release/app-release.apk
   ```

## Building Release Bundle for Google Play

To create an Android App Bundle (AAB) for Google Play:

```bash
./gradlew bundleRelease
```

The bundle will be in: `app/build/outputs/bundle/release/app-release.aab`

Upload this `.aab` file to Google Play Console.

## Troubleshooting

**"Keystore file does not exist"**
- Make sure the `storeFile` path in `keystore.properties` is correct
- Check the file is in the `app/` directory

**"Keystore was tampered with, or password was incorrect"**
- Double-check your password
- Make sure there are no extra spaces in `keystore.properties`

**"Cannot find keystore.properties"**
- Create the file in the project root directory
- Make sure it's not committed to git (add to `.gitignore`)

## Next Steps

1. Generate your keystore using the command above
2. Create `keystore.properties` with your credentials
3. Test building a release APK: `./gradlew assembleRelease`
4. Test the signed APK on a device
5. When ready, build bundle: `./gradlew bundleRelease`
6. Upload the `.aab` file to Google Play Console

Good luck! 🚀
