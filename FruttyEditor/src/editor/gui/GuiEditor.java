package editor.gui;

import static java.nio.file.StandardOpenOption.*;

import editor.gui.GuiEditorProperties.*;
import editor.gui.GuiTextureSelector.*;
import frutty.plugin.event.gui.*;
import frutty.tools.*;
import frutty.world.base.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public final class GuiEditor extends JPanel{
    public final List<EditorZoneButton> zoneButtons = new ArrayList<>();
    public final GuiEditorProperties mapProperties;
    public final TextureSelector textureSelector;
    public final JComboBox<String> zoneList = new JComboBox<>(MapZoneBase.zoneNames());
    
    private GuiEditor(String fileName, int width, int height, String skyName, String nextMap) {
        setLayout(null);
        
        if(fileName != null) {
            mapProperties = new GuiEditorProperties(fileName, skyName, width, height, nextMap);
            zoneList.setSelectedItem("normalZone");
            zoneList.setBounds(width * 64 + 20, 80, 120, 30);
            
            this.textureSelector = new TextureSelector(width, this);
            add(zoneList);
            add(textureSelector.button);
        }else{
            mapProperties = null;
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
    
    private void renderMap() {
        try(var output = new ObjectOutputStream(Files.newOutputStream(Path.of(GeneralFunctions.executionDir + "maps/" + mapProperties.mapName + ".fmap"), WRITE, CREATE, TRUNCATE_EXISTING))){
             var zoneIDCache = zoneButtons.stream().map(button -> button.zoneID).distinct().toArray(String[]::new);
             var textureCache = zoneButtons.stream().map(button -> button.zoneTexture).filter(texture -> texture != null).distinct().toArray(String[]::new);
             
             output.writeObject(zoneIDCache);
             output.writeObject(textureCache);
             output.writeUTF(mapProperties.skyName);
             output.writeShort(mapProperties.width);
             output.writeShort(mapProperties.height);
             output.writeUTF(mapProperties.nextMap);
             
             for(var writeButton : zoneButtons) {
                 output.writeByte(GeneralFunctions.indexOf(writeButton.zoneID, zoneIDCache));
                 
                 if(MapZoneBase.getZoneFromName(writeButton.zoneID) instanceof MapZoneTexturable) {
                     output.writeByte(GeneralFunctions.indexOf(writeButton.zoneTexture, textureCache));
                 }
             }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
         JOptionPane.showMessageDialog(null, "Map rendered as: " + mapProperties.mapName + ".fmap");
    }
    
    private void saveMap() {
        try(var output = Files.newBufferedWriter(Path.of(GeneralFunctions.executionDir + "mapsrc/" + mapProperties.mapName + ".fmf"), WRITE, CREATE, TRUNCATE_EXISTING)){
            output.write(Integer.toString(mapProperties.width) + '\n');
            output.write(Integer.toString(mapProperties.height) + '\n');
            output.write(mapProperties.skyName + '\n');
            output.write(mapProperties.nextMap + '\n');
            
            var textures = zoneButtons.stream()
                                      .map(button -> button.zoneTexture)
                                      .filter(texture -> texture != null)
                                      .distinct()
                                      .toArray(String[]::new);
            
            for(var texture : textures) {
                output.write(texture + ' ');
            }
            output.write("\n");
            
            for(var button : zoneButtons) {
                output.write(button.zoneID + (button.zoneTexture != null ? (" " + GeneralFunctions.indexOf(button.zoneTexture, textures)) : "") + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        JOptionPane.showMessageDialog(null, "Map saved as: " + mapProperties.mapName + ".fmf");
    }
    
    private static void loadMap(String fileName) {
        if(fileName != null && !fileName.isEmpty()) {
            try(var input = Files.newBufferedReader(Path.of(GeneralFunctions.executionDir + "mapsrc/" + fileName))){
                int mapWidth = Integer.parseInt(input.readLine());
                int mapHeight = Integer.parseInt(input.readLine());
                var editor = new GuiEditor(fileName.substring(0, fileName.indexOf('.')), mapWidth, mapHeight, input.readLine(), input.readLine());
                var textureCache = input.readLine().split(" ");
                
                for(int y = 0; y < mapHeight; ++y) {
                    for(int x = 0; x < mapWidth; ++x) {
                        var zoneData = input.readLine().split(" ");
                        var zoneFromName = MapZoneBase.getZoneFromName(zoneData[0]);
                        var button = new EditorZoneButton(zoneFromName.editorTexture.get(), zoneData[0], x * 64, y * 64, editor);
                        
                        if(zoneData.length == 2) {
                            int textureIndexFromCache = Integer.parseInt(zoneData[1]);
                            button.zoneTexture = textureCache[textureIndexFromCache];
                            button.guiButton.setIcon(((MapZoneTexturable)zoneFromName).textureVariants.get()[Material.materialRegistry.get(textureCache[textureIndexFromCache]).index]);
                        }
                        
                        editor.zoneButtons.add(button);
                        editor.add(button.guiButton);
                    }
                }
                
                showEditorFrame(editor, fileName, mapWidth * 64, mapHeight * 64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void newMap(GuiEditor editor) {
        String[] types = {"Normal", "Background"};
        var input = JOptionPane.showOptionDialog(null, "Make Normal or Background map?", "Map Type Chooser", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
        var mapSizeString = (input == 0 ? JOptionPane.showInputDialog("Enter map size!", "10x10") : "14x10").split("x");
        int mapWidth = Integer.parseInt(mapSizeString[0]), bigWidth = mapWidth * 64;
        int mapHeight = Integer.parseInt(mapSizeString[1]), bigHeight = mapHeight * 64;
        var mapName = JOptionPane.showInputDialog("Enter map name!", "mapname");
        var newEditor = new GuiEditor(mapName, mapWidth, mapHeight, "null", "null");
        
        if(mapName != null) {
            for(int yPos = 0; yPos < bigHeight; yPos += 64) {
                for(int xPos = 0; xPos < bigWidth; xPos += 64) {
                    EditorZoneButton button = new EditorZoneButton(MapZoneBase.normalZone.editorTexture.get(), "normalZone", xPos, yPos, newEditor);
                    button.zoneTexture = "normal";
                    newEditor.zoneButtons.add(button);
                    newEditor.add(button.guiButton);
                }
            }
        }
        showEditorFrame(newEditor, mapName, bigWidth, bigHeight);
        ((JFrame)editor.getTopLevelAncestor()).dispose();
    }
    
    private static void showEditorFrame(GuiEditor editor, String titleMapName, int width, int height) {
        EventQueue.invokeLater(() -> {
            var frame = new JFrame("Frutty Map Editor -- " + titleMapName);
            frame.setContentPane(editor);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setBounds(0, 0, width + 200, height + 63);
            frame.setLocationRelativeTo(null);
            frame.setIconImage(GuiHelper.frameIcon);
            
            var menuBar = new JMenuBar();
            var fileMenu = new JMenu("File");
            var mapMenu = new JMenu("Map");
            
            mapMenu.setEnabled(!editor.zoneButtons.isEmpty());
            
            fileMenu.add(newMenuItem("New map", 'N', true, event -> newMap(editor)));
            fileMenu.add(newMenuItem("Load map", 'L', true, event -> {
                var fileChooser = new JFileChooser("./mapsrc/");
                
                if(fileChooser.showOpenDialog(null) == 0) {
                    loadMap(fileChooser.getSelectedFile().getName());
                    ((JFrame)editor.getTopLevelAncestor()).dispose();
                }
            }));
            
            fileMenu.addSeparator();
            fileMenu.add(newMenuItem("Save map", 'S', !editor.zoneButtons.isEmpty(), event -> editor.saveMap()));
            fileMenu.add(newMenuItem("Render map", 'R', !editor.zoneButtons.isEmpty(), event -> editor.renderMap()));
            
            fileMenu.addSeparator();
            fileMenu.add(newMenuItem("Close map", '0', !editor.zoneButtons.isEmpty(), event -> {openEmptyEditor(); ((JFrame)editor.getTopLevelAncestor()).dispose();}));
            fileMenu.add(newMenuItem("Exit to menu", '0', true, event -> {frame.dispose(); GuiMenuEvent.openMainMenu();}));
            fileMenu.add(newMenuItem("Exit app", '0', true, event -> System.exit(0)));
            
            mapMenu.add(newMenuItem("Map Properties", 'P', true, event -> showNewGui(editor.mapProperties.panel, "Map Properties", 350, 350)));
            mapMenu.add(newMenuItem("Map Information", 'I', true, event -> showNewGui(new GuiEditorInfo(editor.zoneButtons), "Map Info", 350, 350)));
            
            menuBar.add(fileMenu);
            menuBar.add(mapMenu);
            frame.setJMenuBar(menuBar);
            frame.setFocusable(true);
            frame.setVisible(true);
        });
    }
    
    private static void showNewGui(Container panel, String name, int width, int height) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame(name);
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
}