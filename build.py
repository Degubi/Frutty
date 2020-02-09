from subprocess import call, DEVNULL
from inspect import cleandoc as format
from shutil import copyfile, rmtree, copytree
from os import mkdir, path, rename, walk, remove

print("Generating runtime")
jlinkCommand = (r"jlink --module-path .;..\lib\app "
                 "--output ./Frutty/ "
                 "--add-modules java.desktop "
                 "--no-man-pages "
                 "--no-header-files "
				 "--compress=2")
  
call(jlinkCommand)

rawdirs = ["src\\frutty"]
for root, subdirs, files in walk("src\\frutty\\"):
    for directory in subdirs:
        rawdirs.append(path.join(root, directory))

packages = ("\\*.java ".join(rawdirs) + "\\*.java").replace("\\", "/")

print("Creating jar file")
call(f"javac -d compile -proc:none {packages} src/module-info.java")
copytree("maps", "Frutty/maps")
copytree("sounds", "Frutty/sounds")
copytree("textures", "Frutty/textures")

manifest = "Main-Class: frutty.Main"

with open("Manifest.txt", "w") as manifestFile:
    manifestFile.write(format(manifest) + "\n")

call("jar cfm Frutty.jar Manifest.txt -C compile frutty -C . META-INF -C compile module-info.class")
rename("Frutty.jar", "./Frutty/Frutty.jar")
remove("Manifest.txt")
rmtree("compile")

copyfile("icon.ico", "./Frutty/icon.ico")
copyfile("createShortcut.vbs", "./Frutty/createShortcut.vbs")

print("Done")