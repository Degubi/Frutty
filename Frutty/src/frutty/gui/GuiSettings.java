package frutty.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

@SuppressWarnings("boxing")
public final class GuiSettings extends JPanel implements ActionListener{
	private final JRadioButton easyButton = new JRadioButton("Easy"), normalButton = new JRadioButton("Normal"), hardButton = new JRadioButton("Hard");
	private final JCheckBox godMode = GuiHelper.newCheckBox("Enable God Mode", 400, 400, Settings.godEnabled);
	private final JCheckBox enemiesDisabled = GuiHelper.newCheckBox("Disable enemies", 400, 370, Settings.disableEnemies);
	private final JCheckBox showCollisionBoxes = GuiHelper.newCheckBox("Debug Collisions", 400, 340, Settings.debugCollisions);
	private final JCheckBox showDebug = GuiHelper.newCheckBox("Show Debug", 400, 310, Settings.showDebug);
	private final JTextField upKey = newTextField(Settings.upKey, 100, 245), downKey = newTextField(Settings.downKey, 100, 275);
	private final JTextField leftKey = newTextField(Settings.leftKey, 100, 305), rightKey = newTextField(Settings.rightKey, 100, 335);
	
	private final JSlider graphicsSlider = new JSlider(JSlider.HORIZONTAL, 0, 2, Settings.graphicsLevel);
	
	public GuiSettings() {
		setLayout(null);
		
		graphicsSlider.setPaintLabels(true);
		graphicsSlider.setBounds(300, 50, 150, 40);
		graphicsSlider.setOpaque(false);
		graphicsSlider.setSnapToTicks(true);
		
		Hashtable<Integer, JLabel> table = new Hashtable<>();
		table.put(0, new JLabel("Low"));
		table.put(1, new JLabel("Medium"));
		table.put(2, new JLabel("High"));
		graphicsSlider.setLabelTable(table);
		
		easyButton.setBounds(100, 20, 70, 30);
		normalButton.setBounds(100, 60, 80, 30);
		hardButton.setBounds(100, 100, 80, 30);
		
		ButtonGroup mapSelectorGroup = new ButtonGroup();
		easyButton.addActionListener(this);
		normalButton.addActionListener(this);
		hardButton.addActionListener(this);
		mapSelectorGroup.add(easyButton);
		mapSelectorGroup.add(normalButton);
		mapSelectorGroup.add(hardButton);
		
		if(Settings.difficulty == 1) {
			normalButton.setSelected(true);
		}else if(Settings.difficulty == 2) {
			hardButton.setSelected(true);
		}else{
			easyButton.setSelected(true);
		}
		
		easyButton.setOpaque(false);
		hardButton.setOpaque(false);
		normalButton.setOpaque(false);
		
		JButton butt = new JButton("Save");
		butt.setBounds(230, 400, 120, 30);
		butt.setMnemonic(100);
		butt.addActionListener(this);
		
		add(graphicsSlider);
		add(butt);
		add(easyButton);
		add(normalButton);
		add(hardButton);
		add(godMode);
		add(enemiesDisabled);
		add(showCollisionBoxes);
		add(showDebug);
		add(upKey);
		add(downKey);
		add(leftKey);
		add(rightKey);
	}
	
	private static JTextField newTextField(int code, int x, int y) {
		JTextField field = new JTextField(Character.toString((char)code));
		field.setBounds(x, y, 20, 20);
		field.setHorizontalAlignment(JTextField.CENTER);
		((AbstractDocument)field.getDocument()).setDocumentFilter(TextFilter.filter);
		return field;
	}
	
	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(0, 0, 576, 480);
		
		graphics.setColor(Color.DARK_GRAY);
		graphics.setFont(GuiHelper.thiccFont);
		graphics.drawString("Difficulty:", 20, 80);
		
		graphics.drawString("Player 2 Controls:", 20, 220);
		graphics.drawString("Up:", 40, 260);
		graphics.drawString("Down:", 40, 290);
		graphics.drawString("Left:", 40, 320);
		graphics.drawString("Right:", 40, 350);
		
