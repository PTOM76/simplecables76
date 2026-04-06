# Simple Cables (English)
This minecraft mod adds cables for transmitting energy in FabricMC.

- CurseForge: https://www.curseforge.com/minecraft/mc-mods/simplecables76
- Modrinth: https://modrinth.com/mod/simplecables76

## Libraries
- Fabric API
- MCPitanLib
- Fabric Language Kotlin
- Reborn Energy API (included internally)

## Blocks
- Energy Cable - A cable that transmits energy, only obtainable in creative mode (512 E/t)
- Copper Cable - A cable that transmits energy (256 E/t)
- Iron Cable - A cable that transmits energy (512 E/t)
- Gold Cable - A cable that transmits energy (1024 E/t)

Right-clicking on the cable without holding any block items will allow you to check the amount of energy stored in that cable.

## Configurations
`config/simplecables76.json` contains the following configuration options

- `rebornEnergyConversionRate` - Energy conversion rate (default: 1.0)
- `energy.transferRate.energyCable` - Transfer rate for Energy Cable (default: 512)
- `energy.transferRate.copperCable` - Transfer rate for Copper Cable (default: 256)
- `energy.transferRate.ironCable` - Transfer rate for Iron Cable (default: 512)
- `energy.transferRate.goldCable` - Transfer rate for Gold Cable (default: 1024)

The transfer rate values represent the amount of energy transferred per tick.

# Simple Cables (日本語)
エネルギー伝送ケーブル単品を追加するFabricMC用のマイクラModです。

## 前提Mod
- Fabric API
- MCPitanLib
- Fabric Language Kotlin
- Reborn Energy API (内部に埋込済み)

## ブロック
- エネルギーケーブル - エネルギーを伝送する、クリエイティブモードでのみ入手可能なケーブル (512 E/t)
- 銅ケーブル - エネルギーを伝送する (256 E/t)
- 鉄ケーブル - エネルギーを伝送する (512 E/t)
- 金ケーブル - エネルギーを伝送する (1024 E/t)

ブロック系アイテムを所持せずに右クリックすると、そのケーブルに入っているエネルギーを確認できます。

## 設定
`config/simplecables76.json`にて、下記の設定項目を変更できます。

- `rebornEnergyConversionRate` - エネルギー変換レート (デフォルト: 1.0)
- `energy.transferRate.energyCable` - エネルギーケーブルの転送速度 (デフォルト: 512)
- `energy.transferRate.copperCable` - 銅ケーブルの転送速度 (デフォルト: 256)
- `energy.transferRate.ironCable` - 鉄ケーブルの転送速度 (デフォルト: 512)
- `energy.transferRate.goldCable` - 金ケーブルの転送速度 (デフォルト: 1024)

転送速度の数値は、1ティックあたりのエネルギー量を表しています。
