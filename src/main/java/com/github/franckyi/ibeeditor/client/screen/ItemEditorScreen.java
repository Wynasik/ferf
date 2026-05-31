package com.github.franckyi.ibeeditor.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;

public class ItemEditorScreen extends Screen {
    private EditBox nameField;
    private EditBox loreField;
    private EditBox damageField;
    public ItemEditorScreen() {
        super(Component.literal("IBE Editor"));
    }

    @Override
    protected void init() {
        this.nameField = new EditBox(
                this.font,
                this.width / 2 - 145,
                this.height - 140,
                180,
                20,
                Component.literal("Новое имя")
        );

        this.nameField.setMaxLength(128);
        this.nameField.setHint(Component.literal("Новое имя предмета"));
        this.addRenderableWidget(this.nameField);

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Имя"),
                        button -> setCustomName()
                ).bounds(
                        this.width / 2 + 40,
                        this.height - 140,
                        50,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Сброс"),
                        button -> clearCustomName()
                ).bounds(
                        this.width / 2 + 95,
                        this.height - 140,
                        60,
                        20
                ).build()
        );

        this.loreField = new EditBox(
                this.font,
                this.width / 2 - 145,
                this.height - 112,
                180,
                20,
                Component.literal("Описание")
        );

        this.loreField.setMaxLength(256);
        this.loreField.setHint(Component.literal("Lore: строка1|строка2"));
        this.addRenderableWidget(this.loreField);

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Lore"),
                        button -> setLore()
                ).bounds(
                        this.width / 2 + 40,
                        this.height - 112,
                        50,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Сброс"),
                        button -> clearLore()
                ).bounds(
                        this.width / 2 + 95,
                        this.height - 112,
                        60,
                        20
                ).build()
        );

        this.damageField = new EditBox(
                this.font,
                this.width / 2 - 145,
                this.height - 84,
                80,
                20,
                Component.literal("Damage")
        );

        this.damageField.setMaxLength(8);
        this.damageField.setHint(Component.literal("Damage"));
        this.addRenderableWidget(this.damageField);

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Damage"),
                        button -> setDamage()
                ).bounds(
                        this.width / 2 - 60,
                        this.height - 84,
                        70,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Починить"),
                        button -> repairItem()
                ).bounds(
                        this.width / 2 + 15,
                        this.height - 84,
                        80,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("-1"),
                        button -> changeCount(-1)
                ).bounds(
                        this.width / 2 - 145,
                        this.height - 56,
                        40,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("+1"),
                        button -> changeCount(1)
                ).bounds(
                        this.width / 2 - 100,
                        this.height - 56,
                        40,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("16"),
                        button -> setCount(16)
                ).bounds(
                        this.width / 2 - 55,
                        this.height - 56,
                        40,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("64"),
                        button -> setCount(64)
                ).bounds(
                        this.width / 2 - 10,
                        this.height - 56,
                        40,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("99"),
                        button -> setCount(99)
                ).bounds(
                        this.width / 2 + 35,
                        this.height - 56,
                        40,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Скопировать данные"),
                        button -> copyItemData()
                ).bounds(
                        this.width / 2 - 105,
                        this.height - 32,
                        130,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Закрыть"),
                        button -> this.onClose()
                ).bounds(
                        this.width / 2 + 35,
                        this.height - 32,
                        80,
                        20
                ).build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xC0101010);

        guiGraphics.drawCenteredString(
                this.font,
                Component.literal("IBE Editor 1.21.4"),
                this.width / 2,
                15,
                0xFFFFFF
        );

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            guiGraphics.drawCenteredString(
                    this.font,
                    Component.literal("Игрок не найден"),
                    this.width / 2,
                    this.height / 2,
                    0xFF5555
            );

            super.render(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }

        ItemStack stack = minecraft.player.getMainHandItem();

        if (stack.isEmpty()) {
            guiGraphics.drawCenteredString(
                    this.font,
                    Component.literal("В руке нет предмета"),
                    this.width / 2,
                    this.height / 2,
                    0xFF5555
            );

            super.render(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }

        int left = 25;
        int top = 42;
        int lineHeight = 12;

        drawLine(guiGraphics, "Название: " + stack.getHoverName().getString(), left, top, 0xFFFFFF);
        top += lineHeight;

        drawLine(guiGraphics, "ID: " + BuiltInRegistries.ITEM.getKey(stack.getItem()), left, top, 0xAAAAFF);
        top += lineHeight;

        drawLine(guiGraphics, "Количество: " + stack.getCount(), left, top, 0xFFFFFF);
        top += lineHeight * 2;

        drawLine(guiGraphics, "Компоненты предмета:", left, top, 0xFFFF55);
        top += lineHeight;

        String componentsText = String.valueOf(stack.getComponents());

        List<String> lines = wrapText(componentsText, 100);

        int maxLines = Math.max(1, (this.height - top - 155) / lineHeight);

        for (int i = 0; i < lines.size() && i < maxLines; i++) {
            drawLine(guiGraphics, lines.get(i), left, top + i * lineHeight, 0xDDDDDD);
        }

        if (lines.size() > maxLines) {
            drawLine(
                    guiGraphics,
                    "... данные не помещаются на экран, нажми \"Скопировать данные\"",
                    left,
                    top + maxLines * lineHeight,
                    0xFFAA00
            );
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void drawLine(GuiGraphics guiGraphics, String text, int x, int y, int color) {
        guiGraphics.drawString(
                this.font,
                Component.literal(text),
                x,
                y,
                color,
                false
        );
    }

    private List<String> wrapText(String text, int maxChars) {
        List<String> result = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            result.add("-");
            return result;
        }

        String remaining = text;

        while (remaining.length() > maxChars) {
            int split = remaining.lastIndexOf(", ", maxChars);

            if (split <= 0) {
                split = maxChars;
            }

            result.add(remaining.substring(0, split).trim());
            remaining = remaining.substring(Math.min(split + 1, remaining.length())).trim();
        }

        if (!remaining.isEmpty()) {
            result.add(remaining);
        }

        return result;
    }
    private void setCustomName() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null || nameField == null) {
            return;
        }

        String name = nameField.getValue().trim();

        if (name.isEmpty()) {
            return;
        }

        minecraft.player.connection.sendCommand("ibe name " + name);
    }

    private void clearCustomName() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null) {
            return;
        }

        minecraft.player.connection.sendCommand("ibe clearname");
    }
    private void setLore() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null || loreField == null) {
            return;
        }

        String lore = loreField.getValue().trim();

        if (lore.isEmpty()) {
            return;
        }

        minecraft.player.connection.sendCommand("ibe lore " + lore);
    }

    private void clearLore() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null) {
            return;
        }

        minecraft.player.connection.sendCommand("ibe clearlore");
    }
    private void setDamage() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null || damageField == null) {
            return;
        }

        String value = damageField.getValue().trim();

        if (value.isEmpty()) {
            return;
        }

        try {
            int damage = Integer.parseInt(value);

            if (damage < 0) {
                damage = 0;
            }

            minecraft.player.connection.sendCommand("ibe damage " + damage);
        } catch (NumberFormatException ignored) {
            minecraft.player.displayClientMessage(
                    Component.literal("IBE Editor: damage должен быть числом"),
                    true
            );
        }
    }

    private void repairItem() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null) {
            return;
        }

        minecraft.player.connection.sendCommand("ibe repair");
    }
    private void changeCount(int delta) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        ItemStack stack = minecraft.player.getMainHandItem();

        if (stack.isEmpty()) {
            return;
        }

        int newCount = stack.getCount() + delta;

        if (newCount < 1) {
            newCount = 1;
        }

        if (newCount > 99) {
            newCount = 99;
        }

        setCount(newCount);
    }

    private void setCount(int amount) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null) {
            return;
        }

        minecraft.player.connection.sendCommand("ibe count " + amount);
    }

    private String getItemDebugText() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return "Player not found";
        }

        ItemStack stack = minecraft.player.getMainHandItem();

        if (stack.isEmpty()) {
            return "Empty hand";
        }

        return """
                IBE Editor item debug
                Name: %s
                ID: %s
                Count: %s
                Components:
                %s
                """.formatted(
                stack.getHoverName().getString(),
                BuiltInRegistries.ITEM.getKey(stack.getItem()),
                stack.getCount(),
                stack.getComponents()
        );
    }

    private void copyItemData() {
        Minecraft minecraft = Minecraft.getInstance();

        minecraft.keyboardHandler.setClipboard(getItemDebugText());

        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(
                    Component.literal("IBE Editor: данные предмета скопированы"),
                    true
            );
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}