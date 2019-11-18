from subprocess import call, DEVNULL
from distutils.dir_util import copy_tree as copydir
from urllib.request import urlretrieve as download
from inspect import cleandoc as format
from shutil import copyfile, rmtree as removedir
from os import mkdir, rename, remove as removefile

print("Generating runtime")
jlinkCommand = (r"jlink --module-path .;..\lib\app "
                 "--output ./Frutty/ "
                 "--add-modules java.desktop "
                 "--no-man-pages "
                 "--no-header-files "
				 "--compress=2")

call(jlinkCommand)

packages = ("src/frutty/*.java "
            "src/frutty/entity/*.java "
            "src/frutty/entity/effects/*.java "
            "src/frutty/entity/zone/*.java "
            "src/frutty/gui/*.java "
            "src/frutty/gui/components/*.java "
            "src/frutty/plugin/*.java "
            "src/frutty/plugin/event/gui/*.java "
            "src/frutty/plugin/event/world/*.java "
            "src/frutty/sound/*.java "
            "src/frutty/tools/*.java "
            "src/frutty/world/*.java "
            "src/frutty/world/base/*.java "
            "src/frutty/world/zones/*.java")

print("Creating jar file")
call(f"javac -d compile -proc:none {packages} src/module-info.java")
copydir("maps", "Frutty/maps")
copydir("sounds", "Frutty/sounds")
copydir("textures", "Frutty/textures")

manifest = "Main-Class: frutty.Main"

with open("Manifest.txt", "w") as manifestFile:
    manifestFile.write(format(manifest) + "\n")
    
call("jar cfm Frutty.jar Manifest.txt -C compile frutty -C . META-INF -C compile module-info.class")
rename("Frutty.jar", "./Frutty/Frutty.jar")
removefile("Manifest.txt")
removedir("compile")

copyfile("icon.ico", "./Frutty/icon.ico")

print("Creating Shortcut Creator")
call("pyinstaller shortcut.py --onefile", stderr = DEVNULL, stdin = DEVNULL)
rename("./dist/shortcut.exe", "./Frutty/CreateShortcut.exe")
removefile("shortcut.spec")
removedir("build")
removedir("dist")

print("Done")