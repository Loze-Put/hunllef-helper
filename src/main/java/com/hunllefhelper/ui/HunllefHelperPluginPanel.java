package com.hunllefhelper.ui;

import com.hunllefhelper.HunllefHelperPlugin;
import com.hunllefhelper.ui.components.Button;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class HunllefHelperPluginPanel extends PluginPanel
{
	private final HunllefHelperPlugin plugin;

	private final JPanel contentPanel;
	private final JPanel activeView;
	private final JPanel inactiveView;

	private final ArrayList<Button> activeButtons = new ArrayList<Button>();
	private final ArrayList<Button> inactiveButtons = new ArrayList<Button>();

	private JLabel timeLabel;
	private JLabel styleLabel;

	@Inject
	public HunllefHelperPluginPanel(HunllefHelperPlugin plugin)
	{
		super(false);
		this.plugin = plugin;

		JLabel title = new JLabel("Hunllef Helper");
		title.setBorder(new EmptyBorder(0, 0, BORDER_OFFSET, 0));
		title.setForeground(Color.WHITE);

		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET, BORDER_OFFSET));
		add(title, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);

		activeView = createActiveView();
		inactiveView = createInactiveView();
	}

	public void setTime(int millis)
	{
		SwingUtilities.invokeLater(() ->
		{
			if (millis <= 9000000)
			{
				timeLabel.setText(String.format("%d", (millis + 1000) / 1000));
			}
			else
			{
				timeLabel.setText("Over 9000!");
			}

			timeLabel.repaint();
		});
	}

	public void setStyle(String styleName, Color color)
	{
		SwingUtilities.invokeLater(() ->
		{
			styleLabel.setText(styleName);
			styleLabel.setForeground(color);
			styleLabel.repaint();
		});
	}

	public void setCounterActiveState(boolean active)
	{
		SwingUtilities.invokeLater(() ->
		{
			contentPanel.removeAll();
			contentPanel.add(active ? activeView : inactiveView, BorderLayout.CENTER);
			contentPanel.revalidate();
			contentPanel.repaint();

			// The styling of buttons is messed up because they are removed from
			// the view when switching between active and not active. Reset the
			// styling to prevent this behaviour.
			if (active)
			{
				for (Button button : inactiveButtons)
				{
					button.resetStyling();
				}
			}
			else
			{
				for (Button button : activeButtons)
				{
					button.resetStyling();
				}
			}
		});
	}

	@Override
	public void onActivate()
	{
		plugin.setKeyListeners();
	}

	@Override
	public void onDeactivate()
	{
		plugin.setKeyListeners();
	}

	private JPanel createActiveView()
	{
		JPanel activePanel = new JPanel();
		activePanel.setLayout(new BorderLayout());

		// North
		activePanel.add(createTimerPanel(), BorderLayout.NORTH);

		// Center
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(createTickButtonsPanel(), BorderLayout.NORTH);
		JButton trampleButton = createTrampleButton();
		activePanel.add(trampleButton);
		centerPanel.add(trampleButton, BorderLayout.CENTER);
		activePanel.add(centerPanel, BorderLayout.CENTER);

		// South
		Button resetButton = createResetButton();
		activeButtons.add(resetButton);
		activePanel.add(resetButton, BorderLayout.SOUTH);

		return activePanel;
	}

	private JPanel createInactiveView()
	{
		JPanel inactivePanel = new JPanel();
		inactivePanel.setLayout(new BorderLayout());

		// North
		JLabel instructionLabel = new JLabel("<html>Press 'Start' right after you got hit by the first Hunllef attack. Press the 'I got trampled!' button if you get trampled by Hunllef.</html>");
		instructionLabel.setBorder(new EmptyBorder(BORDER_OFFSET, 0, BORDER_OFFSET, 0));
		inactivePanel.add(instructionLabel, BorderLayout.NORTH);

		// Center
		Button startRangedButton = createStartRangedButton();
		inactiveButtons.add(startRangedButton);
		inactivePanel.add(startRangedButton, BorderLayout.CENTER);

		// South
		Button startMageButton = createStartMageButton();
		inactiveButtons.add(startMageButton);
		inactivePanel.add(startMageButton, BorderLayout.SOUTH);

		return inactivePanel;
	}

	private JPanel createTimerPanel()
	{
		JPanel timerPanel = new JPanel();
		timerPanel.setLayout(new DynamicGridLayout(0, 1, 0, 20));

		timeLabel = new JLabel("", SwingConstants.CENTER);
		timeLabel.setForeground(Color.WHITE);
		timeLabel.setFont(new Font(timeLabel.getFont().getName(), Font.PLAIN, 50));

		styleLabel = new JLabel("", SwingConstants.CENTER);
		styleLabel.setForeground(Color.CYAN);
		styleLabel.setFont(new Font(styleLabel.getFont().getName(), Font.PLAIN, 50));

		timerPanel.add(styleLabel, BorderLayout.NORTH);
		timerPanel.add(timeLabel, BorderLayout.NORTH);
		timerPanel.setBorder(new EmptyBorder(BORDER_OFFSET, 0, BORDER_OFFSET, 0));

		return timerPanel;
	}

	private Button createStartRangedButton()
	{
		Button button = new Button("Start");
		button.addMouseButton1PressedHandler(() -> plugin.start(true));
		return button;
	}

	private Button createStartMageButton()
	{
		Button button = new Button("Start mage");
		button.setPreferredSize(new Dimension(PANEL_WIDTH, 60));
		button.addMouseButton1PressedHandler(() -> plugin.start(false));
		return button;
	}

	private JPanel createTickButtonsPanel()
	{
		JPanel tickPanel = new JPanel();
		tickPanel.setLayout(new BorderLayout());

		// West
		Button minus1TickButton = new Button("-1 tick");
		activeButtons.add(minus1TickButton);
		minus1TickButton.setPreferredSize(new Dimension(PANEL_WIDTH / 2 + BORDER_OFFSET / 2, 60));
		minus1TickButton.addMouseButton1PressedHandler(() -> plugin.addTicks(-1));
		tickPanel.add(minus1TickButton, BorderLayout.WEST);

		// East
		Button plus1TickButton = new Button("+1 tick");
		activeButtons.add(plus1TickButton);
		plus1TickButton.setPreferredSize(new Dimension(PANEL_WIDTH / 2 + BORDER_OFFSET / 2, 60));
		plus1TickButton.addMouseButton1PressedHandler(() -> plugin.addTicks(1));
		tickPanel.add(plus1TickButton, BorderLayout.EAST);

		return tickPanel;
	}

	private Button createTrampleButton()
	{
		Button button = new Button("I got trampled!");
		button.addMouseButton1PressedHandler(plugin::trample);
		return button;
	}

	private Button createResetButton()
	{
		Button button = new Button("Reset");
		button.setPreferredSize(new Dimension(PANEL_WIDTH, 60));
		button.addMouseButton1PressedHandler(plugin::reset);
		return button;
	}
}
