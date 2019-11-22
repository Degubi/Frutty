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

public final class GuiPlugins extends DefaultListCellRenderer implements HyperlinkListener{
	private GuiPlugins() {}
	
	public static void showPlugins() {
		var plugs = new GuiPlugins();
		var description = new JTextPane();
		var pluginList = new JList<>(Plugin.plugins.toArray(Plugin[]::new));

		pluginList.addListSelectionListener(e -> description.setText(Plugin.plugins.get(pluginList.getSelectedIndex()).getInfo()));
		pluginList.setCellRenderer(plugs);
		pluginList.setForeground(Color.WHITE);
		pluginList.setOpaque(false);
		pluginList.setBackground(new Color(0, 0, 0, 128));
		description.addHyperlinkListener(plugs);
		description.setEditable(false);
		description.setContentType("text/html");
		description.setOpaque(false);
		pluginList.setSelectedIndex(0);
		
		var pluginPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pluginList, description);
		pluginPanel.setResizeWeight(0.5D);
		pluginPanel.setOpaque(false);
		pluginPanel.setEnabled(false);
		pluginPanel.setBorder(null);
		pluginPanel.setDividerSize(0);
		
		var bottomPanel = new JPanel(null);
		bottomPanel.add(GuiHelper.newButton("Menu", 370, 14, e -> GuiMenu.switchMenuGui(GuiMenu.createMenuPanel())));
		pluginPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 530));
		bottomPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
		bottomPanel.setOpaque(false);
		
		var backgroundPanel = new GuiMapBackground("./maps/dev_settings.fmap");
		backgroundPanel.setLayout(new BorderLayout());
		backgroundPanel.add(pluginPanel, BorderLayout.NORTH);
		backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

		GuiMenu.switchMenuGui(backgroundPanel);
		
		new Thread(() -> {
			for(var plugin : Plugin.plugins) {
				if(plugin.version.isOlderThan(Version.fromURL(plugin.versionURL))) {
					plugin.needsUpdate = true;
				}
			}
			backgroundPanel.repaint();
		}).start();
	}

	@Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        var comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(((Plugin) value).needsUpdate) {
            comp.setForeground(Color.RED);
        }
        return comp;
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
}