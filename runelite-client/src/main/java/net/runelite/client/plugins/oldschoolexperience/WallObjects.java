package net.runelite.client.plugins.oldschoolexperience;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WallObjects
{
    TEST(0, false, false);

    private final int wallObjectID;
    private final boolean clickable;
    private final boolean inWilderness;
}
