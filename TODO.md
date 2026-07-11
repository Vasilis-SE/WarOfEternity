# War of Eternity — Modernization & Architecture TODOs

---

## Critical Bugs / Correctness

- [ ] **`Player.SetCharacterName` ignores its parameter** (`Player.java:101`)
  The body reads `this.characterName = "player"` (literal string) instead of `= name`.

- [ ] **`SaveFolderConfig`, `MainFolderConfig`, `SaveGameData` still use `"\\"` path separators**
  Same cross-platform bug that was fixed in `LoadGameData`; the Saves folder is never created
  correctly on Linux/macOS, so saves silently fail.
  Replace with `File.separator` or `Paths.get()` in:
  - `SaveFolderConfig.java:34`
  - `MainFolderConfig.java:37`
  - `SaveGameData.java:42`

- [ ] **`mkdir()` → `mkdirs()` in folder config classes**
  `File.mkdir()` only creates the final directory component; if `~/WarOfEternity` doesn't
  exist yet, `~/WarOfEternity/Saves` silently fails to create. Use `mkdirs()` instead.

- [ ] **`URLDecoder.decode` on file paths is meaningless**
  `System.getProperty("user.home")` never returns a URL-encoded string. The entire
  `URLDecoder.decode(...)` + `replace("%20", " ")` pattern in every path-building method
  is dead code that adds noise and a bogus `UnsupportedEncodingException` catch block.
  Delete it and build paths directly.

- [ ] **`LoadGameForm` uses `EXIT_ON_CLOSE`** (`LoadGameForm.java:41`)
  Closing the Load Game window kills the whole JVM. Change to `DISPOSE_ON_CLOSE`.

---

## Naming Conventions

- [ ] **Rename all methods from PascalCase to camelCase** (Java standard)
  Every getter/setter and action method uses PascalCase (`GetCharacterName`, `SetAreaLocation`,
  `PlayerMainControllingMethodForActionDecision`). This is the C#/Pascal convention, not Java.
  Rename to `getCharacterName`, `setAreaLocation`, etc. throughout all packages.

---

## Modern Java APIs (Java 8–24)

- [ ] **Replace path construction with `java.nio.Path`**
  ```java
  // Before
  usersHome + "\\" + "WarOfEternity" + "\\" + "Saves"
  // After
  Path.of(System.getProperty("user.home"), "WarOfEternity", "Saves")
  ```
  Use `Files.createDirectories()` instead of `File.mkdirs()` for folder creation.

- [ ] **Replace Java object serialization with JSON**
  The `.sav` format is Java-serialized binary. It is fragile (breaks on any class rename/field
  add), a known security risk when loading untrusted files, and not human-readable. Since
  `json-simple` is already a dependency, migrate `SaveGameData`/`LoadGameData` to write/read
  a JSON file. Consider switching to Jackson or Gson for better type support.

- [ ] **Replace raw `ArrayList` with typed generics**
  `new ArrayList()` appears everywhere. Use `new ArrayList<>()` (diamond operator).
  Also `DefaultListModel` in `LoadGameForm` should be `DefaultListModel<String>`.

- [ ] **Replace item-removal loops with `removeIf`**
  In `Player.removeItemFromSelectedItemsByPlayer` and `removeItemFromPlayerEquippedInventory`:
  ```java
  // Before: builds a new list in a loop
  // After
  itemsSelected.removeIf(i -> i.GetItemName().equals(item.GetItemName()));
  ```

- [ ] **Replace `switch` on class strings with `switch` expressions (Java 14+)**
  `calculateGeneralPlayerDamage`, `levelUp`, `getPlayerClassStartingStats` all switch on
  `this.playerClass` (a raw String). Use `switch` expressions with arrow syntax to eliminate
  fall-through and make the result an assigned value.

- [ ] **Replace `try/catch UnsupportedEncodingException` with `StandardCharsets.UTF_8`**
  `URLDecoder.decode(str, StandardCharsets.UTF_8)` (the `Charset` overload) doesn't throw
  the checked exception, eliminating the pointless `catch` blocks.

---

## Architecture

