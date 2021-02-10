package net.runelite.client.plugins.oldschoolsprites;

import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;

class OldSchoolSpritesInventory extends OverlayPanel {
    private static final int INVENTORY_SIZE = 28;
    private static final ImageComponent PLACEHOLDER_IMAGE = new ImageComponent(
            new BufferedImage(Constants.ITEM_SPRITE_WIDTH, Constants.ITEM_SPRITE_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR));

    private final Client client;
    private final ItemManager itemManager;
    private boolean hidden;

    @Inject
    private OldSchoolSpritesInventory(Client client, ItemManager itemManager, OldSchoolSpritesConfig config)
    {
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setPreferredPosition(OverlayPosition.BOTTOM_RIGHT);
        setLayer(OverlayLayer.UNDER_WIDGETS);
        setPreferredLocation(new Point(250,120));

        panelComponent.setWrap(true);
        panelComponent.setGap(new Point(6, 4));
        panelComponent.setPreferredSize(new Dimension(4 * (Constants.ITEM_SPRITE_WIDTH + 6), 0));
        panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
        panelComponent.setBackgroundColor(new Color(0, 0, 0, 0));
        this.itemManager = itemManager;
        this.client = client;
        this.hidden = config.hiddenDefault();
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (hidden)
        {
            return null;
        }

        final ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);

        if (itemContainer == null)
        {
            return null;
        }

        final Item[] items = itemContainer.getItems();

        for (int i = 0; i < INVENTORY_SIZE; i++)
        {
            if (i < items.length)
            {
                final Item item = items[i];
                if (item.getQuantity() > 0)
                {
                    final BufferedImage image = getImage(item);
                    if (image != null)
                    {
                        panelComponent.getChildren().add(new ImageComponent(image));
                        continue;
                    }
                }
            }

            // put a placeholder image so each item is aligned properly and the panel is not resized
            panelComponent.getChildren().add(PLACEHOLDER_IMAGE);
        }

        return super.render(graphics);
    }

    private BufferedImage getImage(Item item)
    {
        ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
        return itemManager.getImage(item.getId(), item.getQuantity(), itemComposition.isStackable());
    }

    protected void toggle()
    {
        hidden = !hidden;
    }
}
