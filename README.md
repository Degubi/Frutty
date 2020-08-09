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
  - Download FruttyLevelEditor.jar from 'Releases'.
  - Move the jar file to the 'plugins' folder inside Frutty's 'app/plugins' directory.
  - The 'Level Editor' button should be visible inside the menu.
  - For the map sources download the 'mapsrc' folder from Github.
  - Move it to 'app/mapsrc'.
  - Browse it from the editor.

### Developer Console:
  - Add '-console' to the end of the 'target' option of the shortcut.
  - For available commands type 'list'.

### Building:
  - Needs jdk14 installed (build file has hardcoded java path for the moment, it will be changed as soon as jdk15 is out)
  - Needs Python installed
  - Run the 'build_installer.py' file

### Plugin development:
  - The LevelEditorPlugin is an example.
  - The 'frutty.plugin' directory is a good starting point. It is well documented with lots of examples.
  - Every plugin jar needs a MANIFEST.MF file with the Plugin-Class attribute.
  - Run the 'build_jar.py' file to build only the game jar (add it to the classPath in the IDE)