### Project:
  - Originally made as a University project (meaning it doesn't use Maven or Gradle or any external dependencies)
  - Supports SinglePlayer games & local Co-Op games
  - Has NPC-s
  - Save files
  - External plugin system
  - Level editor
  - Stats
  - Could use some cleanup, but whatever

![image](https://user-images.githubusercontent.com/13366932/179262945-a89cc1f1-f20b-4742-9efb-7928cb20a503.png)
![image](https://user-images.githubusercontent.com/13366932/179263103-df48e5fc-4d3c-4631-ae7e-2d576bc584b4.png)

### Installation, running:
  - Download 'Frutty.zip' from 'Releases'
  - Unzip it somewhere
  - Start 'Frutty.exe'

### Level Editor plugin:
  - Download LevelEditor.zip from 'Releases'
  - Open up the game, go to 'Plugins'
  - Press 'Install Plugins from Zip', browse for the plugin zip
  - Restart the game
  - The 'Level Editor' button should be visible inside the menu
  - For the map sources download the 'mapsrc' folder from Github
  - Move it to 'app/mapsrc'
  - Browse it from the editor

![image](https://user-images.githubusercontent.com/13366932/179267340-235e7b63-779e-4269-b6f5-27d7c4bf9d22.png)

### Developer Console:
  - Add '-console' to the end of the 'target' option of the shortcut
  - For available commands type 'list'

![image](https://user-images.githubusercontent.com/13366932/179267589-4fcd2232-db78-4b4e-beeb-1a53f66f729c.png)

### Building:
  - Needs Java installed
  - Needs Python installed (for creating the application .zip)
  - Needs WiX Toolset installed (for creating the application .exe)
  - Run the 'build_app.py' file

### Plugin development:
  - The LevelEditorPlugin & LavaZonePlugin plugins are examples
  - The 'frutty.plugin' directory is a good starting point. It is well documented with lots of examples
  - Every plugin jar needs a MANIFEST.MF file with the Plugin-Class attribute
  - Run the 'build_jar.py' file to build only the game jar so you can add it as a library in the IDE