package ntou.soselab.swagger.neo4j.graph;

import ntou.soselab.swagger.neo4j.domain.service.Operation;
import ntou.soselab.swagger.neo4j.domain.service.Resource;

import java.util.ArrayList;

public class ResourceGraph {

    Resource resource;
    ArrayList<OperationGraph> operationGraphs;

    public ResourceGraph(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ArrayList<OperationGraph> getOperationGraphs() {
        return operationGraphs;
    }

    public void setOperationGraphs(ArrayList<OperationGraph> operationGraphs) {
        this.operationGraphs = operationGraphs;
    }

    public void setOperationGraph(OperationGraph operationGraph) {
        if(this.operationGraphs == null){
            this.operationGraphs = new ArrayList<OperationGraph>();
        }
        this.operationGraphs.add(operationGraph);
    }
}
