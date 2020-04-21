package com.emw.ShapeDemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;


import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

@RestController
public class MiddlewareController {
     Map< String,String> variablesMap =
      new HashMap< String,String>();

    int v1;
    int v2;
    @Autowired
    private WorkflowService workflowService;


    private static Document convertStringToXMLDocument(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public  void Readfile(String mob) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        //File xmlFile = new File("GetAccountDetails_Request.xml");
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse("GetAccountDetails_Request.xml");
        // Get the root element
        Node methodcall = doc.getFirstChild();
        NodeList nList = doc.getElementsByTagName("member");
        Node node = nList.item(4);
        Element eElement = (Element) node;

        System.out.println(eElement.getElementsByTagName("name").item(0).getTextContent());
        eElement.getElementsByTagName("value").item(0).setTextContent(mob);
        System.out.println(eElement.getElementsByTagName("value").item(0).getTextContent());
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("GetAccountDetails_Request2.xml"));
        transformer.transform(source, result);
    }

    @GetMapping("middleware/{subscriber}/{workflow}")
    public MiddlewareResponse workflow(@PathVariable String workflow,@PathVariable String subscriber, WebRequest webRequest) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        Map<String, String[]> params = webRequest.getParameterMap();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            System.out.println("Key = " + entry.getKey() +
                   ", Value = " + entry.getValue()[0]);
            variablesMap.put(entry.getKey(), entry.getValue()[0]);
        }
        System.out.println(subscriber);
        Shape[] s=workflowService.getWorkflow(workflow);
        System.out.println(s);
        int count=0;

        for(int i=0; i<s.length; i++)
        {
            if(s[i]!=null)
            {
               count++;
            }
            else
            {
                break;
            }
        }

        Shape temp=s[0];
        NextShape[] mytemp;
        MiddlewareResponse MyResponse= new MiddlewareResponse();

        MyResponse.status="Succes";

        while(true) //for one end and always start at the beginning of the array
        {
           mytemp= temp.getNext();
           if (mytemp[0].getnextX()==0 && mytemp[0].getnextY()==0)
               break;
           if (mytemp.length==1) {//bt2kd mmn l trteb
               int innerCount = count;
               while (innerCount != 0) {

                   if (mytemp[0].getnextX() == s[innerCount - 1].getX() && mytemp[0].getnextY() == s[innerCount - 1].getY()) {

                       temp = s[innerCount - 1];
                       System.out.println(temp);
                       MyResponse.result= executeshape(temp,subscriber);
                       break;
                   } else {
                       innerCount--;
                   }

               }
           }

        }

   return MyResponse;
    }

    int res;
    String finalresult;
   String myaction;
    public String executeshape(Shape s,String mobilenumber) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        String [] var;
        //berg3 l userdata bt3t l shape
        var=s.getUserdata();
       /* System.out.println(var[0]);
        System.out.println(var[1]);
        System.out.println(var[2]);
        System.out.println(var[3]);
        System.out.println(var[4]);*/


         if(var.length>=1)
        {

            for (String name : variablesMap.keySet())
            {
                // search  for value
                if(name.equals(var[0])) { //v1
                     v1 = Integer.parseInt(variablesMap.get(name));
                   System.out.println("Key = " + name + ", Value = " + v1);
                }
                if(var.length==2) {
                    if (name.equals(var[1])) { //v2
                        v2 = Integer.parseInt(variablesMap.get(name));
                       System.out.println("Key = " + name + ", Value = " + v2);
                    }
                }
                if (var.length>=3) {
                    if (name.equals(var[1])) { //v2
                        v2 = Integer.parseInt(variablesMap.get(name));
                        System.out.println("Key = " + name + ", Value = " + v2);
                    }
                    if (name.equals(var[2]))
                        myaction = variablesMap.get(name);
                }
            }
        }
        else
        {
            System.out.println("Empty");
        }
        int counter=0;
       // System.out.println(myaction);
       if(s.getType().equals("diamond"))
         s.setType(myaction);
      //  System.out.println("///////////////");
       // System.out.println(s.getType());
        switch (s.getType()) {
            case "addition":
                 res=v1+v2;
                System.out.println("////////////");
                System.out.println(res);
                finalresult=String.valueOf(res);
                break;
            case "subtraction":
                res=v1-v2;
                System.out.println(res);
                finalresult=String.valueOf(res);
                break;
            case "multiplication":
                res=v1*v2;
                System.out.println(res);
                finalresult=String.valueOf(res);
                break;
            case "division":
                res=v1/v2;
                System.out.println(res);
                finalresult=String.valueOf(res);
                break;
            case "AND":
                res=v1&v2;
                System.out.println(res);
                finalresult=String.valueOf(res);
                break;
            case "OR":
                res=v1 | v2;
                System.out.println(res);
                finalresult=String.valueOf(res);
                break;
            case "NOT":
                res=~v1;
                System.out.println(res);
                finalresult=String.valueOf(res);
                break;
            case "GetAccountDetails":
                ///hktb l mobilel gayly fl URL
                Readfile(mobilenumber);
                ///bgeb l Response
                RestTemplate restTemplate = new RestTemplate();
                String fooResourceUrl
                        = "http://localhost:8080/GetAccountDetails";
                ResponseEntity<String> response
                        = restTemplate.getForEntity(fooResourceUrl , String.class);
                String responsebody=response.getBody();
                Document doc = convertStringToXMLDocument( responsebody );
                //Verify XML document is build correctly
                //System.out.println(doc.getFirstChild().getNodeName());
                Node methodcall = doc.getFirstChild();
                NodeList nList = doc.getElementsByTagName("member");
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    //System.out.println("\nCurrent Element :" + nNode.getNodeName());
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        if (eElement.getElementsByTagName("name").item(0).getTextContent().equals("serviceClassCurrent")) {
                            //  System.out.println("Name : " + eElement.getElementsByTagName("name").item(0).getTextContent());
                            System.out.println("value: " + eElement.getElementsByTagName("value").item(0).getTextContent());
                            finalresult=eElement.getElementsByTagName("value").item(0).getTextContent();
                            break;
                        }
                    }
                }
                break;
            case "ReadMobNumber":
                finalresult=mobilenumber;
                break;
          //  case "diamond"    :

                /*String [] con;
                String [] splitedValue;
                con=s.getUserdata();
                splitedValue=con[0].split(" ");
                for(int i=0; i<splitedValue.length; i++)
                {
                    System.out.println(splitedValue[i]);
                }*/

        }

      return finalresult;
    }


}
