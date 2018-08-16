package frutty.gui.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import frutty.gui.editor.GuiEditor.EditorZoneButton;
import frutty.tools.GuiHelper;
import frutty.tools.IOHelper;

@SuppressWarnings("boxing")
public final class GuiEditorProperties extends JPanel{
	public final String mapName, skyName, nextMap;
	public final int width, height;
	
	public GuiEditorProperties(String mapName, String skyName, int width, int height, String nextMap) {
		setLayout(null);
		JTable table = new JTable(new PropertyTableModel());
		table.setBorder(new LineBorder(Color.GRAY, 1, true));
		table.setBounds(20, 20, 300, 165);
		
		CustomCellRenderer renderer = new CustomCellRenderer();
		table.getColumnModel().getColumn(1).setCellRenderer(renderer);
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);
		
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
		
		add(table);
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
	
	static final class CustomCellRenderer extends DefaultTableCellRenderer{
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			var cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			cell.setForeground(row == 2 || row == 3 ? Color.GRAY : null);
			return cell;
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

			textureSize = "Texture size: " + Arrays.stream(textures).mapToInt(IOHelper::fileSize).sum() + " bytes";
	 		textureCount = "Texture Count: " + textures.length;
	 		
	 		JList<String> textureList = new JList<>(textures);
			textureList.setBounds(60, 150, 200, 120);
			textureList.setBorder(GuiHelper.menuBorder);
	 		
	 		add(textureList);
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