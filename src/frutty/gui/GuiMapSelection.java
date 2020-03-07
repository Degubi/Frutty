package frutty.gui;

import static frutty.tools.GuiHelper.*;

import frutty.gui.components.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public final class GuiMapSelection{
    private GuiMapSelection() {}
    
    public static void showMapSelection() {
        var panel = new JPanel(null);
        panel.setBackground(Color.GRAY);
        
        var mapList = new JList<String>();
        var mapImage = new JLabel();
        var devMode = newCheckBox("Enable Dev Maps", 180, 510, Color.BLACK, false);
        var coopBox = newCheckBox("Coop mode", 40, 510, Color.BLACK, false);
        
        mapList.setForeground(Color.BLACK);
        mapList.setBackground(Color.GRAY);
        mapList.addListSelectionListener(e -> handleMapListChange(e, mapList, mapImage));
        devMode.addActionListener(e -> updateModel(mapList, devMode));
        
        updateModel(mapList, devMode);
        
        var pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapList, mapImage);
        pane.setEnabled(false);
        pane.setBounds(20, 20, 690, 480);
        pane.setBorder(GuiHelper.menuBorder);
        pane.setDividerLocation(200);
        
        panel.add(devMode);
        panel.add(coopBox);
        panel.add(pane);
        panel.add(newButton("Menu", 725, 475, e -> GuiMenu.switchMenuGui(GuiMenu.createMenuPanel())));
        panel.add(newButton("Play", 725, 550, e -> handlePlayButtonPress(mapList, coopBox)));
        
        GuiMenu.switchMenuGui(panel);
    }
    
    private static void handleMapListChange(ListSelectionEvent event, JList<String> mapList, JLabel mapImage) {
        if(!event.getValueIsAdjusting()){
            var path = "./textures/gui/" + mapList.getSelectedValue() + ".jpg";

            if(Files.exists(Path.of(path))) {
                var image = Material.loadTexture("gui", mapList.getSelectedValue() + ".jpg");
                var graph = image.createGraphics();
                graph.setColor(Color.BLACK);
                graph.fillRect(380, 420, 100, 60);
                graph.setColor(Color.WHITE);
                graph.setFont(GuiHelper.bigFont);
                graph.drawString(loadMapSize(mapList.getSelectedValue()), 390, 460);
                graph.dispose();
                mapImage.setIcon(new ImageIcon(image));
            }else{
                mapImage.setIcon(new ImageIcon("./textures/gui/dev.jpg"));
            }
        }
    }
    
    private static void updateModel(JList<String> mapList, JCheckBox devMode) {
        var model = new DefaultListModel<String>();
        var files = Arrays.stream(new File("./maps").list()).filter(name -> name.endsWith(".fmap")).map(name -> name.substring(0, name.indexOf('.')));
        
        if(devMode.isSelected()) {
            files.forEach(map -> model.addElement(map));
        }else{
            files.filter(name -> !name.startsWith("background")).filter(name -> !name.startsWith("dev_")).forEach(map -> model.addElement(map));
        }
        
        model.addElement("Generate Map");
        
        mapList.setModel(model);
        mapList.setSelectedValue("Creepy", false);
    }
    
    private static void handlePlayButtonPress(JList<String> mapList, JCheckBox coopBox) {
        if(mapList.getSelectedValue().equals("Generate Map")) {
            GuiMenu.switchMenuGui(createGenerateMapPanel());
        }else{
            World.loadMap(mapList.getSelectedValue(), coopBox.isSelected());
            GuiIngame.showIngame();
            GuiMenu.closeMainFrame();
        }
    }
    
    public static String loadMapSize(String fileName) {
        try(var input = new ObjectInputStream(Files.newInputStream(Path.of("./maps/" + fileName + ".fmap")))){
            input.readObject(); input.readObject();
            input.readUTF();
            return input.readShort() + "x" + input.readShort();
            
        } catch (IOException | ClassNotFoundException e) {
            return "Can't determine map size!";
        }
    }
    
    private static JPanel createGenerateMapPanel() {
        var sizeField = new JTextField("10x10");
        var panel = new JPanel(null);
        
        sizeField.setBounds(50, 50, 120, 40);
        panel.add(sizeField);
        
        panel.add(new SettingButton(false, "Enable Water", 50, 80));
        panel.add(newButton("Menu", 725, 475, e -> GuiMenu.switchMenuGui(GuiMenu.createMenuPanel())));
        panel.add(newButton("Play", 725, 550, e -> handleGeneratePlayButtonPress(sizeField)));
        
        return panel;
    }
    
    private static void handleGeneratePlayButtonPress(JTextField sizeField) {
        var mapSize = sizeField.getText().split("x");
        
        World.generateMap(Integer.parseInt(mapSize[0]), Integer.parseInt(mapSize[1]), false);
        GuiIngame.showIngame();
        GuiMenu.closeMainFrame();
    }
}