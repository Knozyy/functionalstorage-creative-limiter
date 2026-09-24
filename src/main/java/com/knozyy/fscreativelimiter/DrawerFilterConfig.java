package com.knozyy.fscreativelimiter;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/**
 * Admin-managed list of items the Creative Vending Upgrade won't make infinite.
 * They can still be stored normally; a creative drawer just keeps their real amount.
 * Lives in config/functionalstorage/functionalstorage-creative-limiter.toml, next to Functional Storage's own files.
 * Forge watches the file, so edits are picked up without a restart.
 */
public class DrawerFilterConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCKED_ITEMS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("DrawerFilter");
        BLOCKED_ITEMS = builder
                .comment("Items the Creative Vending Upgrade won't make infinite.",
                        "Listed items can still be stored and upgraded like in any other drawer; a creative drawer just keeps",
                        "their real amount instead of handing out an endless supply.",
                        "",
                        "Entry types (quoted and comma separated):",
                        "  \"modid:item\"       a single item         e.g. \"minecraft:diamond\"",
                        "  \"#namespace:tag\"   every item in a tag   e.g. \"#forge:ingots/iron\"",
                        "  \"modid:*\"          every item of a mod   e.g. \"mekanism:*\"",
                        "Example: blockedItems = [\"functionalstorage:creative_vending_upgrade\", \"minecraft:diamond\", \"#forge:ingots/iron\"]",
                        "",
                        "Finding ids: press F3 + H in game to turn on advanced tooltips; hovering an item then shows its id",
                        "at the bottom of the tooltip (JEI also shows an item's tags).",
                        "",
                        "Changes are picked up as soon as the file is saved, no restart needed. latest.log shows a",
                        "\"Creative Limiter list loaded\" line with the new counts. If the file has a syntax error (missing quote or comma)",
                        "Forge may reset it to the default, so keep a copy before editing.",
                        "",
                        "Normal drawers decide per slot: only the listed item's slot stops being infinite.",
                        "Compacting drawers stop being creative entirely if any tier of their chain is listed, otherwise",
                        "an infinite allowed tier (e.g. nuggets) could be crafted into the listed one (e.g. ingots).")
                .defineListAllowEmpty(List.of("blockedItems"), () -> List.of("functionalstorage:creative_vending_upgrade"),
                        entry -> entry instanceof String);
        builder.pop();
        SPEC = builder.build();
    }
}
