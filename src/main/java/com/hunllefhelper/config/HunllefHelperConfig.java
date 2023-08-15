package com.hunllefhelper.config;

import static com.hunllefhelper.PluginConstants.*;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup(CONFIG_GROUP)
public interface HunllefHelperConfig extends Config
{
	@ConfigItem(
		position = 1,
		keyName = CONFIG_KEY_PANEL_VISIBILITY,
		name = "Show panel",
		description = "Determines when the plugin panel is shown."
	)
	default PanelVisibility panelVisibility()
	{
		return PanelVisibility.InsideGauntlet;
	}

	@ConfigItem(
		position = 2,
		keyName = CONFIG_KEY_AUDIO_MODE,
		name = "Audio mode",
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
		keyName = CONFIG_KEY_AUDIO_VOLUME,
		name = "Audio volume",
		description = "Volume relative to the source (0-200)%."
	)
	default int audioVolume()
	{
		return  100;
	}

	@ConfigSection(
		position = 100,
		name = "hotkeys",
		closedByDefault = false,
		description = ""
	)
	String CONFIG_SECTION_HOTKEYS = "hotkeys";

	@ConfigItem(
		position = 0,
		keyName = CONFIG_KEY_HOTKEYS_ONLY_WITH_PANEL,
		name = "Only with panel open",
		description = "Only enable hotkeys while the plugin panel is visible.",
		section = CONFIG_SECTION_HOTKEYS
	)
	default boolean hotkeysOnlyWithPanel()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = CONFIG_KEY_HOTKEY_START,
		name = "Start",
		description = "",
		section = CONFIG_SECTION_HOTKEYS
	)
	default Keybind hotkeyStart()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		position = 2,
		keyName = CONFIG_KEY_HOTKEY_START_MAGE,
		name = "Start mage",
		description = "",
		section = CONFIG_SECTION_HOTKEYS
	)
	default Keybind hotkeyStartMage()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		position = 3,
		keyName = CONFIG_KEY_HOTKEY_MINUS_ONE_TICK,
		name = "-1 tick",
		description = "",
		section = CONFIG_SECTION_HOTKEYS
	)
	default Keybind hotkeyMinusOneTick()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		position = 4,
		keyName = CONFIG_KEY_HOTKEY_PLUS_ONE_TICK,
		name = "+1 tick",
		description = "",
		section = CONFIG_SECTION_HOTKEYS
	)
	default Keybind hotkeyPlusOneTick()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		position = 5,
		keyName = CONFIG_KEY_HOTKEY_TRAMPLE,
		name = "Trample",
		description = "",
		section = CONFIG_SECTION_HOTKEYS
	)
	default Keybind hotkeyTrample()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		position = 6,
		keyName = CONFIG_KEY_HOTKEY_RESET,
		name = "Reset",
		description = "",
		section = CONFIG_SECTION_HOTKEYS
	)
	default Keybind hotkeyReset()
	{
		return Keybind.NOT_SET;
	}
}
