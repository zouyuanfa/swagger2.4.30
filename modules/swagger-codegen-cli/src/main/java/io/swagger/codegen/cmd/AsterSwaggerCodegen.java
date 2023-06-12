package io.swagger.codegen.cmd;

import io.swagger.models.*;
import io.swagger.models.auth.AuthorizationValue;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.PropertyBuilder;
import io.swagger.parser.SwaggerParser;
import io.swagger.parser.util.ParseOptions;
import io.swagger.util.Json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class AsterSwaggerCodegen {

    public void Codegen(String inputSpec) throws IOException {
        final List<AuthorizationValue> authorizationValues = new ArrayList<>();
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        parseOptions.setFlatten(true);
        Swagger swagger = new SwaggerParser().read(inputSpec, authorizationValues, parseOptions);
        Map<String, Path> paths = swagger.getPaths();
        List<Tag> tags = swagger.getTags();
        Map<String, Model> definitions = swagger.getDefinitions();

//        java.nio.file.Path path = Paths.get("D:\\Users\\kingdee\\swagger2.4.30\\modules\\swagger-codegen-cli\\src\\main\\java\\io\\swagger\\codegen\\aster");
        java.nio.file.Path path = Paths.get("src/main/java/io/swagger/codegen/aster");
        MyFileVisitor mfv = new MyFileVisitor();
        Files.walkFileTree(path, mfv);
        Set<String> pathStringSet = mfv.getPathStringSet();

//        Set<String> asterSet = new HashSet<>();
//        asterSet.add("D:\\Users\\kingdee\\swagger2.4.30\\modules\\swagger-codegen-cli\\src\\main\\java\\io\\swagger\\codegen\\aster/pm/subm/workorder.swagger.json");
//        asterSet.add("D:\\Users\\kingdee\\swagger2.4.30\\modules\\swagger-codegen-cli\\src\\main\\java\\io\\swagger\\codegen\\aster\\scm\\inventory\\inv_assemble.swagger.json");

        for (String s : pathStringSet) {
            Swagger read = new SwaggerParser().read(s, authorizationValues, parseOptions);
            Map<String, Path> swaggerPathsMap = read.getPaths();
            for (String pathKey : swaggerPathsMap.keySet()) {
                if(pathKey.contains("detail")){
                    continue;
                }
                Path swaggerPath = swaggerPathsMap.get(pathKey);
                if(swaggerPath.getGet() != null){
                    Operation get = swaggerPath.getGet();
                    String operationId = get.getOperationId();
                    //创建一个properties
                    read.addDefinition(operationId+"Req",createModel(get));
                    createParameter(operationId+"Req",get);
                }
            }


            paths.putAll(read.getPaths());
            tags.addAll(read.getTags());
            definitions.putAll(read.getDefinitions());
        }

        FileOutputStream fileOutputStream = null;
        File file = new File("src/main/resources/aster.swagger.json");
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

    private void createParameter(String ref, Operation operation) {
        RefModel refModel = new RefModel(ref);
        List<Parameter> parameters = operation.getParameters();
        parameters.clear();
        BodyParameter bodyParameter = new BodyParameter();
        bodyParameter.schema(refModel);
        bodyParameter.name("body");
//        bodyParameter.setDescription("isCustomizeGet");
        parameters.add(bodyParameter);
    }

    private Model createModel(Operation get) {
        Map<String, Property> propertiesMap = new HashMap<>();
        //拿出所有parameters
        List<Parameter> parameters = get.getParameters();
        for (Parameter parameter : parameters) {
            QueryParameter queryParameter = (QueryParameter) parameter;
            Property property = PropertyBuilder.build(queryParameter.getType(), queryParameter.getFormat(), null);
            if (property instanceof ArrayProperty){
                ((ArrayProperty) property).setItems(queryParameter.getItems());
            }

            propertiesMap.put(parameter.getName(),property);
        }
        //拼一个
        Model model = new ModelImpl();
        model.setProperties(propertiesMap);
        return model;
    }


}
