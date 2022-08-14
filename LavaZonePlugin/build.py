from zipfile import ZipFile, ZIP_DEFLATED
from os import remove, walk
from subprocess import call
from shutil import rmtree

def zipdir(path: str, zipFile: ZipFile):
    for root, _, files in walk(path):
        for file in files:
            zipFile.write(root + '/' + file)

print('Creating jar file')
call('javac -d compile --module-path ../bin -proc:none src/lavazone/*.java src/lavazone/zones/*.java src/module-info.java')
call('jar cfm Plugin.jar META-INF/MANIFEST.MF -C compile lavazone -C . META-INF -C compile module-info.class')

print('Creating zip file')
with ZipFile('LavaZone.zip', 'w', ZIP_DEFLATED) as output:
    output.write('Plugin.jar')
    zipdir('textures', output)

rmtree('compile')
remove('Plugin.jar')

print('Done')