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
	
	//0: width, 1: height, 2: p1X, 3: p1Y, 4: p2X, 5: p2Y
	public GuiProperties(String mapName, String textureName, boolean isBackground, int[] data) {
		setLayout(null);
		
		JTable jTable = new JTable(table);
		jTable.setBorder(new LineBorder(Color.GRAY, 1, true));
		jTable.setBounds(20, 20, 300, 130);
		
		jTable.setValueAt("Map Name", 0, 0);
		jTable.setValueAt(mapName, 0, 1);
		jTable.setValueAt("Texture", 1, 0);
		jTable.setValueAt(textureName, 1, 1);
		jTable.setValueAt("Is Background?", 2, 0);
		jTable.setValueAt(isBackground, 2, 1);
		
		
		jTable.setValueAt("Map Width", 3, 0);
		jTable.setValueAt(data[0], 3, 1);
		jTable.setValueAt("Map Height", 4, 0);
		jTable.setValueAt(data[1], 4, 1);
		
		jTable.setValueAt("Player1 Pos X", 5, 0);
		jTable.setValueAt(data[2], 5, 1);
		jTable.setValueAt("Player1 Pos Y", 6, 0);
		jTable.setValueAt(data[3], 6, 1);
		
		jTable.setValueAt("Player2 Pos X", 7, 0);
		jTable.setValueAt(data[4], 7, 1);
		jTable.setValueAt("Player2 Pos Y", 8, 0);
		jTable.setValueAt(data[5], 8, 1);
		
		
		
		CustomCellRenderer render = new CustomCellRenderer();
		jTable.getColumnModel().getColumn(1).setCellRenderer(render);
		jTable.getColumnModel().getColumn(0).setCellRenderer(render);
		
		add(jTable);
	}
	
	public boolean isBackgroundMap() {
		return (boolean) table.getValueAt(2, 1);
	}
	
	public String getMapName() {
		return (String) table.getValueAt(0, 1);
	}
	public String getMapTextureName() {
		return (String) table.getValueAt(1, 1);
	}
	public int getMapWidth() {
		return (int) table.getValueAt(3, 1);
	}
	public int getMapHeight() {
		return (int) table.getValueAt(4, 1);
	}
	public int getPlayer1PosX() {
		return (int) table.getValueAt(5, 1);
	}
	public int getPlayer1PosY() {
		return (int) table.getValueAt(6, 1);
	}
	public int getPlayer2PosX() {
		return (int) table.getValueAt(7, 1);
	}
	public int getPlayer2PosY() {
		return (int) table.getValueAt(8, 1);
	}
	
	public void setPlayer1Pos(JButton button) {
		table.setValueAt(button.getX(), 4, 1);
		table.setValueAt(button.getY(), 5, 1);
	}
	
	public void setPlayer2Pos(JButton button) {
		table.setValueAt(button.getX(), 6, 1);
		table.setValueAt(button.getY(), 7, 1);
	}
	
	private static final class PropertyTableModel extends DefaultTableModel{
		
		@Override
		public int getRowCount() {
			return 9;
		}
		@Override
		public int getColumnCount() {
			return 2;
		}
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 1 && (rowIndex == 0 || rowIndex == 1 || rowIndex == 2); 
		}
	}
	
	private static final class CustomCellRenderer extends DefaultTableCellRenderer{
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if(row != 0 && row != 1 && row != 2) {
				cell.setForeground(Color.GRAY);
			}else{
				cell.setForeground(null); 	//Kell mert különben rákattintás után az összes szürke fontot kap...
			}
			return cell;
		}
	}
}