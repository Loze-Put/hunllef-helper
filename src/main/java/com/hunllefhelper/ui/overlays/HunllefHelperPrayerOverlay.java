package com.hunllefhelper.ui.overlays;

import com.hunllefhelper.HunllefHelperPlugin;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayRenderer;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.awt.*;

public class HunllefHelperPrayerOverlay extends Overlay
{
	HunllefHelperPlugin plugin;
	Client client;
	public HunllefHelperPrayerOverlay(HunllefHelperPlugin plugin, Client client)
	{
		this.plugin = plugin;
		this.client = client;
		this.setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.setPosition(OverlayPosition.DYNAMIC);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if(plugin.getConfig().prayerBook())
		{
			if(plugin.getConfig().autoHide()&&!plugin.isInTheGauntlet())
			{
				return null;
			}
			WidgetInfo prayerWidgetInfo;
			if (plugin.isRanged())
			{
				prayerWidgetInfo = WidgetInfo.PRAYER_PROTECT_FROM_MISSILES;
			}
			else
			{
				prayerWidgetInfo = WidgetInfo.PRAYER_PROTECT_FROM_MAGIC;
			}
			Widget real= client.getWidget(prayerWidgetInfo);
			if (real!=null)
			{
				OverlayUtil.renderPolygon(graphics, real.getBounds(),
						Color.RED);
			}
		}
		return null;
	}
}
