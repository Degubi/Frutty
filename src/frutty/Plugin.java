package frutty;

public final class Plugin {
    public final String description;
    public final String name;
    public final String pluginURL;
    public final String version;
    public final String pluginJarName;
    
    public Plugin(String name, String description, String pluginURL, String version, String pluginJarName) {
        this.name = name;
        this.description = description;
        this.pluginURL = pluginURL;
        this.version = version;
        this.pluginJarName = pluginJarName == null ? null : pluginJarName.substring(0, pluginJarName.lastIndexOf('.'));
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