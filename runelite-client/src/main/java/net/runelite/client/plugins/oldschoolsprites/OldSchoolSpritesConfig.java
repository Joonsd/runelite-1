package net.runelite.client.plugins.oldschoolsprites;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup(OldSchoolSpritesConfig.GROUP)
public interface OldSchoolSpritesConfig extends Config {
    String GROUP = "inventoryViewer";

    @ConfigItem(
            keyName = "toggleKeybind",
            name = "Toggle Overlay",
            description = "Binds a key (combination) to toggle the overlay.",
            position = 0
    )
    default Keybind toggleKeybind()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "hiddenDefault",
            name = "Hidden by default",
            description = "Whether or not the overlay is hidden by default.",
            position = 1
    )
    default boolean hiddenDefault()
    {
        return false;
    }
}
