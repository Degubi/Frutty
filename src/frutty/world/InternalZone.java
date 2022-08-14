package frutty.world;

/**Interface for internal zones. When an internal zone is processed it is replaced with the zone returned by the getReplacementZone() method*/
public interface InternalZone {
    /**
     * Return the zone that needs to replace the zone. E.g: Player spawner zones are replaced by empty zones.
     * @return The zone to replace the internal zone
     */
    WorldZone getReplacementZone();
}