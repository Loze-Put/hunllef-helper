package com.hunllefhelper;

import java.util.function.Supplier;
import net.runelite.client.config.Keybind;
import net.runelite.client.util.HotkeyListener;

class ConditionalHotkeyListener extends HotkeyListener
{
	private final Supplier<Boolean> enabledSupplier;
	private Runnable hotkeyPressedHandler;
	private Supplier<Boolean> hotkeyPressedEnabledSupplier;

	public ConditionalHotkeyListener(Supplier<Keybind> hotkeySupplier, Supplier<Boolean> enabledSupplier)
	{
		super(hotkeySupplier);
		this.enabledSupplier = enabledSupplier;
		hotkeyPressedEnabledSupplier = () -> false;
	}

	public void registerHotkeyPressed(Runnable handler, Supplier<Boolean> enabledSupplier)
	{
		hotkeyPressedHandler = handler;
		hotkeyPressedEnabledSupplier = enabledSupplier;
	}

	@Override
	public void hotkeyPressed()
	{
		if (!enabledSupplier.get())
		{
			notifyAll();
			return;
		}

		if (hotkeyPressedEnabledSupplier.get())
		{
			hotkeyPressedHandler.run();
		}
	}
}
