package ntou.soselab.swagger.transformation;

import ntou.soselab.swagger.neo4j.domain.relationship.*;
import ntou.soselab.swagger.neo4j.domain.service.*;
import ntou.soselab.swagger.neo4j.graph.*;
import ntou.soselab.swagger.neo4j.repositories.relationship.*;
import ntou.soselab.swagger.neo4j.repositories.service.OperationRepository;
import ntou.soselab.swagger.neo4j.repositories.service.ParameterRepository;
import ntou.soselab.swagger.neo4j.repositories.service.ResourceRepository;
import ntou.soselab.swagger.neo4j.repositories.service.ResponseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Neo4jToDatabase {

    Logger log = LoggerFactory.getLogger(Neo4jToDatabase.class);


    @Autowired
    EndpointRepository endpointRepository;
    @Autowired
    InputRepository inputRepository;
    @Autowired
    OutputRepository outputRepository;
    @Autowired
    HaveRepository haveRepository;
    @Autowired
    ActionRepository actionRepository;
    @Autowired
    ResourceRepository resourceRepository;
    @Autowired
    ParameterRepository parameterRepository;
    @Autowired
    ResponseRepository responseRepository;
    @Autowired
    OperationRepository operationRepository;

    @Transactional(rollbackFor=Exception.class)
    public void buildRelationshipStartWithResource(ResourceGraph resourceGraph) {
        log.info("Start store node to neo4j");
        log.info("start resource parseing: {}", resourceGraph.getResource().getTitle());
        log.info("first step: build Relationships Between ConcreteServices");

        // 將 resource 每個 path 裡面的 operation 先建構起來
        for(PathGraph pathGraph: resourceGraph.getPathGraphs()){
            log.info("build Relationships Between ConcreteServices");
            log.info("Path info :{}", pathGraph.getPath().getPath());
            buildRelationshipsBetweenConcreteServices(pathGraph);

            // 再將 operation 與 path 建構起來
            for(OperationGraph operationGraph : pathGraph.getOperationGraphs()) {
                log.info("build Relationship Between Path and Operation");
                buildActionBetweenPathAndOperation(pathGraph.getPath(), operationGraph.getOperation(), operationGraph.getAction());
            }
        }



        log.info("second step: save Resource And Relationship With Path");
        for(PathGraph pathGraph: resourceGraph.getPathGraphs()){
            log.info("save Resource And Relationship With Path");
            saveResourceAndRelationshipWithPath(resourceGraph.getResource(), pathGraph.getPath(), pathGraph.getEndpoint());
        }

    }

    public void buildRelationshipsBetweenConcreteServices(PathGraph pathGraph){

        // 將每個 path 裡面的操作都建構起來
        for(OperationGraph operationGraph : pathGraph.getOperationGraphs()) {
            for(ParameterGraph parameterGraph: operationGraph.getParameterGraphs()){
                buildInputBetweenConcreteServices(operationGraph.getOperation(), parameterGraph.getParameter(), parameterGraph.getInput());
            }
            for(StatusCodeGraph statusCodeGraph: operationGraph.getStatusCodeGraphs()){

                for(ResponseGraph responseGraph :  statusCodeGraph.getResponseGraphs()){
                    buildHaveBetweenConcreteServices(statusCodeGraph.getStatusCode(), responseGraph.getResponse(), responseGraph.getHave());
                }

                buildOutputBetweenConcreteServices(operationGraph.getOperation(), statusCodeGraph.getStatusCode(), statusCodeGraph.getOutput());
            }
        }
    }

    private void buildInputBetweenConcreteServices(Operation operation, Parameter parameter, Input input){
        // Add input relationship
        input.addInputAndParameter(operation, parameter);
        inputRepository.save(input);
    }

    private void buildOutputBetweenConcreteServices(Operation operation, StatusCode statusCode, Output output){

        // Add Output relationship
        output.addOperationAndStatusCode(operation, statusCode);
        outputRepository.save(output);
    }

    private void buildHaveBetweenConcreteServices(StatusCode statusCode, Response response, Have have){

        // Add Have relationship
        have.addStatusCodeAndResponse(statusCode, response);
        haveRepository.save(have);
    }

    private void buildActionBetweenPathAndOperation(Path path, Operation operation, Action action) {
        action.addRelationshipToResourceAndPath(path, operation);
        actionRepository.save(action);
    }

    // save Resource and Action relationship with Operation
    private void saveResourceAndRelationshipWithPath(Resource resource, Path path, Endpoint endpoint){
        endpoint.addRelationshipToResourceAndPath(resource, path);
        endpointRepository.save(endpoint);
    }
}
