package com.hunllefhelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("hunllefhelper")
public interface HunllefHelperConfig extends Config {
    @ConfigItem(
            position = 1,
            keyName = "autoHide",
            name = "Automatically hide",
            description = "If checked, only show the plugin panel inside The Gauntlet"
    )
    default boolean autoHide() {
        return true;
    }
    @ConfigItem(
            position = 1,
            keyName = "prayerBook",
            name = "Show Overlay in Prayer Book",
            description = "Will overlay be shown in prayer book"
    )
    default boolean prayerBook() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "audioMode",
            name = "Audio Mode",
            description = ""
    )
    default AudioMode audioMode() {
        return AudioMode.Default;
    }
    @ConfigItem(
            position = 3,
            keyName = "stompKey",
            name = "Hotkey for stomp",
            description = "triggering this hotkey is the same as hitting I got stomped"
    )
    default Keybind stompKey() {
        return Keybind.NOT_SET;
    }

}
