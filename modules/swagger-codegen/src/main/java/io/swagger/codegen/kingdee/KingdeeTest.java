package io.swagger.codegen.kingdee;

import io.swagger.models.*;
import io.swagger.models.auth.AuthorizationValue;
import io.swagger.parser.SwaggerParser;
import io.swagger.parser.util.ParseOptions;
import io.swagger.util.Json;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class KingdeeTest {

    public void test(String inputSpec) throws IOException {
        final List<AuthorizationValue> authorizationValues = new ArrayList<>();
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        parseOptions.setFlatten(true);
        Swagger swagger = new SwaggerParser().read(inputSpec, authorizationValues, parseOptions);
        Map<String, Path> paths = swagger.getPaths();
        List<Tag> tags = swagger.getTags();
        Map<String, Model> definitions = swagger.getDefinitions();
        java.nio.file.Path path = Paths.get("D:\\Users\\kingdee\\swagger2.4.30\\modules\\swagger-codegen-cli\\src\\main\\java\\io\\swagger\\codegen\\aster");

        MyFileVisitor mfv = new MyFileVisitor();
        Files.walkFileTree(path, mfv);
        Set<String> pathStringSet = mfv.getPathStringSet();

//        Set<String> asterSet = new HashSet<>();
//        asterSet.add("D:\\Users\\kingdee\\swagger2.4.30\\modules\\swagger-codegen-cli\\src\\main\\java\\io\\swagger\\codegen\\aster/pm/subm/workorder.swagger.json");
//        asterSet.add("D:\\Users\\kingdee\\swagger2.4.30\\modules\\swagger-codegen-cli\\src\\main\\java\\io\\swagger\\codegen\\aster\\scm\\inventory\\inv_assemble.swagger.json");

        for (String s : pathStringSet) {
            Swagger read = new SwaggerParser().read(s, authorizationValues, parseOptions);
            paths.putAll(read.getPaths());
            tags.addAll(read.getTags());
            definitions.putAll(read.getDefinitions());
        }

        FileOutputStream fileOutputStream = null;
        File file = new File("src/main/resources/aster1.swagger.json");
        try {
            fileOutputStream = new FileOutputStream(file);
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] bs = Json.pretty(swagger).getBytes();
            fileOutputStream.write(bs);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }


        System.out.println(Json.pretty(swagger));


    }
}
