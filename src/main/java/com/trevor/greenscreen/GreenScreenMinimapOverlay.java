package com.trevor.greenscreen;

import net.runelite.api.Client;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class GreenScreenMinimapOverlay extends Overlay
{
	private static final int[] MINIMAP_COMPONENT_IDS = {
		ComponentID.FIXED_VIEWPORT_MINIMAP_DRAW_AREA,
		ComponentID.RESIZABLE_VIEWPORT_BOTTOM_LINE_MINIMAP_DRAW_AREA,
		ComponentID.RESIZABLE_VIEWPORT_MINIMAP_DRAW_AREA
	};

	/** Component IDs for orbs/buttons around the minimap that must not be covered. */
	private static final int[] MINIMAP_ORB_COMPONENT_IDS = {
		ComponentID.MINIMAP_HEALTH_ORB,
		ComponentID.MINIMAP_PRAYER_ORB,
		ComponentID.MINIMAP_RUN_ORB,
		ComponentID.MINIMAP_SPEC_ORB,
		ComponentID.MINIMAP_QUICK_PRAYER_ORB,
		ComponentID.MINIMAP_TOGGLE_RUN_ORB,
		ComponentID.MINIMAP_XP_ORB,
		ComponentID.MINIMAP_WORLDMAP_ORB,
		ComponentID.MINIMAP_WIKI_BANNER_PARENT
	};

	private final Client client;
	private final GreenScreenConfig config;
	private final GreenScreenPlugin plugin;

	@Inject
	public GreenScreenMinimapOverlay(Client client, GreenScreenConfig config, GreenScreenPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPosition(OverlayPosition.DYNAMIC);
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		if (!plugin.isRenderGreenscreen())
		{
			return null;
		}

		GreenscreenMode mode = config.mode();
		if (mode != GreenscreenMode.MINIMAP_ONLY && mode != GreenscreenMode.BOTH)
		{
			return null;
		}

		Widget minimap = getMinimapWidget();
		if (minimap == null || minimap.isHidden())
		{
			return null;
		}

		Rectangle bounds = minimap.getBounds();
		int centerX = bounds.x + bounds.width / 2;
		int centerY = bounds.y + bounds.height / 2;
		int radius = Math.min(bounds.width, bounds.height) / 2;

		Area fill = new Area(new Ellipse2D.Double(
			centerX - radius,
			centerY - radius,
			radius * 2,
			radius * 2
		));

		for (int componentId : MINIMAP_ORB_COMPONENT_IDS)
		{
			Widget w = client.getWidget(componentId);
			if (w != null && !w.isHidden())
			{
				Rectangle orbBounds = w.getBounds();
				if (orbBounds != null && !orbBounds.isEmpty())
				{
					fill.subtract(new Area(orbBounds));
				}
			}
		}

		g.setColor(config.greenscreenColor());
		g.fill(fill);

		return null;
	}

	private Widget getMinimapWidget()
	{
		for (int componentId : MINIMAP_COMPONENT_IDS)
		{
			Widget w = client.getWidget(componentId);
			if (w != null && !w.isHidden())
			{
				return w;
			}
		}
		return null;
	}
}
