package com.emw.ShapeDemo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
@Service //3shan yfhm el autowired "Dependency injection bdl new service"
public class WorkflowService {

    @Value("${json.dir}")
    private String dir;

    public  Shape[] getWorkflow(String id) {

        Shape[] array= new Shape[100];
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(dir+id+".json"));
            List<Shape> shapes = new Gson().fromJson(reader, new TypeToken<List<Shape>>() {}.getType());
            array = shapes.toArray(new Shape[100]);
            shapes.forEach(System.out::println);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return array;
    }
}
