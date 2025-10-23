package com.hunllefhelper;

import com.google.inject.Provides;
import com.hunllefhelper.config.AudioMode;
import com.hunllefhelper.config.HunllefHelperConfig;
import com.hunllefhelper.config.PanelVisibility;
import static com.hunllefhelper.PluginConstants.*;
import com.hunllefhelper.ui.HunllefHelperPluginPanel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
	name = "Hunllef Helper"
)
public class HunllefHelperPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private HunllefHelperConfig config;

	@Inject
	private AudioPlayer audioPlayer;

	@Inject
	private ConfigManager configManager;

	@Inject
	private KeyManager keyManager;

	private HunllefHelperPluginPanel panel;
	private ScheduledExecutorService executorService;
	private NavigationButton navigationButton;
	private final ArrayList<ConditionalHotkeyListener> keyListeners = new ArrayList<>();

	private int counter;
	private boolean isRanged;
	private boolean isPanelAdded;
	private boolean started;

	@Override
	protected void startUp() throws Exception
	{
		migrate();

		audioPlayer.setVolume(config.audioVolume());
		audioPlayer.tryLoadAudio(config, SOUNDS);

		panel = injector.getInstance(HunllefHelperPluginPanel.class);
		panel.setCounterActiveState(false);

		navigationButton = NavigationButton
			.builder()
			.tooltip("Hunllef Helper")
			.icon(ImageUtil.loadImageResource(getClass(), "/nav-icon.png"))
			.priority(100)
			.panel(panel)
			.build();

		updatePanelVisibility(false);
		setKeyListeners(isPanelAdded);
	}

	@Override
	protected void shutDown() throws Exception
	{
		removeKeyListeners();
		updateNavigationBar(false, false);
		shutdownExecutorService();
		panel = null;
		navigationButton = null;
		audioPlayer.unloadAudio();
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		updatePanelVisibility(true);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
        if (!PluginConstants.CONFIG_GROUP.equals(event.getGroup())) {
            return;
        }

		switch (event.getKey())
		{
			case CONFIG_KEY_AUDIO_MODE:
				audioPlayer.unloadAudio();
				audioPlayer.tryLoadAudio(config, SOUNDS);
				break;
			case CONFIG_KEY_PANEL_VISIBILITY:
				updatePanelVisibility(false);
				break;
			case CONFIG_KEY_AUDIO_VOLUME:
				audioPlayer.setVolume(config.audioVolume());
				break;
			case CONFIG_KEY_HOTKEYS_ONLY_WITH_PANEL:
				setKeyListeners(isPanelAdded);
				break;
		}
	}

	public void start(boolean withRanged)
	{
		started = true;
		isRanged = withRanged;

		if (withRanged)
		{
			panel.setStyle("Ranged", Color.GREEN);
		}
		else
		{
			panel.setStyle("Mage", Color.CYAN);
		}
		panel.setCounterActiveState(started);
		counter = INITIAL_COUNTER;

		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(this::tickCounter, 0, COUNTER_INTERVAL, TimeUnit.MILLISECONDS);
	}

	public void trample()
	{
		counter += ATTACK_DURATION;
	}

	public void addTicks(int ticks)
	{
		counter += ticks * MILLIS_PER_TICK;
	}

	public void reset()
	{
		started = false;
		shutdownExecutorService();
		panel.setCounterActiveState(started);
	}

	public void setKeyListeners(boolean isPanelVisible)
	{
		if (!config.hotkeysOnlyWithPanel() || isPanelVisible)
		{
			if (keyListeners.isEmpty())
			{
				addKeyListeners();
			}
		}
		else
		{
			removeKeyListeners();
		}
	}

	@Provides
	HunllefHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HunllefHelperConfig.class);
	}

	private void tickCounter()
	{
		counter -= COUNTER_INTERVAL;
		panel.setTime(counter);

		if (counter == 2000)
		{
			playSoundClip(SOUND_TWO);
			return;
		}

		if (counter == 1000)
		{
			playSoundClip(SOUND_ONE);
			return;
		}

		if (counter <= 0)
		{
			if (isRanged)
			{
				playSoundClip(SOUND_MAGE);
				panel.setStyle("Mage", Color.CYAN);
			}
			else
			{
				playSoundClip(SOUND_RANGE);
				panel.setStyle("Ranged", Color.GREEN);
			}

			isRanged = !isRanged;
			counter += ROTATION_DURATION;
		}
	}

	private void playSoundClip(String soundFile)
	{
		if (config.audioMode() == AudioMode.Disabled)
		{
			return;
		}

		executorService.submit(() -> audioPlayer.playSoundClip(soundFile));
	}

	private void updatePanelVisibility(boolean selectPanel)
	{
		boolean panelShouldBeAdded = shouldShowPanel();

		if (panelShouldBeAdded != isPanelAdded)
		{
			updateNavigationBar(panelShouldBeAdded, selectPanel);
		}
	}

	private boolean shouldShowPanel()
	{
		switch (config.panelVisibility())
		{
			case Always: return true;
			case InsideGauntlet: return isInTheGauntlet();
			case AtHunllef: return isInHunllefRoom();
			case Never:
			default: return false;
		}
	}

	private boolean isInTheGauntlet()
	{
		Player player = client.getLocalPlayer();

		if (player == null)
		{
			return false;
		}

		int regionId = WorldPoint.fromLocalInstance(client, player.getLocalLocation()).getRegionID();
		return REGION_IDS_GAUNTLET.contains(regionId);
	}

	private boolean isInHunllefRoom()
	{
		Player player = client.getLocalPlayer();

		if (player == null)
		{
			return false;
		}

		WorldPoint playerLocation = WorldPoint.fromLocalInstance(client, player.getLocalLocation());
		int regionId = playerLocation.getRegionID();

		if (regionId != REGION_ID_GAUNTLET_NORMAL && regionId != REGION_ID_GAUNTLET_CORRUPTED)
		{
			return false;
		}

		int playerX = playerLocation.getRegionX();
		int playerY = playerLocation.getRegionY();

		return playerX >= HUNLLEF_ROOM_X_MIN && playerX <= HUNLLEF_ROOM_X_MAX
			&& playerY >= HUNLLEF_ROOM_Y_MIN && playerY <= HUNLLEF_ROOM_Y_MAX;
	}

	private void updateNavigationBar(boolean enable, boolean selectPanel)
	{
		if (enable)
		{
			clientToolbar.addNavigation(navigationButton);
			if (selectPanel)
			{
				SwingUtilities.invokeLater(() -> clientToolbar.openPanel(navigationButton));
				isPanelAdded = true;
			}
		}
		else
		{
			reset();
			clientToolbar.removeNavigation(navigationButton);
			isPanelAdded = false;
		}
	}

	private void shutdownExecutorService()
	{
		if (executorService != null)
		{
			executorService.shutdownNow();
			try
			{
				if (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS))
				{
					log.warn("Executor service dit not shut down within the allocated timeout.");
				}
			}
			catch (InterruptedException ex)
			{
				Thread.currentThread().interrupt();
			}
			executorService = null;
		}
	}

	private void migrate()
	{
		// Migrate the old "autoHide" config to the new panel visibility enum.
		Boolean autoHide = configManager.getConfiguration(CONFIG_GROUP, "autoHide", Boolean.TYPE);
		if (autoHide != null)
		{
			if (!autoHide)
			{
				configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_PANEL_VISIBILITY, PanelVisibility.Always);
			}
			configManager.unsetConfiguration(CONFIG_GROUP, "autoHide");
		}
	}

	private void addKeyListeners()
	{
		keyListeners.add(new ConditionalHotkeyListener(() -> config.hotkeyStart(), () -> start(true), () -> !started));
		keyListeners.add(new ConditionalHotkeyListener(() -> config.hotkeyStartMage(), () -> start(false), () -> !started));
		keyListeners.add(new ConditionalHotkeyListener(() -> config.hotkeyMinusOneTick(), () -> addTicks(-1), () -> started));
		keyListeners.add(new ConditionalHotkeyListener(() -> config.hotkeyPlusOneTick(), () -> addTicks(1), () -> started));
		keyListeners.add(new ConditionalHotkeyListener(() -> config.hotkeyReset(), this::reset, () -> started));
		keyListeners.add(new ConditionalHotkeyListener(() -> config.hotkeyTrample(), this::trample, () -> started));

		for (KeyListener listener : keyListeners)
		{
			keyManager.registerKeyListener(listener);
		}
	}

	private void removeKeyListeners()
	{
		for (KeyListener listener : keyListeners)
		{
			keyManager.unregisterKeyListener(listener);
		}

		keyListeners.clear();
	}
}
