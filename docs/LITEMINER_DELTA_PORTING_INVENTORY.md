# Liteminer → Liteminer Delta 改良点・1.21.1移植インベントリ

## 状態と目的

- 状態: 調査資料。**この文書ではソース変更、ビルド、テスト実行を行わない。**
- 目的: 1.21.1の元LiteminerをLiteminer Deltaへ移植する前に、再現すべきDelta化・機能改良・互換性要件を分けて確定する。
- 結論: 1.20.1にはDelta化済みソースが `1.20.1` ブランチにある。一方、1.21.1ブランチは元Liteminerのままであり、Delta化済みの1.21.1ソースは確認できない。

## 調査対象と根拠

| 対象 | Git参照 | 実態 | 用途 |
|---|---|---|---|
| 元Liteminer 1.20.1 | `upstream/1.20.1` | 上流実装 | Delta化との差分の基準 |
| Liteminer Delta 1.20.1 | `1.20.1` / `origin/1.20.1` | `liteminer_delta`、Fabric・Forge・Quilt | Delta化済みの実例 |
| 元Liteminer 1.21.1 | `1.21.1` | `liteminer`、Fabric・NeoForge | これから移植する基準 |
| 現行Delta | `main` | Stonecutter構成、Fabric・Forge・NeoForge | 非対称接続の後続改善の基準 |

`upstream/1.20.1...1.20.1` の比較では、70ファイル・1,416行追加・401行削除がある。これは単なるMod IDの置換ではない。

## 1. Deltaとして必ず移植する識別子・配布面

| 項目 | 元Liteminer 1.21.1 | Deltaの正しい状態 | 1.21.1での対応 |
|---|---|---|---|
| Mod ID | `liteminer` | `liteminer_delta` | 必須 |
| Java namespace | `com.iamkaf.liteminer` | `net.aosankaku.liteminerdelta` | 必須 |
| 名称・説明・作者・URL | iamkafの上流情報 | Liteminer Delta / AoSankakuの情報 | 必須 |
| jar・Maven座標 | 上流Liteminerの座標 | `net.aosankaku.liteminerdelta` の座標 | 必須 |
| assets / data namespace | `liteminer` | `liteminer_delta` | 必須 |
| mixin / access widener名 | `liteminer.*` | `liteminer_delta.*` | 必須 |
| 翻訳キー・設定キー | `liteminer.*` | `liteminer_delta.*` | 必須 |

対象ファイルは `gradle.properties`、各ローダーのModメタデータ、`common`／`fabric`／`neoforge`のJavaパッケージ、assets、data、mixin定義、access widener、Crowdin設定、README、changelogである。

### 残すべき後方互換性

Delta 1.20.1はMod自身のIDを変更しながら、**既存の `liteminer:*` タグと FTB Ultimineタグの読取り互換性を維持**している。1.21.1でも、既存ワールドやパックを壊さないため次を維持する。

- 新しい公開タグは `liteminer_delta:*`。
- 判定時は旧 `liteminer:*` と FTB Ultimineタグを併せて読む。
- 旧Mod IDをネットワーク識別子として残さない。通信は新IDの任意チャネルに移す。

## 2. 1.20.1 Deltaブランチで確認できる機能改良

以下は `upstream/1.20.1` から `1.20.1` に入った実装差分である。1.21.1にすべてを機械的にコピーせず、対応APIに合わせて同じ利用者向け挙動を再現する。

| 分類 | 改良点 | 主な根拠ファイル | 1.21.1への優先度 |
|---|---|---|---|
| 採掘許可 | 空腹時でも一括破壊を許可する設定 | `LiteminerConfig`、`FoodExhaustion` | 高 |
| 採掘許可 | Farmer's DelightのNourishment効果を考慮 | `FoodExhaustion` | 高（依存Modが存在する場合） |
| ブロック照合 | 通常鉱石と深層岩鉱石、石材バリアントを区別する選択肢 | `BlockFamily`、`ShapelessWalker`、client config | 高 |
| サーバー状態 | プレイヤー別のキー状態・形状・照合設定を保持 | `LiteminerPlayerState`、`LiteminerNetwork` | 高 |
| 同期 | キー状態・形状・空腹要件をC2S/S2Cで同期 | `networking/*` | 高 |
| 採掘形状 | Shapeless、Tunnel、上下Staircase、3x3の選択と境界判定を改善 | `shapes/*` | 高 |
| 表示 | HUD、選択枠、形状表示、空腹不足表示を改善 | `HUD`、`BlockHighlightRenderer` | 中 |
| 設定UI | ForgeのCloth Config統合 | `forge/.../ClothConfigIntegration.java` | 中 |
| データ | 除外ツール、追加許可ツール、除外ブロック、ホワイトリストタグ | `data/liteminer_delta/tags/*` | 高 |
| ローカライズ | 英語以外を含む12言語の翻訳資産 | `assets/liteminer_delta/lang/*` | 中 |
| 安定性 | access widener追加、Mixin・ローダー側初期化の調整 | resource定義、各ローダーのentry point | 高 |

## 3. 非対称接続のために必須の後続Delta改善

この節は1.20.1初期Delta化後に、現行Deltaで追加された改善である。今回の要件に直接関係するため、1.21.1への移植では優先度を最上位にする。

### 3.1 任意ネットワークチャネル

現行Deltaの `LiteminerNetwork` は Amber の `NetworkChannel.createOptional(...)` を使い、送信前に相手側がペイロードを受理できるかを確認する。

