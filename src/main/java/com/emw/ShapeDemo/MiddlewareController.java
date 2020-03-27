package com.emw.ShapeDemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class MiddlewareController {
   // Map< String,Integer> variablesMap =
          //  new HashMap< String,Integer>();
    int v1;
    int v2;
    @Autowired
    private WorkflowService workflowService;
    @GetMapping("middleware/subscriber/{mobileNum}")
    public void workflow(@PathVariable String mobileNum, @RequestParam String var1,@RequestParam String var2,
                         @RequestParam String workflow, @RequestParam String action)
    {
        System.out.println(mobileNum);
       // variablesMap.put("var1",Integer.parseInt(var1));
        //variablesMap.put("var2",Integer.parseInt(var2));
        //System.out.println(variablesMap);
         v1=Integer.parseInt(var1);
        v2=Integer.parseInt(var2);
        Shape[] s=workflowService.getWorkflow(workflow);
        System.out.println(s);
        int count=0;
        System.out.println(s[0].getNext());
        System.out.println();
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
         //always start is first in array


        Shape temp=s[0];
        Shape endd=new Shape(0,0,"end",null,null,null,"");

        while(temp.getNext().getType()!=null) //for one end and always start at the beginning of the array
        {
            int innerCount=count;
            while(innerCount!=0)
            {
                System.out.println(temp.getNext().getX());
                System.out.println(s[innerCount-1].getX());
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



//        while(count!=0)
//        {
//            System.out.println(s[count-1].getType());
//            System.out.println(s[count-1].getNext().getType());
//            executeshape(s[count-1]);
//
//            count--;
//        }
       // for(int i=0; i<s.length; i++) {
         //
      //  }

    }
    //String []variables;

    public void executeshape(Shape s)
    {
       // variables=s.getUserdata();
       // System.out.println(variables[0]);

       // int v1=variablesMap.get();
       // int v2=variablesMap.get(variables[1]);
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
