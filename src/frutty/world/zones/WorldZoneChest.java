package frutty.world.zones;

import frutty.entity.effects.*;
import frutty.entity.living.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class WorldZoneChest extends WorldZoneTexturable {
    public static final BufferedImage chestTexture = Material.loadTexture("world/special", "chest.png");

    public WorldZoneChest() {
        super("chestZone");
    }

    @Override
    public void render(int x, int y, Material material, Graphics graphics) {
        graphics.drawImage(material.texture, x, y, 64, 64, null);
        graphics.drawImage(chestTexture, x, y, 64, 64, null);
    }

    @Override
    public void onZoneEntered(int x, int y, Material material, EntityPlayer player) {
        player.entityEffects.add(new EntityEffectInvisible());

        super.onZoneEntered(x, y, material, player);
    }

    @Override
    protected ImageIcon getEditorIcon() {
        var toReturn = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        var graphics = toReturn.createGraphics();
        graphics.drawImage(WorldZone.normalZone.editorTexture.get().getImage(), 0, 0, null);
        graphics.drawImage(chestTexture, 0, 0, null);
        graphics.dispose();
        return new ImageIcon(toReturn);
    }

    @Override
    public BufferedImage getOverlayTexture() {
        return chestTexture;
    }
}