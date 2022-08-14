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
public final class GuiWorldProperties extends DefaultTableCellRenderer {
    public final String worldName, worldskyName, nextWorldName;
    public final int worldWidth, worldHeight;
    public final JPanel panel = new JPanel(null);

    public GuiWorldProperties(String worldName, String worldSkyName, int worldWidth, int worldHeight, String nextWorldName) {
        var table = new JTable(new PropertyTableModel());
        table.setBorder(new LineBorder(Color.GRAY, 1, true));
        table.setBounds(20, 20, 300, 165);

        table.getColumnModel().getColumn(1).setCellRenderer(this);
        table.getColumnModel().getColumn(0).setCellRenderer(this);

        table.setValueAt("World Name", 0, 0);
        table.setValueAt(this.worldName = worldName, 0, 1);

        table.setValueAt("Sky Texture Name", 1, 0);
        table.setValueAt(this.worldskyName = worldSkyName, 1, 1);

        table.setValueAt("World Width", 2, 0);
        table.setValueAt(this.worldWidth = worldWidth, 2, 1);

        table.setValueAt("World Height", 3, 0);
        table.setValueAt(this.worldHeight = worldHeight, 3, 1);

        table.setValueAt("Next World Name", 4, 0);
        table.setValueAt(this.nextWorldName = nextWorldName, 4, 1);

        panel.add(table);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        var cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        cell.setForeground(row == 2 || row == 3 ? Color.GRAY : null);
        return cell;
    }

    static final class PropertyTableModel extends DefaultTableModel {

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

    static final class GuiEditorInfo extends JPanel {
        private final String textureCount, textureSize;

        public GuiEditorInfo(List<EditorZoneButton> buttons) {
            setLayout(null);

            var textures = buttons.stream()
                                  .filter(button -> button.zoneTexture != null)
                                  .map(button -> GamePaths.TEXTURES_DIR + "world/" + button.zoneTexture + ".png")
                                  .distinct()
                                  .toArray(String[]::new);

            textureCount = "Texture Count: " + textures.length;
            textureSize = "Texture Total Size: " + Arrays.stream(textures).mapToInt(GuiEditorInfo::getFileSize).sum() + " bytes";

            var textureList = new JList<>(textures);
            textureList.setBounds(60, 150, 200, 120);
            textureList.setBorder(GuiHelper.menuBorder);

            add(textureList);
        }

        private static int getFileSize(String filePath) {
            try {
                return (int) Files.size(Path.of(filePath));
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