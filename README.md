# Liteminer Delta

An independently maintained fork of [Liteminer](https://modrinth.com/mod/liteminer), originally created by iamkaf.
Liteminer Delta is distributed under the original MIT license.

![License](https://img.shields.io/badge/license-MIT-blue.svg)

## ⛏️ About

Liteminer Delta adds configurable vein mining with multiple mining shapes, a HUD, and cross-loader support.
Built using a multi-loader architecture supporting Fabric, Forge, and NeoForge.

## 📦 Features

- Vein mining with multiple shapes (Shapeless, Tunnel, 3×3, Staircase Up/Down)
- Configurable behavior (block limit, tool checks, exhaustion, etc.)
- HUD + keybind workflow
- Tag-based block/tool allow/deny lists (compatible with FTB Ultimine tags)

Liteminer Delta uses the `liteminer_delta:*` tag namespace and continues to read legacy
`liteminer:*` tags for compatibility with existing worlds, datapacks, and modpacks.

## ⚠️ Migration from Liteminer

Liteminer Delta is a distinct mod, not an in-place update. Remove upstream Liteminer before installing
Delta: its mod id is `liteminer_delta`, its configuration files and `/liteminer_delta` command are separate,
and its addon API namespace is `net.aosankaku.liteminerdelta`. Only legacy `liteminer:*` data tags are read
automatically; migrate configurations, commands, and API integrations deliberately.

## 🗂️ Monorepo Structure

This repository contains all Minecraft versions of Liteminer Delta:

```
common/                # Shared code across loaders
fabric/                # Fabric implementation
forge/                 # Forge implementation
neoforge/              # NeoForge implementation
versions/              # Minecraft-version overlays
```

## 🚀 Supported Versions

- 26.2 — Active (Fabric, Forge, NeoForge)
- 26.1.2 — Active (Fabric, Forge, NeoForge)
- 26.1.1 — Active (Fabric, Forge, NeoForge)
- 26.1 — Active (Fabric, Forge, NeoForge)
- 1.21.11 — Active (Fabric, Forge, NeoForge)

## 🛠️ Building

Use `just` from the repo root as the command runner.

```bash
# Build all loaders for a specific version
just build 1.21.11

# Build a specific task in a specific version
just run 1.21.11 :fabric:build
just run 1.21.11 :neoforge:build

# Run the game for development
just run 1.21.11 fabric:runClient
just run 1.21.11 neoforge:runClient

# Run tests
just test 1.21.11
```

Built jars are in `<loader>/versions/<version>/build/libs/`. For Modrinth uploads, stage only the Delta release jars instead of selecting files directly from that directory:

```bash
just stage-modrinth 26.2
```

This creates `build/modrinth/<version>/` with exactly one release jar for each enabled loader. Do not upload source, Javadoc, or legacy `liteminer-*.jar` files.

## 💻 Development

### Prerequisites

- Java 21 or higher
- Git
- just (install: `https://github.com/casey/just`)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/AoSankaku/liteminer.git
   cd liteminer
   ```

2. Open the specific version directory in your IDE:
   ```bash
   # Open 1.21.11 in IntelliJ IDEA, for example
   idea 1.21.11
   ```

## 🧩 Addon API

Liteminer exposes a public addon API under `net.aosankaku.liteminerdelta.api` on the active `26.1.2` line.
The API is intended for mods that need to inspect player state, register custom mining shapes, react to
veinmine operations, or adjust the client HUD.

### Reading Liteminer State

Use `LiteminerApi` for server-side player state:

```java
import net.aosankaku.liteminerdelta.api.LiteminerApi;

boolean active = LiteminerApi.isVeinmining(player);
int shapeIndex = LiteminerApi.getSelectedShapeIndex(player);
var selectedShape = LiteminerApi.getSelectedShape(player);
int blockLimit = LiteminerApi.getBlockLimit();
```

You can also set a player's selected shape by id:

```java
import net.minecraft.resources.Identifier;

LiteminerApi.setSelectedShape(player, Identifier.fromNamespaceAndPath("liteminer_delta", "three_by_three"));
```

### Veinmine Events

Server-side lifecycle events live in `net.aosankaku.liteminerdelta.api.event.LiteminerEvents`.

Available events:

- `BEFORE_VEINMINE`: fired before Liteminer processes secondary blocks. Return anything other than `InteractionResult.PASS` to cancel the operation.
- `ALLOW_BLOCK`: fired for each secondary block candidate. Return anything other than `InteractionResult.PASS` to skip that block.
- `AFTER_VEINMINE`: fired after Liteminer finishes processing secondary blocks.

Example:

```java
import net.aosankaku.liteminerdelta.api.event.LiteminerEvents;
import net.minecraft.world.InteractionResult;

LiteminerEvents.ALLOW_BLOCK.register(context -> {
    if (isProtected(context.level(), context.pos(), context.player())) {
        return InteractionResult.FAIL;
    }

    return InteractionResult.PASS;
});
```

Each event context includes the operation type (`BREAK` or `INTERACT`), level, player, origin block,
tool, selected shape, shape index, and block limit. Per-block contexts also include the candidate block.
The after-event context includes the full candidate list, processed blocks, and skipped blocks.

### Custom Shapes

Register custom shapes through `LiteminerShapes`:

```java
import net.aosankaku.liteminerdelta.api.shape.LiteminerShapes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

LiteminerShapes.register(
        Identifier.fromNamespaceAndPath("examplemod", "vertical_column"),
        Component.literal("Vertical Column"),
        (level, player, origin) -> {
            var blocks = new java.util.HashSet<net.minecraft.core.BlockPos>();
            blocks.add(origin);
            blocks.add(origin.above());
            blocks.add(origin.below());
            return blocks;
        }
);
```

Registered shapes participate in Liteminer's shape cycling, HUD text, block highlighting, and server-side
veinmine logic. Shape walkers should usually include the origin in the returned set; Liteminer skips the
origin when processing secondary blocks.

Built-in shape ids are exposed on `LiteminerShapes`:

- `SHAPELESS`
- `SMALL_TUNNEL`
- `STAIRCASE_UP`
- `STAIRCASE_DOWN`
- `THREE_BY_THREE`

### Client HUD Event

Client-side presentation events live in `net.aosankaku.liteminerdelta.api.event.LiteminerClientEvents`.

Use `MODIFY_HUD` to change or hide Liteminer's default HUD:

```java
import net.aosankaku.liteminerdelta.api.event.LiteminerClientEvents;
import net.minecraft.network.chat.Component;

LiteminerClientEvents.MODIFY_HUD.register(context -> {
    context.lines().add(Component.literal("Addon active"));
    context.setTextColor(0xFF55FF55);
});
```

`LiteminerHudContext` exposes the selected block count, selected shape, mutable HUD lines, visibility,
text color, line height, and screen-center offsets.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Links

- **Issues**: https://github.com/AoSankaku/liteminer/issues
- **Modrinth**: https://modrinth.com/mod/liteminer-delta
- **Upstream project**: https://modrinth.com/mod/liteminer

## 👤 Author

**AoSankaku**

- GitHub: [@AoSankaku](https://github.com/AoSankaku)

Original Liteminer author: [iamkaf](https://github.com/iamkaf)
