package com.hunllefhelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PluginConstants
{
	// Region ids
	public static final int REGION_ID_GAUNTLET_LOBBY = 12127;
	public static final int REGION_ID_GAUNTLET_NORMAL = 7512;
	public static final int REGION_ID_GAUNTLET_CORRUPTED = 7768;
	public static final List<Integer> REGION_IDS_GAUNTLET = Collections.unmodifiableList(new ArrayList<Integer>()
	{{
		add(REGION_ID_GAUNTLET_LOBBY);
		add(REGION_ID_GAUNTLET_NORMAL);
		add(REGION_ID_GAUNTLET_CORRUPTED);
	}});

	// Coordinates
	public static final int HUNLLEF_ROOM_X_MIN = 49;
	public static final int HUNLLEF_ROOM_X_MAX = 62;
	public static final int HUNLLEF_ROOM_Y_MIN = 49;
	public static final int HUNLLEF_ROOM_Y_MAX = 62;

	// Timings
	public static final int MILLIS_PER_TICK = 600;
	public static final int COUNTER_INTERVAL = 200;
	public static final int INITIAL_COUNTER = MILLIS_PER_TICK * 14;
	public static final int ATTACK_DURATION = MILLIS_PER_TICK * 5;
	public static final int ROTATION_DURATION = ATTACK_DURATION * 4;

	// Sound files
    public static final String SOUND_TWO = "two.wav";
    public static final String SOUND_ONE = "one.wav";
    public static final String SOUND_MAGE = "mage.wav";
    public static final String SOUND_RANGE = "range.wav";
    public static final String[] SOUNDS = new String[]{SOUND_MAGE, SOUND_RANGE, SOUND_ONE, SOUND_TWO};

    // Configuration
	public static final String CONFIG_GROUP = "hunllefhelper";
	public static final String CONFIG_KEY_AUDIO_MODE = "audioMode";
	public static final String CONFIG_KEY_PANEL_VISIBILITY = "panelVisibility";
	public static final String CONFIG_KEY_AUDIO_VOLUME = "audioVolume";
	public static final String CONFIG_KEY_HOTKEYS_ONLY_WITH_PANEL = "onlyWithPanel";
	public static final String CONFIG_KEY_HOTKEY_START = "hotkeyStart";
	public static final String CONFIG_KEY_HOTKEY_START_MAGE = "hotkeyStartMage";
	public static final String CONFIG_KEY_HOTKEY_MINUS_ONE_TICK = "hotkeyMinusOneTick";
	public static final String CONFIG_KEY_HOTKEY_PLUS_ONE_TICK = "hotkeyPlusOneTick";
	public static final String CONFIG_KEY_HOTKEY_RESET = "hotkeyReset";
	public static final String CONFIG_KEY_HOTKEY_TRAMPLE = "hotkeyTrample";

	private PluginConstants()
	{
	}
}
