package frutty.gui;

import frutty.*;
import frutty.gui.components.*;
import frutty.tools.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.event.HyperlinkEvent.*;

public final class GuiPlugins extends GuiMapBackground implements HyperlinkListener{
	private GuiPlugins() {
		super("./maps/dev_settings.fmap");
		setLayout(new BorderLayout());
	}
	
	public static void showPlugins() {
		var plugs = new GuiPlugins();
		var description = new JTextPane();
		var pluginList = new JList<>(Plugin.plugins.toArray(Plugin[]::new));

		pluginList.addListSelectionListener(e -> description.setText(Plugin.plugins.get(pluginList.getSelectedIndex()).getInfo()));
		pluginList.setCellRenderer(new PluginListRenderer());
		description.addHyperlinkListener(plugs);
		description.setEditable(false);
		description.setContentType("text/html");
		pluginList.setSelectedIndex(0);
		
		var pluginPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pluginList, description);
		pluginPanel.setResizeWeight(0.5D);
		pluginPanel.setEnabled(false);
		pluginPanel.setBackground(Color.GRAY);
		
		var bottomPanel = new JPanel(null);
		bottomPanel.add(GuiHelper.newButton("Menu", 360, 15, e -> GuiHelper.switchGui(new GuiMenu())));
		pluginPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 530));
		bottomPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
		
		plugs.add(pluginPanel, BorderLayout.NORTH);
		plugs.add(bottomPanel, BorderLayout.SOUTH);

		pluginPanel.setOpaque(false);
		bottomPanel.setOpaque(false);
		
		GuiHelper.switchGui(plugs);
		
		new Thread(() -> {
			for(var plugin : Plugin.plugins) {
				if(plugin.version.isOlderThan(Version.fromURL(plugin.versionURL))) {
					plugin.needsUpdate = true;
				}
			}
			plugs.repaint();
		}).start();
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent event) {
		if(event.getEventType() == EventType.ACTIVATED) {
			try {
				Desktop.getDesktop().browse(event.getURL().toURI());
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected static final class PluginListRenderer extends DefaultListCellRenderer{
		
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			var comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if(((Plugin) value).needsUpdate) {
				comp.setForeground(Color.RED);
			}
			return comp;
		}
	}
}