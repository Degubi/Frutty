package editor.gui;

import frutty.world.base.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public final class EditorZoneButton extends MouseAdapter{
    public String zoneTexture, zoneID;
    public final JButton guiButton;
    private final GuiEditor editorInstance;
    
    public EditorZoneButton(ImageIcon texture, String zoneID, int x, int y, GuiEditor editor) {
        this.guiButton = new JButton(texture);
        this.editorInstance = editor;
        this.zoneID = zoneID;
        
        this.guiButton.setBounds(x, y, 64, 64);
        this.guiButton.addMouseListener(this);
        this.guiButton.setFocusable(false);
        this.guiButton.setBorder(new LineBorder(Color.DARK_GRAY, 1));
    }
    
    @Override
    public void mousePressed(MouseEvent event) {
        int pressedButton = event.getButton();
        
        if(pressedButton == MouseEvent.BUTTON1) {
            var activeZoneName = (String) editorInstance.zoneList.getSelectedItem();
            zoneID = activeZoneName;
            guiButton.setIcon(MapZoneBase.getZoneFromName(activeZoneName).editorTexture.get());
            
            if(MapZoneBase.getZoneFromName(activeZoneName) instanceof MapZoneTexturable) {
                guiButton.setIcon(((MapZoneTexturable)MapZoneBase.getZoneFromName(zoneID)).textureVariants.get()[editorInstance.textureSelectorButton.activeMaterial.index]);
                zoneTexture = editorInstance.textureSelectorButton.activeMaterial.name;
            }else{
                zoneTexture = null;
            }
            
        }else if(pressedButton == MouseEvent.BUTTON3) {
            if(MapZoneBase.getZoneFromName(zoneID) instanceof MapZoneTexturable) {
                guiButton.setIcon(((MapZoneTexturable)MapZoneBase.getZoneFromName(zoneID)).textureVariants.get()[editorInstance.textureSelectorButton.activeMaterial.index]);
                zoneTexture = editorInstance.textureSelectorButton.activeMaterial.name;
            }
        }
    }
}