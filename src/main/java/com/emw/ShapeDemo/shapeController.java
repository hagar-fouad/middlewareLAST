package com.emw.ShapeDemo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class shapeController {
    //Shape[] temp = new Shape[100];
    @Autowired //create workflowservice
    private WorkflowService workflowService;
    @GetMapping("/workflow")
    public  Shape[] getShape() {

        return workflowService.getWorkflow("user");
    }
    @PostMapping("/workflow")
    public String saveShape(@RequestBody Shape[] shape) throws IOException {
        try {
            Writer writer = new FileWriter("user.json");
            new Gson().toJson(shape, writer);
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "success";
    }
}

