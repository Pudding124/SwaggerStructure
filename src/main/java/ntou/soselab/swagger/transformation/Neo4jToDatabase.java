package ntou.soselab.swagger.transformation;

import ntou.soselab.swagger.neo4j.domain.relationship.Endpoint;
import ntou.soselab.swagger.neo4j.domain.relationship.Input;
import ntou.soselab.swagger.neo4j.domain.relationship.Output;
import ntou.soselab.swagger.neo4j.domain.service.Operation;
import ntou.soselab.swagger.neo4j.domain.service.Parameter;
import ntou.soselab.swagger.neo4j.domain.service.Resource;
import ntou.soselab.swagger.neo4j.domain.service.Response;
import ntou.soselab.swagger.neo4j.graph.OperationGraph;
import ntou.soselab.swagger.neo4j.graph.ParameterGraph;
import ntou.soselab.swagger.neo4j.graph.ResourceGraph;
import ntou.soselab.swagger.neo4j.graph.ResponseGraph;
import ntou.soselab.swagger.neo4j.repositories.relationship.EndpointRepository;
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
        for(ResponseGraph responseGraph: operationGraph.getResponseGraphs()){
            buildOutputBetweenConcreteServices(operationGraph.getOperation(), responseGraph.getResponse(), responseGraph.getOutput());
        }
    }

    private void buildInputBetweenConcreteServices(Operation operation, Parameter parameter, Input input){
        // Add input relationship
        input.addInputAndParameter(operation, parameter);
        inputRepository.save(input);
    }

    private void buildOutputBetweenConcreteServices(Operation operation, Response response, Output output){

        // Add Output relationship
        output.addOperationAndResponse(operation, response);
        outputRepository.save(output);
    }

    // save Resource and Action relationship with Operation
    private void saveResourceAndRelationshipWithOperation(Resource resource, Operation operation, Endpoint endpoint){
        endpoint.addRelationshipToResourceAndOperation(resource, operation);
        endpointRepository.save(endpoint);
    }
}
