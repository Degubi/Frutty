from os import environ, getcwd as getworkdir
from win32com.client import Dispatch

workdir = getworkdir()
shell = Dispatch("WScript.Shell")

shortcut = shell.CreateShortCut(environ["HOMEPATH"] + r"\Desktop\Frutty.lnk")
shortcut.Targetpath = workdir + r"\bin\javaw.exe"
shortcut.IconLocation = workdir + r"\icon.ico"
shortcut.Arguments = fr'-jar "{workdir}\Frutty.jar"'
shortcut.WorkingDirectory = workdir
shortcut.save()