/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.studioblueplanet.rddownload;

import org.w3c.dom.Document;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

/**
 * Namespace resolver required for the namespaces used
 * @author jorgen
 */
public class NamespaceResolver implements NamespaceContext
{
  //Store the source document to search the namespaces
    private Document sourceDocument;
 
    public NamespaceResolver(Document document) 
    {
        sourceDocument = document;
    }
 
    //The lookup for the namespace uris is delegated to the stored document.
    @Override
    public String getNamespaceURI(String prefix) 
    {
        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) 
        {
            return sourceDocument.lookupNamespaceURI(null);
        } 
        else 
        {
            return sourceDocument.lookupNamespaceURI(prefix);
        }
    }
 
    @Override
    public String getPrefix(String namespaceURI) 
    {
        return sourceDocument.lookupPrefix(namespaceURI);
    }
 
    @Override
    @SuppressWarnings("rawtypes")
    public Iterator getPrefixes(String namespaceURI) 
    {
        return null;
    }
}
