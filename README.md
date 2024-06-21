# 💬 Locale Config X
By providing methods compatible with all Android versions, this library saves you from hardcoding the locales used in the
[per-app language configuration](https://developer.android.com/guide/topics/resources/app-languages), and in addition, 
it helps in choosing the current language of the app correctly.

## 🏗️ Setup
- Step 1: Add Jitpack repository to dependency repositories in `settings.gradle.kts` 
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ..
        maven("https://jitpack.io")
    }
}
```
- Step 2: Add the dependency to build script file
```gradle
dependency {
    implementation("com.github.erfansn:locale-config-x:{latest_version}")
}
```

## ⌨️ Usage
Refer to the [sample](/sample) project and see how to use them using the provided APIs.

## 📜 License
```
Copyright (c) 2024 Erfan Sn

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
