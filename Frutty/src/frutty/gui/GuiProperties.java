package frutty.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("boxing") 
public final class GuiProperties extends JPanel{
	private final PropertyTableModel table = new PropertyTableModel();
	
	public GuiProperties(String mapName, String textureName, int width, int height, int playerPosX, int playerPosY) {
		setLayout(null);
		
		JTable jTable = new JTable(table);
		jTable.setBorder(new LineBorder(Color.GRAY, 1, true));
		jTable.setBounds(20, 20, 300, 100);
		
		jTable.setValueAt("Map Name", 0, 0);
		jTable.setValueAt(mapName, 0, 1);
		jTable.setValueAt("Texture", 1, 0);
		jTable.setValueAt(textureName, 1, 1);
		jTable.setValueAt("Map Width", 2, 0);
		jTable.setValueAt(width, 2, 1);
		jTable.setValueAt("Map Height", 3, 0);
		jTable.setValueAt(height, 3, 1);
		jTable.setValueAt("Player Pos X", 4, 0);
		jTable.setValueAt(playerPosX, 4, 1);
		jTable.setValueAt("Player Pos Y", 5, 0);
		jTable.setValueAt(playerPosY, 5, 1);
		
		CustomCellRenderer render = new CustomCellRenderer();
		jTable.getColumnModel().getColumn(1).setCellRenderer(render);
		jTable.getColumnModel().getColumn(0).setCellRenderer(render);
		
		add(jTable);
	}
	
	public String getMapName() {
		return (String) table.getValueAt(0, 1);
	}
	public String getMapTextureName() {
		return (String) table.getValueAt(1, 1);
	}
	public int getMapWidth() {
		return (int) table.getValueAt(2, 1);
	}
	public int getMapHeight() {
		return (int) table.getValueAt(3, 1);
	}
	public int getPlayerPosX() {
		return (int) table.getValueAt(4, 1);
	}
	public int getPlayerPosY() {
		return (int) table.getValueAt(5, 1);
	}
	
	public void setPlayerPos(JButton button) {
		table.setValueAt(button.getX(), 4, 1);
		table.setValueAt(button.getY(), 5, 1);
	}
	
	private static final class PropertyTableModel extends DefaultTableModel{
		
		@Override
		public int getRowCount() {
			return 6;
		}
		@Override
		public int getColumnCount() {
			return 2;
		}
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex != 0 && rowIndex != 2 && rowIndex != 3 && rowIndex != 4 && rowIndex != 5;
		}
	}
	
	private static final class CustomCellRenderer extends DefaultTableCellRenderer{
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if(row != 0 && row != 1) {
				cell.setForeground(Color.GRAY);
			}else{
				cell.setForeground(null); 	//Kell mert különben rákattintás után az összes szürke fontot kap...
			}
			return cell;
		}
	}
}