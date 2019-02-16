package ntou.soselab.swagger.web.oaspage;

import ntou.soselab.swagger.cluster.Similarity;
import ntou.soselab.swagger.neo4j.domain.service.*;
import ntou.soselab.swagger.neo4j.repositories.service.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class OASPage {

    @Autowired
    ResourceRepository resourceRepository;
    @Autowired
    OperationRepository operationRepository;
    @Autowired
    PathRepository pathRepository;
    @Autowired
    ParameterRepository parameterRepository;
    @Autowired
    ResponseRepository responseRepository;
    @Autowired
    StatusCodeRepository statusCodeRepository;

    Logger log = LoggerFactory.getLogger(OASPage.class);


    @RequestMapping(value = "/getOASInformation/{id}", method = RequestMethod.GET)
    public String getOASBasicInformation(@PathVariable("id")Long resourceId) {

        Resource resource = resourceRepository.findResourceById(resourceId);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setTitle(resource.getTitle());
        resourceInfo.setFeatures(resource.getFeature());
        resourceInfo.setDescription(resource.getDescription());
        resourceInfo.setProvider(resource.getProvider());
        resourceInfo.setHost(resource.getHost());
        resourceInfo.setBaseUrl(resource.getBasePath());
        resourceInfo.setContact("this service provider by yahoo company");

        // get path info
        ArrayList<PathInfo> pathInfos = new ArrayList<>();
        for(Path path : pathRepository.findPathsByResource(resourceId)) {
            PathInfo pathInfo = new PathInfo();
            pathInfo.setEndpoint(path.getPath());

            // get operation info
            ArrayList<OperationInfo> operationInfos = new ArrayList<>();
            for(Operation operation : operationRepository.findOperationsByPath(path.getNodeId())) {
                OperationInfo operationInfo = new OperationInfo();
                operationInfo.setOperation(operation.getOperationAction());
                operationInfo.setDescription(operation.getDescription());

                // get input parameter info
                ArrayList<InputParameterInfo> inputParameterInfos = new ArrayList<>();
                for(Parameter parameter : parameterRepository.findParametersByOperationNoThreshold(operation.getNodeId())) {
                    InputParameterInfo inputParameterInfo = new InputParameterInfo();
                    inputParameterInfo.setId(parameter.getNodeId().toString());
                    inputParameterInfo.setDescription(parameter.getDescription());
                    inputParameterInfo.setIn(parameter.getIn());
                    inputParameterInfo.setParameter(parameter.getName());
                    inputParameterInfo.setRequired(parameter.isRequired());
                    inputParameterInfo.setType(parameter.getMedia_type());
                    inputParameterInfos.add(inputParameterInfo);
                }
                // get status code info
                ArrayList<JSONObject> statusCodeInfos = new ArrayList<>();
                for(StatusCode statusCode : statusCodeRepository.findStatusCodesByOperation(operation.getNodeId())) {
                    JSONObject jsonObject = new JSONObject();
                    String statusNumber = statusCode.getStatusCode();

                    JSONArray responses = new JSONArray();
                    for(Response response : responseRepository.findResponsesByStatusCode(statusCode.getNodeId())) {
                        JSONObject object = new JSONObject();
                        object.put("id", response.getNodeId());
                        object.put("type", response.getMedia_type());
                        object.put("parameter", response.getName());
                        object.put("description", response.getDescription());
                        object.put("required", response.getRequired());
                        responses.put(object);
                    }

                    jsonObject.put(statusNumber, responses);
                    statusCodeInfos.add(jsonObject);
                    log.info("status code :{}", statusCodeInfos);
                }
                operationInfo.setInputParameters(inputParameterInfos);
                operationInfo.setStatusCode(statusCodeInfos);
                operationInfos.add(operationInfo);
            }
            pathInfo.setOperations(operationInfos);
            pathInfos.add(pathInfo);
        }
        resourceInfo.setEndpoints(pathInfos);
        JSONObject jsonObjectMary = new JSONObject(resourceInfo);
        log.info("ans :{}",jsonObjectMary);
        return jsonObjectMary.toString();
    }
}
