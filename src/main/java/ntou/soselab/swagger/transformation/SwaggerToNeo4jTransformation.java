package ntou.soselab.swagger.transformation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.models.*;
import io.swagger.models.auth.SecuritySchemeDefinition;
import io.swagger.models.parameters.*;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.parser.SwaggerParser;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import ntou.soselab.swagger.algo.WordNetExpansion;
import ntou.soselab.swagger.neo4j.domain.relationship.*;
import ntou.soselab.swagger.neo4j.domain.service.*;
import ntou.soselab.swagger.neo4j.domain.service.Operation;
import ntou.soselab.swagger.neo4j.domain.service.Parameter;
import ntou.soselab.swagger.neo4j.domain.service.Path;
import ntou.soselab.swagger.neo4j.domain.service.Response;
import ntou.soselab.swagger.neo4j.graph.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SwaggerToNeo4jTransformation {

    Logger log = LoggerFactory.getLogger(SwaggerToNeo4jTransformation.class);

    @Autowired
    Neo4jToDatabase neo4jToDatabase;

    @Autowired
    WordNetExpansion wordNetExpansion;

    @Autowired
    SwaggerToLDA swaggerToLDA;

    public void parseSwaggerDocument(String swaggerDocument) {
        Swagger swagger = new SwaggerParser().parse(swaggerDocument);
        Resource resource = new Resource();
        ResourceGraph resourceGraph = getResourceInformation(swagger, resource);

        for (String p : swagger.getPaths().keySet()) {

            // 設定路徑
            Path path = new Path();
            path.setPath(p);

            // record relationship type
            Endpoint endpoint = new Endpoint();
            PathGraph pathGraph = new PathGraph();

            pathGraph.setPath(path);
            pathGraph.setEndpoint(endpoint);

            if (swagger.getPaths().get(p).getDelete() != null) {
                log.info("--- operation:DELETE on {}", p);
                OperationGraph operationGraph = getOperationInformation(swagger.getPaths().get(p).getDelete(), "delete");

                findAllTheParametersFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getDelete()); // set
                // Parameters
                findAllTheStatusCodeFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getDelete()); // set Response
                pathGraph.setOperationGraph(operationGraph);
            }
            if (swagger.getPaths().get(p).getGet() != null) {
                log.info("--- operation:GET on {}", p);
                OperationGraph operationGraph = getOperationInformation(swagger.getPaths().get(p).getGet(), "get");

                findAllTheParametersFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getGet()); // set
                findAllTheStatusCodeFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getGet()); // set Response
                pathGraph.setOperationGraph(operationGraph);
            }
            if (swagger.getPaths().get(p).getPatch() != null) {
                log.info("--- operation:PATCH on {}", p);
                OperationGraph operationGraph = getOperationInformation(swagger.getPaths().get(p).getPatch(), "patch");

                findAllTheParametersFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getPatch()); // set
                findAllTheStatusCodeFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getPatch()); // set Response
                pathGraph.setOperationGraph(operationGraph);
            }
            if (swagger.getPaths().get(p).getPost() != null) {
                log.info("--- operation:POST on {}", p);
                OperationGraph operationGraph = getOperationInformation(swagger.getPaths().get(p).getPost(), "post");

                findAllTheParametersFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getPost()); // set
                findAllTheStatusCodeFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getPost()); // set Response
                pathGraph.setOperationGraph(operationGraph);
            }
            if (swagger.getPaths().get(p).getPut() != null) {
                log.info("--- operation:PUT on {}", p);
                OperationGraph operationGraph = getOperationInformation(swagger.getPaths().get(p).getPut(), "put");

                findAllTheParametersFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getPut()); // set
                findAllTheStatusCodeFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getPut()); // set Response
                pathGraph.setOperationGraph(operationGraph);
            }
            resourceGraph.setPathGraph(pathGraph);
        }

        neo4jToDatabase.buildRelationshipStartWithResource(resourceGraph);
    }

    public ResourceGraph getResourceInformation(Swagger swagger, Resource resource) {

        // store swagger parse information
        ArrayList<String> swaggerInfo = new ArrayList<>();

        // For saving key: stemming term --> value: original term
        HashMap<String, String> stemmingAndTermsTable = new HashMap<String, String>();

        // For resource concept
        String title = null;
        String description = null;
        String host = null;
        String basePath = null;
        String version = null;

        // get schemes
        if(swagger.getSchemes() != null) {
            for(Scheme key : swagger.getSchemes()) {
                resource.setScheme(key.toValue());
            }
        }

        // get host
        host = swagger.getHost();
        resource.setHost(host);

        // get basePath
        basePath = swagger.getBasePath();
        resource.setBasePath(basePath);

        // get title
        title = swagger.getInfo().getTitle();
        log.info("Title :{}", title);
        resource.setTitle(title);
        if(title != null) swaggerInfo.add(title);

        // get Logo
        Map<String, Object> info = swagger.getInfo().getVendorExtensions();
        if (info.get("x-logo") != null) {
            Object logo = info.get("x-logo");
            if (logo instanceof ObjectNode) {
                ObjectNode logoNode = (ObjectNode) logo;
                resource.setLogo(logoNode.get("url").textValue());
            }
        }

        // get description
        description = swagger.getInfo().getDescription();
        resource.setDescription(description);
        if(description != null) swaggerInfo.add(description);

        // get provider
        if (info.get("x-providerName") != null) {
            Object provider = info.get("x-providerName");
            if (provider instanceof String) {
                resource.setProvider((String) provider);
            }
        }

        // get swagger url
        if (info.get("x-origin") != null) {
            Object swaggerUrl = info.get("x-origin");
            if(swaggerUrl instanceof ArrayList){
                ArrayList list = (ArrayList)swaggerUrl;
                if (list.size() > 0 && list.get(0) instanceof ObjectNode) {
                    ObjectNode swaggerUrlNode = (ObjectNode)list.get(0);
                    resource.setSwaggerUrl(swaggerUrlNode.get("url").textValue());
                }
            }
        }

        // set id, the id is key=nodeId+title
        resource.setId(resource.toString());

        // set version
        resource.setVersion(version);

        // 獲得全域的認證機制名稱，內容分析 swagger parser 無支援
        if (swagger.getSecurityDefinitions() != null) {
            for (String key : swagger.getSecurityDefinitions().keySet()) {
                SecuritySchemeDefinition ssd = swagger.getSecurityDefinitions().get(key);
                resource.setAuthentication(ssd.getType());
            }
        }

        // set consumes
        if(swagger.getConsumes() != null) {
            for(String key : swagger.getConsumes()) {
                resource.setConsume(key);
            }
        }

        // set produces
        if(swagger.getProduces() != null) {
            for(String key : swagger.getProduces()) {
                resource.setProduce(key);
            }
        }


        // 增加更多 swagger 額外的標註詞彙
        // get x-tags
        if (info.get("x-tags") != null) {
            Object infoTags = info.get("x-tags");
            if (infoTags instanceof ArrayList) {
                ArrayList<String> infoTagsNode = (ArrayList) infoTags;
                // assertEquals(infoTagsNode.get(0), "Azure");
                for (String tag : infoTagsNode) {
                    if(tag != null) swaggerInfo.add(tag);
                }
            }
        }

        try {
            if(swaggerInfo.size() != 0) {
                // parse LDA
                ArrayList<String> LDAWord = swaggerToLDA.swaggerParseLDA(swaggerInfo.toArray(new String[0]), stemmingAndTermsTable);
                // parse WordNet
                ArrayList<String> WordNetWord = wordNetExpansion.getWordNetExpansion(LDAWord, stemmingAndTermsTable);

                resource.setOriginalWord(LDAWord);
                resource.setWordnetWord(WordNetWord);
            }
        } catch (IOException e) {
            log.info("Parse word topic error :{}", e.toString());
        }

        return new ResourceGraph(resource);
    }

    private OperationGraph getOperationInformation(io.swagger.models.Operation swaggerOperation, String Swaggeraction) {

        // store swagger parse information
        ArrayList<String> swaggerInfo = new ArrayList<>();
        // For saving key: stemming term --> value: original term
        HashMap<String, String> stemmingAndTermsTable = new HashMap<String, String>();

        String description = null;

        Operation operation = new Operation();
        description = swaggerOperation.getDescription();
        operation.setDescription(description);
        if(description != null) swaggerInfo.add(description);
        log.info("operation description :{}", swaggerOperation.getDescription());
        operation.setOperationAction(Swaggeraction);
        log.info("operation action :{}", Swaggeraction);

        try {
            if(swaggerInfo.size() != 0) {
                // parse LDA
                ArrayList<String> LDAWord = swaggerToLDA.swaggerParseLDA(swaggerInfo.toArray(new String[0]), stemmingAndTermsTable);
                // parse WordNet
                ArrayList<String> WordNetWord = wordNetExpansion.getWordNetExpansion(LDAWord, stemmingAndTermsTable);

                operation.setOriginalWord(LDAWord);
                operation.setWordnetWord(WordNetWord);
            }
        } catch (IOException e) {
            log.info("Parse word topic error :{}", e.toString());
        }

        // record relationship type
        Action action = new Action();
        OperationGraph operationGraph = new OperationGraph();

        operationGraph.setAction(action);
        operationGraph.setOperation(operation);

        return operationGraph;
    }

    // 將該 path action input 參數全部分析
    private void findAllTheParametersFromOperation(OperationGraph operationGraph, Map<String, Model> definitions, io.swagger.models.Operation operation) {

        List<io.swagger.models.parameters.Parameter> lp = operation.getParameters();

        // check 不同 parameters in --> https://swagger.io/docs/specification/2-0/describing-parameters/
        for (io.swagger.models.parameters.Parameter p : lp) {

            // Body 的 ref model 需要 parse schema, 其他類型若有 ref model 會直接參照, 不用額外 parse
            if (p instanceof BodyParameter) {
                log.info("---- detect the body parameter");
                parseBodyParameter((BodyParameter) p, definitions, operationGraph);
            } else {
                log.info("---- detect the non-body parameter: {}", p.getName());
                ParameterGraph pb = getParameterBeanEntity(p);
                if (pb != null) {
                    operationGraph.setParameterGraph(pb); // 填入參數
                }
            }

//            if(p instanceof PathParameter) {
//                log.info("---- detect the path parameter in :{}", p.getName());
//                log.info("---- detect the path parameter in :{}", p.getIn());
//                log.info("---- detect the path parameter in :{}", p.getDescription());
//                log.info("---- detect the path parameter in :{}", p.getRequired());
//                log.info("---- detect the path parameter in :{}", ((PathParameter) p).getType());
//                log.info("---- detect the path parameter in :{}", ((PathParameter) p).getFormat());
//            }else if(p instanceof QueryParameter) {
//                log.info("---- detect the query parameter in :{}", p.getName());
//            }else if(p instanceof HeaderParameter) {
//                log.info("---- detect the header parameter in :{}", p.getName());
//            }else if(p instanceof BodyParameter) {
//                log.info("---- detect the body parameter in :{}", p.getName());
//                log.info("---- detect the path parameter in :{}", p.getIn());
//                log.info("---- detect the path parameter in :{}", p.getDescription());
//                log.info("---- detect the path parameter in :{}", p.getRequired());
//            }else if(p instanceof FormParameter) {
//                log.info("---- detect the formData parameter in :{}", p.getName());
//            }else if(p instanceof RefParameter) {
//                log.info("---- detect the reference parameter in :{}", p.getName());
//            }
        }
    }

    private void findAllTheStatusCodeFromOperation(OperationGraph operationGraph, Map<String, Model> definitions,
                                                  io.swagger.models.Operation operation) {

        Map<String, io.swagger.models.Response> responses = operation.getResponses();

        for(String key : responses.keySet()) {
            if (responses.get(key) != null) {
                log.info("status code :{}", key);
                log.info("Response Description :{}", responses.get(key).getDescription());

                // 根據不同狀態碼，建立節點
                StatusCode statusCode = new StatusCode();
                statusCode.setStatusCode(key);
                statusCode.setDescription(responses.get(key).getDescription());
                Output output = new Output();
                StatusCodeGraph statusCodeGraph = new StatusCodeGraph(statusCode);
                statusCodeGraph.setOutput(output);

                operationGraph.setStatusCodeGraphs(statusCodeGraph);


                if(responses.get(key).getSchema() != null) {
                    Property property = responses.get(key).getSchema();
                    /*
                     * if(property instanceof RefProperty){ //不取只顯示成功的字樣
                     * log.info("--- detect response: 200");
                     * parseRefProperty((RefProperty)property, definitions, "",
                     * operationBean, "response", new ArrayList<String>()); }
                     */
//                    if (property instanceof RefProperty) { // 不取只顯示成功的字樣
//                        parseStatusCodeRefProperty((RefProperty) property, definitions, "", statusCodeGraph);
//
//                    } else {
//                        parseResponseProperty(null, property, definitions, statusCodeGraph);
//                    }
                    parseResponseProperty(null, property, definitions, statusCodeGraph, key);
                }
            }
        }
    }

    private ParameterGraph getParameterBeanEntity(io.swagger.models.parameters.Parameter swaggerParameter) {
        // store swagger parse information
        ArrayList<String> swaggerInfo = new ArrayList<>();
        // For saving key: stemming term --> value: original term
        HashMap<String, String> stemmingAndTermsTable = new HashMap<String, String>();

        String name = null;
        String in = null;
        String description = null;
        String media_type = null;
        String format = null;
        boolean required = false;
        log.info("------ create ParameterGraph by non body: {}", swaggerParameter.getName());

        Parameter parameter = new Parameter();

        // set parameter Name
        name = swaggerParameter.getName();
        parameter.setName(name);
        if(name != null) swaggerInfo.add(name);
        log.info("Parameter Name :{}", name);

        // set parameter in
        in = swaggerParameter.getIn();
        parameter.setIn(in);
        log.info("Parameter In :{}", in);

        // set parameter description
        description = swaggerParameter.getDescription();
        parameter.setDescription(description);
        if(description != null) swaggerInfo.add(description);
        log.info("Parameter Description :{}", description);

        // set parameter required
        required = swaggerParameter.getRequired();
        parameter.setRequired(required);
        log.info("Parameter Required :{}", required);

        // media_type and format
        if (swaggerParameter instanceof QueryParameter) {
            media_type = ((QueryParameter) swaggerParameter).getType();
            format = ((QueryParameter) swaggerParameter).getFormat();
            parameter.setMedia_type(media_type);
            parameter.setFormat(format);
            log.info("Parameter Media_Type :{}", media_type);
            log.info("Parameter Format :{}", format);
        } else if (swaggerParameter instanceof PathParameter) {
            media_type = ((PathParameter) swaggerParameter).getType();
            format = ((PathParameter) swaggerParameter).getFormat();
            parameter.setMedia_type(media_type);
            parameter.setFormat(format);
            log.info("Parameter Media_Type :{}", media_type);
            log.info("Parameter Format :{}", format);
        } else if (swaggerParameter instanceof FormParameter) {
            media_type = ((FormParameter) swaggerParameter).getType();
            format = ((FormParameter) swaggerParameter).getFormat();
            parameter.setMedia_type(media_type);
            parameter.setFormat(format);
            log.info("Parameter Media_Type :{}", media_type);
            log.info("Parameter Format :{}", format);
        } else if (swaggerParameter instanceof HeaderParameter) {
            media_type = ((HeaderParameter) swaggerParameter).getType();
            format = ((HeaderParameter) swaggerParameter).getFormat();
            parameter.setMedia_type(media_type);
            parameter.setFormat(format);
            log.info("Parameter Media_Type :{}", media_type);
            log.info("Parameter Format :{}", format);
        }

        try {
            if(swaggerInfo.size() != 0) {
                // parse LDA
                ArrayList<String> LDAWord = swaggerToLDA.swaggerParseLDA(swaggerInfo.toArray(new String[0]), stemmingAndTermsTable);
                // parse WordNet
                ArrayList<String> WordNetWord = wordNetExpansion.getWordNetExpansion(LDAWord, stemmingAndTermsTable);

                parameter.setOriginalWord(LDAWord);
                parameter.setWordnetWord(WordNetWord);
            }
        } catch (IOException e) {
            log.info("Parse word topic error :{}", e.toString());
        }

        // Build relationship
        Input input = new Input();
        ParameterGraph parameterGraph = new ParameterGraph(parameter);

        parameterGraph.setInput(input);

        return parameterGraph;
    }

    private ParameterGraph getParameterBeanEntity(String key, Property property, String in) {

        // store swagger parse information
        ArrayList<String> swaggerInfo = new ArrayList<>();
        // For saving key: stemming term --> value: original term
        HashMap<String, String> stemmingAndTermsTable = new HashMap<String, String>();

        log.info("------ create ParameterBean by Property entity: {}", key);
        String name = null;
        String description = null;
        String media_type = null;
        String format = null;
        boolean required = false;

        name = key;
        description = property.getDescription();
        required = property.getRequired();
        media_type = property.getType();
        format = property.getFormat();

        log.info("Parameter Name :{}", name);
        log.info("Parameter In :{}", in);
        log.info("Parameter Description :{}", property.getDescription());
        log.info("Parameter Required :{}", property.getRequired());
        log.info("Parameter Media_Type :{}", property.getType());
        log.info("Parameter Format :{}", property.getFormat());

        Parameter parameter = new Parameter();
        parameter.setName(name);
        parameter.setIn(in);
        parameter.setDescription(description);
        parameter.setRequired(required);
        parameter.setFormat(format);
        parameter.setMedia_type(media_type);

        if(name != null) swaggerInfo.add(name);
        if(description != null) swaggerInfo.add(description);

        try {
            if(swaggerInfo.size() != 0) {
                // parse LDA
                ArrayList<String> LDAWord = swaggerToLDA.swaggerParseLDA(swaggerInfo.toArray(new String[0]), stemmingAndTermsTable);
                // parse WordNet
                ArrayList<String> WordNetWord = wordNetExpansion.getWordNetExpansion(LDAWord, stemmingAndTermsTable);

                parameter.setOriginalWord(LDAWord);
                parameter.setWordnetWord(WordNetWord);
            }
        } catch (IOException e) {
            log.info("Parse word topic error :{}", e.toString());
        }

        // Build relationship
        Input input = new Input();
        ParameterGraph parameterGraph = new ParameterGraph(parameter);

        parameterGraph.setInput(input);

        return parameterGraph;
    }

    private void parseBodyParameter(BodyParameter p, Map<String, Model> definitions, OperationGraph operationGraph) {

        String in = "body";
        if (p.getSchema() instanceof RefModel) {
            RefModel mod = (RefModel) p.getSchema();
            parseRefModel(mod, definitions, in, operationGraph, "parameter");
        } else {
            log.info("----- Error finding body parameter is not RefModel");
        }

    }

    private void parseRefModel(RefModel refModel, Map<String, Model> definitions, String in, OperationGraph operationGraph, String paramOrResponse) {

        log.info("----- go to RefModel {}", refModel.getSimpleRef());
        Model petModel = definitions.get(refModel.getSimpleRef());
        if (petModel instanceof ComposedModel) {
            log.info("---- detect allOf on definition:{} at {}", refModel.getSimpleRef(), paramOrResponse);
            parseComposedModel((ComposedModel) petModel, definitions, in, operationGraph, paramOrResponse);
        }
        if (petModel != null) {
            Map<String, Property> sp = petModel.getProperties();
            if (sp != null) {
                for (String s : sp.keySet()) {
                    log.info("---- create ParameterBean on definition:{} at {} -- key: {}", refModel.getSimpleRef(),
                            paramOrResponse, s);
                    if (sp.get(s) instanceof RefProperty) {
                        parseRefProperty((RefProperty) sp.get(s), definitions, in, operationGraph, paramOrResponse);
                    } else {
                        if (paramOrResponse.equals("parameter")) {
                            ParameterGraph pb = getParameterBeanEntity(s, sp.get(s), in);
                            if (pb != null) {
                                operationGraph.setParameterGraph(pb);
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseComposedModel(ComposedModel mod, Map<String, Model> definitions, String in, OperationGraph operationGraph, String paramOrResponse) {

        for (Model model : mod.getAllOf()) {
            if (model instanceof RefModel) {
                parseRefModel((RefModel) model, definitions, in, operationGraph, paramOrResponse);
            } else {
                if (model.getProperties() != null) {
                    Map<String, Property> sp = model.getProperties();
                    for (String s : sp.keySet()) {
                        log.info("---- create ParameterBean on allOf at {} -- key: {}", paramOrResponse, s);
                        if (paramOrResponse.equals("parameter")) {
                            ParameterGraph pb = getParameterBeanEntity(s, sp.get(s), in);
                            if (pb != null) {
                                operationGraph.setParameterGraph(pb);
                            }
                        }
                        if (sp.get(s) instanceof RefProperty) {
                            parseRefProperty((RefProperty) sp.get(s), definitions, in, operationGraph, paramOrResponse);
                        }
                    }

                }
            }
        }
    }

    private void parseRefProperty(RefProperty ref, Map<String, Model> definitions, String in, OperationGraph operationGraph, String flag) {

        log.info("---- go to RefProperty {}", ref.getSimpleRef());
        Model petModel = definitions.get(ref.getSimpleRef());

        if (petModel != null) {
            Map<String, Property> sp = petModel.getProperties();

            if (petModel instanceof ComposedModel) {
                log.info("---- detect allOf on definition:{} at {}", ref.getSimpleRef(), flag);
                parseComposedModel((ComposedModel) petModel, definitions, in, operationGraph, flag);
            }

            if (sp != null) {
                for (String s : sp.keySet()) {
                    log.info("---- create ParameterBean on definition:{} at {} -- key: {}", ref.getSimpleRef(), flag,
                            s);
                    if (flag.equals("parameter")) {
                        ParameterGraph pb = getParameterBeanEntity(s, sp.get(s), in);
                        if (pb != null) {
                            operationGraph.setParameterGraph(pb);
                        }
                    }
                }
            }
        }
    }

    private void parseStatusCodeRefModel(RefModel refModel, Map<String, Model> definitions, String in, StatusCodeGraph statusCodeGraph, String statusCode) {

        log.info("----- go to RefModel {}", refModel.getSimpleRef());
        Model petModel = definitions.get(refModel.getSimpleRef());
        if (petModel instanceof ComposedModel) {
            log.info("---- detect allOf on definition:{} at {}", refModel.getSimpleRef(), "status code");
            parseStatusCodeComposedModel((ComposedModel) petModel, definitions, in, statusCodeGraph, statusCode);
        }
        if (petModel != null) {
            Map<String, Property> sp = petModel.getProperties();
            if (sp != null) {
                for (String s : sp.keySet()) {
                    log.info("---- create ParameterBean on definition:{} at {} -- key: {}", refModel.getSimpleRef(),
                            "status code", s);
                    if (sp.get(s) instanceof RefProperty) {
                        parseStatusCodeRefProperty((RefProperty) sp.get(s), definitions, in, statusCodeGraph, statusCode);
                    } else {
                        statusCodeGraph.setResponseGraph(getResponseBeanEntity(s, sp.get(s), statusCode));
                    }
                }
            }
        }
    }

    private void parseStatusCodeComposedModel(ComposedModel mod, Map<String, Model> definitions, String in, StatusCodeGraph statusCodeGraph, String statusCode) {

        for (Model model : mod.getAllOf()) {
            if (model instanceof RefModel) {
                parseStatusCodeRefModel((RefModel) model, definitions, in, statusCodeGraph, statusCode);
            } else {
                if (model.getProperties() != null) {
                    Map<String, Property> sp = model.getProperties();
                    for (String s : sp.keySet()) {
                        log.info("---- create ParameterBean on allOf at {} -- key: {}", "status code", s);
                        statusCodeGraph.setResponseGraph(getResponseBeanEntity(s, sp.get(s), statusCode));

                        if (sp.get(s) instanceof RefProperty) {
                            parseStatusCodeRefProperty((RefProperty) sp.get(s), definitions, in, statusCodeGraph, statusCode);
                        }
                    }

                }
            }
        }
    }

    private void parseStatusCodeRefProperty(RefProperty ref, Map<String, Model> definitions, String in, StatusCodeGraph statusCodeGraph, String statusCode) {

        log.info("---- go to RefProperty {}", ref.getSimpleRef());
        Model petModel = definitions.get(ref.getSimpleRef());

        if (petModel != null) {
            Map<String, Property> sp = petModel.getProperties();

            if (petModel instanceof ComposedModel) {
                log.info("---- detect allOf on definition:{} at {}", ref.getSimpleRef(), "status code");
                parseStatusCodeComposedModel((ComposedModel) petModel, definitions, in, statusCodeGraph, statusCode);
            }

            if (sp != null) {
                for (String s : sp.keySet()) {
                    log.info("---- create ParameterBean on definition:{} at {} -- key: {}", ref.getSimpleRef(), "status code", s);
                    statusCodeGraph.setResponseGraph(getResponseBeanEntity(s, sp.get(s), statusCode));
                }
            }
        }
    }

    private void parseResponseProperty(String key, Property property, Map<String, Model> definitions, StatusCodeGraph statusCodeGraph, String statusCode) {

        if (property instanceof ObjectProperty) {
            log.info("---- parse response ObjectProperty: {}", key);
            ObjectProperty op = (ObjectProperty) property;
            Map<String, Property> paramTable = op.getProperties();
            if (paramTable != null) {
                for (Entry<String, Property> entry : paramTable.entrySet()) {
                    parseResponseProperty(entry.getKey(), entry.getValue(), definitions, statusCodeGraph, statusCode);
                }
            }
        } else if (property instanceof ArrayProperty) {
            log.info("---- parse response ArrayProperty: {}", key);
            ArrayProperty array = (ArrayProperty) property;
            Property pItems = array.getItems();
            if (pItems instanceof RefProperty) {
                parseStatusCodeRefProperty((RefProperty) pItems, definitions, "", statusCodeGraph, statusCode);
            }
        } else if (property instanceof RefProperty) {
            log.info("---- parse response RefProperty: {}", key);
            parseStatusCodeRefProperty((RefProperty) property, definitions, "", statusCodeGraph, statusCode);
        } else {
            log.info("----- parse response final Property: {}, and save to operationBean!", key);
            statusCodeGraph.setResponseGraph(getResponseBeanEntity(key, property, statusCode));
        }
    }

    private ResponseGraph getResponseBeanEntity(String key, Property swaggerResponse, String statusCode) {

        // store swagger parse information
        ArrayList<String> swaggerInfo = new ArrayList<>();
        // For saving key: stemming term --> value: original term
        HashMap<String, String> stemmingAndTermsTable = new HashMap<String, String>();

        String name = null;
        String mediaType = null;
        String description = null;
        String format = null;
        boolean required = false;

        Response response = new Response();
        log.info("Response Name :{}", key);
        log.info("Response Media_Type :{}", swaggerResponse.getType());
        log.info("Response Description :{}", swaggerResponse.getDescription());
        log.info("Response Format :{}", swaggerResponse.getFormat());
        log.info("Response Required :{}", swaggerResponse.getRequired());
        name = key;
        mediaType = swaggerResponse.getType();
        description = swaggerResponse.getDescription();
        format = swaggerResponse.getFormat();
        required = swaggerResponse.getRequired();
        response.setName(name);
        response.setMedia_type(mediaType);
        response.setDescription(description);
        response.setFormat(format);
        response.setRequired(required);

        if(name != null) swaggerInfo.add(name);
        if(description != null) swaggerInfo.add(description);

        try {
            if(swaggerInfo.size() != 0 && statusCode.equals("200")) {
                // parse LDA
                ArrayList<String> LDAWord = swaggerToLDA.swaggerParseLDA(swaggerInfo.toArray(new String[0]), stemmingAndTermsTable);
                // parse WordNet
                ArrayList<String> WordNetWord = wordNetExpansion.getWordNetExpansion(LDAWord, stemmingAndTermsTable);

                response.setOriginalWord(LDAWord);
                response.setWordnetWord(WordNetWord);
            }
        } catch (IOException e) {
            log.info("Parse word topic error :{}", e.toString());
        }

        ResponseGraph responseGraph = new ResponseGraph(response);
        // Build relationship
        Have have = new Have();
        responseGraph.setHave(have);

        return responseGraph;
    }
}
