from subprocess import call
from distutils.dir_util import copy_tree as copydir
from urllib.request import urlretrieve as download
from inspect import cleandoc as format
from shutil import copyfile, rmtree as removedir
from os import mkdir, rename, remove as removefile

print("Creating jar file")
call(f"javac -d compile --module-path ../bin -proc:none src/editor/*.java src/editor/gui/*.java src/module-info.java")
call("jar cfm FruttyEditor.jar META-INF/MANIFEST.MF -C compile editor -C . META-INF -C compile module-info.class")
removedir("compile")
print("Done")