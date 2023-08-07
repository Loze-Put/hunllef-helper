package com.hunllefhelper.config;

import com.hunllefhelper.config.AudioMode;
import com.hunllefhelper.config.PanelVisibility;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("hunllefhelper")
public interface HunllefHelperConfig extends Config
{
	@ConfigItem(
		position = 1,
		keyName = "panelVisibility",
		name = "PanelVisibility",
		description = "Determines when the plugin panel is shown."
	)
	default PanelVisibility panelVisibility()
	{
		return PanelVisibility.InsideGauntlet;
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
