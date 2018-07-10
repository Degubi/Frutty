package frutty.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import frutty.Main;
import frutty.gui.editor.GuiEditor;
import frutty.map.Map;
import frutty.map.interfaces.IInternalZone;
import frutty.map.interfaces.ITexturable;
import frutty.map.interfaces.ITransparentZone;
import frutty.map.interfaces.MapZoneBase;
import frutty.tools.Version;

public final class GuiMenu extends JPanel implements ActionListener{
	private final MapZoneBase[] zones = new MapZoneBase[140];
	private final int[] xCoords = new int[140], yCoords = new int[140], textureData = new int[140];
	
	public static JFrame mainFrame;
	
	public GuiMenu() {
		setLayout(null);
		
		loadBackground();
		
		add(GuiHelper.newButton("New Game", 700, 20, this));
		add(GuiHelper.newButton("Exit", 370, 550, this));
		add(GuiHelper.newButton("Settings", 700, 250, this));
		add(GuiHelper.newButton("Load Save", 700, 100, this));
		add(GuiHelper.newButton("Editor", 20, 475, this));
		add(GuiHelper.newButton("Plugins", 20, 400, this));
		add(GuiHelper.newButton("Stats", 700, 330, this));
	}
	
	public static void createMainFrame(boolean checkUpdate) {
		GuiMenu menu = new GuiMenu();
		
		if(checkUpdate) {
			new Thread(() -> {
				if(Version.fromURL(Main.plugins[0].versionURL).isNewerThan(Main.plugins[0].version)) {
					JButton updaterButton = new JButton("Click here to Update...");
					updaterButton.setActionCommand("Update");
					updaterButton.setBounds(660, 600, 240, 40);
					updaterButton.addActionListener(menu);
					updaterButton.setBackground(Color.GREEN);
					updaterButton.setForeground(Color.RED);
					menu.add(updaterButton);
					menu.repaint();
				}
			}, "Menu Version Checker Thread").start();
		}
		
		EventQueue.invokeLater(() -> {
			mainFrame = new JFrame("Frutty");
			mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			mainFrame.setResizable(false);
			mainFrame.setBounds(0, 0, 910, 675);
			mainFrame.setLocationRelativeTo(null);
			mainFrame.setContentPane(menu);
			mainFrame.setFocusable(true);
			mainFrame.setVisible(true);
		});
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		
		for(int k = 0; k < zones.length; ++k) {
			MapZoneBase zone = zones[k];
			zone.render(xCoords[k], yCoords[k], textureData[k], (Graphics2D) graphics);
			if(zone instanceof ITransparentZone) {
				((ITransparentZone) zone).drawAfter(xCoords[k], yCoords[k], textureData[k], graphics);
			}
		}
		
		graphics.setColor(GuiHelper.color_84Black);
		graphics.fillRect(0, 0, 910, 675);
		graphics.setColor(Color.WHITE);
		graphics.setFont(GuiHelper.thiccFont);
		graphics.drawString("Version: " + Main.plugins[0].version, 10, 625);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		
		if(command.equals("New Game")){
			GuiHelper.switchMenuPanel(new GuiMapSelection());
		}else if(command.equals("Exit")){
			System.exit(0);
		}else if(command.equals("Settings")){
			Settings.showGuiSettings();
		}else if(command.equals("Plugins")) {
			GuiPlugins.showPlugins();
		}else if(command.equals("Editor")){
			GuiEditor.openEditor(); mainFrame.dispose();
		}else if(command.equals("Stats")){
			GuiHelper.showNewGui(new GuiStats(), "Frutty", 240, 180);
		}else if(command.equals("Update")){
			try {
				if(JOptionPane.showConfirmDialog(this, "Exiting game to Updater. Game will restart.", "Frutty Updater", JOptionPane.OK_CANCEL_OPTION) == 0) {
					Runtime.getRuntime().exec(System.getProperty("java.version").startsWith("10.") ? "javaw -jar bin/FruttyInstaller.jar"
							  																	   : "runtime\\bin\\javaw -jar bin/FruttyInstaller.jar");
					System.exit(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
		}else{ //Load
			var allMapNames = new File("./saves/").list();
			if(allMapNames.length > 0) {
				if(Map.loadSave((String) JOptionPane.showInputDialog(this, "Chose map file!", "Saves", JOptionPane.QUESTION_MESSAGE, null, allMapNames, allMapNames[0]))) {
					GuiIngame.showIngame();
					((JFrame)getTopLevelAncestor()).dispose();
				}
			}else{
				JOptionPane.showMessageDialog(this, "No saves found in saves directory");
			}
		}
	}
	
	private void loadBackground() {
		try(var input = new ObjectInputStream(new FileInputStream("./maps/background" + Main.rand.nextInt(4) + ".deg"))){
			String[] textures = new String[input.readByte()];
			
			for(int k = 0; k < textures.length; ++k) {
				textures[k] = input.readUTF();
			}
			
			int zoneIDCount = input.readByte();
			String[] zoneIDS = new String[zoneIDCount];
			
			for(int k = 0; k < zoneIDCount; ++k) {
				zoneIDS[k] = input.readUTF();
			}
			
			Main.loadTextures(textures);
			Main.loadSkyTexture(input.readUTF());
			
			input.readShort(); input.readShort();
			input.readShort(); input.readShort(); input.readShort(); input.readShort();
			
			int zoneIndex = 0;
			
			for(int y = 0; y < 640; y += 64) {
				for(int x = 0; x < 896; x += 64) {
					MapZoneBase zone = Main.getZoneFromName(zoneIDS[input.readByte()]);
					
					if(zone instanceof IInternalZone) {
						zone = ((IInternalZone) zone).getReplacementZone();
					}
					
					if(zone instanceof ITexturable) {
						textureData[zoneIndex] = input.readByte();
					}
					xCoords[zoneIndex] = x;
					yCoords[zoneIndex] = y;
					zones[zoneIndex++] = zone;
				}
			}
		}catch(IOException e){
		}
	}
}