package ntou.soselab.swagger.web.homepage;

import ntou.soselab.swagger.neo4j.domain.service.Operation;
import ntou.soselab.swagger.neo4j.domain.service.Resource;
import ntou.soselab.swagger.neo4j.repositories.service.OperationRepository;
import ntou.soselab.swagger.neo4j.repositories.service.ResourceRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class FeatureCount {

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    OperationRepository operationRepository;

    Logger log = LoggerFactory.getLogger(FeatureCount.class);

    @CrossOrigin
    @RequestMapping(value = "/getServiceLevel", method = RequestMethod.GET)
    public String getServiceLevel() {
        int serviceTotal = resourceRepository.totalResource();

        ArrayList<ServiceFeature> result = new ArrayList<>();
        ServiceFeature httpsSupport = new ServiceFeature();
        ServiceFeature userAuthentication = new ServiceFeature();
        ServiceFeature most20perations = new ServiceFeature();
        ServiceFeature exampleAPIConversations = new ServiceFeature();
        httpsSupport.setFeature("HTTPS support");
        userAuthentication.setFeature("User authentication");
        most20perations.setFeature("At most 20 operations");
        exampleAPIConversations.setFeature("Example API conversations");
        int support = 0;
        int Authentication = 0;
        int mostOperations = 0;
        int example = 0;

        for(Resource resource : resourceRepository.findAll()) {
            for(String feature : resource.getFeature()) {
                if(feature.equals("HTTPS support")) {
                    support++;
                }
                if(feature.equals("User authentication")) {
                    Authentication++;
                }
                if(feature.equals("At most 20 operations")) {
                    mostOperations++;
                }
                if(feature.equals("Example API conversations")) {
                    example++;
                }
            }
        }
        log.info("support :{}", support);
        log.info("Authentication :{}", Authentication);
        log.info("mostOperations :{}", mostOperations);
        log.info("example :{}", example);
        httpsSupport.setQuantity(support);
        userAuthentication.setQuantity(Authentication);
        most20perations.setQuantity(mostOperations);
        exampleAPIConversations.setQuantity(example);
        log.info("httpsSupport :{}", httpsSupport);
        log.info("userAuthentication :{}", userAuthentication);
        log.info("most20perations :{}", most20perations);
        log.info("exampleAPIConversations :{}", exampleAPIConversations);
        result.add(httpsSupport);
        result.add(userAuthentication);
        result.add(most20perations);
        result.add(exampleAPIConversations);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalService", serviceTotal);
        jsonObject.put("serviceLevel", result);
        log.info("service feature :{}", jsonObject);
        return jsonObject.toString();
    }

    @CrossOrigin
    @RequestMapping(value = "/getEndpointLevel", method = RequestMethod.GET)
    public String getEndpointLevel() {
        int operationTotal = operationRepository.totalOperation();

        ArrayList<EndpointFeature> result = new ArrayList<>();
        EndpointFeature restStyle = new EndpointFeature();
        EndpointFeature httpStatus = new EndpointFeature();
        EndpointFeature errorMessage = new EndpointFeature();
        EndpointFeature inputJson = new EndpointFeature();
        EndpointFeature outputJson = new EndpointFeature();
        restStyle.setFeature("REST-style URls");
        httpStatus.setFeature("HTTP status code use");
        errorMessage.setFeature("Explain Error messages");
        inputJson.setFeature("Input format JSON");
        outputJson.setFeature("Output format JSON");
        int style = 0;
        int statusCode = 0;
        int error = 0;
        int input = 0;
        int output = 0;

        for(Resource resource : resourceRepository.findAll()) {
            for(Operation operation : operationRepository.findOperationsByResource(resource.getNodeId())) {
                for(String feature : operation.getFeature()) {
                    if(feature.equals("REST-style URls")) {
                        style++;
                    }
                    if(feature.equals("HTTP status code use")) {
                        statusCode++;
                    }
                    if(feature.equals("Explain Error messages")) {
                        error++;
                    }
                    if(feature.equals("Input format JSON")) {
                        input++;
                    }
                    if(feature.equals("Output format JSON")) {
                        output++;
                    }
                }
            }
        }
        log.info("style :{}", style);
        log.info("statusCode :{}", statusCode);
        log.info("error :{}", error);
        log.info("input :{}", input);
        log.info("output :{}", output);
        restStyle.setQuantity(style);
        httpStatus.setQuantity(statusCode);
        errorMessage.setQuantity(error);
        inputJson.setQuantity(input);
        outputJson.setQuantity(output);
        result.add(restStyle);
        result.add(httpStatus);
        result.add(errorMessage);
        result.add(inputJson);
        result.add(outputJson);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalOperation", operationTotal);
        jsonObject.put("endpointLevel", result);
        log.info("endpoint feature :{}", jsonObject);
        return jsonObject.toString();
    }

    @CrossOrigin
    @RequestMapping(value = "/getStandardOAS", method = RequestMethod.GET)
    public String getStandardOAS() {
        ArrayList<StandardOAS> result = new ArrayList<>();

        for(Resource resource : resourceRepository.findAll()) {
            ArrayList<String> serviceFeature = resource.getFeature();
            if(serviceFeature.contains("HTTPS support") && serviceFeature.contains("User authentication") && serviceFeature.contains("At most 20 operations")) {

                for(Operation operation : operationRepository.findOperationsByResource(resource.getNodeId())) {
                    ArrayList<String> endpointFeature = operation.getFeature();
                    if(endpointFeature.contains("REST-style URls") && endpointFeature.contains("HTTP status code use") && endpointFeature.contains("Explain Error messages") && endpointFeature.contains("Input format JSON") && endpointFeature.contains("Output format JSON")) {
                        StandardOAS standardOAS = new StandardOAS();
                        standardOAS.setOASId(resource.getNodeId());
                        standardOAS.setOASTitle(resource.getTitle());
                        standardOAS.setOASProvider(resource.getProvider());
                        result.add(standardOAS);
                        break;
                    }
                }

            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", result);
        log.info("result :{}", jsonObject);
        return jsonObject.toString();
    }
}
