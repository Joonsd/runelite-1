package net.runelite.client.plugins.oldschoolexperience;

import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.inject.Inject;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

@PluginDescriptor(
        name = "OldSchoolExperience",
        description = "Removes some QOL updates from the game and try to make the game feel more like it was in 2013"
)

public class OldSchoolExperiencePlugin extends Plugin
{
    @Inject
    private Client client;


    @Subscribe
    public void onGameTick(GameTick event)
    {
        //scanChatGameMessages();

        /*
                PlayerComposition sdfsdf = client.getLocalPlayer().getPlayerComposition();
                sdfsdf.getEquipmentIds()[KitType.TORSO.getIndex()] = ItemID.BANDOS_CHESTPLATE + 512;
                sdfsdf.setHash();
        */

    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        Optional<GameObjects> foundGameObject = Arrays.stream(GameObjects.values()).filter(game -> game.getGameObjectID() == event.getGameObject().getId()).findFirst();

        if (foundGameObject.isPresent())
        {
            hideGameObject(event.getGameObject());
        }

//        int workBench = 24170;
//
//        GameObject gameObject = event.getGameObject();
//
//        if (gameObject == null) {
//            System.out.println("on nulli");
//            return;
//        }
//
//        if (gameObject.getId() == 9250) {
//            System.out.println("TÄH RÖH TÄH RLÖH");
//
//            Renderable renderable = gameObject.getRenderable();
//            int hh = renderable.getModelHeight();
//
//            tryReplaceComposition(renderable, hh, 10);
//            tryReplaceComposition(gameObject, renderable, renderable);
//
//            System.out.println("MODELIN HEITTI " + hh);
//        }
//
//        if (gameObject.getId() == 23061) {
//            System.out.println("TÄH RÖH TÄH RLÖH");
//
//            Renderable renderable = gameObject.getRenderable();
//
//            tryReplaceComposition(gameObject, renderable, null);
//        }
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event)
    {
        WallObject wallObject = event.getWallObject();

        if (wallObject == null) {
            System.out.println("on nulli");
            return;
        }

        if (wallObject.getId() == 23888) {
            //System.out.println("SEINÄÄÄÄÄÄ");

            Polygon polygon = wallObject.getCanvasTilePoly();

            if (polygon != null) {
                tryReplaceComposition(wallObject, polygon, null);
            }

                        /*

                        Renderable renderable = wallObject.getRenderable1();
                        Renderable renderable2 = wallObject.getRenderable2();

                        if (renderable != null) {
                                tryReplaceComposition(wallObject, renderable, null);
                        }

                        if (renderable != null) {
                                tryReplaceComposition(wallObject, renderable2, null);
                        }
                        */
        }

    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned npcSpawned)
    {
        Optional<NPCs> foundNpc = Arrays.stream(NPCs.values()).filter(npc -> npc.getNpcID() == npcSpawned.getNpc().getId()).findFirst();

        System.out.println(Arrays.toString(npcSpawned.getNpc().getComposition().getModels()));

        if (foundNpc.isPresent())
        {
            NPC npc = npcSpawned.getNpc();

//            NPCComposition basecomp = client.getNpcDefinition(8026); // 1829
//            int[] basemodels =  basecomp.getModels();

//            replaceNpc(npc, client.getNpcDefinition(NpcID.RAT_4594), foundNpc.get().isHasMinimapIcon());
            hideNpc(npc, foundNpc.get().isHasMinimapIcon());
            //changeNpcModels(npc, basemodels);
        }
    }

    public void hideNpc(NPC npc, boolean hideMinimapIcon)
    {
        NPCComposition comp = npc.getComposition();
        //tryReplaceComposition(comp, comp.getModels(), new int[] {40754, 40751, 40749, 40748, 17460}); // man hidden
        tryReplaceComposition(comp, comp.getModels(), new int[] {33141, 596}); // man hidden

//        tryReplaceComposition(comp, comp.isClickable(), Boolean.FALSE);
//        tryReplaceComposition(comp, comp.isInteractible(), Boolean.FALSE);
//        tryReplaceComposition(comp, comp.isVisible(), Boolean.FALSE);

        tryReplaceBoolean(comp, "isClickable");
        tryReplaceBoolean(comp, "isInteractible");
        tryReplaceBoolean(comp, "isVisible");

        if (hideMinimapIcon)
        {
            tryReplaceComposition(comp, comp.isMinimapVisible(), Boolean.FALSE);
        }
    }

    public void replaceNpc(NPC npc, NPCComposition newNpc, boolean hideMinimapIcon)
    {
        NPCComposition comp = npc.getComposition();
        tryReplaceComposition(comp, comp.isClickable(), Boolean.FALSE);
        tryReplaceComposition(comp, comp.getModels(), new int[] {33141, 596});
//        tryReplaceComposition(comp, comp.getModels(), newNpc.getModels());
        tryReplaceComposition(comp, comp.isVisible(), Boolean.FALSE);

        if (hideMinimapIcon)
        {
            tryReplaceComposition(comp, comp.isMinimapVisible(), newNpc.isMinimapVisible());
        }
    }

    public void hideGameObject(GameObject gameObject)
    {
        tryReplaceComposition(gameObject, gameObject.getRenderable(), null);
    }

