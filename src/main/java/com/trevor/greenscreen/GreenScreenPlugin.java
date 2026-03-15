package com.trevor.greenscreen;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
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
}
