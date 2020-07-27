from subprocess import call
from shutil import rmtree, copytree
from os import mkdir, path, walk, remove, rename

print('Creating resources folder')

mkdir('resources')
copytree('maps', 'resources/maps')
copytree('sounds', 'resources/sounds')
copytree('textures', 'resources/textures')

print('Creating Frutty.jar')

rawdirs = ['src\\frutty']
for root, subdirs, files in walk('src\\frutty\\'):
    for directory in subdirs:
        rawdirs.append(path.join(root, directory))

packages = ('\\*.java '.join(rawdirs) + '\\*.java').replace('\\', '/')

print('Creating jar file')

call(f'javac -d compile -proc:none {packages} src/module-info.java')

with open('Manifest.txt', 'w') as manifestFile:
    manifestFile.write('Main-Class: frutty.Main\n')

call('jar cfm Frutty.jar Manifest.txt -C compile frutty -C . META-INF -C compile module-info.class')

print('Creating installer file')

call((r'"C:\Program Files\Java\jdk-14.0.2\bin\jpackage" --module-path Frutty.jar --module frutty.api/frutty.Main --input resources '
      r'--name Frutty --vendor Degubi --description Frutty --icon icon.ico '
      r'--win-per-user-install --win-dir-chooser --win-shortcut'))

print('Cleaning up')

rename('Frutty-1.0.exe', 'FruttyInstaller.exe')
remove('Manifest.txt')
remove('Frutty.jar')
rmtree('compile')
rmtree('resources')

print('Done')