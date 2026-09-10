# Liteminer Delta initial release

- Publishes Liteminer Delta as an independently maintained fork of Liteminer.
- Uses Amber Delta for its optional networking API, so either mod can be absent from the client or server without a connection rejection.
- When the server does not support Liteminer Delta, vein-mining actions are disabled instead of sending unsupported packets.
- Includes Fabric, Forge, and NeoForge artifacts for every currently supported Minecraft version.
