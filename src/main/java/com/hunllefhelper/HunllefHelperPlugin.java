package com.hunllefhelper;

import com.google.inject.Provides;

import static com.hunllefhelper.PluginConstants.ATTACK_DURATION;
import static com.hunllefhelper.PluginConstants.COUNTER_INTERVAL;
import static com.hunllefhelper.PluginConstants.INITIAL_COUNTER;
import static com.hunllefhelper.PluginConstants.REGION_IDS_GAUNTLET;
import static com.hunllefhelper.PluginConstants.ROTATION_DURATION;
import static com.hunllefhelper.PluginConstants.SOUND_MAGE;
import static com.hunllefhelper.PluginConstants.SOUND_ONE;
import static com.hunllefhelper.PluginConstants.SOUND_RANGE;
import static com.hunllefhelper.PluginConstants.SOUND_TWO;

import com.hunllefhelper.ui.HunllefHelperPluginPanel;

import java.awt.Color;
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

	private HunllefHelperPluginPanel panel;

	private ScheduledExecutorService executorService;

	private int counter;
	private boolean isRanged;
	private boolean wasInInstance;
	private AudioMode audioMode;

	private NavigationButton navigationButton;

	@Override
	protected void startUp() throws Exception
	{
		audioPlayer.tryLoadAudio(config, new String[]{SOUND_MAGE, SOUND_RANGE, SOUND_ONE, SOUND_TWO});
		audioMode = config.audioMode();

		panel = injector.getInstance(HunllefHelperPluginPanel.class);

		navigationButton = NavigationButton
			.builder()
			.tooltip("Hunllef Helper")
			.icon(ImageUtil.loadImageResource(getClass(), "/nav-icon.png"))
			.priority(100)
			.panel(panel)
			.build();

		panel.setCounterActiveState(false);

		wasInInstance = isInTheGauntlet();
		updateNavigationBar((!config.autoHide() || wasInInstance), false);
	}

	@Override
	protected void shutDown() throws Exception
	{
		updateNavigationBar(false, false);
		shutdownExecutorService();
		panel = null;
		navigationButton = null;
		audioPlayer.unloadAudio();
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (!config.autoHide())
		{
			return;
		}

		boolean isInInstance = isInTheGauntlet();
		if (isInInstance != wasInInstance)
		{
			updateNavigationBar(isInInstance, true);
			wasInInstance = isInInstance;
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		wasInInstance = isInTheGauntlet();
		updateNavigationBar((!config.autoHide() || wasInInstance), false);

		if (audioMode != config.audioMode())
		{
			audioPlayer.unloadAudio();
			audioPlayer.tryLoadAudio(config, new String[]{SOUND_MAGE, SOUND_RANGE, SOUND_ONE, SOUND_TWO});
			audioMode = config.audioMode();
		}
	}

	public void start(boolean withRanged)
	{
		isRanged = withRanged;

		if (withRanged)
		{
			panel.setStyle("Ranged", Color.GREEN);
		}
		else
		{
			panel.setStyle("Mage", Color.CYAN);
		}
		panel.setCounterActiveState(true);
		counter = INITIAL_COUNTER;

		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(this::tickCounter, 0, COUNTER_INTERVAL, TimeUnit.MILLISECONDS);
	}

	public void trample()
	{
		counter += ATTACK_DURATION;
	}

	public void reset()
	{
		shutdownExecutorService();
		panel.setCounterActiveState(false);
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
			counter = ROTATION_DURATION;
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

	private void updateNavigationBar(boolean enable, boolean selectPanel)
	{
		if (enable)
		{
			clientToolbar.addNavigation(navigationButton);
			if (selectPanel)
			{
				SwingUtilities.invokeLater(() ->
				{
					if (!navigationButton.isSelected())
					{
						navigationButton.getOnSelect().run();
					}
				});
			}
		}
		else
		{
			reset();
			navigationButton.setSelected(false);
			clientToolbar.removeNavigation(navigationButton);
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
}
