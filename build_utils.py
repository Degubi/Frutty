import zipfile
import os

def zipdir(path, zipFile):
    for root, dirs, files in os.walk(path):
        for file in files:
            zipFile.write(os.path.join(root, file))