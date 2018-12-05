package ntou.soselab.swagger.neo4j.domain.relationship;

import ntou.soselab.swagger.neo4j.domain.service.Operation;
import ntou.soselab.swagger.neo4j.domain.service.Response;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type="output")
public class Output {
    @GraphId
    Long graphId;

    @StartNode
    Operation operation ;

    @EndNode
    Response response;

    public Output() {
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    private void addOutputToOperationAndResponse(){
        if(this.operation != null){
            this.operation.setOutput(this);
        }

        if(this.response != null){
            this.response.setOutput(this);
        }
    }

    public void addOperationAndResponse(Operation operation, Response response){
        this.operation = operation;
        this.response = response;
        addOutputToOperationAndResponse();
    }
}
