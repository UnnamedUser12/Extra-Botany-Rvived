# Changelog

## 1.0.3

Botania Revived 2.0.2 Required

## 1.0.2
- The mod now requires Botania Revived 2.0.0 or newer, and is built and tested against Botania Revived 26.1.2 (2.0.0).

## 1.0.1
- Ported to Minecraft 26.1.2 / NeoForge 26.1.2.126, built against Botania Revived 26.1.2 (1.0.7).
- Rendering moved to the 26.1 render states: entities, items, armor (now with `equipment` assets) and the GUI.
- Recipes extend `BotaniaRecipe` and use the Botania serializers. The four custom recipe types are synced to the client.
- Entities, block entities, fluids and energy use the new `ValueInput`/`ValueOutput` and transfer APIs.
- Network packets are sent as NBT, because the JSON codec no longer exists in 26.1.
- Mixins audited against 26.1.2. `UniversalRuneMixin` now works on `Ingredient.test`, and Botania 1.0.7 adds the Rune of Deception, which the Rune of Sin replaces.
- Known differences: the custom shaders (Gaia grain and the boss health bar) became vanilla pipelines, so that effect is gone. The EMI integration is disabled because EMI has no 26.1.2 version. Milk now also removes Black Sabbath, Blood Temptation, Constant Pain and Divine Justice, because effect cures no longer exist in 26.1.
- 235 GameTests (recipes, weapons and relics, armor, baubles, flowers, bosses, mounts, Mini Handbag, effects and loot) in a separate `gametest` source set that is not part of the release jar. Every test runs in its own isolated environment.

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
