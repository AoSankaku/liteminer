# Liteminer Delta client-only server support: TDD evidence

## Source and user journey

No source plan was provided. The journey was derived from the request:

> As a player with Liteminer Delta, Amber, and Konfig installed only on the client, I can join a server that does not have those mods; attempting to activate Liteminer Delta stays inactive and explains that the server does not support it.

The inverse deployment was also covered: servers must not send optional packets to clients that did not negotiate the relevant payload.

## RED and GREEN evidence

| Behavior | RED evidence | GREEN evidence | Guarantee |
|---|---|---|---|
| Liteminer Delta blocks unsupported activation | `ClientActivationGateTest` initially failed to compile because `ClientActivationGate` did not exist; the initial source-contract suite also failed 3/3 | Java gate test exits 0; `bun test test/unit` passes 5/5 with 33 assertions | Unsupported activation never sends the key-state packet, never enables the HUD state, and notifies once per key press |
| Amber permits one-sided installation | Initial Amber contract suite failed 5/5 | `bun test test/unit` passes 5/5 with 19 assertions | Optional channels query negotiated support on Fabric, Forge, and NeoForge; individual and broadcast sends omit unsupported peers |
| Konfig permits one-sided installation | Initial Konfig contract suite failed 4/4 | `bun test test/unit` passes 4/4 with 9 assertions | Main config-sync transport is optional and snapshots are sent only to negotiated peers |
| Minecraft 26.2 displays the unavailable notice | The first 26.2 build failed because `displayClientMessage(Component, boolean)` no longer exists | Contract test passes and the 26.2 build succeeds using `sendOverlayMessage(Component)` | The unavailable notice uses the current 26.2 action-bar API |

## Build verification

| Target | Command summary | Result |
|---|---|---|
| Amber 1.21.11 | Common, Fabric, Forge, and NeoForge `build` | PASS (50 tasks in the full build; Forge/NeoForge broadcast follow-up also passed) |
| Amber 26.2 | Common, Fabric, Forge, and NeoForge `build` | PASS (44 tasks in the full build; Forge/NeoForge broadcast follow-up also passed) |
| Konfig 1.21.11 | Common, Fabric, Forge, and NeoForge `build` | PASS (53 tasks) |
| Konfig 26.2 | Common, Fabric, Forge, and NeoForge `build` | PASS (46 tasks) |
| Liteminer Delta 1.21.11 | Common, Fabric, Forge, and NeoForge `build` | PASS (50 tasks) |
| Liteminer Delta 26.2 | Common, Fabric, Forge, and NeoForge `build` | PASS (48 tasks) |

Generated 26.2 bytecode and Forge metadata were also inspected. They contain Amber's `createOptional`/support-query API, Konfig's Forge `ChannelBuilder.optional`, NeoForge `PayloadRegistrar.optional`, per-peer support checks, Liteminer's `canSendToServer` activation gate, and `displayTest="IGNORE_ALL_VERSION"`.

## Coverage and known gaps

These repositories do not expose an instrumented coverage task for the added source-contract tests, so no percentage is claimed. The pure activation state machine, loader source contracts, compiled artifacts, and two Minecraft-version build matrices are covered. A live two-process Minecraft client/server connection test was not automated; connection safety is established from the loader negotiation APIs present in the generated artifacts rather than an interactive E2E session.
