from zipfile import ZipFile, ZIP_DEFLATED
from os import remove
from subprocess import call
from shutil import rmtree

print('Creating jar file')
call('javac -d compile --module-path ../bin -proc:none src/editor/*.java src/editor/gui/*.java src/module-info.java')
call('jar cfm Plugin.jar META-INF/MANIFEST.MF -C compile editor -C . META-INF -C compile module-info.class')

print('Creating zip file')
with ZipFile('LevelEditor.zip', 'w', ZIP_DEFLATED) as output:
    output.write('Plugin.jar')

rmtree('compile')
remove('Plugin.jar')

print('Done')