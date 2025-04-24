# Swagger Codegen 多语言代码生成指南

## 一、通用命令结构

```sh
java -jar "E:\path\to\swagger-codegen-cli.jar" generate ^
  -i "API规范文件路径" ^      REM 必填，支持 json/yaml
  -l 目标语言 ^              REM 必填，如 java/python/go/php
  -o "输出目录" ^            REM 必填，建议空目录
  -c "配置文件路径" ^        REM 可选，高级配置
  --additional-properties "key=value" ^  REM 扩展参数
  --type-mappings "类型映射"  REM 可选，类型转换
```

------

## 二、各语言生成示例

### 1. Java 客户端

```sh
java -jar "E:\Java\Kingdee\jar\swagger-codegen-cli.jar" generate `
  -i "modules/swagger-codegen-cli/src/main/resources/aster_modified.swagger.json" `
  -o "E:\Java\Kingdee\java_XCSDK" `
  -l java `
  -c "modules/swagger-codegen-cli/src/main/resources/javaconfig.json"
```

**配置文件示例** `javaconfig.json`

```json
{
  "library": "okhttp4-gson",
  "developerName": "kingdee",
  "developerOrganization": "open.jdy.com",
  "invokerPackage": "com.kingdee.service",
  "modelPackage": "com.kingdee.service.data.entity",
  "apiPackage": "com.kingdee.service.data.api",
  "serializableModel": true,
  "withComments": true,
  "additionalProperties": {
    "generateModelDocumentation": true,
    "generateApiDocumentation": true
  },
  "artifactId": "kingdee-xw-openapi"
}
```

### 2. Python 客户端

```sh
java -jar "E:\Java\Kingdee\jar\swagger-codegen-cli.jar" generate `
  -i "modules/swagger-codegen-cli/src/main/resources/aster_modified.swagger.json" `
  -o "E:\Java\Kingdee\python_XCSDK" `
  -l python `
  -c "modules/swagger-codegen-cli/src/main/resources/pythonconfig.json"
```

**配置文件示例** `pyhtonconfig.json`

```json
{
  "library": "okhttp4-gson",
  "developerName": "kingdee",
  "developerOrganization": "open.jdy.com",
  "invokerPackage": "com.kingdee.service",
  "modelPackage": "com.kingdee.service.data.entity",
  "apiPackage": "com.kingdee.service.data.api",
  "serializableModel": true,
  "withComments": true,
  "additionalProperties": {
    "generateModelDocumentation": true,
    "generateApiDocumentation": true
  },
  "artifactId": "kingdee-xw-openapi"
}
```



### 3. Go 客户端

```sh
java -jar "E:\Java\Kingdee\jar\swagger-codegen-cli.jar" generate `
  -i "modules/swagger-codegen-cli/src/main/resources/aster_modified.swagger.json" `
  -o "E:\Java\Kingdee\go_XCSDK" `
  -l go `
  -c "modules/swagger-codegen-cli/src/main/resources/goconfig.json"
```

**配置文件示例** `goconfig.json`

```json
{
  "packageName": "kingdee_xw_openapi",
  "packageVersion": "1.0.0",
  "hideGenerationTimestamp": true,
  "withGoCodegenComment": true,
  "structPrefix": true,
  "additionalProperties": {
    "generateInterfaces": true,
    "enumClassPrefix": true
  }
}
```



### 4. PHP 客户端

```sh
java -jar "E:\Java\Kingdee\jar\swagger-codegen-cli.jar" generate `
  -i "modules/swagger-codegen-cli/src/main/resources/aster_modified.swagger.json" `
  -o "E:\Java\Kingdee\php_XCSDK" `
  -l php `
  -c "modules/swagger-codegen-cli/src/main/resources/php_config.json"
```

**配置文件示例** `phpconfig.json`

```json
{
  "invokerPackage": "Kingdee\\XwOpenapi",
  "packagePath": "kingdee-xw-openapi-php",
  "artifactVersion": "1.0.0",
  "modelPackage": "Kingdee\\XwOpenapi\\Model",
  "apiPackage": "Kingdee\\XwOpenapi\\Api",
  "composerVendorName": "kingdee",
  "composerProjectName": "xw-openapi-sdk",
  "srcBasePath": "src",
  "variableNamingConvention": "camelCase",
  "additionalProperties": {
    "generateModelDocumentation": true,
    "generateApiDocumentation": true,
    "hideGenerationTimestamp": true
  }
}
```



## 三、**配置统一性说明**

1. **保留原始配置项**：

   - 所有语言均保留 `developerName` 和 `developerOrganization`
   - 文档生成开关（`generateModelDocumentation` 和 `generateApiDocumentation`）保持一致

2. **语言特性适配**：

   - **Python**：添加 `pythonAttrNoneIfUnset` 处理空字段
   - **Go**：启用 `withGoModules` 并隐藏时间戳
   - **PHP**：配置 Composer 包名和命名空间

3. **生成后操作**：

   ```sh
   # Python
   cd python_XCSDK && pip install -e .
   
   # Go
   cd go_XCSDK && go mod init kingdee_xcsdk && go mod tidy
   
   # PHP
   cd php_XCSDK && composer install
   ```

4. **输出结构一致性**：
   所有语言生成的目录均包含：

   ```markdown
   ├── docs/            # API文档
   ├── examples/        # 调用示例
   ├── {src,client}/    # 源码主目录
   └── README.md        # 语言特定的使用说明
   ```

## 五、常见问题排查

### 1. 路径问题

```diff
# 错误示例（Windows）
-o C:\code\gen# 路径含特殊字符

# 正确写法（所有系统）
-o "./output" # 使用引号包裹路径[3](@ref)
```

### 2. 依赖冲突解决

```sh
# Java项目清理旧生成文件
mvn clean compile

# Python虚拟环境重建
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
```

### 3. 多版本管理

通过 `-Dswagger.codegen.version` 参数指定生成器版本

```sh
java -Dswagger.codegen.version=3.0.29 \
  -jar swagger-codegen-cli.jar generate ...
```

------

## 六、高级技巧

### 1. 自定义模板

```sh
java -jar swagger-codegen-cli.jar generate \
  -t ./custom-templates \ # 自定义模板目录
  -i api.yaml \
  -l java
```

### 2. 多语言文档生成



```sh
# 生成HTML文档
java -jar swagger-codegen-cli.jar generate \
  -i api.yaml \
  -l html \
  -o ./docs
```

### 3. CI/CD 集成示例（GitLab）

```yaml
generate-java:
  stage: build
  script:
    - java -jar swagger-codegen-cli.jar generate 
        -i $API_SPEC_URL 
        -l java 
        -o ./generated-code
  artifacts:
    paths:
      - generated-code/
```



详细请参考：[swagger-api/swagger-codegen: swagger-codegen contains a template-driven engine to generate documentation, API clients and server stubs in different languages by parsing your OpenAPI / Swagger definition.](https://github.com/swagger-api/swagger-codegen)
