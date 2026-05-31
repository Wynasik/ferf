package com.github.franckyi.ibeeditor.command;

import com.github.franckyi.ibeeditor.IBEEditor;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber(
        modid = IBEEditor.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class IBECommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("ibe")
                        .then(Commands.literal("count")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 99))
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                                return 0;
                                            }

                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            stack.setCount(amount);
                                            updatePlayerInventory(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("IBE Editor: количество изменено на " + amount),
                                                    false
                                            );

                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("name")
                                .then(Commands.argument("text", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                                return 0;
                                            }

                                            String name = StringArgumentType.getString(context, "text");
                                            stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
                                            updatePlayerInventory(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("IBE Editor: имя изменено на " + name),
                                                    false
                                            );

                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("clearname")
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ItemStack stack = player.getMainHandItem();

                                    if (stack.isEmpty()) {
                                        context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                        return 0;
                                    }

                                    stack.remove(DataComponents.CUSTOM_NAME);
                                    updatePlayerInventory(player);

                                    context.getSource().sendSuccess(
                                            () -> Component.literal("IBE Editor: имя предмета очищено"),
                                            false
                                    );

                                    return 1;
                                })
                        )
                        .then(Commands.literal("lore")
                                .then(Commands.argument("text", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                                return 0;
                                            }

                                            String loreText = StringArgumentType.getString(context, "text");

                                            List<Component> lines = Arrays.stream(loreText.split("\\|"))
                                                    .map(String::trim)
                                                    .filter(line -> !line.isEmpty())
                                                    .map(line -> (Component) Component.literal(line))
                                                    .toList();

                                            if (lines.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: lore пустой"));
                                                return 0;
                                            }

                                            stack.set(DataComponents.LORE, new ItemLore(lines));
                                            updatePlayerInventory(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("IBE Editor: lore изменён"),
                                                    false
                                            );

                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("clearlore")
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ItemStack stack = player.getMainHandItem();

                                    if (stack.isEmpty()) {
                                        context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                        return 0;
                                    }

                                    stack.remove(DataComponents.LORE);
                                    updatePlayerInventory(player);

                                    context.getSource().sendSuccess(
                                            () -> Component.literal("IBE Editor: lore очищен"),
                                            false
                                    );

                                    return 1;
                                })
                        )
                        .then(Commands.literal("damage")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                                return 0;
                                            }

                                            if (!stack.isDamageableItem()) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: этот предмет нельзя повредить"));
                                                return 0;
                                            }

                                            int amount = IntegerArgumentType.getInteger(context, "amount");
                                            int maxDamage = stack.getMaxDamage();

                                            if (amount > maxDamage) {
                                                amount = maxDamage;
                                            }

                                            stack.setDamageValue(amount);
                                            updatePlayerInventory(player);

                                            int finalAmount = amount;
                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("IBE Editor: damage изменён на " + finalAmount),
                                                    false
                                            );

                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("unbreakable")
                                .then(Commands.argument("enabled", StringArgumentType.word())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                                return 0;
                                            }

                                            String raw = StringArgumentType.getString(context, "enabled").toLowerCase(Locale.ROOT);

                                            Boolean enabled = switch (raw) {
                                                case "true", "on", "1", "yes" -> true;
                                                case "false", "off", "0", "no" -> false;
                                                default -> null;
                                            };

                                            if (enabled == null) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: unbreakable = true/false"));
                                                return 0;
                                            }

                                            if (enabled) {
                                                stack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
                                            } else {
                                                stack.remove(DataComponents.UNBREAKABLE);
                                            }

                                            updatePlayerInventory(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("IBE Editor: unbreakable = " + enabled),
                                                    false
                                            );

                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("rarity")
                                .then(Commands.argument("value", StringArgumentType.word())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                                return 0;
                                            }

                                            String raw = StringArgumentType.getString(context, "value").toLowerCase(Locale.ROOT);

                                            Rarity rarity = switch (raw) {
                                                case "common" -> Rarity.COMMON;
                                                case "uncommon" -> Rarity.UNCOMMON;
                                                case "rare" -> Rarity.RARE;
                                                case "epic" -> Rarity.EPIC;
                                                default -> null;
                                            };

                                            if (rarity == null) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: rarity = common/uncommon/rare/epic"));
                                                return 0;
                                            }

                                            stack.set(DataComponents.RARITY, rarity);
                                            updatePlayerInventory(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("IBE Editor: rarity изменён на " + raw),
                                                    false
                                            );

                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("clearrarity")
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ItemStack stack = player.getMainHandItem();

                                    if (stack.isEmpty()) {
                                        context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                        return 0;
                                    }

                                    stack.remove(DataComponents.RARITY);
                                    updatePlayerInventory(player);

                                    context.getSource().sendSuccess(
                                            () -> Component.literal("IBE Editor: rarity очищен"),
                                            false
                                    );

                                    return 1;
                                })
                        )
                        .then(Commands.literal("glint")
                                .then(Commands.argument("enabled", StringArgumentType.word())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                                return 0;
                                            }

                                            String raw = StringArgumentType.getString(context, "enabled").toLowerCase(Locale.ROOT);

                                            Boolean enabled = switch (raw) {
                                                case "true", "on", "1", "yes" -> true;
                                                case "false", "off", "0", "no" -> false;
                                                default -> null;
                                            };

                                            if (enabled == null) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: glint = true/false"));
                                                return 0;
                                            }

                                            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, enabled);
                                            updatePlayerInventory(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("IBE Editor: glint = " + enabled),
                                                    false
                                            );

                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("clearglint")
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ItemStack stack = player.getMainHandItem();

                                    if (stack.isEmpty()) {
                                        context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                        return 0;
                                    }

                                    stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
                                    updatePlayerInventory(player);

                                    context.getSource().sendSuccess(
                                            () -> Component.literal("IBE Editor: glint очищен"),
                                            false
                                    );

                                    return 1;
                                })
                        )
                        .then(Commands.literal("repaircost")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(0, 1000000))
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                                return 0;
                                            }

                                            int amount = IntegerArgumentType.getInteger(context, "amount");

                                            stack.set(DataComponents.REPAIR_COST, amount);
                                            updatePlayerInventory(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("IBE Editor: repair cost = " + amount),
                                                    false
                                            );

                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("maxstack")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 99))
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                                return 0;
                                            }

                                            int amount = IntegerArgumentType.getInteger(context, "amount");

                                            if (stack.isDamageableItem() && amount > 1) {
                                                context.getSource().sendFailure(Component.literal("IBE Editor: у damageable предметов max stack обычно должен быть 1"));
                                                return 0;
                                            }

                                            stack.set(DataComponents.MAX_STACK_SIZE, amount);

                                            if (stack.getCount() > amount) {
                                                stack.setCount(amount);
                                            }

                                            updatePlayerInventory(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("IBE Editor: max stack size = " + amount),
                                                    false
                                            );

                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("clearcustom")
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    ItemStack stack = player.getMainHandItem();

                                    if (stack.isEmpty()) {
                                        context.getSource().sendFailure(Component.literal("IBE Editor: в руке нет предмета"));
                                        return 0;
                                    }

                                    stack.remove(DataComponents.CUSTOM_NAME);
                                    stack.remove(DataComponents.LORE);
                                    stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
                                    stack.remove(DataComponents.REPAIR_COST);
                                    stack.remove(DataComponents.RARITY);
                                    stack.remove(DataComponents.UNBREAKABLE);

                                    updatePlayerInventory(player);

                                    context.getSource().sendSuccess(
                                            () -> Component.literal("IBE Editor: базовые кастомные компоненты очищены"),
                                            false
                                    );

                                    return 1;
                                })
                        )
        );
    }

    private static void updatePlayerInventory(ServerPlayer player) {
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
    }
}