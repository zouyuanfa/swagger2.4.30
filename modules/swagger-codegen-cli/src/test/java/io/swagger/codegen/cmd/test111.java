package io.swagger.codegen.cmd;

import io.swagger.codegen.ClientOptInput;
import io.swagger.codegen.kingdee.KingdeeTest;

import mockit.Injectable;
import org.testng.annotations.Test;

import java.io.IOException;

public class test111 {


    @Test
    public void test() throws IOException {
        System.out.println(System.getProperty("user.dir"));
        KingdeeTest kingdeeTest = new KingdeeTest();
        kingdeeTest.test("src/main/resources/aster.swagger.json");
    }
}
