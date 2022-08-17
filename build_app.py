from subprocess import call
from shutil import rmtree, copytree, make_archive
from os import mkdir, remove, chmod
from stat import S_IWRITE

print('Collecting resources')
mkdir('resources')
copytree('worlds', 'resources/worlds')
copytree('sounds', 'resources/sounds')
copytree('textures', 'resources/textures')

call(['python', 'build_jar.py'], shell = True)

print('Building app')
call(('jpackage --type app-image --module-path Frutty.jar --module frutty.api/frutty.Main --input resources '
      '--name Frutty --vendor Degubi --description Frutty --icon icon.ico'))

print('Creating .zip file')
make_archive(base_name = 'Frutty', format = 'zip', base_dir = 'Frutty')

remove('Frutty.jar')
rmtree('resources')

def del_rw(_, name: str, __):
    chmod(name, S_IWRITE)
    remove(name)

rmtree('Frutty', onerror = del_rw)

print('\nDone!')