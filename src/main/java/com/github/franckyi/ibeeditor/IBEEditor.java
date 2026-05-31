package com.github.franckyi.ibeeditor;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(IBEEditor.MOD_ID)
public class IBEEditor {
    public static final String MOD_ID = "ibeeditor";
    private static final Logger LOGGER = LogUtils.getLogger();

    public IBEEditor() {
        LOGGER.info("IBE Editor 1.21.4 loaded");
    }
}