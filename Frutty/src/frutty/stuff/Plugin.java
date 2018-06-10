package frutty.stuff;

public final class Plugin {
	public static final Plugin pluginLoaderPlugin = new Plugin("Plugin Loader", "Base plugin loading module for Frutty", null, Version.from(1, 0, 0), null);
	
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