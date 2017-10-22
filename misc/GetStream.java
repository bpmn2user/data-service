package com.sample;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;        

public class GetStream 
{
	


    public static void main( String[] args ) throws Exception
    {

    	//https://stackoverflow.com/questions/9676588/how-can-you-authenticate-using-the-jersey-client-against-a-jaas-enabled-web-serv
    	//final HTTPBasicAuthFilter authFilter = new HTTPBasicAuthFilter(username, password);
        //client.addFilter(authFilter);
        //client.addFilter(new LoggingFilter());
    	
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://www.thomas-bayer.com").path("sqlrest/INVOICE/");

        Builder builder =   target.request();
        
        InputStream stream = target.request().get().readEntity(InputStream.class);
        //Response response  = builder.get();
        //String result  = builder.get(String.class);
        //System.out.println(target.getUri().toString());
        //System.out.println("Result=" + result);
       /*
        Reader reader = new InputStreamReader(stream, "UTF-8");
        int i;
		while ((i = reader.read()) != -1) {
			System.out.print((char) i);
		}
        */
        
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xsr = xif.createXMLStreamReader( new InputStreamReader(stream, "UTF-8"));
        xsr.nextTag(); // Advance to statements element

        
        int i=0;
       
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        
        ByteArrayOutputStream os;// = new ByteArrayOutputStream();
        //aClass.outputStreamMethod(os);
        String eachxml; // = new String(os.toByteArray(),"UTF-8");
        
        while(xsr.nextTag() == XMLStreamConstants.START_ELEMENT) {
            //File file = new File("out/"  +i+ ".xml");
            //FileOutputStream fos=new FileOutputStream(file,true);
        	os = new ByteArrayOutputStream();
            t.transform(new StAXSource(xsr), new StreamResult(os));
            eachxml = new String(os.toByteArray(),"UTF-8");
            System.out.println("**********" +eachxml);
            i++;
                
            
        }
        
    }
}
    

