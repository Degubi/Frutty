/**
 * Frutty Module for plugin dev
 */
module frutty.api{
	requires java.base;
	requires javafx.swing;
	exports frutty.plugin;
	exports frutty.registry;
	exports frutty.map;
	exports frutty.map.interfaces;
	exports frutty.map.zones;
	exports frutty.entity;
}