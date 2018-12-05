package ntou.soselab.swagger.neo4j.graph;

import ntou.soselab.swagger.neo4j.domain.relationship.Endpoint;
import ntou.soselab.swagger.neo4j.domain.service.Operation;

import java.util.ArrayList;

public class OperationGraph {

    Operation operation;
    Endpoint endpoint;
    ArrayList<ParameterGraph> parameterGraphs;
    ArrayList<StatusCodeGraph> statusCodeGraphs;

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

//    public ArrayList<ParameterGraph> getParameterGraphs() {
//        return parameterGraphs;
//    }

    // 避免該 path 沒有參數
    public ArrayList<ParameterGraph> getParameterGraphs() {
        if(parameterGraphs == null){
            parameterGraphs = new ArrayList<ParameterGraph>();
        }
        return parameterGraphs;
    }

    public void setParameterGraphs(ArrayList<ParameterGraph> parameterGraphs) {
        this.parameterGraphs = parameterGraphs;
    }

    public void setParameterGraph(ParameterGraph parameterGraph) {
        if(this.parameterGraphs == null){
            this.parameterGraphs = new ArrayList<ParameterGraph>();
        }
        this.parameterGraphs.add(parameterGraph);
    }

    public ArrayList<StatusCodeGraph> getStatusCodeGraphs() {
        return statusCodeGraphs;
    }

    public void setStatusCodeGraphs(ArrayList<StatusCodeGraph> statusCodeGraphs) {
        this.statusCodeGraphs = statusCodeGraphs;
    }

    public void setStatusCodeGraphs(StatusCodeGraph statusCodeGraph) {
        if(this.statusCodeGraphs == null){
            this.statusCodeGraphs = new ArrayList<StatusCodeGraph>();
        }
        this.statusCodeGraphs.add(statusCodeGraph);
    }
}
