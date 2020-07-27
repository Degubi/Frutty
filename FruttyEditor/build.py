from subprocess import call
from shutil import rmtree

print("Creating jar file")
call(f"javac -d compile --module-path ../bin -proc:none src/editor/*.java src/editor/gui/*.java src/module-info.java")
call("jar cfm FruttyEditor.jar META-INF/MANIFEST.MF -C compile editor -C . META-INF -C compile module-info.class")
rmtree("compile")
print("Done")