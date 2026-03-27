package com.trevor.greenscreen;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.api.events.ClientTick;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "Green Screen"
)
public class GreenScreenPlugin extends Plugin
{
	private static final int[] MINIMAP_ORB_COMPONENT_IDS = {
		ComponentID.MINIMAP_HEALTH_ORB,
		ComponentID.MINIMAP_PRAYER_ORB,
		ComponentID.MINIMAP_RUN_ORB,
		ComponentID.MINIMAP_SPEC_ORB,
		ComponentID.MINIMAP_QUICK_PRAYER_ORB,
		ComponentID.MINIMAP_TOGGLE_RUN_ORB,
		ComponentID.MINIMAP_XP_ORB,
		ComponentID.MINIMAP_WORLDMAP_ORB
	};

	private Integer compassOriginalX;
	private Integer compassOriginalY;
	private Integer specOriginalX;
	private Integer specOriginalY;

	@Inject
	private Client client;

	@Inject
	private GreenScreenConfig config;

	@Inject
	private GreenScreenOverlay overlay;

	@Inject
	private GreenScreenMinimapOverlay minimapOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private KeyManager keyManager;

	private boolean renderGreenscreen;

	public boolean isRenderGreenscreen()
	{
		return renderGreenscreen;
	}

	private final HotkeyListener hotkeyListener = new HotkeyListener(() -> config.hotkey())
	{
		@Override
		public void hotkeyPressed()
		{
			renderGreenscreen = !renderGreenscreen;
		}
	};

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		overlayManager.add(minimapOverlay);
		renderGreenscreen = config.defaultState();
		keyManager.registerKeyListener(hotkeyListener);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		overlayManager.remove(minimapOverlay);
		keyManager.unregisterKeyListener(hotkeyListener);
	}

	@Provides
	GreenScreenConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GreenScreenConfig.class);
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick)
	{
		Widget modernMinimap = client.getWidget(ComponentID.RESIZABLE_VIEWPORT_MINIMAP_DRAW_AREA);
		boolean inResizableModern = modernMinimap != null && !modernMinimap.isHidden();
		if (!inResizableModern)
		{
			return;
		}

		if (config.hideXpOrb() || config.moveSpecToXpSlot())
		{
			hideWidget(ComponentID.MINIMAP_XP_ORB);
		}

		if (config.hideAllOrbs())
		{
			for (int componentId : MINIMAP_ORB_COMPONENT_IDS)
			{
				hideWidget(componentId);
			}
		}

		if (config.hideCompass())
		{
			// Keep this resilient across API versions where compass constant names may differ.
			Integer compassId = componentIdIfPresent("MINIMAP_COMPASS");
			if (compassId != null)
			{
				hideWidget(compassId);
			}
		}
		else if (config.moveCompassTopLeft())
		{
			moveCompassTopLeft();
		}
		else
		{
			restoreCompassPositionIfNeeded();
		}

		if (config.moveSpecToXpSlot() && !config.hideAllOrbs())
		{
			moveSpecOrbToXpSlot();
		}
		else
		{
			restoreSpecOrbPositionIfNeeded();
		}
	}

	private void hideWidget(int componentId)
	{
		Widget widget = client.getWidget(componentId);
		if (widget != null && !widget.isHidden())
		{
			widget.setHidden(true);
		}
	}

	private Integer componentIdIfPresent(String fieldName)
	{
		try
		{
			return (Integer) ComponentID.class.getField(fieldName).get(null);
		}
		catch (NoSuchFieldException | IllegalAccessException ignored)
		{
			return null;
		}
	}

	private void moveCompassTopLeft()
	{
		Integer compassId = componentIdIfPresent("MINIMAP_COMPASS");
		if (compassId == null)
		{
			return;
		}

		Widget compass = client.getWidget(compassId);
		if (compass == null)
		{
			return;
		}

		if (compassOriginalX == null || compassOriginalY == null)
		{
			compassOriginalX = compass.getOriginalX();
			compassOriginalY = compass.getOriginalY();
		}

		compass.setHidden(false);
		compass.setXPositionMode(WidgetPositionMode.ABSOLUTE_LEFT);
		compass.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
		compass.setOriginalX(config.compassOffsetX());
		compass.setOriginalY(config.compassOffsetY());
		compass.revalidate();
	}

	private void restoreCompassPositionIfNeeded()
	{
		if (compassOriginalX == null || compassOriginalY == null)
		{
			return;
		}

		Integer compassId = componentIdIfPresent("MINIMAP_COMPASS");
		if (compassId == null)
		{
			return;
		}

		Widget compass = client.getWidget(compassId);
		if (compass == null)
		{
			return;
		}

		compass.setOriginalX(compassOriginalX);
		compass.setOriginalY(compassOriginalY);
		compass.revalidate();
		compassOriginalX = null;
		compassOriginalY = null;
	}

	private void moveSpecOrbToXpSlot()
	{
		Widget specOrb = client.getWidget(ComponentID.MINIMAP_SPEC_ORB);
		Widget xpOrb = client.getWidget(ComponentID.MINIMAP_XP_ORB);
		if (specOrb == null || xpOrb == null)
		{
			return;
		}

		if (specOriginalX == null || specOriginalY == null)
		{
			specOriginalX = specOrb.getOriginalX();
			specOriginalY = specOrb.getOriginalY();
		}

		specOrb.setHidden(false);
		specOrb.setOriginalX(xpOrb.getOriginalX());
		specOrb.setOriginalY(xpOrb.getOriginalY());
		specOrb.revalidate();
	}

	private void restoreSpecOrbPositionIfNeeded()
	{
		if (specOriginalX == null || specOriginalY == null)
		{
			return;
		}

		Widget specOrb = client.getWidget(ComponentID.MINIMAP_SPEC_ORB);
		if (specOrb == null)
		{
			return;
		}

		specOrb.setOriginalX(specOriginalX);
		specOrb.setOriginalY(specOriginalY);
		specOrb.revalidate();
		specOriginalX = null;
		specOriginalY = null;
	}
}
