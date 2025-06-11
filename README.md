# <img src="pluginIcon.svg" width="40" height="40" alt="EnvDiff Icon" align="left"/> Kolesa EnvDiff Notifier
🔧 IntelliJ IDEA plugin for comparing `.env` files (like `.env` vs `env.testing`) and showing you the differences instantly. Designed for teams who maintain both local and test/staging environments.

---

## Features

- Compares `.env` and `env.testing` (or custom files)
- Ignores keys listed in `envdiffignore.json` or fallback `envdiffignore.default.json`
- Git pull listener: notifies you if the files differ
- Customizable filenames and ignore list via plugin settings
- One-click diff view in Tools menu
- Localized in English and Russian
- Safe, fast, and open-source

---

## ⚙️ Installation

#### ✅ Option 1: Install from JetBrains Marketplace

1. Open IntelliJ IDEA
2. Go to `Settings` → `Plugins`
3. Search for `Kolesa EnvDiff Notifier`
4. Click `Install` and restart the IDE

#### 📦 Option 2: Install from local .zip

1. Build the plugin using `./gradlew buildPlugin`
2. Go to `Settings` → `Plugins`
3. Click the ⚙️ icon → `Install Plugin from Disk`
4. Choose the downloaded `.zip` file from `build/distributions/`
5. Restart the IDE

---

## 📝 How to use

1. Place your `.env` and `env.testing` files in your project root
2. (Optional) Add keys to ignore in `envdiffignore.json`
3. Trigger the plugin from `Tools → 🔍 Check .env Diff`
4. Or simply `git pull` — and you'll see a notification if something changed

---
## 🤝 Contributions

Issues and PRs are welcome!

Feel free to localize into other languages or improve UI/UX 🙌

---

## 🛡 License

MIT © Kolesa Group, 2025
