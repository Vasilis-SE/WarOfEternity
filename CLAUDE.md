# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

War of Eternity is a text-based Java RPG with a Swing GUI, voice recognition (CMU Sphinx4), and Java object serialization for save/load. The Maven subproject lives in `WarOfEternity/`.

## Build & Run Commands

All commands must be run from the repo root (where `pom.xml` lives):

```bash
# Compile
mvn compile

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=YourTestClassName

# Package into JAR
mvn package

# Build fat JAR with all dependencies (needed to run standalone)
mvn assembly:single

# Run the game
java -jar target/WarOfEternity-1.0-SNAPSHOT-jar-with-dependencies.jar
```

The entry point is `main.java.View.StartGUI.main()`.

## Architecture

### Package Structure

| Package | Responsibility |
|---|---|
| `main.java.View.View` | Swing GUI forms: `StartGUI` (main menu / ESC menu), `NewGameForm` (character creation), `MainGame` (gameplay loop), `MapForm`, `LoadGameForm`, `HelpForm` |
| `characters` | Entity classes (`Player`, `Enemies`, `Merchant`, `DockYard`) and their `*Controller` / `*ActionModel` pairs |
| `Map` | `Area` nodes + `AreaConnectionMaker` edges; `MapController` bootstraps the graph by reading text files |
| `Items` | `Item` entities, area-item placement via `ItemConnectionWithArea`, `ItemController` |
| `Parsers` | `ParserController` + `ParserModel` — splits player input into verb/noun and maps the verb to an action category |
| `Serialization` | `SaveGameData` / `LoadGameData` — Java object serialization of `Player`, `MapController`, `ItemController` |
| `GameFileConfiguration` | Bootstraps the runtime folder tree under `user.home\WarOfEternity\` and copies `DataAccessObjects` there |
| `Interfaces` | `ICharacter`, `IItem` |
| `DataAccessObjects/` | Plain text files that define all game data (see below) |

### Data-Driven Design

All world data lives in `.txt` files under `src/main/java/DataAccessObjects/`. On first launch, `ResourceFolderConfig` copies this directory to `%USERPROFILE%\WarOfEternity\DataAccessObjects\` and subsequent reads happen from there.

| File | Contents |
|---|---|
| `GameAreas.txt` | Area name, description, image filename (tab-delimited, `@`-separated) |
| `GameAreaConnections.txt` | `currentArea \| direction \| nextArea` triples |
| `GameEnemies.txt` | Enemy stats and area placement |
| `GameItems.txt` | Item definitions |
| `GameItemConnections.txt` | Item-to-area placement |
| `MerchantConnections.txt` / `DockYardConnections.txt` | NPC area placement |
| `*Parser.txt` | One verb per line; used by `ParserController` to classify commands |

### Command Parsing Flow

1. Player types (or speaks) a command in `MainGame`.
2. `ParserController.ParserControllingMethodForActionDecision()` splits input and matches the first word against six verb lists (direction, item, transaction, battle, sail, inspect).
3. Returns a category string (`"direction"`, `"item"`, `"battle"`, etc.).
4. `PlayerController` (and domain-specific `*ActionModel` classes) execute the action.

### Save / Load

Saves are Java-serialized `.sav` files written to `%USERPROFILE%\WarOfEternity\Saves\<playerName>.sav`. The three serialized objects are `Player`, `MapController`, and `ItemController` — written in that order and read back in the same order.

### Windows Path Assumption

The game was written for Windows; file paths are hardcoded with `\\` separators and use `System.getProperty("user.home")` and `System.getProperty("user.dir")`. Running on Linux/macOS requires path separator fixes in `GameFileConfiguration/*.java` and `Serialization/*.java`.

### Voice Recognition

`MainGame` uses **Vosk** (`com.alphacephei:vosk`) for optional offline voice input. On startup it loads a model from `~/WarOfEternity/vosk-model/`; recognition is silently disabled if that directory is absent. When the recognition button is clicked a background thread opens the microphone, blocks until a complete utterance is detected, then populates the command text field on the EDT.

To enable voice recognition, download a Vosk English model from https://alphacephei.com/vosk/models and extract it so the directory structure is `~/WarOfEternity/vosk-model/am/`, `~/WarOfEternity/vosk-model/conf/`, etc.