package net.runelite.client.plugins.oldschoolexperience;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

public interface OldSchoolExperienceConfig extends Config
{
    @ConfigItem(
            position = 1,
            keyName = "replaceModels",
            name = "Replace Models",
            description = "Configures whether or not NPCs and items should be replaced with their 2005/2006 variant."
    )
    default boolean replaceModels()
    {
        return true;
    }

    @ConfigItem(
            position = 2,
            keyName = "replaceIgnoreListIcon",
            name = "Replace Ignore List Icon",
            description = "Replaces the account management icon with the old ignore list icon."
    )
    default boolean replaceIgnoreListIcon()
    {
        return true;
    }
}
