//package net.runelite.client.plugins.oldschoolgraphics;
//
//import com.google.inject.Provides;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//
//import net.runelite.api.*;
//import net.runelite.api.VarClientInt;
//import net.runelite.api.events.*;
//import net.runelite.api.kit.KitType;
//import net.runelite.api.widgets.Widget;
//import net.runelite.api.widgets.WidgetID;
//import net.runelite.cache.definitions.ItemDefinition;
//import net.runelite.client.events.ConfigChanged;
//
//import net.runelite.cache.definitions.ModelDefinition;
//import net.runelite.cache.definitions.loaders.ModelLoader;
//import net.runelite.client.config.ConfigManager;
//import net.runelite.client.eventbus.EventBus;
//import net.runelite.client.eventbus.Subscribe;
//import net.runelite.client.game.ItemManager;
//import net.runelite.client.game.SpriteManager;
//import net.runelite.client.game.chatbox.ChatboxItemSearch;
//import net.runelite.client.plugins.Plugin;
//import net.runelite.client.plugins.PluginDescriptor;
//import net.runelite.client.plugins.PluginManager;
//import net.runelite.client.plugins.examine.ExamineType;
//import net.runelite.client.plugins.interfacestyles.InterfaceStylesPlugin;
//
//import net.runelite.client.plugins.skillcalculator.beans.SkillData;
//import net.runelite.client.ui.overlay.OverlayUtil;
//import net.runelite.client.util.AsyncBufferedImage;
//import net.runelite.client.util.ImageUtil;
//import net.runelite.http.api.examine.ExamineClient;
//import net.runelite.http.api.npc.NpcInfo;
//import org.apache.commons.compress.utils.IOUtils;
//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.commons.lang3.reflect.FieldUtils;
//import org.apache.commons.lang3.reflect.MethodUtils;
//
//import javax.imageio.ImageIO;
//import javax.inject.Inject;
//import javax.sound.midi.MidiSystem;
//import javax.sound.midi.Sequence;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//
//import static net.runelite.api.widgets.WidgetID.CHATBOX_GROUP_ID;
//import static net.runelite.api.widgets.WidgetID.MUSIC_GROUP_ID;
//
//@PluginDescriptor(
//        name = "OldSchoolGraphics",
//        description = "Downgrade models, textures, interfaces and objects to that of the 2005 / 2006 client.",
//        tags = {"downgrades", "npcs", "textures", "interfaces", "models", "objects"},
//        enabledByDefault = false
//)
//@Slf4j
//
//public class OldSchoolGraphicsPlugin extends Plugin {
//
//        int WIDGET_ACCOUNT_MANAGEMENT = 35913767;
//
//        //<editor-fold desc="Dependency Injection & Configuration">
//        @Inject
//        private Client client;
//
//        @Inject
//        private OldSchoolGraphicsConfig config;
//
//        @Inject
//        private ExamineClient examineClient;
//
//        @Inject
//        private ItemManager itemManager;
//
//        @Inject
//        private SpriteManager spriteManager;
//
//        private HashSet<NpcID> hiddenNpcs;
//        private HashSet<ObjectID> hiddenObjects;
//
//        private List<NPC> npcs;
//        private List<NPC> oldNpcs;
//
//        private boolean busy;
//
//        private boolean onFirstTick = false;
//        private int tickCounter = 0;
//
//        private final int EXAMINE_TEXT_PARENT_ID = 10616890;
//
//        @Provides
//        OldSchoolGraphicsConfig provideConfig(ConfigManager configManager)
//        {
//                return configManager.getConfig(OldSchoolGraphicsConfig.class);
//        }
//
//        @Override
//        protected void startUp()
//        {
//                createHashSets();
//        }
//
//        @Subscribe
//        public void onGameTick(GameTick event)
//        {
//                scanChatGameMessages();
//
//        /*
//                PlayerComposition sdfsdf = client.getLocalPlayer().getPlayerComposition();
//                sdfsdf.getEquipmentIds()[KitType.TORSO.getIndex()] = ItemID.BANDOS_CHESTPLATE + 512;
//                sdfsdf.setHash();
//        */
//
//        }
//
//        @Subscribe
//        public void onClientTick(ClientTick event)
//        {
//                scanChatGameMessages();
//        }
//
//        @Subscribe
//        public void onChatMessage(ChatMessage event) {
//
//        }
//
//        @Subscribe
//        public void onWidgetLoaded(WidgetLoaded event) {
//                System.out.println("loaded: " + event.getClass().toString());
//        }
//
//        @Subscribe
//        public void onWidgetMenuOptionClicked(WidgetMenuOptionClicked event) {
//                System.out.println("NÄI O NÄREET: " + event.getClass().toString());
//        }
//
//        @Subscribe
//        public void onWidgetHiddenChanged(WidgetHiddenChanged event)
//        {
//                /*
//
//                if (event.getWidget().getParentId() != EXAMINE_TEXT_PARENT_ID) {
//                        new Thread( new Runnable() {
//                                @SneakyThrows
//                                public void run()  {
//                                        try  {
//                                                Widget chatWidget = client.getWidget(162, 58);
//
//                                                Thread.sleep( 1 );
//
//                                                for (Widget wgg : chatWidget.getChildren()) {
//                                                        if (wgg.getText().contains("You need level")) {
//                                                                System.out.println("vaihetaa examine ");
//                                                                tryReplaceComposition(wgg, wgg.getText(), "You need a higher Herblore level");
//                                                        }
//                                                        //System.out.println("NÄI O NÄREET parentis: " + wg.getId() + " " + wg.getText());
//                                                }
//                                        }
//                                        catch (Exception err)  {
//                                                System.out.println("Err" + err.getMessage());
//                                                System.out.println("Song not found. Playing OSRS music");
//                                        }
//                                }
//                        } ).start();
//                }
//
//
//                if (event.getWidget().getParentId() == EXAMINE_TEXT_PARENT_ID) {
//                        System.out.println("no saadana");
//                        //System.out.println("NÄI O NÄREET hiddeNIS: " + event.getWidget().getText() + event.getWidget().getType());
//                        // event.getWidget().setName("tere tulemasrt");
//                        //event.getWidget().setHidden(true);
//
//                        if (event.getWidget().getParent() != null) {
//                                for (Widget wg : event.getWidget().getParent().getChildren()) {
//                                        if (wg.getText().contains("You need level")) {
//                                                tryReplaceComposition(wg, wg.getText(), "You need a higher Herblore level");
//                                        }
//                                        //System.out.println("NÄI O NÄREET parentis: " + wg.getId() + " " + wg.getText());
//                                }
//                        }
//                        else {
//                                System.out.println("viddu nulli");
//                        }
///*
//                        new Thread( new Runnable() {
//                                @SneakyThrows
//                                public void run()  {
//                                        try  {
//                                                Thread.sleep( 400 );
//
//                                                System.out.println("NÄI O NÄREET hiddeNIS: " + event.getWidget().getText() + event.getWidget().getType());
//
//                                                Widget widgetti = event.getWidget();
//                                                event.getWidget().setText("asdasdasd");
//                                                String texti = event.getWidget().getText();
//
//                                                tryReplaceComposition(widgetti, texti, "fsdfsdfsdnfsd");
//                                        }
//                                        catch (Exception err)  {
//                                                System.out.println("Err" + err.getMessage());
//                                                System.out.println("Song not found. Playing OSRS music");
//                                        }
//                                }
//                        } ).start(); */
//
//                }
//
//                //System.out.println("hidden id " + event.getWidget().getId());
//                //System.out.println("hidden id parent " + event.getWidget().getParentId())
//
//        @Subscribe
//        public void onGameObjectSpawned(GameObjectSpawned event)
//        {
//                int workBench = 24170;
//
//                GameObject gameObject = event.getGameObject();
//
//                if (gameObject == null) {
//                        System.out.println("on nulli");
//                        return;
//                }
//
//                if (gameObject.getId() == 9250) {
//                        System.out.println("TÄH RÖH TÄH RLÖH");
//
//                        Renderable renderable = gameObject.getRenderable();
//                        int hh = renderable.getModelHeight();
//
//                        tryReplaceComposition(renderable, hh, 10);
//                        tryReplaceComposition(gameObject, renderable, renderable);
//
//                        System.out.println("MODELIN HEITTI " + hh);
//                }
//
//                if (gameObject.getId() == 23061) {
//                        System.out.println("TÄH RÖH TÄH RLÖH");
//
//                        Renderable renderable = gameObject.getRenderable();
//
//                        tryReplaceComposition(gameObject, renderable, null);
//                }
//
//
//        }
//
//        @Subscribe
//        public void onItemSpawned(ItemSpawned event) {
//                // same logic for grimy herbs
//                System.out.println("item SPAWNAAAAAAAAAAS");
//                if (event.getItem() != null) {
//
//                        tryReplaceComposition(event.getItem(), event.getItem().getModelHeight(), 500);
//                        tryReplaceComposition(event.getItem(), event.getItem().getModel(), null);
//
//                        for (TileItem tile : event.getTile().getGroundItems())
//                        {
//                                System.out.println(tile.toString());
//                                tryReplaceComposition(tile, tile.getModelHeight(), 500);
//                                tryReplaceComposition(tile, tile.getModel(), null);
//                        }
//                }
//        }
//
//        @Subscribe
//        public void onItemContainerChanged(ItemContainerChanged event) throws IOException {
//
//         for (Item item : event.getItemContainer().getItems()) {
//
//                 if (item != null) {
//
//                         ItemComposition ic = client.getItemDefinition(item.getId());
//
//                         if (ic != null) {
//
//                                 replaceGrimyHerb(ic);
//
//
//                                 if (item.getId() == 2566) {
//
//                                         //System.out.println("invi modeli: " + invModel);
//                                         //System.out.println("äksöneiden möörö: " + ic.getInventoryActions().length);
//
//                                         for (String action : ic.getInventoryActions()) {
//                                                 if (action != null) {
//                                                         //System.out.println(action.toString());
//                                                 }
//                                         }
//
//                                         String[] actions = new String[] {"Wear", "Rub", "asd", "asdasd", "asdasdasd"};
//                                         tryReplaceComposition(ic, ic.getInventoryActions(), actions);
//
//                                 }
//
//                                 if (item.getId() == 209) {
//                                        String itemName = ic.getName();
//                                         tryReplaceComposition(ic, itemName, "asd");
//
//                                         String[] actions = new String[] {"sdffsd", "fsdfsd"};
//                                         String[] oldAcs = ic.getInventoryActions();
//
//                                         for (String action : oldAcs) {
//                                                 if (action != null) {
//                                                         //System.out.println(action.toString());
//                                                 }
//                                         }
//                                 }
//                         }
//                 }
//         }
//        }
//
//        @Subscribe
//        public void onWallObjectSpawned(WallObjectSpawned event)
//        {
//                WallObject wallObject = event.getWallObject();
//
//                if (wallObject == null) {
//                        System.out.println("on nulli");
//                        return;
//                }
//
//                if (wallObject.getId() == 23888) {
//                        //System.out.println("SEINÄÄÄÄÄÄ");
//
//                        Polygon polygon = wallObject.getCanvasTilePoly();
//
//                        if (polygon != null) {
//                                tryReplaceComposition(wallObject, polygon, null);
//                        }
//
//                        /*
//
//                        Renderable renderable = wallObject.getRenderable1();
//                        Renderable renderable2 = wallObject.getRenderable2();
//
//                        if (renderable != null) {
//                                tryReplaceComposition(wallObject, renderable, null);
//                        }
//
//                        if (renderable != null) {
//                                tryReplaceComposition(wallObject, renderable2, null);
//                        }
//                        */
//                }
//
//        }
//
//
//        @Subscribe
//        public void onMenuOpened(MenuOpened event) {
//                System.out.println("menu avattu");
//
//                if (event.getMenuEntries() != null) {
//                        MenuEntry[] menuEntries = event.getMenuEntries();
//
//                        boolean firsti = true;
//                        for (MenuEntry menu : menuEntries) {
//                                System.out.println(menu.getOption());
//
//                                if (firsti) {
//                                        firsti = false;
//                                        continue;
//                                }
//
//                                //tryReplaceComposition(menu, menu.getOption(), null);
//                                menu.setOption("");
//                                //menu.setTarget("");
//                                //menu.setType(0);
//                        }
//
//                        /*
//                                                for (int i = menuEntries.length - 1; i > 1 ; i--) {
//                                menuEntries = ArrayUtils.removeElement(menuEntries, i);
//                        }
//                         */
//
//
//                        //menuEntries[0].setOption("Zanaris");
//                        System.out.println(menuEntries.length);
//                        client.setMenuEntries(menuEntries);
//                }
//        }
//
//        @Subscribe
//        public void onNpcSpawned(NpcSpawned npcSpawned)
//        {
//                NPC npc = npcSpawned.getNpc();
//
//                NPCComposition basecomp = client.getNpcDefinition(8026); // 1829
//                int[] basemodels =  basecomp.getModels();
//
//                changeNpcModels(npc, basemodels);
//        }
//
//        public void hideNpc(NPC npc)
//        {
//                NPCComposition comp = npc.getComposition();
//                tryReplaceComposition(comp, comp.isMinimapVisible(), false);
//                tryReplaceComposition(comp, comp.isClickable(), false);
//                tryReplaceComposition(comp, comp.getModels(), null);
//        }
//
//        public void changeNpcModels(NPC npc, int[] newModels)
//        {
//                NPCComposition comp = npc.getComposition();
//
//                if (Arrays.equals(newModels, comp.getModels())) {
//                        // newModel is same as the old one, return
//                        return;
//                }
//
//                tryReplaceComposition(comp, comp.getModels(), newModels);
//        }
//
//        public void changeNpcName(NPC npc, String newName)
//        {
//                NPCComposition comp = npc.getComposition();
//
//                if (newName == comp.getName()) {
//                        // newName is same as the old one, return
//                        return;
//                }
//
//                tryReplaceComposition(comp, comp.getName(), newName);
//        }
//
//        public void scanChatGameMessages()
//        {
//                try  {
//                        Widget chatWidget = client.getWidget(162, 58);
//
//                        for (Widget wg : chatWidget.getChildren()) {
//                                if (wg.getText().contains("Herblore to clean the Grimy")) {
//                                        replaceWidgetText(wg, "You need a higher Herblore level");
//                                }
//                                else if (wg.getText().contains("You clean the Grimy")) {
//                                        String herb = wg.getText().substring(20, wg.getText().length());
//                                        String capitalizedHerb = herb.substring(0, 1).toUpperCase() + herb.substring(1);
//                                        replaceWidgetText(wg, "This herb is a " + capitalizedHerb + ".");
//                                }
//                                else if (wg.getText().contains("You clean the ardrigal")) {
//                                        replaceWidgetText(wg, "You identify the herb. It is Ardrigal.");
//                                }
//                                else if (wg.getText().contains("You clean the sito foil")) {
//                                        replaceWidgetText(wg, "You identify the herb. It is Sito Foil.");
//                                }
//                                else if (wg.getText().contains("You clean the volencia moss")) {
//                                        replaceWidgetText(wg, "You identify the herb. It is Volencia Moss.");
//                                }
//                                else if (wg.getText().contains("You clean the Rogue's Purse")) {
//                                        replaceWidgetText(wg, "You identify the herb. It is Rogue's Purse.");
//                                }
//                                else if (wg.getText().contains("You clean the snake weed")) {
//                                        replaceWidgetText(wg, "You identify the herb. It is Snake Weed.");
//                                }
//                        }
//                }
//                catch (Exception err)  {
//
//                }
//        }
//
//        public void replaceGrimyHerb(ItemComposition item) {
//
//                //Skill skill = Skill.HERBLORE;
//                //int exp = this.client.getSkillExperience(skill);
//
//                switch (item.getId()) {
//                        case ItemID.GRIMY_RANARR_WEED:
//                        case ItemID.GRIMY_AVANTOE:
//                        case ItemID.GRIMY_CADANTINE:
//                        case ItemID.GRIMY_DWARF_WEED:
//                        case ItemID.GRIMY_GUAM_LEAF:
//                        case ItemID.GRIMY_HARRALANDER:
//                        case ItemID.GRIMY_IRIT_LEAF:
//                        case ItemID.GRIMY_KWUARM:
//                        case ItemID.GRIMY_LANTADYME:
//                        case ItemID.GRIMY_MARRENTILL:
//                        case ItemID.GRIMY_SNAPDRAGON:
//                        case ItemID.GRIMY_TARROMIN:
//                        case ItemID.GRIMY_TORSTOL:
//                        case ItemID.GRIMY_TOADFLAX:
//                        case ItemID.GRIMY_ARDRIGAL:
//                        case ItemID.GRIMY_ROGUES_PURSE:
//                        case ItemID.GRIMY_SITO_FOIL:
//                        case ItemID.GRIMY_SNAKE_WEED:
//                        case ItemID.GRIMY_VOLENCIA_MOSS:
//                                replaceItemCompositionName(item, "Herb");
//                                replaceItemCompositionInventoryAction(item, 0, "Identify");
//                                break;
//                }
//        }
//
//        public void replaceItemCompositionName(ItemComposition item, String replace)
//        {
//                tryReplaceComposition(item, item.getName(), replace);
//        }
//
//        public void replaceItemCompositionInventoryAction(ItemComposition item, int index, String replace)
//        {
//                String[] actions = item.getInventoryActions();
//                actions[index] = replace;
//        }
//
//        public void replaceWidgetText(Widget widget, String replace)
//        {
//                tryReplaceComposition(widget, widget.getText(), replace);
//        }
//
//        public void tryReplaceComposition(Object parent, Object find, Object replace)
//        {
//                try {
//                        String memoryFieldName = getFieldName(parent, find);
//
//                        if (memoryFieldName == null) {
//                                log.error("Failed to lookup object name, can't replace field. Is your find object inside the parent?");
//                                return;
//                        }
//
//                        Field field = parent.getClass().getDeclaredField(memoryFieldName);
//                        field.setAccessible(true);
//                        field.set(parent, replace);
//
//
//                } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                } catch (NoSuchFieldException e) {
//                        e.printStackTrace();
//                }
//        }
//
//        public String getFieldName(Object parent, Object find)
//        {
//                for (Field field : FieldUtils.getAllFields(parent.getClass()))
//                {
//                        field.setAccessible(true);
//
//                        try {
//                                if (field.get(parent) != null && field.get(parent).equals(find)) {
//                                        return field.getName();
//                                }
//                        } catch (IllegalAccessException e) {
//                                System.out.println(e.getMessage());
//                                return null;
//                        }
//                        catch (Exception e) {
//                                System.out.println(e.getMessage());
//                        }
//                }
//
//                System.out.println("Could not find correct field for " + find);
//
//                return null;
//        }
//
//        public void tryReplaceItemCompositionModel(ItemComposition parent, int find, int replace)
//        {
//                try {
//                        String memoryFieldName = getItemCompositionFieldName(parent, find, replace);
//
//                        if (memoryFieldName == null) {
//                                log.error("Failed to lookup object name, can't replace field. Is your find object inside the parent?");
//                                return;
//                        }
//
//                        Field field = parent.getClass().getDeclaredField(memoryFieldName);
//                        field.setAccessible(true);
//                        field.set(parent, replace);
//
//
//                } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                } catch (NoSuchFieldException e) {
//                        e.printStackTrace();
//                }
//        }
//
//        @SneakyThrows
//        public String getItemCompositionFieldName(ItemComposition parent, int find, int replace)
//        {
//                for (Field field : FieldUtils.getAllFields(parent.getClass()))
//                {
//                        field.setAccessible(true);
//
//                        try {
//
//                                if (field.getType() == int.class) {
//                                        System.out.println("Current field is: " + field.get(parent) + "-" + field.getName() + " and trying to find " + find);
//                                }
//
//                                if (field.get(parent) != null && field.get(parent).equals(find)) {
//                                        return field.getName();
//                                }
//                        } catch (IllegalAccessException e) {
//                                System.out.println(e.getMessage());
//                                return null;
//                        }
//                        catch (Exception e) {
//                                System.out.println(e.getMessage());
//                        }
//                }
//
//                System.out.println("Could not find correct field for " + find);
//
//                return null;
//        }
//
//        private void updateModels()
//        {
//
//                try {
//                        int BGS_ITEM_ID = ItemID.BANDOS_GODSWORD;
//                        int BGS_MODEL_ID = 28059;
//                        int BUCKET_ITEM_ID = 1925;
//                        int BUCKET_MODEL_ID = 2397;
//
//                        // Raw data
//                        byte[] data = loadModel(BUCKET_MODEL_ID);
//                        System.out.println("koitoetaa vaihtaa");
//                        // Runelite
//                        // r:92 -> g:19 -> g:22
//                        // r = client[panel]
//
//                        ItemComposition item = client.getItemDefinition(itemManager.canonicalize(BUCKET_ITEM_ID));
//                        ItemComposition bgs = client.getItemDefinition(itemManager.canonicalize(BGS_ITEM_ID));
//
//                        ItemComposition bgs2 = itemManager.getItemComposition(BGS_ITEM_ID);
//                        ItemComposition item2 = itemManager.getItemComposition(BUCKET_ITEM_ID);
//
//                        tryReplaceComposition(item, item.getName(), bgs2.getName());
//
//                        PlayerComposition sdfsdf = client.getLocalPlayer().getPlayerComposition();
//                        sdfsdf.getEquipmentIds()[KitType.TORSO.getIndex()] = ItemID.BANDOS_CHESTPLATE + 512;
//                        sdfsdf.setHash();
//
//                        // New
//                        ModelLoader loader = new ModelLoader();
//                        ModelDefinition model = loader.load(BUCKET_MODEL_ID, data);
//
//                        log.debug("Test");
//
//                } catch (IOException e) {
//                        System.out.println(e.getStackTrace());
//                }
//                catch (Exception ex) {
//                        System.out.println(ex.getMessage());
//                }
//        }
//
//        private byte[] loadModel(int id) throws IOException
//        {
//                String path = "models/" + Integer.toString(id) + ".mdl";
//
//                InputStream file = OldSchoolGraphicsPlugin.class.getResourceAsStream(path);
//
//                return IOUtils.toByteArray(file);
//        }
//
//        private void createHashSets()
//        {
//
//        }
//
//
//        /*
//        public void tryReplaceComposition(NPC parent, NPCComposition find, NPCComposition replace)
//        {
//                try {
//                        String memoryFieldName =  getFieldName(parent, find); //parent.getName();  //getFieldName(parent, find);
//
//                        if (memoryFieldName == null) {
//                                log.error("Failed to lookup object name, can't replace field. Is your find object inside the parent?");
//                                return;
//                        }
//                        System.out.println("memoryFieldName: " + memoryFieldName );
//
//                        Field field = parent.getClass().getDeclaredField(memoryFieldName);
//
//                        System.out.println("field " + field.getClass() );
//
//                        field.setAccessible(true);
//                        field.set(parent, replace);
//
//
//                } catch (NoSuchFieldException | IllegalAccessException e) {
//                        e.printStackTrace();
//                }
//        }
//
//        public String getFieldName(Object parent, Object find)
//        {
//
//
//                for (Field field : parent.getClass().getDeclaredFields())
//                {
//                        field.setAccessible(true);
//
//                        try {
//                                if (field.get(parent).equals(find)) {
//                                        return field.getName();
//                                }
//                        } catch (IllegalAccessException e) {
//                                return null;
//                        }
//                        catch (Exception e) {
//
//                        }
//                }
//
//return  null;
//        } */
//
//        /*
//
//@Subscribe
//public void onConfigChanged(ConfigChanged event)
//        {
//        if (event.getKey().equals("replaceIgnoreListIcon")) {
//        if (event.getNewValue().equals("true")) {
//        updateIgnoreListIcon();
//        } else {
//        restoreIgnoreListIcon();
//        }
//        }
//
//        if (event.getKey().equals("replaceModels")) {
//        if (event.getNewValue().equals("true")) {
//        updateModels();
//        } else {
//        restoreModels();
//        }
//        }
//        }
//
//public void startUp()
//        {
//        if (config.replaceIgnoreListIcon()) {
//        updateIgnoreListIcon();
//        }
//
//        if (config.replaceModels()) {
//        updateModels();
//        }
//        }
//
//public void shutDown()
//        {
//        if (config.replaceIgnoreListIcon()) {
//        restoreIgnoreListIcon();
//        }
//
//        if (config.replaceModels()) {
//        restoreModels();
//        }
//        }
////</editor-fold>
//
////<editor-fold desc="Model replacements">
//private void updateModels()
//        {
//
//        try {
//
//        int BUCKET_ITEM_ID = 1925;
//        int BUCKET_MODEL_ID = 2397;
//
//        // Raw data
//        byte[] data = loadModel(BUCKET_MODEL_ID);
//
//        // Runelite
//        // r:92 -> g:19 -> g:22
//        // r = client[panel]
//        ItemComposition item = client.getItemDefinition(BUCKET_ITEM_ID);
//
//        // New
//        ModelLoader loader = new ModelLoader();
//        ModelDefinition model = loader.load(BUCKET_MODEL_ID, data);
//
//        log.debug("Test");
//
//        } catch (IOException e) {
//        e.printStackTrace();
//        }
//        }
//
//private byte[] loadModel(int id) throws IOException
//        {
//        String path = "models/" + Integer.toString(id) + ".mdl";
//        InputStream file = OldSchoolGraphicsConfig.class.getResourceAsStream(path);
//        return IOUtils.toByteArray(file);
//        }
//
//private void restoreModels()
//        {
//        // TODO
//        }
//
//
////    @Subscribe
////    public void onClientTick(ClientTick event)
////    {
////        int SKELETON_MAGE = 84;
////
////        // Skeleton composition ID's
////        //  - 74
////        //  - 75
////        //  - 76
////        //
////        //  comp.k = ['attack']
////        //  comp.l = 'Skeleon'
////        //  comp.ck = <composition id>
////        //  comp.w = <models>
////
////        NPCComposition basecomp = client.getNpcDefinition(SKELETON_MAGE);
////        int[] basemodels = basecomp.getModels();
////
////
////        for (NPC npc: client.getNpcs()) {
////            NPCComposition comp = npc.getComposition();
////            int[] models = comp.getModels();
////
////            if (Arrays.equals(models, basemodels)) {
////                continue;
////            }
////
////            tryReplaceComposition(comp, models, basemodels);
////        }
////    }
////
////    public void tryReplaceComposition(Object parent, Object find, Object replace)
////    {
////        try {
////            String memoryFieldName = getFieldName(parent, find);
////
////            if (memoryFieldName == null) {
////                log.error("Failed to lookup object name, can't replace field. Is your find object inside the parent?");
////                return;
////            }
////
////            Field field = parent.getClass().getDeclaredField(memoryFieldName);
////            field.setAccessible(true);
////            field.set(parent, replace);
////
////        } catch (NoSuchFieldException | IllegalAccessException e) {
////            e.printStackTrace();
////        }
////    }
////
////    public String getFieldName(Object parent, Object find)
////    {
////        for (Field field : parent.getClass().getDeclaredFields())
////        {
////            field.setAccessible(true);
////
////            try {
////                if (field.get(parent) != null && field.get(parent).equals(find)) {
////                    return field.getName();
////                }
////            } catch (IllegalAccessException e) {
////                return null;
////            }
////        }
////
////        return null;
////    }
////</editor-fold>
//
//
////<editor-fold desc="Ignore list icon">
//private void updateIgnoreListIcon()
//        {
//        String path = "sprites/" + Integer.toString(SpriteID.TAB_IGNORES) + ".png";
//
//        try (InputStream inputStream = OldSchoolGraphicsConfig.class.getResourceAsStream(path))
//        {
//        log.debug("Loading: " + path);
//        BufferedImage spriteImage = ImageIO.read(inputStream);
//        SpritePixels spritePixels = ImageUtil.getImageSpritePixels(spriteImage, client);
//
//        if (spritePixels != null)
//        {
//        client.getWidgetSpriteOverrides().put(WIDGET_ACCOUNT_MANAGEMENT, spritePixels);
//        }
//        }
//        catch (IOException ex)
//        {
//        log.debug("Unable to load image: ", ex);
//        }
//        catch (IllegalArgumentException ex)
//        {
//        log.debug("Input stream of file path " + path + " could not be read: ", ex);
//        }
//        }
//
//private void restoreIgnoreListIcon()
//        {
//        client.getWidgetSpriteOverrides().remove(WIDGET_ACCOUNT_MANAGEMENT);
//        }*/
//        //</editor-fold>
//}