    public void changeNpcModels(NPC npc, int[] newModels)
    {
        NPCComposition comp = npc.getComposition();

        if (Arrays.equals(newModels, comp.getModels())) {
            // newModel is same as the old one, return
            return;
        }

        tryReplaceComposition(comp, comp.getModels(), newModels);
    }

    public void changeNpcName(NPC npc, String newName)
    {
        NPCComposition comp = npc.getComposition();

        if (newName == comp.getName()) {
            // newName is same as the old one, return
            return;
        }

        tryReplaceComposition(comp, comp.getName(), newName);
    }

    public void scanChatGameMessages()
    {
        try  {
            Widget chatWidget = client.getWidget(162, 58);

            for (Widget wg : chatWidget.getChildren()) {
                if (wg.getText().contains("Herblore to clean the Grimy")) {
                    replaceWidgetText(wg, "You need a higher Herblore level");
                }
                else if (wg.getText().contains("You clean the Grimy")) {
                    String herb = wg.getText().substring(20, wg.getText().length());
                    String capitalizedHerb = herb.substring(0, 1).toUpperCase() + herb.substring(1);
                    replaceWidgetText(wg, "This herb is a " + capitalizedHerb + ".");
                }
                else if (wg.getText().contains("You clean the ardrigal")) {
                    replaceWidgetText(wg, "You identify the herb. It is Ardrigal.");
                }
                else if (wg.getText().contains("You clean the sito foil")) {
                    replaceWidgetText(wg, "You identify the herb. It is Sito Foil.");
                }
                else if (wg.getText().contains("You clean the volencia moss")) {
                    replaceWidgetText(wg, "You identify the herb. It is Volencia Moss.");
                }
                else if (wg.getText().contains("You clean the Rogue's Purse")) {
                    replaceWidgetText(wg, "You identify the herb. It is Rogue's Purse.");
                }
                else if (wg.getText().contains("You clean the snake weed")) {
                    replaceWidgetText(wg, "You identify the herb. It is Snake Weed.");
                }
            }
        }
        catch (Exception err)  {

        }
    }

    public void replaceGrimyHerb(ItemComposition item) {

        //Skill skill = Skill.HERBLORE;
        //int exp = this.client.getSkillExperience(skill);

        switch (item.getId()) {
            case ItemID.GRIMY_RANARR_WEED:
            case ItemID.GRIMY_AVANTOE:
            case ItemID.GRIMY_CADANTINE:
            case ItemID.GRIMY_DWARF_WEED:
            case ItemID.GRIMY_GUAM_LEAF:
            case ItemID.GRIMY_HARRALANDER:
            case ItemID.GRIMY_IRIT_LEAF:
            case ItemID.GRIMY_KWUARM:
            case ItemID.GRIMY_LANTADYME:
            case ItemID.GRIMY_MARRENTILL:
            case ItemID.GRIMY_SNAPDRAGON:
            case ItemID.GRIMY_TARROMIN:
            case ItemID.GRIMY_TORSTOL:
            case ItemID.GRIMY_TOADFLAX:
            case ItemID.GRIMY_ARDRIGAL:
            case ItemID.GRIMY_ROGUES_PURSE:
            case ItemID.GRIMY_SITO_FOIL:
            case ItemID.GRIMY_SNAKE_WEED:
            case ItemID.GRIMY_VOLENCIA_MOSS:
                replaceItemCompositionName(item, "Herb");
                replaceItemCompositionInventoryAction(item, 0, "Identify");
                break;
        }
    }

    public void replaceItemCompositionName(ItemComposition item, String replace)
    {
        tryReplaceComposition(item, item.getName(), replace);
    }

    public void replaceItemCompositionInventoryAction(ItemComposition item, int index, String replace)
    {
        String[] actions = item.getInventoryActions();
        actions[index] = replace;
    }

    public void replaceWidgetText(Widget widget, String replace)
    {
        tryReplaceComposition(widget, widget.getText(), replace);
    }

    public void tryReplaceComposition(Object parent, Object find, Object replace)
    {
        try {
            String memoryFieldName = getFieldName(parent, find);

            if (memoryFieldName == null) {
                return;
            }

            Field field = parent.getClass().getDeclaredField(memoryFieldName);
            field.setAccessible(true);
            field.set(parent, replace);


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public String getFieldName(Object parent, Object find)
    {
        for (Field field : FieldUtils.getAllFields(parent.getClass()))
        {
            field.setAccessible(true);

            try {
                if (field.get(parent) != null && field.get(parent).equals(find)) {
                    return field.getName();
                }
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
                return null;
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Could not find correct field for " + find);

        return null;
    }

    private void tryReplaceBoolean(Object parent, String booleanFieldName)
    {
        try {
            Field booleanField = parent.getClass().getField(booleanFieldName);

            booleanField.setAccessible(true);
            booleanField.setBoolean(parent, false);
//            booleanField.set(parent, Boolean.FALSE);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getItemCompositionFieldName(ItemComposition parent, int find, int replace)
    {
        for (Field field : FieldUtils.getAllFields(parent.getClass()))
        {
            field.setAccessible(true);

            try {

                if (field.getType() == int.class) {
                    System.out.println("Current field is: " + field.get(parent) + "-" + field.getName() + " and trying to find " + find);
                }

                if (field.get(parent) != null && field.get(parent).equals(find)) {
                    return field.getName();
                }
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
                return null;
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Could not find correct field for " + find);

        return null;
    }
}
