package com.hunllefhelper;

import java.util.function.Supplier;
import net.runelite.client.config.Keybind;
import net.runelite.client.util.HotkeyListener;

class ConditionalHotkeyListener extends HotkeyListener
{
	private final Runnable hotkeyPressedHandler;
	private final Supplier<Boolean> hotkeyPressedEnabledSupplier;

	public ConditionalHotkeyListener(Supplier<Keybind> hotkeySupplier, Runnable hotkeyPressedHandler, Supplier<Boolean> hotkeyPressedEnabledSupplier)
	{
		super(hotkeySupplier);
		this.hotkeyPressedHandler = hotkeyPressedHandler;
		this.hotkeyPressedEnabledSupplier = hotkeyPressedEnabledSupplier;
	}

	@Override
	public void hotkeyPressed()
	{
		if (hotkeyPressedEnabledSupplier.get())
		{
			hotkeyPressedHandler.run();
		}
	}
}
