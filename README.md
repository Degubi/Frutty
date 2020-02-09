### Menü:
  - Új játék kezdés
  - Régi játék betöltés
  - Statok és beállítások

### Letöltés, futtatás:
  - https://drive.google.com/uc?id=1n1y3h5OOTMKLNeTxZhhCq3PLYtu423t6&export=download
  - Unzipelni (érdemes pl. Program Files-ba)
  - Lekell futtatni a CreateShortcut.vbs-t
  - Az Asztalon levő parancsikonnal kell futtatni a programot

### Pályaszerkesztő plugin:
  - https://drive.google.com/uc?id=1MZedVi3QZSZK8gDicZ6XWlkpKjPQi4o8&export=download
  - A jar fájlt be kell tenni a játék mappájában levő plugins mappába
  - Menüben megjelenik az Editor gomb

### Pluginíráshoz:
  - Alap minta pl. A FruttyEditor plugin
  - A frutty.plugin mappában levő classokból érdemes elindulni. Egész sok példa és dokumentáció van
  - Generált plugin jar-ba kell egy MANIFEST.MF Plugin-Class attribútummal

### Buildeléshez:
  - Kell lennie Pythonnak telepítve és JDK-nak PATH-on
  - Lekell futtatni a build.py-t
  - A Frutty mappába kialakul a Google Drive-on található zip struktúrája