package frutty.gui.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import frutty.gui.GuiHelper;

public final class GuiProperties extends JPanel implements ActionListener{
	public static enum EnumProperty{
		MapName("Map Name", 0),
		Texture("Texture", 1),
		SkyTexture("SkyTexture", 2),
		IsBackground("Is Background?", 3),
		MapWidth("Map Width", 4),
		MapHeight("Map Height", 5),
		Player1PosX("Player1 Pos X", 6),
		Player1PosY("Player1 Pos Y", 7),
		Player2PosX("Player2 Pos X", 8),
		Player2PosY("Player2 Pos Y", 9);
		
		private final String propertyName;
		private final int index;
		
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
	
	public GuiProperties(String mapName, String textureName, String skyName, boolean isBackground, int[] data) {
		setLayout(null);
		
		table.setBorder(new LineBorder(Color.GRAY, 1, true));
		table.setBounds(20, 20, 300, 165);
		CustomCellRenderer render = new CustomCellRenderer();
		table.getColumnModel().getColumn(1).setCellRenderer(render);
		table.getColumnModel().getColumn(0).setCellRenderer(render);
		
		EnumProperty[] props = EnumProperty.values();
		
		props[0].register(table, mapName);
		props[1].register(table, textureName);
		props[2].register(table, skyName);
		props[3].register(table, String.valueOf(isBackground));
		props[4].register(table, data[0]);
		props[5].register(table, data[1]);
		
		props[6].register(table, data[2]);
		props[7].register(table, data[3]);
		props[8].register(table, data[4]);
		props[9].register(table, data[5]);
		
		add(GuiHelper.newButton("Texture Selector", 100, 250, 150, this));
		add(table);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getActionCommand().equals("Texture Selector")) {
			String[] textures = new File("./textures/map").list();
			GuiHelper.showNewFrame(new TextureSelector(textures, this), "Texture selector", JFrame.DISPOSE_ON_CLOSE, 200 + (textures.length - 1) * 128, 300 + ((textures.length / 5)) * 64);
		}
	}

	public void setMapTextureName(String texture) {
		table.setValueAt(texture, 1, 1);
	}
	
	public void setPlayer1Pos(int x, int y) {
		table.setValueAt(x, 6, 1);
		table.setValueAt(y, 7, 1);
	}
	
	public void setPlayer2Pos(int x, int y) {
		table.setValueAt(x, 8, 1);
		table.setValueAt(y, 9, 1);
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
	
	private static final class PropertyTableModel extends DefaultTableModel{
		@Override
		public int getRowCount() {
			return EnumProperty.values().length;
		}
		
		@Override
		public int getColumnCount() {
			return 2;
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 1 && (rowIndex == 0 || rowIndex == 2); 
		}
		
	}
	
	private static final class CustomCellRenderer extends DefaultTableCellRenderer{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if(row != 0 && row != 2) {
				cell.setForeground(Color.GRAY);
			}else{
				cell.setForeground(null); 	//Kell mert különben rákattintás után az összes szürke fontot kap...
			}
			return cell;
		}
	}
	
	private static final class TextureSelector extends JPanel implements ActionListener{
		private final GuiProperties mapProperties;
		
		public TextureSelector(String[] textures, GuiProperties props) {
			mapProperties = props;
			
			setLayout(null);
			
			int xPosition = 10, yPosition = 20, index = 0;
			
			JButton[] buttons = new JButton[textures.length - 1];
			
			for(String texture : textures) {
				if(texture.endsWith(".png")) {
					JButton button = new JButton();
					button.setActionCommand(texture.substring(0, texture.length() - 4));
					button.setBounds(xPosition, yPosition, 128, 128);
					xPosition += 138;
					
					if(xPosition > 600) {
						xPosition = 10;
						yPosition += 138;
					}
					button.addActionListener(this);
					buttons[index++] = button;
					add(button);
				}
			}
			
			new Thread(() -> {
				int index2 = 0;
				for(String texture : textures) {
					if(texture.endsWith(".png")) {
						buttons[index2++].setIcon(new ImageIcon(new ImageIcon("./textures/map/" + texture).getImage().getScaledInstance(128, 128, Image.SCALE_DEFAULT)));
					}
				}
			}).start();
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getSource() instanceof JButton) {
				mapProperties.setMapTextureName(event.getActionCommand());
				mapProperties.repaint();
				((JFrame)getTopLevelAncestor()).dispose();
			}
		}
	}
}