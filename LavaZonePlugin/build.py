import zipfile
import sys
import os
import subprocess
import shutil

sys.path.append(os.getcwd() + '/..')
import build_utils

print('Creating jar file')
subprocess.call('javac -d compile --module-path ../bin -proc:none src/lavazone/*.java src/lavazone/zones/*.java src/module-info.java')
subprocess.call('jar cfm Plugin.jar META-INF/MANIFEST.MF -C compile lavazone -C . META-INF -C compile module-info.class')

print('Creating zip file')
with zipfile.ZipFile('LavaZone.zip', 'w', zipfile.ZIP_DEFLATED) as output:
    output.write('Plugin.jar')
    build_utils.zipdir('textures', output)

shutil.rmtree('compile')
os.remove('Plugin.jar')

print('Done')