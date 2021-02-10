package net.runelite.client.plugins.oldschoolexperience;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Herbs
{
    TEST(0, false);

    private final int itemID;
    private final boolean specialHerb;
}
