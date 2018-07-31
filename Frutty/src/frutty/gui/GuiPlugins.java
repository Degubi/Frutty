package frutty.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import frutty.plugin.internal.Plugin;
import frutty.tools.GuiHelper;
import frutty.tools.Version;

public final class GuiPlugins implements ListSelectionListener, HyperlinkListener{
	protected final JList<Plugin> pluginList = new JList<>(Plugin.plugins.toArray(new Plugin[Plugin.plugins.size()]));
	protected final JTextPane description = new JTextPane();

	public static void showPlugins() {
		GuiPlugins plugs = new GuiPlugins();
		
		plugs.pluginList.addListSelectionListener(plugs);
		plugs.pluginList.setCellRenderer(new PluginListRenderer());
		plugs.description.addHyperlinkListener(plugs);
		plugs.description.setEditable(false);
		plugs.description.setContentType("text/html");
		plugs.pluginList.setSelectedIndex(0);
		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, plugs.pluginList, plugs.description);
		pane.setResizeWeight(0.5D);
		pane.setEnabled(false);
		
		GuiHelper.showNewGui(pane, "Frutty Plugins", 576, 480);
		
		new Thread(() -> {
			for(Plugin plugin : Plugin.plugins) {
				if(plugin.version.isOlderThan(Version.fromURL(plugin.versionURL))) {
					plugin.needsUpdate = true;
				}
			}
			pane.repaint();
		}).start();
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		description.setText(null);
		description.setText(Plugin.plugins.get(pluginList.getSelectedIndex()).getInfo());
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
			Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if(((Plugin) value).needsUpdate) {
				comp.setForeground(Color.RED);
			}
			return comp;
		}
	}
}