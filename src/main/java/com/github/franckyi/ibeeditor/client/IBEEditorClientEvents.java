package com.github.franckyi.ibeeditor.client;

import com.github.franckyi.ibeeditor.IBEEditor;
import com.github.franckyi.ibeeditor.client.screen.ItemEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = IBEEditor.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class IBEEditorClientEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        while (IBEEditorClient.OPEN_ITEM_EDITOR.consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.player != null && minecraft.screen == null) {
                minecraft.setScreen(new ItemEditorScreen());
            }
        }
    }
}