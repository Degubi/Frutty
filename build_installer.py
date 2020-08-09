from subprocess import call
from shutil import rmtree, copytree
from os import mkdir, remove, rename

print('Creating resources folder')
mkdir('resources')
copytree('maps', 'resources/maps')
copytree('sounds', 'resources/sounds')
copytree('textures', 'resources/textures')

call(['python', 'build_jar.py'], shell = True)

print('Creating installer file')
call((r'"C:\Program Files\Java\jdk-14.0.2\bin\jpackage" --module-path Frutty.jar --module frutty.api/frutty.Main --input resources '
      r'--name Frutty --vendor Degubi --description Frutty --icon icon.ico '
      r'--win-per-user-install --win-dir-chooser --win-shortcut'))

rename('Frutty-1.0.exe', 'FruttyInstaller.exe')
remove('Frutty.jar')
rmtree('resources')

print('\nDone!')