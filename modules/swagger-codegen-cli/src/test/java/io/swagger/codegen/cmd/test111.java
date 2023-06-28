package io.swagger.codegen.cmd;



import mockit.Mocked;
import org.testng.annotations.Test;

import java.io.IOException;

public class test111 {



    @Test
    public void test() throws IOException {

        AsterSwaggerCodegen asterSwaggerCodegen = new AsterSwaggerCodegen();
        System.out.println(System.getProperty("user.dir"));
        asterSwaggerCodegen.Codegen("src/main/resources/old.aster.swagger.json");
    }
}