		graphics.drawString("Debug:", 380, 300);
		
		graphics.drawString("Graphics level:", 320, 40);
	}
	
	public static void showGuiSettings() {
		Settings.loadSettings();
		GuiHelper.showNewFrame(new GuiSettings(), "Tutty Frutty Options", JFrame.DISPOSE_ON_CLOSE, 576, 480);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if(easyButton.isSelected()) {
			Settings.difficulty = 0;
		}
		if(normalButton.isSelected()) {
			Settings.difficulty = 1;
		}
		if(hardButton.isSelected()) {
			Settings.difficulty = 2;
		}
		
		Settings.showDebug = showDebug.isSelected();
		Settings.debugCollisions = showCollisionBoxes.isSelected();
		Settings.godEnabled = godMode.isSelected();
		Settings.graphicsLevel = graphicsSlider.getValue();
		Settings.disableEnemies = enemiesDisabled.isSelected();
		Settings.upKey = upKey.getText().charAt(0);
		Settings.downKey = downKey.getText().charAt(0);
		Settings.leftKey = leftKey.getText().charAt(0);
		Settings.rightKey = rightKey.getText().charAt(0);
		
		if(event.getActionCommand().equals("Save")) {
			Settings.saveSettings();
			((JFrame)getTopLevelAncestor()).dispose();
		}
		
		GuiMenu.menuGui.repaint();
	}
	
	private static final class TextFilter extends DocumentFilter{
		public static final TextFilter filter = new TextFilter();
		
		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			super.replace(fb, offset, length, text.toUpperCase(), attrs);
			if(offset > 0) {
				super.remove(fb, 0, 1);
			}
		}
	}
	
	public static final class Settings{
		public static int difficulty, upKey, downKey, leftKey, rightKey, graphicsLevel;
		public static boolean godEnabled, disableEnemies, debugCollisions, showDebug;
		public static String lastMap = "Creepy";
		
		public static void loadSettings() {
			try(BufferedReader input = new BufferedReader(new FileReader("settings.cfg"))){
				String[] data = input.readLine().split(" ");
				difficulty = Integer.parseInt(data[0]);
				godEnabled = Boolean.parseBoolean(data[1]);
				upKey = Integer.parseInt(data[2]);
				downKey = Integer.parseInt(data[3]);
				leftKey = Integer.parseInt(data[4]);
				rightKey = Integer.parseInt(data[5]);
				disableEnemies = Boolean.parseBoolean(data[6]);
				debugCollisions = Boolean.parseBoolean(data[7]);
				lastMap = data[8];
				showDebug = Boolean.parseBoolean(data[9]);
				graphicsLevel = Integer.parseInt(data[10]);
			} catch (IOException e) {
				try(PrintWriter output = new PrintWriter("settings.cfg")){
					output.print(0);
					output.print(' ');
					output.print(false);
					output.print(' ');
					output.print((int)'W');
					output.print(' ');
					output.print((int)'S');
					output.print(' ');
					output.print((int)'A');
					output.print(' ');
					output.print((int)'D');
					output.print(' ');
					output.print(false);
					output.print(' ');
					output.print(false);
					output.print(' ');
					output.print("Creepy");
					output.print(' ');
					output.print(false);
					output.print(' ');
					output.print(2);
				} catch (FileNotFoundException ex) {}
			}
		}
		
		public static void saveSettings() {
			try(PrintWriter output = new PrintWriter("settings.cfg")){
				output.print(difficulty);
				output.print(' ');
				output.print(godEnabled);
				output.print(' ');
				output.print(upKey);
				output.print(' ');
				output.print(downKey);
				output.print(' ');
				output.print(leftKey);
				output.print(' ');
				output.print(rightKey);
				output.print(' ');
				output.print(disableEnemies);
				output.print(' ');
				output.print(debugCollisions);
				output.print(' ');
				output.print(lastMap);
				output.print(' ');
				output.print(showDebug);
				output.print(' ');
				output.print(graphicsLevel);
			} catch (FileNotFoundException e) {}
		}
	}
}