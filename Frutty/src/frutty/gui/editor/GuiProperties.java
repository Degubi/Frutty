package frutty.gui.editor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("boxing")
public final class GuiProperties extends JPanel{
	public static enum EnumProperty{
		MapName("Map Name", 0),
		SkyTexture("SkyTexture", 1),
		IsBackground("Is Background?", 2),
		MapWidth("Map Width", 3),
		MapHeight("Map Height", 4),
		Player1PosX("Player1 Pos X", 5),
		Player1PosY("Player1 Pos Y", 6),
		Player2PosX("Player2 Pos X", 7),
		Player2PosY("Player2 Pos Y", 8);
		
		protected static final EnumProperty[] props = values();
		
		protected final String propertyName;
		protected final int index;
		
		private EnumProperty(String name, int ind) {
			propertyName = name;
			index = ind;
		}
		
		public void register(JTable table, Object defaultValue) {
			table.setValueAt(propertyName, index, 0);
			table.setValueAt(defaultValue, index, 1);
		}
	}
	
	private final JTable table = new JTable(new PropertyTableModel());
	
	public GuiProperties(String mapName, String skyName, boolean isBackground, int[] data) {
		setLayout(null);
		
		table.setBorder(new LineBorder(Color.GRAY, 1, true));
		table.setBounds(20, 20, 300, 165);
		CustomCellRenderer render = new CustomCellRenderer();
		table.getColumnModel().getColumn(1).setCellRenderer(render);
		table.getColumnModel().getColumn(0).setCellRenderer(render);
		
		EnumProperty.props[0].register(table, mapName);
		EnumProperty.props[1].register(table, skyName);
		EnumProperty.props[2].register(table, String.valueOf(isBackground));
		EnumProperty.props[3].register(table, data[0]);
		EnumProperty.props[4].register(table, data[1]);
		
		EnumProperty.props[5].register(table, data[2]);
		EnumProperty.props[6].register(table, data[3]);
		EnumProperty.props[7].register(table, data[4]);
		EnumProperty.props[8].register(table, data[5]);
		
		add(table);
	}
	
	public void setPlayer1Pos(int x, int y) {
		table.setValueAt(x, 5, 1);
		table.setValueAt(y, 6, 1);
	}
	
	public void setPlayer2Pos(int x, int y) {
		table.setValueAt(x, 7, 1);
		table.setValueAt(y, 8, 1);
	}
	
	public boolean getBooleanProperty(EnumProperty prop) {
		return Boolean.parseBoolean((String) table.getValueAt(prop.index, 1));
	}
	
	public String getProperty(EnumProperty prop) {
		return (String) table.getValueAt(prop.index, 1);
	}
	
	public int getIntProperty(EnumProperty prop) {
		return (int) table.getValueAt(prop.index, 1);
	}
	
	protected static final class PropertyTableModel extends DefaultTableModel{
		@Override
		public int getRowCount() {
			return EnumProperty.props.length;
		}
		
		@Override
		public int getColumnCount() {
			return 2;
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 1 && rowIndex < 3; 
		}
	}
	
	protected static final class CustomCellRenderer extends DefaultTableCellRenderer{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if(row > 2) {
				cell.setForeground(Color.GRAY);
			}else{
				cell.setForeground(null); 	//Kell mert különben rákattintás után az összes szürke fontot kap...
			}
			return cell;
		}
	}
}