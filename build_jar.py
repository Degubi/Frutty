from subprocess import call
from shutil import rmtree
from os import path, walk, remove

print('Creating Frutty.jar')

rawdirs = ['src\\frutty']
for root, subdirs, files in walk('src\\frutty\\'):
    for directory in subdirs:
        rawdirs.append(path.join(root, directory))

packages = ('\\*.java '.join(rawdirs) + '\\*.java').replace('\\', '/')
call(f'javac -d compile -proc:none {packages} src/module-info.java')

with open('Manifest.txt', 'w') as manifestFile:
    manifestFile.write('Main-Class: frutty.Main\n')

call('jar cfm Frutty.jar Manifest.txt -C compile frutty -C . META-INF -C compile module-info.class')
remove('Manifest.txt')

rmtree('compile')