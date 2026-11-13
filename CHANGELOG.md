# Changelog

## 1.0.4
- Fixed the lenses, the Gaia Hammer and the liquid inside cocktails, wines and water grenades being completely transparent.
- Fixed the Shadow Katana texture, which was drawn stretched because its animation file was missing.

## 1.0.3

Botania Revived 2.0.2 Required

## 1.0.2
- The mod now requires Botania Revived 2.0.0 or newer, and is built and tested against Botania Revived 2.0.0.

## 1.0.1
- No gameplay changes. This version is built and uploaded automatically by GitHub Actions from the source repository.

## 1.0.0

- First port of Extra Botany to Minecraft 1.21.1 / NeoForge, compiled against Botania Revived 1.0.3.
- Botania API calls, registry names, recipes, tags, loot tables, advancements and Patchouli entries adapted to Botania Revived.
- Mixins into Botania adapted (Wand of the Forest, Gaia Guardian summon check, HUD, flowers).
- Single-module Gradle project (moddev 2.0.147, Gradle 9.8.0).
- Source reorganized: `registry/` (one class per registry type), `datagen/` (providers for advancements, loot, models, patchouli, recipes, sounds, tags), `config/`, `event/`, `integration/`. Root package is now `velrondevs.extrabotany` (mod id unchanged).
- Content of the original ExtraBotany 1.12.2 that was missing and is now ported: Ring of Mana Drive, Mana Lens: Cloud, Tri-Bloodstone, Mini Handbag (24 slots), the three Airships (manasteel, elementium, terrasteel), the effects Black Sabbath, Blood Temptation, Constant Pain, Mind Crack and Temptation, the bottle loot injected into seven vanilla chests, and the dispenser that hits a pedestal with a hammer.
- The ExtraBotany creative tab no longer lists a stack twice (Nature Orb and Master Band of Mana are registered as JEI subtypes).
- Fixed while testing: Das Rheingold left the relic without an owner, the blacksmith chest loot injection never applied, Constant Pain never dealt true damage, and several lang keys were missing.
- 234 GameTests (recipes, weapons and relics, armor, baubles, flowers, bosses, mounts, Mini Handbag, effects and loot) in a separate `gametest` source set that is not part of the release jar.
