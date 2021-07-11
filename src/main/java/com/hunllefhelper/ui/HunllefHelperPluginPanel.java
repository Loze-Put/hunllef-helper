package com.hunllefhelper.ui;

import com.hunllefhelper.HunllefHelperPlugin;
import com.hunllefhelper.ui.components.Button;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.inject.Inject;
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

	private final JLabel timeLabel;
	private final JLabel styleLabel;

	private Button startRangedButton;
	private Button startMageButton;
	private Button trampleButton;
	private Button resetButton;

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

		JLabel instructionLabel = new JLabel("<html>Press 'Start' right after you got hit by the first Hunllef attack. Press the 'I got trampled!' button if you get trampled by Hunllef.</html>");
		instructionLabel.setBorder(new EmptyBorder(BORDER_OFFSET, 0, BORDER_OFFSET, 0));

		activeView = new JPanel();
		activeView.setLayout(new BorderLayout());
		activeView.add(timerPanel, BorderLayout.NORTH);

		inactiveView = new JPanel();
		inactiveView.setLayout(new BorderLayout());
		inactiveView.add(instructionLabel, BorderLayout.NORTH);
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
			// Recreate the buttons in order to reset the styling. The styling
			// messes up because the button is removed from the view on click.
			// Because of this, some of the mouse events do not occur.
			if (active)
			{
				createTrampleButton();
				createResetButton();
			}
			else
			{
				createStartRangedButton();
				createStartMageButton();
			}

			contentPanel.removeAll();
			contentPanel.add(active ? activeView : inactiveView, BorderLayout.CENTER);
			contentPanel.revalidate();
			contentPanel.repaint();
		});
	}

	private void createStartRangedButton()
	{
		if (startRangedButton != null)
		{
			inactiveView.remove(startRangedButton);
		}

		startRangedButton = new Button("Start");
		startRangedButton.addMouseButton1PressedHandler(() -> plugin.start(true));

		inactiveView.add(startRangedButton, BorderLayout.CENTER);
	}

	private void createStartMageButton()
	{
		if (startMageButton != null)
		{
			inactiveView.remove(startMageButton);
		}

		startMageButton = new Button("Start mage");
		startMageButton.setPreferredSize(new Dimension(PANEL_WIDTH, 60));
		startMageButton.addMouseButton1PressedHandler(() -> plugin.start(false));

		inactiveView.add(startMageButton, BorderLayout.SOUTH);
	}

	private void createTrampleButton()
	{
		if (trampleButton != null)
		{
			activeView.remove(trampleButton);
		}

		trampleButton = new Button("I got trampled!");
		trampleButton.addMouseButton1PressedHandler(plugin::trample);

		activeView.add(trampleButton, BorderLayout.CENTER);
	}

	private void createResetButton()
	{
		if (resetButton != null)
		{
			activeView.remove(resetButton);
		}

		resetButton = new Button("Reset");
		resetButton.setPreferredSize(new Dimension(PANEL_WIDTH, 60));
		resetButton.addMouseButton1PressedHandler(plugin::reset);

		activeView.add(resetButton, BorderLayout.SOUTH);
	}
}
