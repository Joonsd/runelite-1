package net.runelite.client.plugins.oldschoolexperience;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.GameObject;

@AllArgsConstructor
@Getter
public enum GameObjects
{
    TEST(0, false, false),
    BARSTOOL_LUMBRIDGE(24322, false, false);

    private final int gameObjectID;
    private final boolean clickable;
    private final boolean inWilderness;
}
