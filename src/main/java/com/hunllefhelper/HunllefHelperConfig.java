package com.hunllefhelper;

import com.hunllefhelper.config.AudioMode;
import com.hunllefhelper.config.VisibilityMode;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("hunllefhelper")
public interface HunllefHelperConfig extends Config
{
	@ConfigItem(
		position = 1,
		keyName = "visibilityMode",
		name = "Visibility Mode",
		description = "Determines when the plugin panel is shown."
	)
	default VisibilityMode visibilityMode()
	{
		return VisibilityMode.InsideGauntlet;
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
