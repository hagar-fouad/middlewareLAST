package com.emw.ShapeDemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class MiddlewareController {
    Map< String,Integer> variablesMap =
            new HashMap< String,Integer>();
    @Autowired
    private WorkflowService workflowService;
    @GetMapping("middleware/subscriber/{mobileNum}")
    public void workflow(@PathVariable String mobileNum, @RequestParam String var1,@RequestParam String var2,
                         @RequestParam String workflow, @RequestParam String action)
    {
        System.out.println(mobileNum);
        variablesMap.put("var1",Integer.parseInt(var1));
        variablesMap.put("var2",Integer.parseInt(var2));
        System.out.println(variablesMap);
        Shape[] s=workflowService.getWorkflow(workflow);
        System.out.println(s);
        for(int i=0; i<s.length; i++) {
            executeshape(s[i]);
        }

    }
    String []variables;

    public void executeshape(Shape s)
    {
        variables=s.getUserdata();
        System.out.println(variables[0]);
        int v1=variablesMap.get(variables[0]);
        int v2=variablesMap.get(variables[1]);
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

        }

    }
}
