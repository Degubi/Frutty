package frutty.entity.living;

import frutty.*;
import frutty.entity.*;
import frutty.entity.zone.*;
import frutty.world.*;
import java.awt.*;
import java.awt.image.*;

public abstract class EntityEnemy extends EntityBase {

    protected int textureIndex;
    private boolean animSwitch;
    private final BufferedImage[] textures;
    public boolean active = true;

    public EntityEnemy(int x, int y, BufferedImage[] textures) {
        super(x, y);

        this.textures = textures;
    }

    public static EntityEnemy create(int x, int y) {
        var rng = Main.rand.nextInt(4);

        return rng > 1 ? new EntityFastEnemy(x, y)
                       : new EntityNormalEnemy(x, y);
    }

    @Override
    public void render(Graphics graphics) {
        if(textureIndex == 3) {
            graphics.drawImage(textures[0], renderPosX + 64, renderPosY, -64, 64, null);
        }else{
            if(animSwitch || textureIndex == 0) {
                graphics.drawImage(textures[textureIndex], renderPosX, renderPosY, 64, 64, null);
            }else{
                graphics.drawImage(textures[textureIndex], renderPosX + 64, renderPosY, -64, 64, null);
            }
        }
    }

    @Override
    public void onKilled(EntityBase killer) {
        if(killer instanceof EntityPlayer || killer instanceof EntityApple) {
            World.score += 100;
        }

        active = false;
        super.onKilled(killer);
    }

    @Override
    public void updateClient() {
        checkPlayers();
    }

    @Override
    public void updateServer() {
        animSwitch = !animSwitch;
    }
}