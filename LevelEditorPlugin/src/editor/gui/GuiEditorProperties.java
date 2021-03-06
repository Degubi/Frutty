package editor.gui;

import frutty.tools.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

@SuppressWarnings("boxing")
public final class GuiEditorProperties extends DefaultTableCellRenderer {
    public final String mapName, skyName, nextMap;
    public final int width, height;
    public final JPanel panel = new JPanel(null);
    
    public GuiEditorProperties(String mapName, String skyName, int width, int height, String nextMap) {
        var table = new JTable(new PropertyTableModel());
        table.setBorder(new LineBorder(Color.GRAY, 1, true));
        table.setBounds(20, 20, 300, 165);
        
        table.getColumnModel().getColumn(1).setCellRenderer(this);
        table.getColumnModel().getColumn(0).setCellRenderer(this);
        
        table.setValueAt("Map Name", 0, 0);
        table.setValueAt(this.mapName = mapName, 0, 1);
        
        table.setValueAt("SkyTexture", 1, 0);
        table.setValueAt(this.skyName = skyName, 1, 1);
        
        table.setValueAt("Map Width", 2, 0);
        table.setValueAt(this.width = width, 2, 1);
        
        table.setValueAt("Map Height", 3, 0);
        table.setValueAt(this.height = height, 3, 1);
        
        table.setValueAt("Next Map", 4, 0);
        table.setValueAt(this.nextMap = nextMap, 4, 1);
        
        panel.add(table);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        var cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        cell.setForeground(row == 2 || row == 3 ? Color.GRAY : null);
        return cell;
    }
    
    static final class PropertyTableModel extends DefaultTableModel{
        
        @Override
        public int getRowCount() {
            return 5;
        }
        
        @Override
        public int getColumnCount() {
            return 2;
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1 && rowIndex != 2 && rowIndex != 3;
        }
    }
    
    static final class GuiEditorInfo extends JPanel{
        private final String textureCount, textureSize;
        
        public GuiEditorInfo(List<EditorZoneButton> buttons) {
            setLayout(null);
            
            var textures = buttons.stream()
                                  .filter(button -> button.zoneTexture != null)
                                  .map(button -> "./textures/map/" + button.zoneTexture + ".png")
                                  .distinct()
                                  .toArray(String[]::new);

            textureSize = "Texture size: " + Arrays.stream(textures).mapToInt(GuiEditorInfo::getFileSize).sum() + " bytes";
            textureCount = "Texture Count: " + textures.length;
            
            var textureList = new JList<>(textures);
            textureList.setBounds(60, 150, 200, 120);
            textureList.setBorder(GuiHelper.menuBorder);
            
            add(textureList);
        }
        
        private static int getFileSize(String filePath) {
            try {
                return (int) Files.size(Path.of(GeneralFunctions.getWorkdir() + filePath));
            } catch (IOException e) {
                return -1;
            }
        }
        
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            
            graphics.setFont(GuiHelper.thiccFont);
            graphics.drawString(textureCount, 20, 20);
            graphics.drawString(textureSize, 20, 40);
            graphics.drawString("Textures Used:", 20, 145);
        }
    }
}