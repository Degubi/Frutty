package frutty;

public final class Plugin{
	public final String description, name, pluginURL;
	public final String version;
	public boolean needsUpdate = false;
	
	public Plugin(String name, String description, String pluginURL, String version) {
	    this.name = name;
		this.description = description;
		this.pluginURL = pluginURL;
		this.version = version;
	}
		
	@Override
	public String toString() {
		return name;
	}
		
	public String getInfo() {
		return "<b><font color=white>Name: " + name +
		       "<br>Version: " + version +
		       "<br>URL: <a href=" + pluginURL + ">" + pluginURL + "</a>" +
		       "<br>Description: " + description;
	}
}