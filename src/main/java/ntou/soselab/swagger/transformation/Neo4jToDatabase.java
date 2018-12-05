package ntou.soselab.swagger.transformation;

import ntou.soselab.swagger.neo4j.domain.relationship.Endpoint;
import ntou.soselab.swagger.neo4j.domain.relationship.Have;
import ntou.soselab.swagger.neo4j.domain.relationship.Input;
import ntou.soselab.swagger.neo4j.domain.relationship.Output;
import ntou.soselab.swagger.neo4j.domain.service.*;
import ntou.soselab.swagger.neo4j.graph.*;
import ntou.soselab.swagger.neo4j.repositories.relationship.EndpointRepository;
import ntou.soselab.swagger.neo4j.repositories.relationship.HaveRepository;
import ntou.soselab.swagger.neo4j.repositories.relationship.InputRepository;
import ntou.soselab.swagger.neo4j.repositories.relationship.OutputRepository;
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
        for(OperationGraph operationGraph: resourceGraph.getOperationGraphs()){
            log.info("build Relationships Between ConcreteServices");
            log.info("Operation info :{}", operationGraph.getOperation().getPath());
            buildRelationshipsBetweenConcreteServices(operationGraph);
        }

        log.info("second step: save Resource And Relationship With Operation");
        for(OperationGraph operationGraph: resourceGraph.getOperationGraphs()){
            log.info("save Resource And Relationship With Operation");
            saveResourceAndRelationshipWithOperation(resourceGraph.getResource(), operationGraph.getOperation(), operationGraph.getEndpoint());
        }

    }

    public void buildRelationshipsBetweenConcreteServices(OperationGraph operationGraph){

        for(ParameterGraph parameterGraph: operationGraph.getParameterGraphs()){
            log.info("Operation name :{} To Parameter name :{}", operationGraph.getOperation().getPath(), parameterGraph.getParameter().getName());
            buildInputBetweenConcreteServices(operationGraph.getOperation(), parameterGraph.getParameter(), parameterGraph.getInput());
        }
        for(StatusCodeGraph statusCodeGraph: operationGraph.getStatusCodeGraphs()){

            for(ResponseGraph responseGraph :  statusCodeGraph.getResponseGraphs()){
                buildHaveBetweenConcreteServices(statusCodeGraph.getStatusCode(), responseGraph.getResponse(), responseGraph.getHave());
            }

            buildOutputBetweenConcreteServices(operationGraph.getOperation(), statusCodeGraph.getStatusCode(), statusCodeGraph.getOutput());
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

    // save Resource and Action relationship with Operation
    private void saveResourceAndRelationshipWithOperation(Resource resource, Operation operation, Endpoint endpoint){
        endpoint.addRelationshipToResourceAndOperation(resource, operation);
        endpointRepository.save(endpoint);
    }
}
