package frutty.entity.effects;

import frutty.entity.*;
import java.awt.*;

public final class EntityEffectInvisible extends EntityEffect {
    private boolean animSwitch = false;
    
    public EntityEffectInvisible() {
        super(20);
    }
    
    @Override
    public void renderEffect(EntityPlayer player, Graphics graphics) {
        if(animSwitch) {
            graphics.setColor(Color.BLACK);
            graphics.fillRect(player.renderPosX, player.renderPosY, 64, 64);
        }
        animSwitch = !animSwitch;
    }
}