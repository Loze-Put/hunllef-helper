package com.hunllefhelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("hunllefhelper")
public interface HunllefHelperConfig extends Config
{
	@ConfigItem(
		position = 1,
		keyName = "autoHide",
		name = "Automatically hide",
		description = "If checked, only show the plugin panel inside The Gauntlet"
	)
	default boolean autoHide()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "audioMode",
		name = "Audio Mode",
		description = ""
	)
	default AudioMode audioMode()
	{
		return AudioMode.Default;
	}
}
