Set Shell = CreateObject("WScript.Shell")
scriptPath = CreateObject("Scripting.FileSystemObject").GetParentFolderName(WScript.ScriptFullName)
Set link = Shell.CreateShortcut(Shell.SpecialFolders("Desktop") & "\Frutty.lnk")
link.Arguments = "-jar " & """" & scriptPath & "\Frutty.jar" & """"
link.TargetPath = scriptPath & "\bin\javaw.exe"
link.IconLocation = scriptPath & "\icon.ico"
link.WorkingDirectory = scriptPath
link.Save