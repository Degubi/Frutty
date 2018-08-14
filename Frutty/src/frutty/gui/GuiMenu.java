package frutty.gui;

import static frutty.tools.GuiHelper.newButton;

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
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

import frutty.FruttyMain;
import frutty.plugin.event.gui.GuiMenuEvent;
import frutty.plugin.internal.EventHandle;
import frutty.plugin.internal.Plugin;
import frutty.tools.GuiHelper;
import frutty.tools.Material;
import frutty.tools.Version;
import frutty.world.World;
import frutty.world.base.IInternalZone;
import frutty.world.base.ITransparentZone;
import frutty.world.base.MapZoneBase;
import frutty.world.base.MapZoneTexturable;

public final class GuiMenu extends JPanel implements ActionListener{
	private final MapZoneBase[] zones = new MapZoneBase[140];
	private final int[] xCoords = new int[140], yCoords = new int[140];
	private final Material[] materials = new Material[140];
	private static final JTextArea devMessage = new JTextArea();
	
	public static JFrame mainFrame;
	
	public GuiMenu() {
		setLayout(null);
		
		loadBackgroundMap("./maps/background" + FruttyMain.rand.nextInt(4) + ".deg", xCoords, yCoords, materials, zones);
		
		if(devMessage.getText().isEmpty()) {
			new Thread(() -> {
				try(var input = new URL("https://pastebin.com/raw/tffU5Vu6").openStream()){
					byte[] kek = new byte[255];
					devMessage.setText(new String(kek, 0, input.readNBytes(kek, 0, 255)));
					
					devMessage.setEditable(false);
					devMessage.setForeground(Color.WHITE);
					devMessage.setBackground(new Color(0, 0, 0, 192));
					devMessage.setBorder(new CompoundBorder(new MatteBorder(0, 12, 0, 0, Color.YELLOW), new MatteBorder(4, 4, 4, 4, Color.LIGHT_GRAY)));
					devMessage.setBounds(20, 40, devMessage.getText().indexOf('\n') * 7, 80);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}, "Menu Dev Message Thread").start();
		}
		
		add(newButton("New Game", 700, 20, this));
		add(newButton("Exit", 370, 550, this));
		add(newButton("Settings", 700, 250, this));
		add(newButton("Load Save", 700, 100, this));
		add(newButton("Editor", 20, 475, this));
		add(newButton("Plugins", 20, 400, this));
		add(newButton("Stats", 700, 330, this));
		add(devMessage);
		
		if(FruttyMain.hasPlugins && !EventHandle.menuInitEvents.isEmpty()) {
			ArrayList<JButton> eventButtons = new ArrayList<>(0);
			EventHandle.handleEvent(new GuiMenuEvent(eventButtons), EventHandle.menuInitEvents);
			for(JButton butt : eventButtons) add(butt);
		}
	}
	
	public static void createMainFrame(boolean checkUpdate) {
		GuiMenu menu = new GuiMenu();
		
		if(checkUpdate) {
			new Thread(() -> {
				var netVersion = Version.fromURL(Plugin.plugins.get(0).versionURL);
				if(netVersion.isNewerThan(Plugin.plugins.get(0).version)) {
					JButton updaterButton = new JButton("New Update available: " + netVersion);
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
			zone.render(xCoords[k], yCoords[k], materials[k], (Graphics2D) graphics);
			if(zone instanceof ITransparentZone) {
				((ITransparentZone) zone).drawAfter(xCoords[k], yCoords[k], materials[k], graphics);
			}
		}
		
		graphics.setColor(GuiHelper.color_84Black);
		graphics.fillRect(0, 0, 910, 675);
		graphics.setColor(Color.WHITE);
		graphics.setFont(GuiHelper.thiccFont);
		graphics.drawString("Version: " + Plugin.plugins.get(0).version, 10, 625);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		
		if(command.equals("New Game")){
			GuiHelper.switchMenuPanel(new GuiMapSelection());
		}else if(command.equals("Exit")){
			System.exit(0);
		}else if(command.equals("Settings")){
			GuiSettings.showGuiSettings();
		}else if(command.equals("Plugins")) {
			GuiPlugins.showPlugins();
		}else if(command.equals("Editor")){
			GuiEditor.openEditor(); mainFrame.dispose();
		}else if(command.equals("Stats")){
			GuiHelper.switchMenuPanel(new GuiStats());
		}else{ //Load
			var allMapNames = new File("./saves/").list();
			if(allMapNames.length > 0) {
				if(World.loadSave((String) JOptionPane.showInputDialog(this, "Chose map file!", "Saves", JOptionPane.QUESTION_MESSAGE, null, allMapNames, allMapNames[0]))) {
					GuiIngame.showIngame();
					((JFrame)getTopLevelAncestor()).dispose();
				}
			}else{
				JOptionPane.showMessageDialog(this, "No saves found in saves directory");
			}
		}
	}
	
	public static void loadBackgroundMap(String mapName, int[] xCoords, int[] yCoords, Material[] materials, MapZoneBase[] zones) {
		try(var input = new ObjectInputStream(new FileInputStream(mapName))){
			String[] zoneIDCache = (String[]) input.readObject();
			String[] textureCache = (String[]) input.readObject();
			
			FruttyMain.loadTextures(textureCache);
			input.readUTF(); //Sky texture
			input.readShort(); input.readShort();  //Width height felesleges, 14x10 az összes
			input.readUTF(); //Next map
			
			for(int y = 0, zoneIndex = 0; y < 640; y += 64) {
				for(int x = 0; x < 896; x += 64) {
					MapZoneBase zone = FruttyMain.getZoneFromName(zoneIDCache[input.readByte()]);
					
					if(zone instanceof IInternalZone) {
						zone = ((IInternalZone) zone).getReplacementZone();
					}
					
					if(zone instanceof MapZoneTexturable) {
						materials[zoneIndex] = Material.materialRegistry.get(textureCache[input.readByte()]);
					}
					xCoords[zoneIndex] = x;
					yCoords[zoneIndex] = y;
					zones[zoneIndex++] = zone;
				}
			}
		}catch(IOException | ClassNotFoundException e){}
	}
}