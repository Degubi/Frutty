package frutty.tools;

import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.*;

public final class Version{
	public final int majorVersion, minorVersion, patchVersion;
	public static final Version INVALID_VERSION = new Version(-1, -1, -1);
	
	private Version(int major, int minor, int patch) {
		majorVersion = major;
		minorVersion = minor;
		patchVersion = patch;
	}
	
	public static Version fromString(String versionString) {
		if(versionString == null || versionString.isEmpty()) {
			return INVALID_VERSION;
		}
		
		var split = versionString.split("\\.");
		if(split.length == 0) {
			return INVALID_VERSION;
		}else if(split.length == 1) {
			return new Version(Integer.parseInt(split[0]), 0, 0);
		}else if(split.length == 2) {
			return new Version(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0);
		}
		
		return new Version(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
	}
	
	public static Version from(int major, int minor, int patch) {
		return new Version(major, minor, patch);
	}
	
	public static Version fromURL(String url) {
		if(!url.isEmpty()) {
			try(InputStream stream = new URL(url).openConnection().getInputStream()){
				int first = stream.read() - 48;
				stream.read();
				int second = stream.read() - 48;
				stream.read();
				return new Version(first, second, stream.read() - 48);
			} catch (MalformedURLException e) {
				System.err.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ": Invalid url: " + url);
			} catch (IOException e) {}
		}
		return INVALID_VERSION;
	}
	
	public boolean isNewerThan(Version otherVersion) {
		if(this == INVALID_VERSION || otherVersion == INVALID_VERSION) {
			return false;
		}
		return (majorVersion > otherVersion.majorVersion || (majorVersion == otherVersion.majorVersion && minorVersion > otherVersion.minorVersion) || 
				(majorVersion == otherVersion.majorVersion && minorVersion == otherVersion.minorVersion && patchVersion > otherVersion.patchVersion));
	}
	
	public boolean isOlderThan(Version otherVersion) {
		if(this == INVALID_VERSION || otherVersion == INVALID_VERSION) {
			return false;
		}
		return (majorVersion < otherVersion.majorVersion || (majorVersion == otherVersion.majorVersion && minorVersion < otherVersion.minorVersion) || 
				(majorVersion == otherVersion.majorVersion && minorVersion == otherVersion.minorVersion && patchVersion < otherVersion.patchVersion));
	}
	
	public boolean isSameVersion(Version otherVersion) {
		if(this == INVALID_VERSION || otherVersion == INVALID_VERSION) {
			return false;
		}
		return majorVersion == otherVersion.majorVersion && minorVersion == otherVersion.minorVersion && patchVersion == otherVersion.patchVersion;
	}
	
	public boolean isValid() {
		return this != INVALID_VERSION;
	}
	
	@Override
	@Deprecated
	public boolean equals(Object obj) {
		return false;
	}
	
	@Override
	public String toString() {
		if(this == INVALID_VERSION) {
			return "Invalid Version";
		}
		return majorVersion + "." + minorVersion + "." + patchVersion;
	}
	
	@Override
	public int hashCode() {
		return 12 + (31 * majorVersion + minorVersion - patchVersion);
	}
}