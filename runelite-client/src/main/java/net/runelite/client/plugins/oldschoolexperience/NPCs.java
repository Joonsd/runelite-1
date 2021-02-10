package net.runelite.client.plugins.oldschoolexperience;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;

@AllArgsConstructor
@Getter
public enum NPCs 
{
    TEST(0, false, false, false),
    IRONMAN_TUTOR_LUMBRIDGE(NpcID.IRON_MAN_TUTOR, false, true, false),
    ARTHUR_CLUE_HUNTER(NpcID.ARTHUR_THE_CLUE_HUNTER, false, true, false),
    BARTENDER_LUMBRIDGE(NpcID.BARTENDER_7546, false, true, false),
    VEOS_LUMBRIDGE(NpcID.VEOS_8484, false, true, false);

    private final int npcID;
    private final boolean attackable;
    private final boolean hasMinimapIcon;
    private final boolean inWilderness;
}
