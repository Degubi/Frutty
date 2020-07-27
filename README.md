### Menu:
  - Start new game
  - Load game from save file
  - Stats & settings
  - Plugins

### Installation, running:
  - Download 'FruttyInstaller' from 'Releases'.
  - Run 'FruttyInstaller' (don't install to ProgramFiles).
  - Start 'Frutty' from desktop.

### Level Editor plugin:
  - Download FruttyEditor.jar from 'Releases'.
  - Move the jar file to the 'plugins' folder inside Frutty's 'app/plugins' directory.
  - The 'Editor' button should be visible inside the menu.
  - For the map sources download the 'mapsrc' folder from Github.
  - Move it to 'app/mapsrc'.
  - Browse it from the editor.

### Developer Console:
  - Add '-console' to the end of the 'target' option of the shortcut.
  - For available commands type 'list'.

### Building:
  - Needs jdk14 installed (build file has hardcoded java path for the moment, it will be changed as soon as jdk15 is out)
  - Needs Python installed
  - Run the 'build.py' file

### Plugin development:
  - The FruttyEditor is in itself a plugin.
  - The 'frutty.plugin' directory is a good starting point. It is well documented with lots of examples.
  - Every plugin jar needs a MANIFEST.MF file with the Plugin-Class attribute.