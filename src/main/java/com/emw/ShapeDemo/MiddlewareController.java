package com.emw.ShapeDemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
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
            variablesMap.put(entry.getKey(), entry.getValue()[0]);
        }

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
        //hwa hwa el shape
        NextShape[] mytemp;
        MiddlewareResponse MyResponse= new MiddlewareResponse();
        MyResponse.status="Succes";
        String [] conditionUserdata;
        NextShape [] conditionNexts;
        while(true) //for one end and always start at the beginning of the array
        {
            mytemp= temp.getNext();

           if (mytemp[0].getnextX()==0 && mytemp[0].getnextY()==0)
               break;
           if (mytemp.length==1) {//bt2kd an start awl haga btdkholi msh diamond awl haga tkhosh
               int innerCount = count;
               temp= getCompleteShape(mytemp,s,count);
               if (temp.getType().equals("diamond"))
               {
                   conditionUserdata =temp.getUserdata();
                   if(variablesMap.containsKey(conditionUserdata[0])) {
                       myaction = variablesMap.get(conditionUserdata[0]);
                       conditionNexts=temp.getNext();
                       if(conditionUserdata[1].contains("<")||conditionUserdata[1].contains(">")||conditionUserdata[1].contains("=")||conditionUserdata[1].contains("!")){
                           System.out.println("hellooooooooooooooooooooooooo");
                       }
                       if(conditionNexts[0].getnextType().equals(myaction))
                       {
                           mytemp[0]=conditionNexts[0];
                           temp= getCompleteShape(mytemp,s,count);
                       }
                       else
                       {
                           mytemp[0]=conditionNexts[1];
                           temp= getCompleteShape(mytemp,s,count);
                       }
                   }
                   else
                   {
                       MyResponse.status="failer";
                   }

               }
               MyResponse.result= executeshape(temp,subscriber);
           }
        }
   return MyResponse;
    }

    public Shape getCompleteShape(NextShape mytemp[], Shape[] s, int count){

        int innerCount = count;
        Shape temp=null;
        while (innerCount != 0) {
            if (mytemp[0].getnextX() == s[innerCount - 1].getX() && mytemp[0].getnextY() == s[innerCount - 1].getY()) {
                temp = s[innerCount - 1];
                break;
            } else {
                innerCount--;
            }
        }
        return  temp;
    }


    int res;
    String finalresult;
   String myaction;
   int resultCount=1;
    public String executeshape(Shape s,String mobilenumber) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        String [] var;
        var=s.getUserdata();
         if(var.length>=1)
        {

            for (String name : variablesMap.keySet())
            {
                // search  for value
                if(var.length==1) {
                    if (name.equals(var[0])) { //v1
                        v1 = Integer.parseInt(variablesMap.get(name));
                        System.out.println("Key = " + name + ", Value = " + v1);
                    }
                }
                if(var.length==2) {
                    if (name.equals(var[1])) { //v2
                        v2 = Integer.parseInt(variablesMap.get(name));

                    }
                }
            }
        }
        else
        {
            System.out.println("Empty");
        }
        int counter=0;
        switch (s.getType()) {
            case "addition":
                 res=v1+v2;
                finalresult=String.valueOf(res);
                variablesMap.put("res"+resultCount, finalresult);
                resultCount++;
                break;
            case "subtraction":
                res=v1-v2;
                finalresult=String.valueOf(res);
                variablesMap.put("res"+resultCount, finalresult);
                resultCount++;
                break;
            case "multiplication":
                res=v1*v2;
                finalresult=String.valueOf(res);
                variablesMap.put("res"+resultCount, finalresult);
                resultCount++;
                break;
            case "division":
                res=v1/v2;
                finalresult=String.valueOf(res);
                variablesMap.put("res"+resultCount, finalresult);
                resultCount++;
                break;
            case "AND":
                res=v1&v2;
                finalresult=String.valueOf(res);
                variablesMap.put("res"+resultCount, finalresult);
                resultCount++;
                break;
            case "OR":
                res=v1 | v2;
                finalresult=String.valueOf(res);
                variablesMap.put("res"+resultCount, finalresult);
                resultCount++;
                break;
            case "NOT":
                res=~v1;
                finalresult=String.valueOf(res);
                variablesMap.put("res"+resultCount, finalresult);
                resultCount++;
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
        }

      return finalresult;
    }


}
