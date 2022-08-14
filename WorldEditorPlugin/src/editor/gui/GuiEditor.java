package editor.gui;

import static java.nio.file.StandardOpenOption.*;

import editor.gui.GuiWorldProperties.*;
import editor.gui.GuiTextureSelector.*;
import frutty.plugin.event.gui.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public final class GuiEditor extends JPanel {
    public static final String WORLD_SRC_DIR = GamePaths.WORK_DIR + "worldsrcs/";
    public static final String WORLD_SRC_FILE_EXTENSION = ".fwsrc";

    public final List<EditorZoneButton> zoneButtons = new ArrayList<>();
    public final GuiWorldProperties worldProperties;
    public final TextureSelector textureSelector;
    public final JComboBox<String> zoneList = new JComboBox<>(WorldZone.zoneNames());

    private GuiEditor(String fileName, int width, int height, String skyName, String nextWorldName) {
        setLayout(null);

        if(fileName != null) {
            worldProperties = new GuiWorldProperties(fileName, skyName, width, height, nextWorldName);
            zoneList.setSelectedItem("normalZone");
            zoneList.setBounds(width * 64 + 20, 80, 120, 30);

            this.textureSelector = new TextureSelector(width, this);
            add(zoneList);
            add(textureSelector.button);
        }else{
            worldProperties = null;
            textureSelector = null;
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if(!zoneButtons.isEmpty()) {
            graphics.setFont(GuiHelper.thiccFont);
            graphics.drawString("Current texture: " + textureSelector.activeMaterial.name, getWidth() - 175, 190);
        }
    }

    public static void openEmptyEditor() {
        showEditorFrame(new GuiEditor(null, 0, 0, null, null), "", 800, 600);
        GuiMenuEvent.closeMainMenu();
    }

    private void renderWorld() {
        try(var output = new ObjectOutputStream(Files.newOutputStream(Path.of(GamePaths.WORLDS_DIR + worldProperties.worldName + GamePaths.WORLD_FILE_EXTENSION), WRITE, CREATE, TRUNCATE_EXISTING))){
             var zoneIDCache = zoneButtons.stream().map(button -> button.zoneID).distinct().toArray(String[]::new);
             var textureCache = zoneButtons.stream().map(button -> button.zoneTexture).filter(texture -> texture != null).distinct().toArray(String[]::new);

             output.writeObject(zoneIDCache);
             output.writeObject(textureCache);
             output.writeUTF(worldProperties.worldskyName);
             output.writeShort(worldProperties.worldWidth);
             output.writeShort(worldProperties.worldHeight);
             output.writeUTF(worldProperties.nextWorldName);

             for(var writeButton : zoneButtons) {
                 output.writeByte(indexOf(writeButton.zoneID, zoneIDCache));

                 if(WorldZone.getZoneFromName(writeButton.zoneID) instanceof WorldZoneTexturable) {
                     output.writeByte(indexOf(writeButton.zoneTexture, textureCache));
                 }
             }
        } catch (IOException e) {
            e.printStackTrace();
        }

         JOptionPane.showMessageDialog(null, "World rendered as: " + worldProperties.worldName + GamePaths.WORLD_FILE_EXTENSION);
    }

    private void saveWorldSource() {
        try(var output = Files.newBufferedWriter(Path.of(WORLD_SRC_DIR + worldProperties.worldName + WORLD_SRC_FILE_EXTENSION), WRITE, CREATE, TRUNCATE_EXISTING)) {
            output.write(worldProperties.worldWidth + "\n");
            output.write(worldProperties.worldHeight + "\n");
            output.write(worldProperties.worldskyName + "\n");
            output.write(worldProperties.nextWorldName + "\n");

            var textures = zoneButtons.stream()
                                      .map(button -> button.zoneTexture)
                                      .filter(texture -> texture != null)
                                      .distinct()
                                      .toArray(String[]::new);

            output.write(String.join(" ", textures) + "\n");

            for(var button : zoneButtons) {
                output.write(button.zoneID + (button.zoneTexture != null ? (" " + indexOf(button.zoneTexture, textures)) : "") + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, "World saved as: " + worldProperties.worldName + WORLD_SRC_FILE_EXTENSION);
    }

    private static void loadWorldSource(String fileName) {
        if(fileName != null && !fileName.isEmpty()) {
            try(var input = Files.newBufferedReader(Path.of(WORLD_SRC_DIR + fileName))) {
                var worldWidth = Integer.parseInt(input.readLine());
                var worldHeight = Integer.parseInt(input.readLine());
                var editor = new GuiEditor(fileName.substring(0, fileName.indexOf('.')), worldWidth, worldHeight, input.readLine(), input.readLine());
                var textureCache = input.readLine().split(" ");

                for(var y = 0; y < worldHeight; ++y) {
                    for(var x = 0; x < worldWidth; ++x) {
                        var zoneData = input.readLine().split(" ");
                        var zoneFromName = WorldZone.getZoneFromName(zoneData[0]);
                        var button = new EditorZoneButton(zoneFromName.editorTexture.get(), zoneData[0], x * 64, y * 64, editor);

                        if(zoneData.length == 2) {
                            var textureIndexFromCache = Integer.parseInt(zoneData[1]);
                            button.zoneTexture = textureCache[textureIndexFromCache];
                            button.guiButton.setIcon(((WorldZoneTexturable) zoneFromName).textureVariants.get()[Material.materialRegistry.get(textureCache[textureIndexFromCache]).index]);
                        }

                        editor.zoneButtons.add(button);
                        editor.add(button.guiButton);
                    }
                }

                showEditorFrame(editor, fileName, worldWidth * 64, worldHeight * 64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void newWorld(GuiEditor editor) {
        var worldTypes = new String[]{ "Normal", "Background" };
        var worldTypeIndex = JOptionPane.showOptionDialog(null, "Make Normal or Background world?", "World Type Picker", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, worldTypes, worldTypes[0]);
        var worldSizeString = (worldTypeIndex == 0 ? JOptionPane.showInputDialog("Enter world size!", "10x10") : "14x10").split("x");
        var worldWidth = Integer.parseInt(worldSizeString[0]);
        var worldHeight = Integer.parseInt(worldSizeString[1]);
        var bigWidth = worldWidth * 64;
        var bigHeight = worldHeight * 64;
        var worldName = JOptionPane.showInputDialog("Enter world name!", "worldname");
        var newEditor = new GuiEditor(worldName, worldWidth, worldHeight, "null", "null");

        if(worldName != null) {
            for(var yPos = 0; yPos < bigHeight; yPos += 64) {
                for(var xPos = 0; xPos < bigWidth; xPos += 64) {
                    var button = new EditorZoneButton(WorldZone.normalZone.editorTexture.get(), "normalZone", xPos, yPos, newEditor);
                    button.zoneTexture = "normal";
                    newEditor.zoneButtons.add(button);
                    newEditor.add(button.guiButton);
                }
            }
        }

        showEditorFrame(newEditor, worldName, bigWidth, bigHeight);
        ((JFrame)editor.getTopLevelAncestor()).dispose();
    }

    private static void showEditorFrame(GuiEditor editor, String worldName, int width, int height) {
        EventQueue.invokeLater(() -> {
            var frame = new JFrame("Frutty World Editor -- " + worldName);
            frame.setContentPane(editor);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setBounds(0, 0, width + 200, height + 63);
            frame.setLocationRelativeTo(null);
            frame.setIconImage(GuiHelper.frameIcon);

            var menuBar = new JMenuBar();
            var fileMenu = new JMenu("File");
            var worldMenu = new JMenu("World");

            worldMenu.setEnabled(!editor.zoneButtons.isEmpty());

            fileMenu.add(newMenuItem("New World", 'N', true, event -> newWorld(editor)));
            fileMenu.add(newMenuItem("Load World", 'L', true, event -> {
                var fileChooser = new JFileChooser(WORLD_SRC_DIR);

                if(fileChooser.showOpenDialog(null) == 0) {
                    loadWorldSource(fileChooser.getSelectedFile().getName());
                    ((JFrame) editor.getTopLevelAncestor()).dispose();
                }
            }));

            fileMenu.addSeparator();
            fileMenu.add(newMenuItem("Save World", 'S', !editor.zoneButtons.isEmpty(), event -> editor.saveWorldSource()));
            fileMenu.add(newMenuItem("Render World", 'R', !editor.zoneButtons.isEmpty(), event -> editor.renderWorld()));

            fileMenu.addSeparator();
            fileMenu.add(newMenuItem("Close World", '0', !editor.zoneButtons.isEmpty(), event -> { openEmptyEditor(); ((JFrame)editor.getTopLevelAncestor()).dispose(); }));
            fileMenu.add(newMenuItem("Exit to Menu", '0', true, event -> { frame.dispose(); GuiMenuEvent.openMainMenu(); }));
            fileMenu.add(newMenuItem("Exit Application", '0', true, event -> System.exit(0)));

            worldMenu.add(newMenuItem("World Properties", 'P', true, event -> showNewGui(editor.worldProperties.panel, "World Properties", 350, 350)));
            worldMenu.add(newMenuItem("World Information", 'I', true, event -> showNewGui(new GuiEditorInfo(editor.zoneButtons), "World Info", 350, 350)));

            menuBar.add(fileMenu);
            menuBar.add(worldMenu);
            frame.setJMenuBar(menuBar);
            frame.setFocusable(true);
            frame.setVisible(true);
        });
    }

    private static void showNewGui(Container panel, String name, int width, int height) {
        EventQueue.invokeLater(() -> {
            var frame = new JFrame(name);
            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setResizable(false);
            frame.setBounds(0, 0, width, height);
            frame.setIconImage(GuiHelper.frameIcon);
            frame.setLocationRelativeTo(null);
            frame.setFocusable(true);
            frame.setVisible(true);
        });
    }

    private static JMenuItem newMenuItem(String text, char shortcut, boolean setEnabled, ActionListener listener) {
        var item = new JMenuItem(text);

        if(shortcut != '0') {
            item.setAccelerator(KeyStroke.getKeyStroke(shortcut, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        }

        item.setEnabled(setEnabled);
        item.addActionListener(listener);
        return item;
    }

    private static<T> int indexOf(T value, T[] values) {
        for(var k = 0; k < values.length; ++k) {
            if(values[k].equals(value)) {
                return k;
            }
        }
        throw new IllegalArgumentException("Should not get there...");
    }
}