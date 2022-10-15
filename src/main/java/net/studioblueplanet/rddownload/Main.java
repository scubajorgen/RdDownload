/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.rddownload;


/**
 * Main class. Executes the download and conversion.
 * @author jorgen
 */
public class Main
{
    /**
     * The main function
     * @param args Arguments, not used
     */
    public static void main(String[] args)
    {
        RdInfoParser parser=new RdInfoParser();
        
        parser.downloadAndParse();
    }
}
