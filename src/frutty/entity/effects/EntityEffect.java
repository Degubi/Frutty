package frutty.entity.effects;

import frutty.entity.living.*;
import java.awt.*;

public abstract class EntityEffect {
    public int ticks;

    public EntityEffect(int duration) {
        ticks = duration;
    }

    public abstract void renderEffect(EntityPlayer player, Graphics graphics);
}