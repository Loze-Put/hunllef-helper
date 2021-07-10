package com.hunllefhelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class PluginConstants
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

	// Timings
	public static final int MILLIS_PER_TICK = 600;
	public static final int COUNTER_INTERVAL = 200;
	public static final int INITIAL_COUNTER = MILLIS_PER_TICK * 14;
	public static final int ATTACK_DURATION = MILLIS_PER_TICK * 5;
	public static final int ROTATION_DURATION = ATTACK_DURATION * 4;

	// Sound files
	public static final String SOUND_TWO = "/audio/two.wav";
	public static final String SOUND_ONE = "/audio/one.wav";
	public static final String SOUND_MAGE = "/audio/mage.wav";
	public static final String SOUND_RANGE = "/audio/range.wav";

	private PluginConstants()
	{
	}
}
