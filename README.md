# Offline AI — Study Assistant (Offline-First)

> A hobby build of an ongoing project - an Android app that studies your PDFs and answers offline. Built entirely in Termux + Vim on Android phone. No cloud, no API keys, no internet required after setup.

[Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue)
[Compose](https://img.shields.io/badge/Compose-Material3-green)
[License](https://img.shields.io/badge/License-MIT-lightgrey)
[Status](https://img.shields.io/badge/Status-WIP_&_Hobby-orange)

## Table of Contents
- [About](#about)
- [Why Offline AI](#why-offline-ai)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Build Instructions](#build-instructions)
- [What Is NOT Committed](#what-is-not-committed)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [Author](#author)
- [Copyright & License](#copyright--license)

## About
Offline AI is an experimental Android app that lets students import their study materials (PDF, DOCX, images) and chat with them offline using a local LLM (GGUF via llama.cpp). This repository is my personal learning lab for Android, RAG, OCR, and on-device AI. Development is ongoing and built 100% from my phone.

## Why Offline AI
- Privacy First: Your notes never leave your device.
- No Internet Needed: Works in areas with poor connectivity.
- Phone-Only Dev: Proving you can build a real Android app without a laptop, just Termux + Vim.

## Features
Done:
- [x] Initial Android project setup
- [x] Material 3 theming
- [x] Clean Git structure

In Progress / Planned:
- [ ] Dashboard UI with Compose
- [ ] File importer and Subject library
- [ ] PDF / DOCX / Image text extraction + OCR
- [ ] SQLite knowledge database + FTS5 search
- [ ] llama.cpp Android integration
- [ ] RAG pipeline with source citations
- [ ] Chat history and sessions

## Tech Stack
- Language: Kotlin
- UI: Jetpack Compose, Material 3
- Build: Gradle Wrapper (built via Termux)
- Editor: Vim in Termux
- Local AI: llama.cpp, GGUF models
- Database: Room / SQLite + FTS5

## Getting Started
Prerequisites: Android phone with Termux, Android SDK in Termux, JDK 17, Git + gh cli.

Clone:
git clone https://github.com/chessTica542319/offline-ai.git
cd offline-ai

## Build Instructions
chmod +x gradlew
./gradlew clean
./gradlew assembleDebug
./gradlew installDebug
APK output: app/build/outputs/apk/debug/app-debug.apk

## What Is NOT Committed
This repo ignores build/,.gradle/,.kotlin/,.idea/, local.properties, *.apk, *.aab, *.db, *.sqlite, /models/, *.gguf, /imported/. See.gitignore.

## Roadmap
Initial project -> Dashboard UI -> Navigation -> Chat screen -> File importer -> Document extraction -> SQLite + FTS5 -> llama.cpp -> RAG -> v1.0

## Contributing
This is a personal hobby build, but ideas and PRs are welcome. Fork, create branch feat/your-feature, commit, push and open a PR.

## Author
RudaDev / chessTica542319
GitHub: https://github.com/chessTica542319
Portfolio: https://ruda-dev.vercel.app
Email: rudasam@isufst.edu.ph
Location: Bacolod City, Philippines
Built on: Android + Termux + Vim

## Copyright & License
Copyright (c) 2026 RudaDev. All Rights Reserved.

This project is licensed under the MIT License.

MIT License
Copyright (c) 2026 RudaDev

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
