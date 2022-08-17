package frutty.gui;

import frutty.tools.*;
import frutty.world.*;
import java.awt.event.*;
import javax.swing.*;

public final class GuiPauseMenu extends WindowAdapter implements ActionListener, KeyListener {
    public final JPanel panel = new JPanel(null);

    public GuiPauseMenu() {
        panel.setBackground(GuiHelper.color_84Black);
        panel.add(GuiHelper.newButton("Resume", 220, 180, this));
        panel.add(GuiHelper.newButton("Save", 220, 260, this));
        panel.add(GuiHelper.newButton("Menu", 220, 340, this));
        panel.add(GuiHelper.newButton("Exit", 220, 420, this));
        panel.add(GuiHelper.newLabel("Score: " + World.score, 220, 20));
        panel.add(GuiHelper.newLabel("Top score: " + GuiStats.topScore, 220, 80));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        var cmd = event.getActionCommand();

        if(cmd.equals("Resume")) {
            GuiIngame.unpause();
        }else if(cmd.equals("Exit")) {
            GuiIngame.showSaveQuestion();
            GuiStats.saveStats();
            System.exit(0);
        }else if(cmd.equals("Menu")) {
            GuiIngame.showSaveQuestion();
            GuiIngame.ingameFrame.dispose();
            GuiMainMenu.createMainFrame();
            GuiStats.saveStats();
            World.cleanUp();
        }else if(cmd.equals("Save")){
            GuiIngame.pause();
            World.createSave(JOptionPane.showInputDialog("Enter save name!"));
            GuiIngame.unpause();
            GuiStats.saveStats();
        }
        ((JFrame) panel.getTopLevelAncestor()).dispose();
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.VK_ESCAPE) {
            ((JFrame) panel.getTopLevelAncestor()).dispose();
            GuiIngame.unpause();
        }
    }

    @Override
    public void windowClosing(WindowEvent event) {
        GuiIngame.unpause();
    }

    @Override public void keyTyped(KeyEvent e) {} @Override public void keyReleased(KeyEvent e) {}
}