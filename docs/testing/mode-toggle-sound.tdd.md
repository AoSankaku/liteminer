# Mode toggle sound: TDD evidence

## Source and user journey

No source plan was provided. The journey was derived from the request:

> As a player, I hear immediate audio feedback when my keybind successfully changes Liteminer's bulk-breaking mode, with distinguishable enabled and disabled sounds.

Automatic ore targeting and rejected activation do not count as keybind-driven mode changes and therefore do not play the toggle sound.

## RED and GREEN evidence

| Behavior | RED evidence | GREEN evidence | Guarantee |
|---|---|---|---|
| Accepted keybind changes play feedback | `bun test test/unit/mode-toggle-sound.test.ts` failed because the client sources did not contain `SoundEvents.UI_BUTTON_CLICK` | The same command passes with 1/1 tests and 25 assertions | All five supported source overlays play the UI click only after an accepted effective state change caused by the keybind |
| Automatic targeting remains quiet | The refined test failed because keybind transitions were not tracked separately | The same command passes after adding `previousKeybindState` and `keybindStateChanged` | Moving the crosshair onto or away from automatically mined ores does not produce toggle sounds |
| ON and OFF are distinguishable | Covered by the initial RED result | The source-contract test verifies pitches `1.2F` and `0.8F` in every overlay | Enabling has the higher pitch and disabling has the lower pitch |

## Verification

| Check | Command summary | Result |
|---|---|---|
| Focused unit test | `bun test test/unit/mode-toggle-sound.test.ts` | PASS: 1 test, 25 assertions |
| Unit suite and coverage | `bun test test/unit --coverage` | PASS: 6 tests, 58 assertions; test files report 100% functions and lines |
| Oldest supported build | Common, Fabric, Forge, and NeoForge `build` for 1.21.11 | PASS |
| Latest supported build | Common, Fabric, Forge, and NeoForge `build` for 26.2 | PASS |

The combined loader build completed 98 tasks successfully. No production-code coverage percentage is claimed because the Bun suite validates Java source contracts rather than instrumenting Minecraft runtime classes.

## Known gaps

Minecraft 26.1, 26.1.1, and 26.1.2 common-module compilation currently fails on pre-existing `displayClientMessage`, `NetworkChannel.createOptional`, and `canSendToServer` API mismatches. The compiler reported no errors in the newly added sound API. Audible output was not verified in a live Minecraft client because the current automated client test API does not expose captured audio.
