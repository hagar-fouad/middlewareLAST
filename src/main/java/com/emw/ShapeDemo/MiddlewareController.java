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

        //webRequest holds any number of variables in the url
        Map<String, String[]> params = webRequest.getParameterMap();
        //returns parms into our Global variable map where key is the variable name and value is the value of that variable
        for (Map.Entry<String, String[]> entry : params.entrySet())
            variablesMap.put(entry.getKey(), entry.getValue()[0]);

        //getting the array of shapes from JSON into workflowShapes
        Shape[] workflowShapes=workflowService.getWorkflow(workflow);
        int count=0;
        //this loop is for counting the number of shapes i have
        //we can't use workflowShapes.length because it returns a static size of 100
        for(int i=0; i<workflowShapes.length; i++)
            if(workflowShapes[i]!=null)
               count++;
            else
                break;

        //complete shape will always start with the start bubble which is always the first shape in the array of shapes(workflowShapes)
        Shape completeShape=workflowShapes[0];
        NextShape[] nextHalfShape;//nextHalfShape will be the .next of out completeShape but won't contain the userData of the completeShape
        MiddlewareResponse Response2Execution= new MiddlewareResponse();//the response returned after executing the workflow
        Response2Execution.status="Success";//initialize status of response to be successfully executed
        String [] conditionUserdata;
        NextShape [] conditionNexts;
        //loops over each bubble/shape executing it in the process
        while(true) //for one end and always start at the beginning of the array
        {
            nextHalfShape= completeShape.getNext();
            if (nextHalfShape[0].getnextX()==0 && nextHalfShape[0].getnextY()==0)//breaks when end shape is reached
               break;
            ///--------el if condition de malhash ay talateen lazma garabe sheleha ma3 hagar------
            if (nextHalfShape.length==1) {//bt2kd an start awl haga btdkholi msh diamond awl haga tkhosh
               int innerCount = count;
               completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
               if (completeShape.getType().equals("diamond"))//if the complete shape is a diamond, execute a certain path don't execute the diamond itself
               {
                   conditionUserdata =completeShape.getUserdata();
                   if(variablesMap.containsKey(conditionUserdata[0])) {
                       myaction = variablesMap.get(conditionUserdata[0]);
                       conditionNexts=completeShape.getNext();
                       String numberOnly= conditionUserdata[1].replaceAll("[^0-9]", "");
                       if(conditionUserdata[1].contains("<")||conditionUserdata[1].contains(">")||conditionUserdata[1].contains("=")||conditionUserdata[1].contains("!")){
                           if(conditionUserdata[1].contains("<"))
                           {
                               if(Integer.parseInt(myaction)<Integer.parseInt(numberOnly))
                               {
                                   nextHalfShape[0]=conditionNexts[0];
                                   completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                               }
                               else{
                                   nextHalfShape[0]=conditionNexts[1];
                                   completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                               }
                           }
                           else if(conditionUserdata[1].contains("<="))
                           {
                               if(Integer.parseInt(myaction)<=Integer.parseInt(numberOnly))
                               {
                                   nextHalfShape[0]=conditionNexts[0];
                                   completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                               }
                               else{
                                   nextHalfShape[0]=conditionNexts[1];
                                   completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                               }
                           }
                           else if(conditionUserdata[1].contains(">"))
                           {
                               if(Integer.parseInt(myaction)>Integer.parseInt(numberOnly))
                               {
                                   nextHalfShape[0]=conditionNexts[0];
                                   completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                               }
                               else{
                                   nextHalfShape[0]=conditionNexts[1];
                                   completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                               }
                           }
                           else if(conditionUserdata[1].contains(">="))
                           {
                               if(Integer.parseInt(myaction)>=Integer.parseInt(numberOnly))
                               {
                                   nextHalfShape[0]=conditionNexts[0];
                                   completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                               }
                               else{
                                   nextHalfShape[0]=conditionNexts[1];
                                   completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                               }
                           }
                           else if(conditionUserdata[1].contains("=="))
                           {
                               if(Integer.parseInt(myaction)==Integer.parseInt(numberOnly))
                               {
                                   nextHalfShape[0]=conditionNexts[0];
                                   completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                               }
                               else{
                                   nextHalfShape[0]=conditionNexts[1];
                                   completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                               }
                           }
                           else if(conditionUserdata[1].contains("!="))
                           {
                               if(Integer.parseInt(myaction)!=Integer.parseInt(numberOnly))
                               {
                                   nextHalfShape[0]=conditionNexts[0];
                                   completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                               }
                               else{
                                   nextHalfShape[0]=conditionNexts[1];
                                   completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                               }
                           }
                       }
                      else if(conditionNexts[0].getnextType().equals(myaction))
                       {
                           nextHalfShape[0]=conditionNexts[0];
                           completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                       }
                       else if(conditionNexts[1].getnextType().equals(myaction))
                       {
                           nextHalfShape[0]=conditionNexts[1];
                           completeShape= getCompleteShape(nextHalfShape,workflowShapes,count);
                       }
                   }
                   else
                   {
                       Response2Execution.status="failer";
                   }

               }
               Response2Execution.result= executeshape(completeShape,subscriber);
           }
        }
   return Response2Execution;
    }

    public Shape getCompleteShape(NextShape mytemp[], Shape[] s, int innerCount){
        Shape temp=null;
        while (innerCount != 0) {
            if (mytemp[0].getnextX() == s[innerCount - 1].getX() && mytemp[0].getnextY() == s[innerCount - 1].getY()) {
                temp = s[innerCount - 1];
                break;
            }
            else {
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
                    if(name.equals(var[0]))
                    {
                        v1=Integer.parseInt(variablesMap.get(name));
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
