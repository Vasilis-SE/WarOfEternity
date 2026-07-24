# War of Eternity — Modernization & Architecture TODOs

---

## Critical Bugs / Correctness

- [ ] **`mkdir()` → `mkdirs()` in folder config classes**
  `File.mkdir()` only creates the final directory component; if `~/WarOfEternity` doesn't
  exist yet, `~/WarOfEternity/Saves` silently fails to create. Use `mkdirs()` instead.

- [ ] **`URLDecoder.decode` on file paths is meaningless**
  `System.getProperty("user.home")` never returns a URL-encoded string. The entire
  `URLDecoder.decode(...)` + `replace("%20", " ")` pattern in every path-building method
  is dead code that adds noise. Delete it and build paths directly.
  Still present in:
  - `utils/SaveFolderConfig.java`
  - `utils/MainFolderConfig.java`
  - `serialization/service/SaveFilePathResolver.java`

- [ ] **`LoadGameForm` uses `EXIT_ON_CLOSE`** (`view/LoadGameForm.java`)
  Closing the Load Game window kills the whole JVM. Change to `DISPOSE_ON_CLOSE`.

---

## Modern Java APIs (Java 8–24)

- [ ] **Replace path construction with `java.nio.Path`**
  ```java
  // Before
  usersHome + File.separator + "WarOfEternity" + File.separator + "Saves"
  // After
  Path.of(System.getProperty("user.home"), "WarOfEternity", "Saves")
  ```
  Use `Files.createDirectories()` instead of `File.mkdir()` for folder creation.

- [ ] **Replace Java object serialization with JSON**
  The `.sav` format is Java-serialized binary. It is fragile (breaks on any class rename/field
  add), a known security risk when loading untrusted files, and not human-readable. Since
  `json-simple` is already a dependency, migrate `serialization.service.SaveGameService`/
  `LoadGameService` to write/read a JSON file. Consider switching to Jackson or Gson for
  better type support.

- [ ] **Replace raw `ArrayList` with typed generics**
  `new ArrayList()` still appears in `item/controller/ItemController.java` and
  `characters/controller/TransactionController.java`. Use `new ArrayList<>()` (diamond
  operator). Also `DefaultListModel` in `view/LoadGameForm.java` should be
  `DefaultListModel<String>`.

- [ ] **Replace item-removal loops with `removeIf`**
  In `characters.service.PlayerService.removeItemFromSelectedItemsByPlayer` and
  `removeItemFromPlayerEquippedInventory`:
  ```java
  // Before: builds a new list in a loop
  // After
  itemsSelected.removeIf(i -> i.getItemName().equals(item.getItemName()));
  ```

- [ ] **Replace `switch` statements with `switch` expressions (Java 14+)**
  `player.getPlayerClass()` is already a `PlayerClassesEnum` (no longer a raw String), but
  `PlayerService.calculateGeneralPlayerDamage`, `levelUp`, and `getPlayerClassStartingStats`
  still switch on it with old-style colon/`break` statements. Convert to arrow-syntax
  `switch` expressions that return/assign a value directly.

---

## Architecture

- [ ] **Wire up the existing `ItemType` enum to replace magic integers**
  `item.enums.ItemTypeEnum` already exists but is unused everywhere — `ItemModel.itemType`
  is still a raw `int` compared against literals (`== 3`, `== 4`, `== 5 || == 6`) scattered
  across `ItemService`, `PlayerService`, and `BattleService`. Switch `ItemModel` to store the
  enum and replace all numeric comparisons with `item.getItemType() == ItemType.WEAPON`.

- [ ] **Replace the `ItemModel` constructors with a Builder**
  `ItemModel` has six different constructors for different item types, several with similar
  parameter lists but different semantics. Other models (`PlayerModel`,
  `CharacterAbstractModel`) already use Lombok `@SuperBuilder` — do the same for `ItemModel`
  (`ItemModel.builder().name(...).type(...)`) to make construction readable.

- [ ] **Replace `JSONObject` as an internal data carrier with a `record`**
  `PlayerService.getPlayerClassStartingStats` and `getAttributePointsFromEquippedItems` use
  `JSONObject` as a plain map. Define a small `record ClassStats(int strength, int agility,
  int intelligence)` and return that instead. No JSON library needed for in-memory data.

- [ ] **Remove the dead pseudo-field constants from `ItemInterface`**
  `item.interfaces.ItemInterface` declares `String itemName = ""; ... int itemType = 0;` etc.
  In an interface these are implicitly `public static final` — they compile, but they are
  unused dead constants that look like instance fields. Remove them; the interface should
  declare only method signatures.

- [ ] **Extract path constants into a single `GamePaths` utility class**
  The `~/WarOfEternity/…` path fragments are still rebuilt from scratch in
  `utils.SaveFolderConfig`, `utils.MainFolderConfig`, and
  `serialization.service.SaveFilePathResolver`. Centralise them:
  ```java
  class GamePaths {
      static final Path ROOT = Path.of(System.getProperty("user.home"), "WarOfEternity");
      static final Path SAVES = ROOT.resolve("Saves");
      static Path saveFile(String name) { return SAVES.resolve(name + ".sav"); }
  }
  ```

- [ ] **Replace controller instantiation inside `PlayerController` with injected dependencies**
  `PlayerController.playerMainControllingMethodForActionDecision` still creates
  `DirectionController`, `ItemController`, `TransactionController`, `DockYardController`, and
  `TabletItemsController` inline on every call. Extract these as constructor-injected
  collaborators so the controller is testable without invoking side-effectful constructors.

---

## Testing

- [ ] **Upgrade from JUnit 3/4 to JUnit 5**
  The `pom.xml` declares both JUnit 3.8.1 and 4.10. Consolidate on JUnit 5 (`junit-jupiter`).
  There is currently no `src/test` directory at all.

- [ ] **Add unit tests for `command.service.CommandParseService`**
  The command parser has no tests; it's the core input-handling path.

- [ ] **Add unit tests for `characters.service.PlayerService` stat calculations**
  `calculateGeneralPlayerDamage`, `levelUp`, `battleExperienceEarned` contain non-trivial
  arithmetic with class-dependent branches — good candidates for parameterised tests.

---

## Code Quality

- [ ] **Suppress or fix all raw-type compiler warnings**
  `mvn compile` still flags unchecked/raw-type usage (e.g. `characters/controller/
  BattleController.java`). Fix as part of the generics cleanup above.

- [ ] **Remove remaining empty `catch` blocks**
  Several JSON-loading methods silently swallow `ParseException` with an empty block:
  - `item/service/ItemService.java` (×2)
  - `characters/service/MerchantService.java`
  - `map/service/AreaService.java` (×2)
  - `map/service/DockYardConnectionService.java`

  At minimum log the exception with the offending resource path.

- [ ] **Replace `System.exit(0)` in config classes with a thrown exception**
  `utils.MainFolderConfig` and `utils.SaveFolderConfig` call `System.exit(0)` on a
  `SecurityException`. Throw a custom `GameInitException` instead and let the top-level
  `StartGUI` catch it and show the dialog — this makes the classes testable and separates
  concerns.
