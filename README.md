### Project:
  - Originally made as a University project
  - Could use some cleanup, but whatever
  - Supports SinglePlayer games
  - Has NPC-s
  - Save files
  - External plugin system
  - Level editor
  - Stats

### Installation, running:
  - Download 'FruttyInstaller' from 'Releases'.
  - Run 'FruttyInstaller' (don't install to ProgramFiles).
  - Start 'Frutty' from desktop.

### Level Editor plugin:
  - Download LevelEditor.zip from 'Releases'.
  - Open up the game, go to 'Plugins'
  - Press 'Install Plugins from Zip', browse for the plugin zip
  - Restart the game
  - The 'Level Editor' button should be visible inside the menu.
  - For the map sources download the 'mapsrc' folder from Github.
  - Move it to 'app/mapsrc'.
  - Browse it from the editor.

### Developer Console:
  - Add '-console' to the end of the 'target' option of the shortcut.
  - For available commands type 'list'.

### Building:
  - Needs at least java 14 installed
  - Needs Python installed (for running the installer)
  - Needs WiX Toolsed installed (for creating the installer)
  - Run the 'build_installer.py' file

### Plugin development:
  - The LevelEditorPlugin is an example.
  - The 'frutty.plugin' directory is a good starting point. It is well documented with lots of examples.
  - Every plugin jar needs a MANIFEST.MF file with the Plugin-Class attribute.
  - Run the 'build_jar.py' file to build only the game jar (add it to the classPath in the IDE)