- クライアント→サーバー送信は `canSendToServer(...)` が真のときだけ許可する。
- サーバー→クライアント送信は、各接続先が受理できるときだけ行う。
- 任意チャネルの採用により、Amber Delta／Liteminer Deltaが片側にないだけでログインが失敗しないことを目指す。

### 3.2 クライアント有効化ゲート

現行Deltaの `ClientActivationGate` と `LiteminerClient` は、サーバーが対応していない場合に以下を保証する。

1. 一括破壊の有効化を拒否する。
2. キー状態パケットを送らない。
3. 一括破壊用HUD状態を有効にしない。
4. キー操作による拒否通知は1回だけ表示する。

これは「Deltaクライアント → 対象Modなしサーバー」で、一括破壊が作動しないという要件の中核である。1.21.1では現行の `ClientActivationGate` を挙動仕様として移植し、Minecraft 1.21.1のoverlay／action-bar APIに合わせる。

### 3.3 ローダーの片側導入許容

現行Deltaでは、ローダーごとのメタデータを片側導入を拒否しない設定へ更新している。特にForge／NeoForgeの表示テストまたはペイロード登録が必須Mod前提へ戻らないことを確認する。

この機能はLiteminerだけで完結しない。Amber DeltaおよびKonfigを同じMinecraft版・ローダーで片側導入可能な版にそろえ、各Modの任意通信設定を合わせる必要がある。

## 4. 1.21.1固有の移植上の差

| 観点 | 1.20.1 Delta実例 | 1.21.1元Liteminer | 方針 |
|---|---|---|---|
| ローダー | Fabric / Forge / Quilt | Fabric / NeoForge | Fabricは対応、NeoForgeはAPI差分に合わせて移植、Forgeは新規ローダー作業 |
| ビルド構成 | 旧Architecturyの単一版root構成 | 旧Architecturyの単一版root構成 | まず既存1.21.1構成をDelta化し、Stonecutter移行は別判断 |
| Mod ID | 既に `liteminer_delta` | `liteminer` | metadata・namespace・resourceを一括移行 |
| Amber | 旧Amber `1.3.0-beta.2+1.20.1` | 旧Amber `1.2.0-beta.5` | Amber Delta 1.21.1のAPIへ依存関係・通信呼出しを更新 |
| 非対称対応 | 初期Delta枝には未保証 | 元実装は双方必須前提 | 現行Deltaの任意チャネル・activation gateを追加 |

**重要:** 元Liteminer 1.21.1にはForgeモジュールがない。したがって「1.21.1のFabric／Forge／NeoForgeをすべて公開対象にする」場合、ForgeはDelta化とは別に新規ポートとして見積もる。まずFabricとNeoForgeを正しくDelta化し、Forgeを独立タスクにする。

## 5. 1.21.1での実装順序（次の作業用）

1. `1.21.1` ブランチから隔離作業ブランチを作成する。
2. 1.20.1 Deltaとの差分を基に、Mod ID・namespace・resources・metadataをDeltaへ移す。
3. 旧 `liteminer:*`／FTB Ultimineタグの読取り互換を実装・確認する。
4. Amber Delta 1.21.1と必要なKonfig依存関係を導入し、通信を任意チャネルへ更新する。
5. `ClientActivationGate` 相当を追加し、非対応サーバーでは一括破壊と状態送信を止める。
6. 1.20.1 Deltaの採掘判定、状態同期、形状、タグ、設定を1.21.1 APIへ適合させる。
7. Fabric、NeoForgeをビルド・単体検証する。Forgeを追加する場合は独立して実装・検証する。
8. 非対称TeaKitの対象ノードへ1.21.1を追加し、両方向の接続と「一括破壊が不作動」を実証する。

## 6. 受入条件

### Delta化の完了

- [ ] 配布物、Mod ID、Java namespace、assets、data、mixin、access widener、翻訳キーに上流の `liteminer` 識別子が残っていない（互換タグ読取りを除く）。
- [ ] 既存の `liteminer:*` とFTB Ultimineタグを使うパックで採掘対象の制御が維持される。
- [ ] FabricとNeoForgeで、Deltaとして読み込まれる。
- [ ] Forgeを公開対象に含める場合、Forgeでも上記を満たす。

### 非対称契約の完了

- [ ] Amber Delta＋Liteminer Deltaクライアントは、対象Modなしサーバーへ接続でき、kickされない。
- [ ] その状態で一括破壊キーを押しても、照準ブロック以外を破壊せず、状態パケットも送らない。
- [ ] 対象Modなしクライアントは、Deltaサーバーへ接続でき、kickされない。
- [ ] Amber／Liteminer／Konfigのいずれも、非対応相手に任意ペイロードを送らない。
- [ ] 非対称TeaKitでFabric・NeoForgeの両方向を通す。Forgeを追加した場合はForgeも通す。

## 7. 現時点で確認した成果物状況

- 1.20.1 Deltaの**ソース**: 存在する（`1.20.1` ブランチ）。
- 1.20.1 Deltaの**配布用Fabric／Forge jar**: 現在のワークスペース上には存在しない。中間common jarや依存キャッシュを配布・試験用jarとして使わない。
- 1.21.1 Deltaの**ソースおよびjar**: 確認できない。存在するのは元Liteminerの `1.21.1` ブランチと作業ツリーである。

## 8. 実装時に再確認するコマンド

以下は将来の実装・検証時の参照用であり、この調査では実行しない。

```text
git diff --name-status upstream/1.20.1...1.20.1
git diff --name-status 1.21.1...<1.21.1-delta-work-branch>
just build <1.21.1-node>
just test-asymmetric-case <1.21.1-node> delta-client
just test-asymmetric-case <1.21.1-node> delta-server
```
