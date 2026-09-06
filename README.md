# Offline AI — Offline Study Assistant

> A personal Android AI study assistant that runs completely offline using a local language model. Built entirely on an Android phone with Termux and Vim — no cloud AI API or API key required.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-green)](https://developer.android.com/compose)
[![llama.cpp](https://img.shields.io/badge/AI-llama.cpp-orange)](https://github.com/ggml-org/llama.cpp)
[![Qwen2.5](https://img.shields.io/badge/Model-Qwen2.5--1.5B-purple)](https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF)
[![Status](https://img.shields.io/badge/Status-WIP-orange)](https://github.com/chessTica542319/offline-ai)
[![License](https://img.shields.io/badge/License-MIT-lightgrey)](LICENSE)

## Table of Contents

* [About](#about)
* [Why Offline AI](#why-offline-ai)
* [Current Status](#current-status)
* [Features](#features)
* [Supported Study Materials](#supported-study-materials)
* [Architecture](#architecture)
* [Tech Stack](#tech-stack)
* [Project Structure](#project-structure)
* [Getting Started](#getting-started)
* [Build Instructions](#build-instructions)
* [Local AI Model](#local-ai-model)
* [What Is Not Committed](#what-is-not-committed)
* [Current Limitations](#current-limitations)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [Author](#author)
* [License](#license)

## About

Offline AI is an experimental Android study assistant designed to let students organize their learning materials and interact with them using an on-device language model.

The application is designed around an offline-first architecture:

* Study materials remain on the device.
* AI inference runs locally.
* No cloud AI API is required.
* No API key is required.
* The application is designed to work without an internet connection after setup.

The project is also a personal learning laboratory for Android development, Jetpack Compose, Room/SQLite, OCR, document processing, JNI, native C++, llama.cpp, and retrieval-augmented generation.

The entire application is being developed directly on an Android phone using Termux and Vim.

## Why Offline AI

### Privacy

Study materials stay on the device instead of being uploaded to a remote AI service.

### Offline Access

The application is designed for situations where internet access is unavailable, unreliable, expensive, or intentionally disabled.

### Phone-Only Development

This project demonstrates that a functional Android application with native AI inference can be developed directly from an Android phone.

## Current Status

The core offline AI engine is working.

### Completed

* [x] Android project setup
* [x] Jetpack Compose + Material 3 UI
* [x] Application navigation and navigation drawer
* [x] Chat interface
* [x] Temporary chat sessions
* [x] AI response counter with session limit
* [x] Stop and interrupt AI generation
* [x] Retry AI responses
* [x] Copy AI responses
* [x] Loading and thinking animation
* [x] Scroll-to-bottom behavior
* [x] Local Room database
* [x] Subjects and lessons structure
* [x] Study content records
* [x] File import workflow
* [x] Document text extraction
* [x] Image OCR
* [x] Camera capture workflow
* [x] Local llama.cpp integration
* [x] JNI bridge between Kotlin and native C++
* [x] Persistent local AI model loading
* [x] Qwen2.5 1.5B Instruct GGUF integration
* [x] ARM64 native libraries
* [x] Offline AI generation on Android
* [x] Custom application icon
* [x] Android APK builds directly from Termux

### Currently Being Developed

* [ ] Synchronize imported study content with the AI retrieval and generation pipeline
* [ ] SQLite FTS5 knowledge search connected to chat
* [ ] Retrieval-augmented generation (RAG)
* [ ] Source and page citations in AI responses
* [ ] Improved document-to-knowledge processing
* [ ] Better context selection for large study materials
* [ ] Native inference performance optimization

## Features

### Chat

The main dashboard provides an offline chat interface for interacting with the local AI model.

Current chat features include:

* Local AI inference
* User and assistant message bubbles
* Animated thinking indicator
* Stop generation
* Retry response
* Copy response
* Temporary chat history
* Response limit per session
* Automatic scrolling behavior
* Scroll-to-bottom control

Chat history is currently temporary and is not stored permanently in the database.

### Subjects and Lessons

Study materials are organized using a hierarchy:

```text
Subject
└── Lesson
    └── Study Content
```

A subject can contain multiple lessons, and lessons can contain multiple study-content records.

Each imported file is treated as an individual study-content item rather than automatically merging multiple files.

### File Import

The application includes a local workflow for importing study materials into the knowledge database.

The intended workflow is:

```text
Select Files
      ↓
Choose or Create Subject
      ↓
Choose or Create Lesson
      ↓
Extract Text / OCR
      ↓
Review Content
      ↓
Save Study Content
      ↓
Local Knowledge Database
```

### OCR

Images can be processed locally using on-device OCR.

OCR is intended to work with:

* Camera captures
* Imported image files
* Image-based study materials

Scanned documents can also be processed as the document extraction pipeline continues to develop.

## Supported Study Materials

The application targets the following study and content formats:

```text
PDF
DOC
DOCX
TXT
RTF

JPG
JPEG
PNG
WEBP
HEIC

PPT
PPTX

XLS
XLSX
CSV
```

Executable files, application packages, arbitrary code files, and unrelated web or configuration formats are intentionally outside the supported study-material whitelist.

## Architecture

The application follows an offline-first architecture:

```text
┌──────────────────────────────┐
│       Jetpack Compose UI     │
├──────────────────────────────┤
│      App Navigation Layer    │
├──────────────────────────────┤
│      Study Repository        │
├──────────────────────────────┤
│        Room / SQLite         │
│ Subjects / Lessons / Content │
├──────────────────────────────┤
│    Document Extraction/OCR   │
├──────────────────────────────┤
│          AI / JNI            │
├──────────────────────────────┤
│          llama.cpp           │
├──────────────────────────────┤
│       Local GGUF Model       │
│         Qwen2.5 1.5B         │
└──────────────────────────────┘
```

The local AI engine can already load the model and generate responses directly on the Android device.

The next major integration step is connecting imported knowledge to the AI retrieval and context pipeline.

## Tech Stack

### Android

* Kotlin
* Jetpack Compose
* Material 3
* AndroidX
* Room
* SQLite

### Native AI

* C++
* JNI
* llama.cpp
* GGUF
* Qwen2.5 1.5B Instruct

### Development Environment

* Android phone
* Termux
* Vim
* OpenJDK
* Clang
* CMake
* Android SDK
* Android NDK
* Gradle Wrapper
* Git

## Project Structure

```text
offline-ai/
├── app/
│   ├── libs/
│   │   └── poi-on-android.jar
│   ├── schemas/
│   └── src/main/
│       ├── cpp/
│       ├── java/
│       │   └── com/offlineai/app/
│       │       ├── ai/
│       │       ├── data/
│       │       │   ├── database/
│       │       │   ├── extraction/
│       │       │   ├── ocr/
│       │       │   └── repository/
│       │       └── ui/
│       │           ├── camera/
│       │           ├── chat/
│       │           ├── components/
│       │           ├── importfiles/
│       │           ├── navigation/
│       │           ├── splash/
│       │           ├── subjects/
│       │           └── theme/
│       ├── jniLibs/
│       │   └── arm64-v8a/
│       └── res/
├── gradle/
├── gradle.properties
├── gradlew
├── gradlew.bat
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
└── .gitignore
```

Generated build directories and local model files are intentionally excluded from version control.

## Getting Started

### Requirements

The project is primarily developed and tested on:

* Android 14+
* ARM64 / `arm64-v8a`
* Termux
* OpenJDK
* Android SDK
* Android NDK
* Git

Clone the repository:

```bash
git clone https://github.com/chessTica542319/offline-ai.git
cd offline-ai
```

Make the Gradle wrapper executable:

```bash
chmod +x gradlew
```

## Build Instructions

Build the debug APK:

```bash
./gradlew assembleDebug
```

The generated APK will be located at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

For a clean build:

```bash
./gradlew clean
./gradlew assembleDebug
```

If an Android installation environment is available:

```bash
./gradlew installDebug
```

The project also contains native C++ components used by the local AI engine.

## Local AI Model

Offline AI uses a local GGUF language model through llama.cpp.

Current development model:

```text
Model: Qwen2.5 1.5B Instruct
Quantization: Q4_K_M
Format: GGUF
Architecture: Qwen2
```

The model is distributed separately and is intentionally not committed to this repository because the GGUF file is approximately 1.1 GB.

The model must be obtained separately and placed locally at:

```text
models/qwen2.5-1.5b-instruct-q4_k_m.gguf
```

For Android packaging during development, the model is copied into:

```text
app/src/main/assets/models/
```

The large model file is excluded from Git using `.gitignore`.

Official model repository:

https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF

## What Is Not Committed

Large generated or machine-specific files are excluded from the repository.

Examples include:

```text
build/
app/build/
native-build/
.gradle/
.idea/
local.properties
*.apk
*.aab
models/
*.gguf
```

The local Qwen GGUF model is intentionally excluded because of its size.

Native Android libraries currently used by the application are kept under:

```text
app/src/main/jniLibs/arm64-v8a/
```

These libraries are part of the current Android build checkpoint.

## Current Limitations

This project is still a work in progress.

The local AI engine can already generate responses independently, but imported study materials are not yet fully synchronized with the AI retrieval pipeline.

Current state:

```text
Imported Files
      ↓
Extraction / OCR
      ↓
Room / SQLite
      X
AI Retrieval Context
```

Target architecture:

```text
Imported Files
      ↓
Extraction / OCR
      ↓
Room / SQLite
      ↓
FTS5 / Retrieval
      ↓
Relevant Study Content
      ↓
AI Context
      ↓
Local Qwen Model
      ↓
Answer + Sources
```

Additional limitations:

* Chat history is currently temporary.
* AI responses are limited per session.
* RAG is not yet fully connected to imported study content.
* Source and page citations are still being developed.
* Native inference performance can still be improved.
* The current model is intentionally small for practical on-device inference.

## Roadmap

```text
Project Setup
     ↓
Compose UI
     ↓
Navigation
     ↓
Chat Interface
     ↓
Subjects / Lessons
     ↓
File Import
     ↓
Document Extraction
     ↓
OCR
     ↓
Room / SQLite
     ↓
Local llama.cpp
     ↓
Qwen GGUF
     ↓
FTS5 Retrieval
     ↓
RAG Integration
     ↓
Source Citations
     ↓
Performance Optimization
     ↓
Stable Offline Study Assistant
```

Future ideas:

* Hybrid FTS5 + embeddings retrieval
* Better context ranking
* Page-level source citations
* Improved scanned-PDF OCR
* Handwriting recognition
* More local model options
* Model management
* Improved memory and performance optimization
* Optional persistent chat sessions

## Contributing

This is primarily a personal learning and hobby project.

Suggestions, issues, experiments, and pull requests are welcome.

For larger changes, consider opening an issue first to discuss the proposed architecture or implementation.

## Author

**RudaDev / Sam Ruda**

GitHub:

https://github.com/chessTica542319

Portfolio:

https://ruda-dev.vercel.app

Built entirely using:

```text
Android Phone
     +
Termux
     +
Vim
     +
Kotlin
     +
C++
     +
llama.cpp
```

## License

Copyright (c) 2026 RudaDev.

This project is licensed under the MIT License.

See the `LICENSE` file for the complete license text.

The Qwen model and llama.cpp are separate third-party components and remain subject to their respective licenses.

