/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.rddownload;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import net.studioblueplanet.mapdatumconvert.DatumCoordinate;
import net.studioblueplanet.mapdatumconvert.LatLonCoordinate;
import net.studioblueplanet.mapdatumconvert.MapDatumConvert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * This class downloads the RD Info dataset from PDOK and converts
 * the reference points to the OziExplorer waypoint file
 * @author jorgen
 */
public class RdInfoParser
{
 
    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String POST_PARAMS = "userName=JohnDoe";    

    /**
     * Constructor
     */
    public RdInfoParser()
    {
    }

    /**
     * Executes a HTTP request
     * @param getUrl The URL to request
     * @return The response as list of strings
     * @throws IOException 
     */
    private StringBuffer sendGet(String getUrl) throws IOException 
    {
        StringBuffer response = new StringBuffer();
        URL obj = new URL(getUrl);
        HttpURLConnection con = (HttpURLConnection)obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) 
        { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) 
            {
                response.append(inputLine);
            }
            in.close();
        } 
        else 
        {
            System.err.println("GET request not worked");
        }
        return response;
    }
    
    /**
     * Parse the XML response from the RD Info service using XPath queries
     * @param doc The response
     * @param wpts Master list of waypoints to add the newly converted waypoints to
     * @throws Exception If something went wrong
     */
    private void parseResponse(Document doc, List<Waypoint> wpts) throws Exception
    {
        NamespaceResolver   nsc;
        Waypoint            wpt;
        
        XPath xpath = XPathFactory.newInstance().newXPath();
        nsc=new NamespaceResolver(doc);
        xpath.setNamespaceContext(nsc);

        XPathExpression exprMember  = xpath.compile("/wfs:FeatureCollection/wfs:member/rdinfo:punten");
        XPathExpression exprName    = xpath.compile("rdinfo:benaming/text()");
        XPathExpression exprYear    = xpath.compile("rdinfo:uitgavejaarext/text()");
        XPathExpression exprXrd     = xpath.compile("rdinfo:xrd/text()");
        XPathExpression exprYrd     = xpath.compile("rdinfo:yrd/text()");
        XPathExpression exprGps     = xpath.compile("rdinfo:gps/text()");
        NodeList nodes = (NodeList)exprMember.evaluate(doc, XPathConstants.NODESET);
        System.out.println("Got " + nodes.getLength() + " nodes");
        int j=0;
        while (j<nodes.getLength())
        {
            wpt=new Waypoint();
            wpt.name     = (String)exprName.evaluate((Element)nodes.item(j), XPathConstants.STRING);
            wpt. year    = (String)exprYear.evaluate((Element)nodes.item(j), XPathConstants.STRING);
            wpt.xrd      = (String)exprXrd.evaluate((Element)nodes.item(j), XPathConstants.STRING);
            wpt.yrd      = (String)exprYrd.evaluate((Element)nodes.item(j), XPathConstants.STRING);
            wpt.gps      = (String)exprGps.evaluate((Element)nodes.item(j), XPathConstants.STRING);
            wpts.add(wpt);
            j++;
        }
    }

    /**
     * Parse the XML into a document
     * @param xml XML string
     * @return The document
     * @throws Exception 
     */
    private Document parseXml(String xml) throws Exception
    {
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         dbFactory.setNamespaceAware(true);
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
         return doc;
    }    
    
    /**
     * Writes the waypoints to file in the Ozi wpt format, except that the
     * coordinates are writen as RD coordinates.
     * @throws Exception When something goes wrong
     */
    private void writeFile(List<Waypoint> wpts) throws Exception
    {
        MapDatumConvert     mdc         =new MapDatumConvert();
        FileWriter          myWriter;
        DatumCoordinate     dc;
        LatLonCoordinate    llc;
        String              fileName;
        
        fileName="RdInfo";
        fileName+=new SimpleDateFormat("yyyyMMdd").format(new Date());
        fileName+=".wpt";
        myWriter= new FileWriter(fileName);
        
        myWriter.write("OziExplorer Waypoint File Version 1.1\n");
        myWriter.write("WGS 84\n");
        myWriter.write("Reserved 2\n");
        myWriter.write("garmin\n");       
        
        int wptCount=1;
        for(Waypoint wpt : wpts)
        {
            dc          =new DatumCoordinate();
            dc.easting  =Double.parseDouble(wpt.xrd);
            dc.northing =Double.parseDouble(wpt.yrd);
            llc         =mdc.rdToWgs84(dc);
            
            myWriter.write(Integer.toString(wptCount)+","+wpt.name.replace(",", ":")+"-"+wpt.year+","+llc.phi+","+llc.lambda+",44848.7500,");
            if (wpt.gps.equals("1"))
            {
                myWriter.write("165");
            }
            else
            {
                myWriter.write("167");
            }
            myWriter.write(",0,3,0,65535,,0,0,0,-777,6,0,17,0,10,2,,,,120\n"); 
            wptCount++;
        }
        
        myWriter.flush();
        myWriter.close();        
    }
    
    /**
     * The main function: downloads the RD Info and parses the responss.
     */
    public void downloadAndParse()
    {
        List<Waypoint>  waypoints;
        StringBuffer    response;
        Document        doc;
        NodeList        list;
        String          url="https://service.pdok.nl/kadaster/rdinfo/wfs/v1_0?request=GetFeature&service=WFS&version=2.0.0&typenames=punten";
        NamespaceResolver  nsc;
        
        waypoints=new ArrayList<>();
        try
        {
            while (url!=null && !url.equals(""))
            {
                response=sendGet(url);
                doc=parseXml(response.toString());
                System.out.println("Doc "+doc.getDocumentElement().getNodeName());
                System.out.println(url);
                url=doc.getDocumentElement().getAttribute("next");
                parseResponse(doc, waypoints);
            }
            writeFile(waypoints);

        }
        catch (Exception e)
        {
            System.err.println("ERROR "+e.getMessage());
        }
        
    }
}
