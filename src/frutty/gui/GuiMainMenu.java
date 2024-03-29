package frutty.gui;

import static frutty.tools.GuiHelper.*;

import frutty.*;
import frutty.gui.components.*;
import frutty.plugin.event.gui.*;
import frutty.tools.*;
import frutty.world.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;

public final class GuiMainMenu {
    private static JFrame mainFrame;

    public static JPanel createMenuPanel() {
        var panel = new GuiWorldBackground("background" + Main.rand.nextInt(4));
        panel.setLayout(null);

        panel.add(newButton("New Game", 700, 20, e -> GuiWorldSelection.showWorldSelection()));
        panel.add(newButton("Exit", 370, 550, e -> System.exit(0)));
        panel.add(newButton("Settings", 700, 250, e -> GuiSettings.showGuiSettings()));
        panel.add(newButton("Load Save", 700, 100, e -> handleSaveLoading(panel)));
        panel.add(newButton("Plugins", 20, 400, e -> GuiPlugins.showPlugins()));
        panel.add(newButton("Stats", 700, 330, e -> GuiStats.showStatsGui()));

        var versionLabel = new JLabel("Version: " + Main.loadedPlugins[0].version);
        versionLabel.setBounds(20, 600, 100, 30);
        versionLabel.setForeground(Color.WHITE);
        versionLabel.setFont(GuiHelper.thiccFont);
        panel.add(versionLabel);

        if(Main.menuInitEvents.length > 0) {
            var eventComponents = new ArrayList<JComponent>(0);
            Main.invokeEvent(new GuiMenuEvent(eventComponents), Main.menuInitEvents);
            for(var comp : eventComponents) panel.add(comp);
        }

        return panel;
    }

    public static void closeMainFrame() {
        mainFrame.dispose();
    }

    public static void createMainFrame() {
        System.out.println(Main.guiSystemLabel + "Switching to menu frame");

        mainFrame = new JFrame("Frutty");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setIconImage(frameIcon);
        mainFrame.setBounds(0, 0, 910, 675);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setContentPane(createMenuPanel());
        mainFrame.setFocusable(true);
        mainFrame.setVisible(true);
    }

    public static void switchMenuGui(Container panel) {
        EventQueue.invokeLater(() -> {
            GuiMainMenu.mainFrame.setContentPane(panel);
            GuiMainMenu.mainFrame.revalidate();
        });
    }

    private static void handleSaveLoading(GuiWorldBackground panel) {
        try(var files = Files.list(Path.of(GamePaths.SAVES_DIR))){
            var saveFolderList = files.map(Path::getFileName).map(Path::toString).toArray(String[]::new);

            if(saveFolderList.length > 0) {
                var saveName = (String) JOptionPane.showInputDialog(panel, "Choose world file!", "Saves", JOptionPane.QUESTION_MESSAGE, null, saveFolderList, saveFolderList[0]);

                if(World.loadSave(saveName)) {
                    GuiIngame.showIngame();
                    ((JFrame)panel.getTopLevelAncestor()).dispose();
                }
            }else{
                JOptionPane.showMessageDialog(panel, "No saves found in saves directory");
            }
        } catch (IOException e) {}
    }
}