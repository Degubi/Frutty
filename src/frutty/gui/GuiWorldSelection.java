package frutty.gui;

import static frutty.tools.GuiHelper.*;

import frutty.*;
import frutty.gui.components.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.event.*;

public final class GuiWorldSelection {
    private static final String GENERATE_WORLD_LABEL = "Generate World";

    private GuiWorldSelection() {}

    public static void showWorldSelection() {
        System.out.println(Main.guiSystemLabel + "Switching to world selection frame");

        var panel = new JPanel(null);
        panel.setBackground(Color.GRAY);

        var worldList = new JList<String>();
        var worldPreviewImage = new JLabel();
        var devMode = newCheckBox("Show Dev Worlds", 180, 510, Color.BLACK, false);
        var coopBox = newCheckBox("Coop mode", 40, 510, Color.BLACK, false);

        worldList.setForeground(Color.BLACK);
        worldList.setBackground(Color.GRAY);
        worldList.addListSelectionListener(e -> handleWorldListChange(e, worldList, worldPreviewImage));
        devMode.addActionListener(e -> updateModel(worldList, devMode));

        updateModel(worldList, devMode);

        var pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, worldList, worldPreviewImage);
        pane.setEnabled(false);
        pane.setBounds(20, 20, 690, 480);
        pane.setBorder(GuiHelper.menuBorder);
        pane.setDividerLocation(200);

        panel.add(devMode);
        panel.add(coopBox);
        panel.add(pane);
        panel.add(newButton("Menu", 725, 475, e -> GuiMenu.switchMenuGui(GuiMenu.createMenuPanel())));
        panel.add(newButton("Play", 725, 550, e -> handlePlayButtonPress(worldList, coopBox)));

        GuiMenu.switchMenuGui(panel);
    }

    private static void handleWorldListChange(ListSelectionEvent event, JList<String> worldList, JLabel worldPreviewImage) {
        if(!event.getValueIsAdjusting()){
            var path = GamePaths.TEXTURES_DIR + "gui/" + worldList.getSelectedValue() + ".jpg";

            if(Files.exists(Path.of(path))) {
                var image = Material.loadTexture("gui", worldList.getSelectedValue() + ".jpg");
                var graph = image.createGraphics();
                graph.setColor(Color.BLACK);
                graph.fillRect(380, 420, 100, 60);
                graph.setColor(Color.WHITE);
                graph.setFont(GuiHelper.bigFont);
                graph.drawString(loadWorldSize(worldList.getSelectedValue()), 390, 460);
                graph.dispose();
                worldPreviewImage.setIcon(new ImageIcon(image));
            }else{
                worldPreviewImage.setIcon(new ImageIcon(GamePaths.TEXTURES_DIR + "gui/dev.jpg"));
            }
        }
    }

    private static void updateModel(JList<String> worldList, JCheckBox devMode) {
        var model = new DefaultListModel<String>();

        try(var worldsFolder = Files.list(Path.of(GamePaths.WORLDS_DIR))){
            var worldFiles = worldsFolder.map(Path::getFileName)
                                         .map(Path::toString)
                                         .filter(name -> name.endsWith(GamePaths.WORLD_FILE_EXTENSION))
                                         .map(name -> name.substring(0, name.indexOf('.')));

            var worldFilestoAdd = devMode.isSelected() ? worldFiles
                                                       : worldFiles.filter(name -> !name.startsWith("background")).filter(name -> !name.startsWith("dev_"));

            worldFilestoAdd.forEach(model::addElement);
        } catch (IOException e) {}

        System.out.println(Main.guiSystemLabel + "Listed " + model.getSize() + " worlds");
        model.addElement(GENERATE_WORLD_LABEL);

        worldList.setModel(model);
        worldList.setSelectedValue("Creepy", false);
    }

    private static void handlePlayButtonPress(JList<String> worldList, JCheckBox coopBox) {
        if(worldList.getSelectedValue().equals(GENERATE_WORLD_LABEL)) {
            GuiMenu.switchMenuGui(createGenerateWorldPanel());
        }else{
            World.load(worldList.getSelectedValue(), coopBox.isSelected());
            GuiIngame.showIngame();
            GuiMenu.closeMainFrame();
        }
    }

    public static String loadWorldSize(String fileName) {
        try(var input = new ObjectInputStream(Files.newInputStream(Path.of(GamePaths.WORLDS_DIR + fileName + GamePaths.WORLD_FILE_EXTENSION)))){
            input.readObject(); input.readObject();
            input.readUTF();
            return input.readShort() + "x" + input.readShort();

        } catch (IOException | ClassNotFoundException e) {
            return "Can't determine world size!";
        }
    }

    private static JPanel createGenerateWorldPanel() {
        var backgroundPanel = new GuiWorldBackground("dev_settings" + GamePaths.WORLD_FILE_EXTENSION);
        backgroundPanel.setLayout(null);

        var sizeField = new SettingButtonField("10x10", "World Size", 50, 20);
        backgroundPanel.add(sizeField);
        backgroundPanel.add(new SettingButton(false, "Enable Water", 50, 100));
        backgroundPanel.add(newButton("Menu", 725, 475, e -> GuiMenu.switchMenuGui(GuiMenu.createMenuPanel())));
        backgroundPanel.add(newButton("Play", 725, 550, e -> handleGeneratePlayButtonPress(sizeField.dataField)));

        return backgroundPanel;
    }

    private static void handleGeneratePlayButtonPress(JTextField sizeField) {
        var worldSize = sizeField.getText().split("x");

        World.generate(Integer.parseInt(worldSize[0]), Integer.parseInt(worldSize[1]), false);
        GuiIngame.showIngame();
        GuiMenu.closeMainFrame();
    }
}