package frutty.gui;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import frutty.Main;

public final class GuiPlugins implements ListSelectionListener, HyperlinkListener{
	protected final JList<Object> pluginList = new JList<>(Main.pluginList.toArray());
	protected final JTextPane description = new JTextPane();

	public static void showPlugins() {
		GuiPlugins plugs = new GuiPlugins();
		
		plugs.pluginList.addListSelectionListener(plugs);
		plugs.description.addHyperlinkListener(plugs);
		plugs.description.setEditable(false);
		plugs.description.setContentType("text/html");
		plugs.description.setText(Main.pluginList.get(0).getInfo());
		
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, plugs.pluginList, plugs.description);
		pane.setDividerLocation(285);
		pane.setEnabled(false);
		
		EventQueue.invokeLater(() -> {
			JFrame returnFrame = new JFrame("Frutty Plugins");
			returnFrame.setContentPane(pane);
			returnFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			returnFrame.setResizable(false);
			returnFrame.setBounds(0, 0, 576, 480);
			returnFrame.setLocationRelativeTo(null);
			returnFrame.setFocusable(true);
			returnFrame.setVisible(true);
		});
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		description.setText(null);
		description.setText(Main.pluginList.get(pluginList.getSelectedIndex()).getInfo());
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