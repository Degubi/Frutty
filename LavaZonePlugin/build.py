import zipfile
import sys
import os
import subprocess
import shutil

def zipdir(path, zipFile):
    for root, dirs, files in os.walk(path):
        for file in files:
            zipFile.write(os.path.join(root, file))

print('Creating jar file')
subprocess.call('javac -d compile --module-path ../bin -proc:none src/lavazone/*.java src/lavazone/zones/*.java src/module-info.java')
subprocess.call('jar cfm Plugin.jar META-INF/MANIFEST.MF -C compile lavazone -C . META-INF -C compile module-info.class')

print('Creating zip file')
with zipfile.ZipFile('LavaZone.zip', 'w', zipfile.ZIP_DEFLATED) as output:
    output.write('Plugin.jar')
    zipdir('textures', output)

shutil.rmtree('compile')
os.remove('Plugin.jar')

print('Done')