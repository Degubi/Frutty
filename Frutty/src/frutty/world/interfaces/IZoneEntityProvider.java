package frutty.world.interfaces;

import frutty.entity.zone.EntityZone;

public interface IZoneEntityProvider {
	EntityZone getZoneEntity(int x, int y, int zoneIndex);
}