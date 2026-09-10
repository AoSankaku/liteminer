# Shape cycle keybind fallback: TDD evidence

## Source and user journey

No source plan was provided. The journey was derived from the request:

> As a player who cannot use mouse-wheel shape cycling, I can bind separate previous/next mining-shape keys, while mouse-wheel cycling remains available and discoverable.

## RED and GREEN evidence

| Behavior | RED evidence | GREEN evidence | Guarantee |
|---|---|---|---|
| Unbound fallback keybinds | `bun test test/unit/shape-cycle-keybinds.test.ts` failed because the previous/next key mappings did not exist | The same command passes with 3/3 tests and 54 assertions | All five supported source overlays register previous/next keybinds with `InputConstants.UNKNOWN` |
| Shared keyboard and wheel behavior | The focused test failed because shape cycling only existed in `HUD.onMouseScroll` | The same command passes after introducing `cycleShape(boolean)` and routing both inputs through it | Both input methods cycle only while vein mining is active and synchronize the selected shape to the server |
| Mouse-wheel discoverability | The focused test failed because no control labels described wheel operation | The same command passes after adding English and Japanese key labels | The Controls screen identifies Mouse Wheel Up/Down as the built-in alternatives |

RED checkpoint: `7d35174 test: add shape cycling keybind contract`.

## Verification

| Check | Command summary | Result |
|---|---|---|
| Focused unit test | `bun test test/unit/shape-cycle-keybinds.test.ts` | PASS: 3 tests, 54 assertions |
| Unit suite and coverage | `bun test test/unit --coverage` | PASS: 9 tests, 112 assertions; test files report 100% functions and lines |
| Translation syntax | Parse every `lang/*.json` with `ConvertFrom-Json` | PASS: 15/15 files |
| Oldest and latest supported builds | Common, Fabric, Forge, and NeoForge `build` for 1.21.11 and 26.2 | PASS: 98 tasks |

The combined all-version build still fails on the pre-existing 26.1, 26.1.1, and 26.1.2 `displayClientMessage`, `NetworkChannel.createOptional`, and `canSendToServer` API mismatches. None of those diagnostics refer to the new key mappings or shape-cycling method.

## Coverage and known gaps

The Bun tests validate source contracts across every supported overlay; they do not instrument Minecraft runtime classes, so no production-code coverage percentage is claimed. Live keyboard and mouse input were not automated because the current TeaKit suite has no control-remapping primitive.
