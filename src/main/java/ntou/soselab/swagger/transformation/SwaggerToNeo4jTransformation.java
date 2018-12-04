package ntou.soselab.swagger.transformation;

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
import ntou.soselab.swagger.neo4j.domain.relationship.Endpoint;
import ntou.soselab.swagger.neo4j.domain.service.Operation;
import ntou.soselab.swagger.neo4j.domain.service.Resource;
import ntou.soselab.swagger.neo4j.graph.OperationGraph;
import ntou.soselab.swagger.neo4j.graph.ParameterGraph;
import ntou.soselab.swagger.neo4j.graph.ResourceGraph;
import ntou.soselab.swagger.neo4j.graph.ResponseGraph;
import ntou.soselab.swagger.neo4j.repositories.service.ResourceRepository;
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
    ResourceRepository resourceRepository;
    @Autowired
    Neo4jToDatabase neo4jToDatabase;

    public void parseSwaggerDocument(String swaggerDocument) {
        Swagger swagger = new SwaggerParser().parse(swaggerDocument);
        Resource resource = new Resource();
        ResourceGraph resourceGraph = getResourceInformation(swagger, resource);

        for (String p : swagger.getPaths().keySet()) {
            if (swagger.getPaths().get(p).getDelete() != null) {
                log.info("--- operation:DELETE on {}", p);
                OperationGraph operationGraph = getOperationInformation(swagger.getPaths().get(p).getDelete(),
                        "delete", p);
                findAllTheParametersFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getDelete()); // set
                // Parameters
                findAllTheResponsesFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getDelete()); // set Response
                resourceGraph.setOperationGraph(operationGraph);
            }
            if (swagger.getPaths().get(p).getGet() != null) {
                log.info("--- operation:GET on {}", p);
                OperationGraph operationGraph = getOperationInformation(swagger.getPaths().get(p).getGet(), "get",
                        p);
                findAllTheParametersFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getGet()); // set
                findAllTheResponsesFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getGet()); // set Response
                resourceGraph.setOperationGraph(operationGraph);
            }
            if (swagger.getPaths().get(p).getPatch() != null) {
                log.info("--- operation:PATCH on {}", p);
                OperationGraph operationGraph = getOperationInformation(swagger.getPaths().get(p).getPatch(),
                        "patch", p);
                findAllTheParametersFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getPatch()); // set
                findAllTheResponsesFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getPatch()); // set Response
                resourceGraph.setOperationGraph(operationGraph);
            }
            if (swagger.getPaths().get(p).getPost() != null) {
                log.info("--- operation:POST on {}", p);
                OperationGraph operationGraph = getOperationInformation(swagger.getPaths().get(p).getPost(),
                        "post", p);
                findAllTheParametersFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getPost()); // set
                findAllTheResponsesFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getPost()); // set Response
                resourceGraph.setOperationGraph(operationGraph);
            }
            if (swagger.getPaths().get(p).getPut() != null) {
                log.info("--- operation:PUT on {}", p);
                OperationGraph operationGraph = getOperationInformation(swagger.getPaths().get(p).getPut(), "put",
                        p);
                findAllTheParametersFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getPut()); // set
                findAllTheResponsesFromOperation(operationGraph, swagger.getDefinitions(),
                        swagger.getPaths().get(p).getPut()); // set Response
                resourceGraph.setOperationGraph(operationGraph);
            }
        }

        // neo4jToDatabase.buildRelationshipStartWithResource(resourceGraph);
    }

    public ResourceGraph getResourceInformation(Swagger swagger, Resource resource) {

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
        resource.setHost(swagger.getHost());

        // get basePath
        resource.setBasePath(swagger.getBasePath());

        // get title
        title = swagger.getInfo().getTitle();
        log.info("Title :{}", title);
        resource.setTitle(swagger.getInfo().getTitle());

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
        resource.setDescription(swagger.getInfo().getDescription());

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

        return new ResourceGraph(resource);
    }

    private OperationGraph getOperationInformation(io.swagger.models.Operation swaggerOperation, String Swaggeraction, String path) {

        Operation operation = new Operation();
        operation.setDescription(swaggerOperation.getDescription());
        log.info("operation description :{}", swaggerOperation.getDescription());
        operation.setPath(path);
        log.info("operation path :{}", path);
        operation.setOperationAction(Swaggeraction);
        log.info("operation action :{}", Swaggeraction);

        // record relationship type
        Endpoint endpoint = new Endpoint();
        OperationGraph operationGraph = new OperationGraph();

        operationGraph.setEndpoint(endpoint);
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

    private void findAllTheResponsesFromOperation(OperationGraph operationGraph, Map<String, Model> definitions,
                                                  io.swagger.models.Operation operation) {

        Map<String, io.swagger.models.Response> responses = operation.getResponses();

        for(String key : responses.keySet()) {
            if (responses.get(key) != null) {
                log.info("status code :{}", key);
                log.info("Response Description :{}", responses.get(key).getDescription());

                if(responses.get(key).getSchema() != null) {
                    Property property = responses.get(key).getSchema();
                    /*
                     * if(property instanceof RefProperty){ //不取只顯示成功的字樣
                     * log.info("--- detect response: 200");
                     * parseRefProperty((RefProperty)property, definitions, "",
                     * operationBean, "response", new ArrayList<String>()); }
                     */
                    if (property instanceof RefProperty) { // 不取只顯示成功的字樣
                        parseRefProperty((RefProperty) property, definitions, "", operationGraph, "response");

                    } else {
                        parseResponseProperty(null, property, definitions, operationGraph, new ArrayList<String>());
                    }
                }
            }
        }
    }

    private ParameterGraph getParameterBeanEntity(io.swagger.models.parameters.Parameter swaggerParameter) {
        String name = null;
        String in = null;
        String description = null;
        String media_type = null;
        String format = null;
        boolean required = false;
        log.info("------ create ParameterGraph by non body: {}", swaggerParameter.getName());

        ntou.soselab.swagger.neo4j.domain.service.Parameter parameter = new ntou.soselab.swagger.neo4j.domain.service.Parameter();

        // set parameter Name
        name = swaggerParameter.getName();
        parameter.setName(name);
        log.info("Parameter Name :{}", name);
        // set parameter in
        in = swaggerParameter.getIn();
        parameter.setIn(in);
        log.info("Parameter In :{}", in);
        // set parameter description
        description = swaggerParameter.getDescription();
        parameter.setDescription(description);
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

        ParameterGraph parameterGraph = new ParameterGraph(parameter);
        return parameterGraph;
    }

    private ParameterGraph getParameterBeanEntity(String key, Property property, String in) {

        log.info("------ create ParameterBean by Property entity: {}", key);


        ntou.soselab.swagger.neo4j.domain.service.Parameter parameter = new ntou.soselab.swagger.neo4j.domain.service.Parameter();
        parameter.setDescription(property.getDescription());
        parameter.setName(key);
        parameter.setMedia_type(property.getType());
        log.info("Parameter Name :{}", key);
        log.info("Parameter In :{}", in);
        log.info("Parameter Description :{}", property.getDescription());
        log.info("Parameter Required :{}", property.getRequired());
        log.info("Parameter Media_Type :{}", property.getType());
        log.info("Parameter Format :{}", property.getFormat());


        ParameterGraph parameterGraph = new ParameterGraph(parameter);

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
//                        else if (paramOrResponse.equals("response")) {
//                            operationGraph
//                                    .setResponseBean(getResponseBeanEntity(s, sp.get(s), newparentParameterConcept));
//                        }
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
//                        else if (paramOrResponse.equals("response")) {
//                            operationGraph.setResponseBean(getResponseBeanEntity(s, sp.get(s), parentParameterConcept));
//                        }
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
                    } else if (flag.equals("response")) {
                        operationGraph.setResponseGraph(getResponseBeanEntity(s, sp.get(s)));
                    }
                }
            }
        }
    }

    private void parseResponseProperty(String key, Property property, Map<String, Model> definitions, OperationGraph operationGraph, ArrayList<String> parentConcepts) {

        if (key != null) {
            parentConcepts.add(key);
        }

        if (property instanceof ObjectProperty) {
            log.info("---- parse response ObjectProperty: {}", key);
            ObjectProperty op = (ObjectProperty) property;
            Map<String, Property> paramTable = op.getProperties();
            if (paramTable != null) {
                for (Entry<String, Property> entry : paramTable.entrySet()) {
                    parseResponseProperty(entry.getKey(), entry.getValue(), definitions, operationGraph, parentConcepts);
                }
            }
        } else if (property instanceof ArrayProperty) {
            log.info("---- parse response ArrayProperty: {}", key);
            ArrayProperty array = (ArrayProperty) property;
            Property pItems = array.getItems();
            if (pItems instanceof RefProperty) {
                parseRefProperty((RefProperty) pItems, definitions, "", operationGraph, "response");
            }
        } else if (property instanceof RefProperty) {
            log.info("---- parse response RefProperty: {}", key);
            parseRefProperty((RefProperty) property, definitions, "", operationGraph, "response");
        } else {
            log.info("----- parse response final Property: {}, and save to operationBean!", key);
            operationGraph.setResponseGraph(getResponseBeanEntity(key, property));
        }
    }

    private ResponseGraph getResponseBeanEntity(String key, Property swaggerResponse) {

        ntou.soselab.swagger.neo4j.domain.service.Response response = new ntou.soselab.swagger.neo4j.domain.service.Response();
        response.setName(key);
        response.setMedia_type(swaggerResponse.getType());
        log.info("Response Name :{}", key);
        log.info("Response Media_Type :{}", swaggerResponse.getType());
        log.info("Response Description :{}", swaggerResponse.getDescription());
        log.info("Response Format :{}", swaggerResponse.getFormat());
        log.info("Response Required :{}", swaggerResponse.getRequired());

        ResponseGraph responseGraph = new ResponseGraph(response);

        return responseGraph;
    }
}
