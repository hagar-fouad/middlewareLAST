package com.emw.ShapeDemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@RestController
public class MiddlewareController {
     Map< String,String> variablesMap =
      new HashMap< String,String>();
    int v1;
    int v2;
    @Autowired
    private WorkflowService workflowService;



   @GetMapping("middleware/subscriber/{workflow}")
    public void workflow(@PathVariable String workflow, WebRequest webRequest)

    {
        Map<String, String[]> params = webRequest.getParameterMap();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
//            System.out.println("Key = " + entry.getKey() +
//                    ", Value = " + entry.getValue()[0]);
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
        while(temp.getNext().getType()!=null) //for one end and always start at the beginning of the array
        {
            int innerCount=count;
            while(innerCount!=0)
            {

               if(temp.getNext().getX()==s[innerCount-1].getX() && temp.getNext().getY()==s[innerCount-1].getY())
               {

                  temp=s[innerCount-1];

                   executeshape(temp);
                  break;
               }
               else
               {
                   innerCount--;
               }

            }
            System.out.println(temp.getNext().getType());

        }


    }


    public void executeshape(Shape s)
    {
        String [] var;

        var=s.getUserdata();
        if(var.length>=1)
        {

            for (String name : variablesMap.keySet())
            {

                // search  for value
                if(name.equals(var[0])) { //v2
                     v1 = Integer.parseInt(variablesMap.get(name));
//                    System.out.println("Key = " + name + ", Value = " + v1);
                }
                if(var.length==2) {
                    if (name.equals(var[1])) { //v1
                        v2 = Integer.parseInt(variablesMap.get(name));
//                        System.out.println("Key = " + name + ", Value = " + v2);
                    }
                }
            }

        }
        else
        {
            System.out.println("fadyaaaaaaaaaaaa");
        }
        int counter=0;
        int res;
        switch (s.getType()) {
            case "addition":
              //  System.out.println(variables[0]);
                 res=v1+v2;
                System.out.println(res);
                break;
            case "subtraction":
                res=v1-v2;
                System.out.println(res);
                break;
            case "multiplication":
                res=v1*v2;
                System.out.println(res);
                break;
            case "division":
                res=v1/v2;
                System.out.println(res);
                break;
            case "AND":
                res=v1&v2;
                System.out.println(res);
                break;
            case "OR":
                res=v1 | v2;
                System.out.println(res);
                break;
            case "NOT":
                res=~v1;
                System.out.println(res);
                break;
            case "diamond"    :
                String [] con;
                String [] splitedValue;
                con=s.getUserdata();
                splitedValue=con[0].split(" ");
                for(int i=0; i<splitedValue.length; i++)
                {
                    System.out.println(splitedValue[i]);
                }
        }

    }
}
