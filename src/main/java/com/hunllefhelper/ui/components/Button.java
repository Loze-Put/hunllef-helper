package com.hunllefhelper.ui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class Button extends JButton
{
	public Button(String text)
	{
		super(text);
		setFocusable(false);
	}

	public void addMouseButton1PressedHandler(Runnable callback)
	{
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					callback.run();
				}
			}
		});
	}
}
