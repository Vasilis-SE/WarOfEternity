# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

War of Eternity is a text-based Java RPG with a Swing GUI, offline voice recognition (Vosk), and Java object serialization for save/load.

## Build & Run Commands

All commands run from the repo root (where `pom.xml` lives):

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
```

The entry point is `view.StartGUI.main()`. There is currently no `src/test` directory — `mvn test` runs zero tests.

**Known pom.xml bug**: the `maven-assembly-plugin` manifest sets `<mainClass>main.java.View.StartGUI</mainClass>`, which is a stale package name from before the `View` → `view` package rename. The real class is `view.StartGUI`. `java -jar target/WarOfEternity-1.0-SNAPSHOT-jar-with-dependencies.jar` will fail with `ClassNotFoundException` until this is fixed to `view.StartGUI` (or run via `java -cp target/...jar-with-dependencies.jar view.StartGUI`).

## Architecture

### Package Structure

Every gameplay domain follows the same **`view → controller → service`** layering: Swing forms in `view` call into a `*Controller` (thin orchestration, wires services together, holds per-session state), which delegates real business logic to a `*Service` (stateless-ish, no Swing/IO awareness beyond reading `DataAccessObjects`). Model classes are plain data holders under `*/model`.

| Package | Responsibility |
|---|---|
| `view` | Swing GUI forms: `StartGUI` (main menu / ESC menu), `NewGameForm` (character creation), `MainGame` (gameplay loop), `MapForm`, `LoadGameForm`, `HelpForm` |
| `characters` | `PlayerModel`, `EnemyModel`, `MerchantModel` + controllers/services for player actions, battle, merchants, doctor (healing), captain (dock talk) |
| `map` | `AreaModel` nodes + `AreaConnectionModel` edges; `MapController` builds the graph via `AreaService`; `DockYardController`/`DockYardActionService`/`DockYardConnectionService` handle sail routes |
| `item` | `ItemModel` entities (7 numeric item types, see below), area placement via `ItemConnectionModel`, `ItemController` for item action commands, `TabletItemsController` for stone-tablet inspection |
| `command` | Command parsing: `CommandsEnum` (verb → action-type table) + `CommandParseService` (splits input into verb/noun and resolves the action type) + `CommandParserController` |
| `serialization` | `SaveLoadController` (used by `view`) → `SaveGameService`/`LoadGameService` — Java object serialization of `PlayerModel`, `MapController`, `ItemController` into `LoadedGameData` |
| `voice` | `VoiceRecognitionController`/`VoiceRecognitionService` — loads the Vosk model and captures a single microphone utterance |
| `utils` | Cross-cutting infra: `MainFolderConfig`/`SaveFolderConfig` (bootstrap `%USERPROFILE%\WarOfEternity\` folder tree), `MusicConfiguration` (background music playback), `TextFileProcessing` (classpath resource reading), `HelpFormInfoConfig` (in-game help text) |
| `DataAccessObjects/` (under `src/main/resources`) | JSON files defining all game data, read straight from the classpath at runtime — nothing is copied to disk anymore |

### Data-Driven Design

World data lives entirely in `.json` files under `src/main/resources/DataAccessObjects/`, read directly off the classpath via `utils.TextFileProcessing.readResource(...)` — there is no copy-to-disk step (despite what older docs/comments may imply).

| File | Contents |
|---|---|
| `GameAreas.json` | Area name, description, image filename |
| `GameAreaConnections.json` | `currentArea` / `nextArea` / `direction` triples |
| `GameEnemies.json` | Enemy stats and area placement |
| `GameItems.json` | Item definitions |
| `GameItemConnections.json` | Item-to-area placement (`item` / `area` / `usage`) |
| `MerchantConnections.json` | `area` / `merchant` pairs for NPC area placement |
| `DockYardConnections.json` | `startingArea` / `destinationArea` / `sailingFee` triples for sail routes |

Item types are a raw `int` (1–7) on `ItemModel`, not an enum, despite `item.enums.ItemTypeEnum` existing — that enum is currently unreferenced anywhere in the codebase:
1 Consumable · 2 Miscellaneous/keys · 3 Weapon · 4 Door/Gate · 5 Armor · 6 Shield · 7 Tablet.

### Command Parsing Flow

1. Player types (or speaks) a command in `MainGame`.
2. `CommandParserController.parserControllingMethodForActionDecision()` → `CommandParseService.parseCommandAction()` splits the input into verb/noun and matches the verb against `CommandsEnum` to resolve an action type (`"direction"`, `"item"`, `"battle"`, `"sail"`, `"inspect"`, `"transaction"`, or `"conversation"`).
3. `PlayerController.playerMainControllingMethodForActionDecision()` first checks battle integrity/triggers, then switches on the resolved action type to the matching domain controller (`DirectionController`, `ItemController`, `TransactionController`, `BattleController`, `DockYardController`, `TabletItemsController`).
4. **Gotcha**: `CommandsEnum.TALK` resolves to action type `"conversation"`, but `PlayerController`'s switch only has a `"transaction"` case — "talk to ..." commands currently fall through with no handler and produce an empty result message.

### Save / Load

`view.StartGUI`/`LoadGameForm`/`NewGameForm` go through `serialization.controller.SaveLoadController`, never touching the service layer directly. Saves are Java-serialized `.sav` files written to `%USERPROFILE%\WarOfEternity\Saves\<playerName>.sav`. The three serialized objects are `PlayerModel`, `MapController`, and `ItemController` — written in that order by `SaveGameService` and read back in the same order by `LoadGameService` into a `LoadedGameData` record-like holder. Every model reachable from those three roots (`AreaModel`, `AreaConnectionModel`, `ItemModel`, `ItemConnectionModel`, `EnemyModel`, `MerchantModel`, ...) must stay `Serializable`.

### Windows Path Assumption

The game was originally written for Windows. `utils.MainFolderConfig`/`SaveFolderConfig`/`serialization.service.SaveFilePathResolver` now build paths with `File.separator` and `URLDecoder.decode(String, StandardCharsets.UTF_8)`, so they're cross-platform. Double-check any *new* file-path code for hardcoded `\\` separators before assuming it works on Linux/macOS.

### Voice Recognition

`MainGame` uses **Vosk** (`com.alphacephei:vosk`) for optional offline voice input, via `voice.controller.VoiceRecognitionController`. On startup it loads a model from `~/WarOfEternity/vosk-model/`; recognition is silently disabled if that directory is absent. When the recognition button is clicked, a background thread calls `VoiceRecognitionService.recognizeSpeechFromMicrophone()`, which blocks until a complete utterance is detected, then the result is pushed onto the command text field on the EDT via `SwingUtilities.invokeLater`.

To enable voice recognition, download a Vosk English model from https://alphacephei.com/vosk/models and extract it so the directory structure is `~/WarOfEternity/vosk-model/am/`, `~/WarOfEternity/vosk-model/conf/`, etc.

### Lombok

Lombok (`provided` scope) is used throughout controllers/services — mostly `@RequiredArgsConstructor`, `@Getter`/`@Setter`, `@NoArgsConstructor`, and `@lombok.experimental.UtilityClass` (e.g. `TextFileProcessing`). Prefer it over hand-written boilerplate for new classes, but skip `@Getter`/`@Setter` on `boolean` fields whose accessor names must stay `getX()`/`setX()` — Lombok generates `isX()` for primitive booleans, which will break existing call sites expecting `getX()` (see `utils.MusicConfiguration`, which hand-writes those for this reason).