- [ ] **Introduce an `ItemType` enum to replace magic integers**
  `Item.itemType` is compared against raw integers (`== 3`, `== 4`, `== 5 || == 6`) scattered
  across `Player`, `BattleActionModel`, and `ItemActionModel`. Define:
  ```java
  enum ItemType { CONSUMABLE, MISC, WEAPON, GATE, ARMOR, SHIELD, TABLET }
  ```
  and replace all numeric comparisons with `item.getItemType() == ItemType.WEAPON`.

- [ ] **Replace the 7 `Item` constructors with a Builder**
  `Item` has seven different constructors for different item types, several with identical
  parameter signatures but different semantics. A Builder (`Item.builder().name(...).type(...)`)
  makes construction readable and eliminates constructor confusion.

- [ ] **Replace `JSONObject` as an internal data carrier with a `record`**
  `getPlayerClassStartingStats` and `getAttributePointsFromEquippedItems` use `JSONObject`
  as a plain map. Define a small `record ClassStats(int strength, int agility, int intelligence)`
  and return that instead. No JSON serialization library needed for in-memory data.

- [ ] **Replace the 6 sequential verb-scan loops in `ParserController` with a `map`**
  `PlayerActionDecider` runs six `for` loops over six lists for every command. Pre-populate
  a `map<String, String>` (verb → category) once at construction time and use a single
  `map.get(verb.toLowerCase())` for O(1) lookup. This also fixes the last-wins ambiguity
  if a word appears in multiple verb lists.

- [ ] **Fix the `ICharacter` interface — remove state from it**
  `ICharacter` declares `Area areaLocation = null; String characterName = ""` etc. as fields.
  In an interface these are implicitly `public static final` — they compile, but they are
  unused dead constants that look like instance fields. Remove them; the interface should
  declare only method signatures.

- [ ] **Extract path constants into a single `GamePaths` utility class**
  The `~/WarOfEternity/…` path fragments are re-built from scratch in every class
  (`SaveFolderConfig`, `MainFolderConfig`, `LoadGameData`, `SaveGameData`). Centralise them:
  ```java
  class GamePaths {
      static final Path ROOT = Path.of(System.getProperty("user.home"), "WarOfEternity");
      static final Path SAVES = ROOT.resolve("Saves");
      static Path saveFile(String name) { return SAVES.resolve(name + ".sav"); }
  }
  ```

- [ ] **Replace action model instantiation inside `PlayerController` with injected dependencies**
  `PlayerMainControllingMethodForActionDecision` creates `DirectionActionModel`, `ItemController`,
  `TransactionController`, etc. inline on every call. Extract these as constructor-injected
  collaborators so the controller is testable without invoking side-effectful constructors.

---

## Testing

- [ ] **Upgrade from JUnit 3/4 to JUnit 5**
  The `pom.xml` declares both JUnit 3.8.1 and 4.10. Consolidate on JUnit 5 (`junit-jupiter`).

- [ ] **Add unit tests for `ParserController`**
  The parser has no tests; it's the core input-handling path and has a silent last-wins bug
  that only tests would catch.

- [ ] **Add unit tests for `Player` stat calculations**
  `calculateGeneralPlayerDamage`, `levelUp`, `battleExperienceEarned` contain non-trivial
  arithmetic with class-dependent branches — good candidates for parameterised tests.

---

## Code Quality

- [ ] **Suppress or fix all raw-type compiler warnings**
  Running `mvn compile` will surface dozens of unchecked/raw-type warnings. Fix them as part
  of the generics cleanup above.

- [ ] **Remove empty `catch` blocks**
  `SaveGameData.SavePlayerData` silently swallows `IOException` (line 59). At minimum log the
  exception; ideally propagate it so the UI can show an error dialog.

- [ ] **Replace `System.exit(0)` in config classes with a thrown exception**
  `MainFolderConfig` and `SaveFolderConfig` call `System.exit(0)` on a `SecurityException`.
  Throw a custom `GameInitException` instead and let the top-level `StartGUI` catch it and
  show the dialog — this makes the classes testable and separates concerns.