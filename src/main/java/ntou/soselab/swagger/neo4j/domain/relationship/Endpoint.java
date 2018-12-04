package ntou.soselab.swagger.neo4j.domain.relationship;

import ntou.soselab.swagger.neo4j.domain.service.Operation;
import ntou.soselab.swagger.neo4j.domain.service.Resource;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type="endpoint")
public class Endpoint {
    @GraphId
    Long graphId;

    @StartNode
    Resource resource;

    @EndNode
    Operation operation;

    public Endpoint() {}

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public void addRelationshipToResourceAndOperation(Resource resource, Operation operation){
        this.resource=resource;
        this.operation=operation;

        if(this.resource != null){
            this.resource.setEndpoints(this);
        }
        if(this.operation != null){
            this.operation.setEndpoints(this);
        }
    }
}
