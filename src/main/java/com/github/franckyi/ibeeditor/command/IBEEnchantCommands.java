package com.github.franckyi.ibeeditor.command;

import com.github.franckyi.ibeeditor.IBEEditor;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = IBEEditor.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class IBEEnchantCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("ibe")
                        .then(
                                Commands.literal("enchant")
                                        .then(
                                                Commands.argument("id", StringArgumentType.word())
                                                        .then(
                                                                Commands.argument("level", IntegerArgumentType.integer(1, 255))
                                                                        .executes(context -> {
                                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                                            ItemStack stack = player.getMainHandItem();

                                                                            if (stack.isEmpty()) {
                                                                                context.getSource().sendFailure(
                                                                                        Component.literal("IBE Editor: в руке нет предмета")
                                                                                );
                                                                                return 0;
                                                                            }

                                                                            String id = StringArgumentType.getString(context, "id");
                                                                            int level = IntegerArgumentType.getInteger(context, "level");

                                                                            if (!id.contains(":")) {
                                                                                id = "minecraft:" + id;
                                                                            }

                                                                            String command = "enchant "
                                                                                    + player.getGameProfile().getName()
                                                                                    + " "
                                                                                    + id
                                                                                    + " "
                                                                                    + level;

                                                                            player.server.getCommands().performPrefixedCommand(
                                                                                    player.createCommandSourceStack()
                                                                                            .withPermission(4)
                                                                                            .withSuppressedOutput(),
                                                                                    command
                                                                            );

                                                                            player.getInventory().setChanged();
                                                                            player.containerMenu.broadcastChanges();

                                                                            String finalId = id;
                                                                            context.getSource().sendSuccess(
                                                                                    () -> Component.literal("IBE Editor: команда чар выполнена: " + finalId + " " + level),
                                                                                    false
                                                                            );

                                                                            return 1;
                                                                        })
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("clearenchants")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ItemStack stack = player.getMainHandItem();

                                            if (stack.isEmpty()) {
                                                context.getSource().sendFailure(
                                                        Component.literal("IBE Editor: в руке нет предмета")
                                                );
                                                return 0;
                                            }

                                            stack.remove(DataComponents.ENCHANTMENTS);

                                            player.getInventory().setChanged();
                                            player.containerMenu.broadcastChanges();

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("IBE Editor: чары очищены"),
                                                    false
                                            );

                                            return 1;
                                        })
                        )
        );
    }
}