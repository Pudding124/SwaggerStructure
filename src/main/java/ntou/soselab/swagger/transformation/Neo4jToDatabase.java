package ntou.soselab.swagger.transformation;

import ntou.soselab.swagger.neo4j.domain.relationship.Endpoint;
import ntou.soselab.swagger.neo4j.domain.service.Operation;
import ntou.soselab.swagger.neo4j.domain.service.Resource;
import ntou.soselab.swagger.neo4j.graph.OperationGraph;
import ntou.soselab.swagger.neo4j.graph.ResourceGraph;
import ntou.soselab.swagger.neo4j.repositories.relationship.EndpointRepository;
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

    @Transactional(rollbackFor=Exception.class)
    public void buildRelationshipStartWithResource(ResourceGraph resourceGraph) {
        log.info("Start store node to neo4j");
        for(OperationGraph operationGraph: resourceGraph.getOperationGraphs()){
            log.info("save Resource And Relationship With Operation");
            saveResourceAndRelationshipWithOperation(resourceGraph.getResource(), operationGraph.getOperation(), operationGraph.getEndpoint());
        }
    }

    // save Resource and Action relationship with Operation
    private void saveResourceAndRelationshipWithOperation(Resource resource, Operation operation, Endpoint endpoint){

        endpoint.addRelationshipToResourceAndOperation(resource, operation);
        endpointRepository.save(endpoint);
    }
}
