package com.hunllefhelper.config;

import static com.hunllefhelper.PluginConstants.CONFIG_KEY_AUDIO_MODE;
import static com.hunllefhelper.PluginConstants.CONFIG_KEY_PANEL_VISIBILITY;
import static com.hunllefhelper.PluginConstants.CONFIG_KEY_VOLUME;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("hunllefhelper")
public interface HunllefHelperConfig extends Config
{
	@ConfigItem(
		position = 1,
		keyName = CONFIG_KEY_PANEL_VISIBILITY,
		name = "Show Panel",
		description = "Determines when the plugin panel is shown."
	)
	default PanelVisibility panelVisibility()
	{
		return PanelVisibility.InsideGauntlet;
	}

	@ConfigItem(
		position = 2,
		keyName = CONFIG_KEY_AUDIO_MODE,
		name = "Audio Mode",
		description = ""
	)
	default AudioMode audioMode()
	{
		return AudioMode.Default;
	}

	@Range(
		min = 0,
		max = 200
	)
	@ConfigItem(
		position = 3,
		keyName = CONFIG_KEY_VOLUME,
		name = "Volume",
		description = "(0-200)%"
	)
	default int volume()
	{
		return  100;
	}
}
