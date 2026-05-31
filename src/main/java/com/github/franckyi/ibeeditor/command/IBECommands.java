package com.github.franckyi.ibeeditor.command;

import com.github.franckyi.ibeeditor.IBEEditor;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(
        modid = IBEEditor.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class IBECommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("ibe")
                        .then(
                                Commands.literal("count")
                                        .then(
                                                Commands.argument("amount", IntegerArgumentType.integer(1, 99))
                                                        .executes(context -> {
                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                            int amount = IntegerArgumentType.getInteger(context, "amount");

                                                            ItemStack stack = player.getMainHandItem();

                                                            if (stack.isEmpty()) {
                                                                context.getSource().sendFailure(
                                                                        Component.literal("IBE Editor: в руке нет предмета")
                                                                );
                                                                return 0;
                                                            }

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
                        .then(
                                Commands.literal("name")
                                        .then(
                                                Commands.argument("text", StringArgumentType.greedyString())
                                                        .executes(context -> {
                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                            String name = StringArgumentType.getString(context, "text");

                                                            ItemStack stack = player.getMainHandItem();

                                                            if (stack.isEmpty()) {
                                                                context.getSource().sendFailure(
                                                                        Component.literal("IBE Editor: в руке нет предмета")
                                                                );
                                                                return 0;
                                                            }

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
                        .then(
                                Commands.literal("clearname")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(
                                                        Component.literal("IBE Editor: в руке нет предмета")
                                                );
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
                        .then(
                                Commands.literal("lore")
                                        .then(
                                                Commands.argument("text", StringArgumentType.greedyString())
                                                        .executes(context -> {
                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                            String loreText = StringArgumentType.getString(context, "text");

                                                            ItemStack stack = player.getMainHandItem();

                                                            if (stack.isEmpty()) {
                                                                context.getSource().sendFailure(
                                                                        Component.literal("IBE Editor: в руке нет предмета")
                                                                );
                                                                return 0;
                                                            }

                                                            List<Component> lines = Arrays.stream(loreText.split("\\|"))
                                                                    .map(String::trim)
                                                                    .filter(line -> !line.isEmpty())
                                                                    .map(line -> (Component) Component.literal(line))
                                                                    .toList();

                                                            if (lines.isEmpty()) {
                                                                context.getSource().sendFailure(
                                                                        Component.literal("IBE Editor: lore пустой")
                                                                );
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
                        .then(
                                Commands.literal("clearlore")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(
                                                        Component.literal("IBE Editor: в руке нет предмета")
                                                );
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
                        .then(
                                Commands.literal("damage")
                                        .then(
                                                Commands.argument("amount", IntegerArgumentType.integer(0))
                                                        .executes(context -> {
                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                            int amount = IntegerArgumentType.getInteger(context, "amount");

                                                            ItemStack stack = player.getMainHandItem();

                                                            if (stack.isEmpty()) {
                                                                context.getSource().sendFailure(
                                                                        Component.literal("IBE Editor: в руке нет предмета")
                                                                );
                                                                return 0;
                                                            }

                                                            if (!stack.isDamageableItem()) {
                                                                context.getSource().sendFailure(
                                                                        Component.literal("IBE Editor: этот предмет нельзя повредить")
                                                                );
                                                                return 0;
                                                            }

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
                        .then(
                                Commands.literal("repair")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(
                                                        Component.literal("IBE Editor: в руке нет предмета")
                                                );
                                                return 0;
                                            }

                                            if (!stack.isDamageableItem()) {
                                                context.getSource().sendFailure(
                                                        Component.literal("IBE Editor: этот предмет нельзя починить")
                                                );
                                                return 0;
                                            }

                                            stack.setDamageValue(0);
                                            updatePlayerInventory(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("IBE Editor: предмет починен"),
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