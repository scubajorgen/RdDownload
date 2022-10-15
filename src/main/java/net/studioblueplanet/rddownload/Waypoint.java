/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.rddownload;

/**
 * Waypoint representation
 * @author jorgen
 */
public class Waypoint
{
    public String gps;      // 1 for kernnetpunten, 0 for other
    public String xrd;      // Rijksdriehoeksmeting easting in m
    public String yrd;      // Rijksdriehoeksmeting northing in m
    public String name;     // Name of the reference point
    public String year;     // Year
}
