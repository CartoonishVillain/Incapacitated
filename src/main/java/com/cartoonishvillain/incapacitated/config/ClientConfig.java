package com.cartoonishvillain.incapacitated.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class ClientConfig {
    public static final String CLIENTCATEGORY_CONFIGS = "Options";
    public ConfigHelper.ConfigValueListener<Boolean> GRAYSCREEN;

    public ClientConfig(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber){
        builder.comment("Modify Client-Side changes").push(CLIENTCATEGORY_CONFIGS);
        this.GRAYSCREEN = subscriber.subscribe(builder.comment("Does the player screen desaturate on their last down?").define("lastDownDesaturate", false));
        builder.pop();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path){
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();;
        spec.setConfig(configData);
    }
}

