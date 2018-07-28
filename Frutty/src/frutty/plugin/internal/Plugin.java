package frutty.plugin.internal;

import frutty.tools.Version;

public final class Plugin{
	public final String description, ID, updateURL, versionURL;
	public final Version version;
	public boolean needsUpdate = false;
		
	public Plugin(String name, String desc, String url, Version ver, String verURL) {
		description = desc;
		ID = name;
		updateURL = url;
		version = ver;
		versionURL = verURL;
	}
		
	@Override
	public String toString() {
		return ID;
	}
		
	public String getInfo() {
		return "Name: " + ID + 
				"<br>Version: " + version + 
				"<br>URL: " + (updateURL == null ? "" : ("<a href=" + updateURL + ">" + updateURL + "</a>")) + 
				"<br>Needs update: <b><font color=" + (needsUpdate ? "red>" : "green>") + (needsUpdate ? "Yes" : "No") + "</font></b>" + (
				description != null ? ("<br>Description: " + description) : "");
	}
}