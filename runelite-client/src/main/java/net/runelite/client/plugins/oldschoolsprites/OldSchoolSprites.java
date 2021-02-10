package net.runelite.client.plugins.oldschoolsprites;

import com.google.inject.Provides;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

@PluginDescriptor(
        name = "OldSchoolSprites",
        description = "Change OSRS inventory and widget sprites to older ones. Works with inventory plugin",
        enabledByDefault = false
)

public class OldSchoolSprites extends Plugin
{
    @Inject
    private OldSchoolSpritesConfig config;

    @Inject
    private OldSchoolSpritesInventory overlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private KeyManager keyManager;

    @Provides
    OldSchoolSpritesConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(OldSchoolSpritesConfig.class);
    }

    private boolean start = false;
    private int jj = 0;

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        keyManager.registerKeyListener(hotkeyListener);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        keyManager.unregisterKeyListener(hotkeyListener);
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {

        jj++;
        //String newSong = client.getWidget(239, 6).getText();

        //client.getSpriteOverrides().put(SpriteID., spritePixels);

        //tryDoSpriteStuff();

        if (jj > 10 && start == false) {
            System.out.println("reached 10");
            start = true;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        System.out.println("State changes to: " + event.getGameState());

        switch (event.getGameState())
        {
            case LOGGING_IN:
            case CONNECTION_LOST:
            case HOPPING:
            case LOGIN_SCREEN:
            case LOGIN_SCREEN_AUTHENTICATOR:
            case LOADING:
                break;

            case STARTING:
                start = true;
        }
    }

    @Subscribe
        public void onItemContainerChanged(ItemContainerChanged event)  {
        if (start == false)
            return;
        }

    private final HotkeyListener hotkeyListener = new HotkeyListener(() -> config.toggleKeybind())
    {
        @Override
        public void hotkeyPressed()
        {
            overlay.toggle();
        }
    };
}
