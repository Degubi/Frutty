package frutty.world;

import frutty.*;
import frutty.tools.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public final class Particle implements Serializable{
    private static final long serialVersionUID = -9182849456014867036L;

    public final Color color;
    public int lifeTime, posX, posY;
    public final int motionX, motionY;
    
    public Particle(int x, int y, int motionX, int motionY, Color color) {
        this.posX = x;
        this.posY = y;
        this.motionX = motionX;
        this.motionY = motionY;
        this.lifeTime = 25 + Main.rand.nextInt(20);
        this.color = color;
    }
    
    public void render(Graphics graphics) {
        var posXLocal = posX;
        var posYLocal = posY;

        graphics.setColor(color);
        graphics.fillRect(posXLocal, posYLocal, 4, 4);
        graphics.setColor(GuiHelper.color_84Black);
        
        //Depth render
        for(int till = posYLocal / 240, k = 0; k < till && k < 4; ++k) {
            graphics.fillRect(posXLocal, posYLocal, 4, 4);
        }
    }
    
    public final void renderDebug(Graphics graphics) {
        render(graphics);
        
        graphics.setColor(Color.WHITE);
        graphics.drawRect(posX, posY, 4, 4);
    }
    
    public void update(Iterator<Particle> iterator) {
        posY += motionY;
        posX += motionX;
        
        if(--lifeTime == 0) {
            iterator.remove();
        }
    }
}