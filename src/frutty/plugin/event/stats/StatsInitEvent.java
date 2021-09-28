package frutty.plugin.event.stats;

import frutty.*;
import frutty.tools.*;
import javax.swing.tree.*;

/**Event is fired when stats menu is initialized. can new stats to existing categories or add new categories. Can access the main stats file too.
 * UI: Need to use GuiStatSavedEvent too to make sure stats are getting saved.
 */
@FruttyEventMarker
public final class StatsInitEvent {
    public final PropertyFile statsFile;
    private final DefaultMutableTreeNode basicNodes, zoneNodes, enemyNodes, topNode;

    public StatsInitEvent(PropertyFile stats, DefaultMutableTreeNode basic, DefaultMutableTreeNode zones, DefaultMutableTreeNode enemies, DefaultMutableTreeNode top) {
        statsFile = stats;
        basicNodes = basic;
        zoneNodes = zones;
        enemyNodes = enemies;
        topNode = top;
    }

    public void addNewBasicStat(String stat) {
        basicNodes.add(new DefaultMutableTreeNode(stat));
    }

    public void addNewZoneStat(String stat) {
        zoneNodes.add(new DefaultMutableTreeNode(stat));
    }

    public void addNewEnemyStat(String stat) {
        enemyNodes.add(new DefaultMutableTreeNode(stat));
    }

    public void addNewStatCategory(DefaultMutableTreeNode categoryNode) {
        topNode.add(categoryNode);
    }
}