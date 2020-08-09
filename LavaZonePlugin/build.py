from subprocess import call
from shutil import rmtree

print("Creating jar file")
call(f"javac -d compile --module-path ../bin -proc:none src/lavazone/*.java src/lavazone/zones/*.java src/module-info.java")
call("jar cfm FruttyLava.jar META-INF/MANIFEST.MF -C compile lavazone -C . META-INF -C compile module-info.class")
rmtree("compile")
print("Done")