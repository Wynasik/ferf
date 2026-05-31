package com.github.franckyi.ibeeditor.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemEditorScreen extends Screen {
    private enum Tab {
        BASIC("Основные"),
        DISPLAY("Отображение"),
        ENCHANTS("Чары"),
        ATTRIBUTES("Модификаторы атрибутов"),
        HIDDEN("Скрытые данные"),
        BLOCKS("Ломаемые блоки");

        private final String title;

        Tab(String title) {
            this.title = title;
        }
    }

    private Tab selectedTab = Tab.BASIC;

    private EditBox itemIdField;
    private EditBox countField;
    private EditBox damageField;

    private EditBox nameField;
    private EditBox loreField;
    private EditBox repairCostField;
    private EditBox maxStackField;

    public ItemEditorScreen() {
        super(Component.literal("IBE Editor"));
    }

    @Override
    protected void init() {
        rebuildIbeWidgets();
    }

    private void rebuildIbeWidgets() {
        this.clearWidgets();

        int leftPanelX = 10;
        int leftPanelY = 42;
        int leftPanelWidth = 205;

        int tabY = leftPanelY + 8;

        for (Tab tab : Tab.values()) {
            addButton(
                    leftPanelX + 10,
                    tabY,
                    leftPanelWidth - 20,
                    22,
                    tab.title,
                    () -> {
                        selectedTab = tab;
                        rebuildIbeWidgets();
                    }
            );
            tabY += 30;
        }

        switch (selectedTab) {
            case BASIC -> initBasicTab();
            case DISPLAY -> initDisplayTab();
            case ENCHANTS -> initPlaceholderTab("Редактор чар будет следующим шагом");
            case ATTRIBUTES -> initPlaceholderTab("Модификаторы атрибутов будут позже");
            case HIDDEN -> initHiddenTab();
            case BLOCKS -> initPlaceholderTab("Ломаемые блоки будут позже");
        }

        addButton(
                this.width - 90,
                this.height - 30,
                80,
                20,
                "Закрыть",
                this::onClose
        );
    }

    private void initBasicTab() {
        int x = 305;
        int y = 62;
        int labelX = 225;
        int fieldWidth = this.width - x - 80;

        ItemStack stack = getCurrentStack();

        String id = stack.isEmpty() ? "" : String.valueOf(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        String count = stack.isEmpty() ? "1" : String.valueOf(stack.getCount());
        String damage = stack.isEmpty() || !stack.isDamageableItem() ? "0" : String.valueOf(stack.getDamageValue());

        this.itemIdField = addTextField(x, y, fieldWidth, id, 256);
        addSmallActionButton(x + fieldWidth + 8, y, "⟳", () -> {
            if (!getCurrentStack().isEmpty()) {
                itemIdField.setValue(String.valueOf(BuiltInRegistries.ITEM.getKey(getCurrentStack().getItem())));
            }
        });
        y += 36;

        this.countField = addTextField(x, y, fieldWidth, count, 3);
        addSmallActionButton(x + fieldWidth + 8, y, "✓", () -> sendIntFromField("ibe count ", countField, 1, 99));
        y += 36;

        this.damageField = addTextField(x, y, fieldWidth, damage, 8);
        addSmallActionButton(x + fieldWidth + 8, y, "✓", () -> sendIntFromField("ibe damage ", damageField, 0, Integer.MAX_VALUE));
        y += 36;

        addButton(x, y, 110, 20, "Починить", () -> sendCommand("ibe repair"));
        addButton(x + 120, y, 90, 20, "ВКЛ", () -> sendCommand("ibe unbreakable true"));
        addButton(x + 220, y, 90, 20, "ВЫКЛ", () -> sendCommand("ibe unbreakable false"));
        y += 36;

        addButton(x, y, 110, 20, "Count 1", () -> sendCommand("ibe count 1"));
        addButton(x + 120, y, 110, 20, "Count 64", () -> sendCommand("ibe count 64"));

        // Метки рисуются в renderBasicTab()
    }

    private void initDisplayTab() {
        int x = 305;
        int y = 62;
        int fieldWidth = this.width - x - 80;

        this.nameField = addTextField(x, y, fieldWidth, "", 128);
        addSmallActionButton(x + fieldWidth + 8, y, "✓", () -> sendTextFromField("ibe name ", nameField));
        y += 28;
        addButton(x, y, 120, 20, "Очистить имя", () -> sendCommand("ibe clearname"));

        y += 36;
        this.loreField = addTextField(x, y, fieldWidth, "", 256);
        addSmallActionButton(x + fieldWidth + 8, y, "✓", () -> sendTextFromField("ibe lore ", loreField));
        y += 28;
        addButton(x, y, 120, 20, "Очистить lore", () -> sendCommand("ibe clearlore"));

        y += 36;
        this.repairCostField = addTextField(x, y, 120, "", 8);
        addButton(x + 130, y, 130, 20, "Repair Cost", () -> sendIntFromField("ibe repaircost ", repairCostField, 0, 1_000_000));

        y += 36;
        this.maxStackField = addTextField(x, y, 120, "", 3);
        addButton(x + 130, y, 130, 20, "Max Stack", () -> sendIntFromField("ibe maxstack ", maxStackField, 1, 99));

        y += 36;
        addButton(x, y, 90, 20, "Common", () -> sendCommand("ibe rarity common"));
        addButton(x + 100, y, 100, 20, "Uncommon", () -> sendCommand("ibe rarity uncommon"));
        addButton(x + 210, y, 70, 20, "Rare", () -> sendCommand("ibe rarity rare"));
        addButton(x + 290, y, 70, 20, "Epic", () -> sendCommand("ibe rarity epic"));

        y += 30;
        addButton(x, y, 100, 20, "Glint ON", () -> sendCommand("ibe glint true"));
        addButton(x + 110, y, 100, 20, "Glint OFF", () -> sendCommand("ibe glint false"));
        addButton(x + 220, y, 120, 20, "Очистить glint", () -> sendCommand("ibe clearglint"));

        y += 30;
        addButton(x, y, 180, 20, "Очистить custom", () -> sendCommand("ibe clearcustom"));
    }

    private void initHiddenTab() {
        int x = 305;
        int y = 62;

        addButton(x, y, 180, 20, "Скопировать данные", this::copyItemData);
    }

    private void initPlaceholderTab(String text) {
        int x = 305;
        int y = 62;

        addButton(x, y, 260, 20, text, () -> {
        });
    }

    private EditBox addTextField(int x, int y, int width, String value, int maxLength) {
        EditBox field = new EditBox(this.font, x, y, width, 20, Component.empty());
        field.setMaxLength(maxLength);
        field.setValue(value);
        this.addRenderableWidget(field);
        return field;
    }

    private void addSmallActionButton(int x, int y, String text, Runnable action) {
        addButton(x, y, 24, 20, text, action);
    }

    private void addButton(int x, int y, int width, int height, String text, Runnable action) {
        this.addRenderableWidget(
                Button.builder(Component.literal(text), button -> action.run())
                        .bounds(x, y, width, height)
                        .build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);

        renderPanels(guiGraphics);

        renderTitle(guiGraphics);

        switch (selectedTab) {
            case BASIC -> renderBasicTab(guiGraphics);
            case DISPLAY -> renderDisplayTab(guiGraphics);
            case ENCHANTS -> renderPlaceholder(guiGraphics, "Здесь будет редактор чар");
            case ATTRIBUTES -> renderPlaceholder(guiGraphics, "Здесь будут модификаторы атрибутов");
            case HIDDEN -> renderHiddenTab(guiGraphics);
            case BLOCKS -> renderPlaceholder(guiGraphics, "Здесь будут ломаемые блоки");
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderBackground(GuiGraphics guiGraphics) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xE0101010);
    }

    private void renderPanels(GuiGraphics guiGraphics) {
        int leftPanelX = 10;
        int leftPanelY = 42;
        int leftPanelWidth = 205;
        int panelHeight = this.height - 82;

        int rightPanelX = 225;
        int rightPanelY = 42;
        int rightPanelWidth = this.width - rightPanelX - 10;

        guiGraphics.fill(leftPanelX, leftPanelY, leftPanelX + leftPanelWidth, leftPanelY + panelHeight, 0xAA1C1C1C);
        guiGraphics.fill(rightPanelX, rightPanelY, rightPanelX + rightPanelWidth, rightPanelY + panelHeight, 0xAA1C1C1C);

        guiGraphics.fill(leftPanelX, leftPanelY, leftPanelX + leftPanelWidth, leftPanelY + 1, 0xFF555555);
        guiGraphics.fill(rightPanelX, rightPanelY, rightPanelX + rightPanelWidth, rightPanelY + 1, 0xFF555555);
    }

    private void renderTitle(GuiGraphics guiGraphics) {
        guiGraphics.drawCenteredString(
                this.font,
                Component.literal("Предмет"),
                this.width / 2,
                18,
                0xFFFFFF
        );
    }

    private void renderBasicTab(GuiGraphics guiGraphics) {
        int labelX = 235;
        int y = 68;

        drawRightLabel(guiGraphics, "ID предмета", labelX, y);
        y += 36;

        drawRightLabel(guiGraphics, "Количество", labelX, y);
        y += 36;

        drawRightLabel(guiGraphics, "Прочность", labelX, y);
        y += 36;

        ItemStack stack = getCurrentStack();
        boolean unbreakable = !stack.isEmpty() && stack.has(DataComponents.UNBREAKABLE);

        drawRightLabel(guiGraphics, "Неразрушимый: " + (unbreakable ? "ДА" : "НЕТ"), labelX, y);

        stack = getCurrentStack();
        if (!stack.isEmpty()) {
            int infoY = this.height - 70;
            drawLine(guiGraphics, "Текущий предмет: " + stack.getHoverName().getString(), 235, infoY, 0xAAAAAA);
            drawLine(guiGraphics, "Компоненты: " + stack.getComponents().size(), 235, infoY + 12, 0xAAAAAA);
        }
    }

    private void renderDisplayTab(GuiGraphics guiGraphics) {
        int labelX = 235;
        int y = 68;

        drawRightLabel(guiGraphics, "Название", labelX, y);
        y += 64;

        drawRightLabel(guiGraphics, "Описание", labelX, y);
        y += 64;

        drawRightLabel(guiGraphics, "Repair Cost", labelX, y);
        y += 36;

        drawRightLabel(guiGraphics, "Max Stack", labelX, y);
        y += 36;

        drawRightLabel(guiGraphics, "Редкость", labelX, y);
        y += 30;

        drawRightLabel(guiGraphics, "Блеск", labelX, y);
    }

    private void renderHiddenTab(GuiGraphics guiGraphics) {
        ItemStack stack = getCurrentStack();

        int x = 235;
        int y = 95;

        if (stack.isEmpty()) {
            drawLine(guiGraphics, "В руке нет предмета", x, y, 0xFF5555);
            return;
        }

        drawLine(guiGraphics, "Скрытые данные / Components", x, 68, 0xFFFF55);

        String componentsText = String.valueOf(stack.getComponents());
        List<String> lines = wrapText(componentsText, 115);

        int maxLines = Math.max(1, (this.height - y - 80) / 12);

        for (int i = 0; i < lines.size() && i < maxLines; i++) {
            drawLine(guiGraphics, lines.get(i), x, y + i * 12, 0xDDDDDD);
        }

        if (lines.size() > maxLines) {
            drawLine(guiGraphics, "... данные не помещаются, нажми \"Скопировать данные\"", x, y + maxLines * 12, 0xFFAA00);
        }
    }

    private void renderPlaceholder(GuiGraphics guiGraphics, String text) {
        drawLine(guiGraphics, text, 235, 68, 0xAAAAAA);
    }

    private void drawRightLabel(GuiGraphics guiGraphics, String text, int x, int y) {
        guiGraphics.drawString(this.font, Component.literal(text), x, y, 0xFFFFFF, false);
    }

    private void drawLine(GuiGraphics guiGraphics, String text, int x, int y, int color) {
        guiGraphics.drawString(this.font, Component.literal(text), x, y, color, false);
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

    private ItemStack getCurrentStack() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return ItemStack.EMPTY;
        }

        return minecraft.player.getMainHandItem();
    }

    private void sendTextFromField(String prefix, EditBox field) {
        if (field == null) {
            return;
        }

        String value = field.getValue().trim();

        if (value.isEmpty()) {
            return;
        }

        sendCommand(prefix + value);
    }

    private void sendIntFromField(String prefix, EditBox field, int min, int max) {
        Minecraft minecraft = Minecraft.getInstance();

        if (field == null) {
            return;
        }

        String value = field.getValue().trim();

        if (value.isEmpty()) {
            return;
        }

        try {
            int number = Integer.parseInt(value);

            if (number < min) {
                number = min;
            }

            if (number > max) {
                number = max;
            }

            sendCommand(prefix + number);
        } catch (NumberFormatException ignored) {
            if (minecraft.player != null) {
                minecraft.player.displayClientMessage(
                        Component.literal("IBE Editor: нужно ввести число"),
                        true
                );
            }
        }
    }

    private void sendCommand(String command) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.player.connection == null) {
            return;
        }

        minecraft.player.connection.sendCommand(command);
    }

    private String getItemDebugText() {
        ItemStack stack = getCurrentStack();

        if (stack.isEmpty()) {
            return "Empty hand";
        }

        return """
                IBE Editor item debug
                Name: %s
                ID: %s
                Count: %s
                Max stack size: %s
                Damage: %s
                Max damage: %s
                Components:
                %s
                """.formatted(
                stack.getHoverName().getString(),
                BuiltInRegistries.ITEM.getKey(stack.getItem()),
                stack.getCount(),
                stack.getMaxStackSize(),
                stack.isDamageableItem() ? stack.getDamageValue() : "-",
                stack.isDamageableItem() ? stack.getMaxDamage() : "-",
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