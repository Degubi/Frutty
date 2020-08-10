package frutty.gui;

import static java.nio.file.StandardOpenOption.*;

import frutty.*;
import frutty.gui.components.*;
import frutty.tools.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.event.HyperlinkEvent.*;
import javax.swing.filechooser.*;

public final class GuiPlugins extends DefaultListCellRenderer implements HyperlinkListener{
    private GuiPlugins() {}
    
    public static void showPlugins() {
        var plugs = new GuiPlugins();
        var description = new JTextPane();
        var pluginList = new JList<>(Main.loadedPlugins);
        var backgroundPanel = new GuiMapBackground("maps/dev_settings.fmap");
        var uninstallPluginButton = GuiHelper.newButton("Uninstall Selected Plugins", 700, 120, e -> handleUninstallPluginButtonPress(pluginList, backgroundPanel));

        pluginList.addListSelectionListener(e -> onPluginListSelectionChange(description, pluginList, uninstallPluginButton));
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
        pluginPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 430));

        var bottomPanel = new JPanel(null);
        bottomPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 200));
        bottomPanel.setOpaque(false);
        bottomPanel.add(GuiHelper.newButton("Menu", 370, 114, e -> GuiMenu.switchMenuGui(GuiMenu.createMenuPanel())));
        bottomPanel.add(GuiHelper.newButton("Install Plugins from Zip", 700, 40, e -> handleInstallPluginButtonPress(backgroundPanel)));
        bottomPanel.add(uninstallPluginButton);

        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(pluginPanel, BorderLayout.NORTH);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        GuiMenu.switchMenuGui(backgroundPanel);
    }

    private static void onPluginListSelectionChange(JTextPane description, JList<Plugin> pluginList, JButton uninstallPluginButton) {
        var selectedPlugins = pluginList.getSelectedValuesList();
        var uninstallButtonVisible = selectedPlugins.stream().noneMatch(k -> k.name.equals("Frutty") || k.name.equals("Frutty Plugin Loader"));
        
        description.setText(selectedPlugins.size() == 1 ? selectedPlugins.get(0).getInfo() : null);
        uninstallPluginButton.setVisible(uninstallButtonVisible);
    }

    private static void handleInstallPluginButtonPress(JPanel parent) {
        var fileChooser = new JFileChooser(".");
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Zip files", "zip"));
        
        if(fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            var contentToWrite = Arrays.stream(fileChooser.getSelectedFiles())
                                       .map(File::getAbsolutePath)
                                       .map(k -> "I " + k)
                                       .collect(Collectors.joining("\n"));
            
            updatePluginManagementFile(contentToWrite);
            JOptionPane.showMessageDialog(parent, "Plugins will get installed only at next startup!\nNote: Don't change the path of the plugin zip file!");
        }
    }
    
    private static void handleUninstallPluginButtonPress(JList<Plugin> pluginList, JPanel parent) {
        var contentToWrite = pluginList.getSelectedValuesList().stream()
                                       .map(k -> "U " + k.pluginJarName)
                                       .collect(Collectors.joining("\n"));
        
        updatePluginManagementFile(contentToWrite);
        JOptionPane.showMessageDialog(parent, "Plugins will get uninstalled only at next startup!");
    }
    
    private static void updatePluginManagementFile(String content) {
        try {
            Files.writeString(Path.of(Main.executionDir + "pluginManagement.txt"), content + '\n' , WRITE, CREATE, APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
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