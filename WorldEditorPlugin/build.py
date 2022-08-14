from zipfile import ZipFile, ZIP_DEFLATED
from os import remove, walk, rename
from subprocess import call
from shutil import rmtree

def zipdir(path: str, zipFile: ZipFile):
    for root, _, files in walk(path):
        for file in files:
            zipFile.write(root + '/' + file)

print('Creating jar file')
call('javac -d compile --module-path ../bin -proc:none src/editor/*.java src/editor/gui/*.java src/module-info.java')
call('jar cfm Plugin.jar META-INF/MANIFEST.MF -C compile editor -C . META-INF -C compile module-info.class')

rename('../worldsrcs', 'worldsrcs')

print('Creating zip file')
with ZipFile('WorldEditor.zip', 'w', ZIP_DEFLATED) as output:
    output.write('Plugin.jar')
    zipdir('worldsrcs', output)

rmtree('compile')
rename('worldsrcs', '../worldsrcs')
remove('Plugin.jar')

print('Done')