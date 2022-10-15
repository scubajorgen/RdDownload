## RD Download

### Introduction
This is a small and quick-and-dirty tool to download the '[RD Info](https://www.pdok.nl/geo-services/-/article/rdinfo)' dataset from PDOK and converts it to the OziExplorer Waypoint (.wpt) format.

The 'RD Info' dataset contains the location 4482 Rijksdriehoeksmeting reference points covering the Netherlands. It is offered by PDOK as a WFS web service and as a WMS web service. The services are for free and can be used without authentication.

The software requests the WFS service. The reference points are delivered in batches of 1000 points, so multiple paged calls are required. It then writes the points to waypoints.wpt file. On the fly the Map Datum is converted from Rijksdriehoeksmeting to WGS84.

```
OziExplorer Waypoint File Version 1.1
WGS 84
Reserved 2
garmin
1,H.K. Hollum-2000,53.4357256300645,5.640948909047605,44848.7500,167,0,3,0,65535,,0,0,0,-777,6,0,17,0,10,2,,,,120
2,Vuurtoren Hollum Ameland-2000,53.44905417093161,5.625826220110928,44848.7500,167,0,3,0,65535,,0,0,0,-777,6,0,17,0,10,2,,,,120
3,Dorpstoren Ballum-2000,53.44402138121058,5.686702525605591,44848.7500,167,0,3,0,65535,,0,0,0,-777,6,0,17,0,10,2,,,,120
...
```
The name of the waypoint is a concatenation from fields: 'benaming'-'uitgavejaarext', so for example 'H.K. Hollum-2000'

### Dependencies
For conversion my project [MapDatumConvert](https://github.com/scubajorgen/MapDatumConvert) is used as library.

### Disclaimer
At the time of writing, the PDOK datasets appear to contain large errors in the location. The OLV tower in Amersfoort, the origin of RD, is located at [155029, 463001], whereas it should be at [155000, 463000]. The error persists of course in the converted file.

