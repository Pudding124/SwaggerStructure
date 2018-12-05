package ntou.soselab.swagger.neo4j.graph;

import ntou.soselab.swagger.neo4j.domain.relationship.Endpoint;
import ntou.soselab.swagger.neo4j.domain.service.Operation;

import java.util.ArrayList;

public class OperationGraph {

    Operation operation;
    Endpoint endpoint;
    ArrayList<ParameterGraph> parameterGraphs;
    ArrayList<ResponseGraph> responseGraphs;

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

//    public ArrayList<ResponseGraph> getResponseGraphs() {
//        return responseGraphs;
//    }
    public ArrayList<ResponseGraph> getResponseGraphs() {
        if(this.responseGraphs == null){
            this.responseGraphs=new ArrayList<ResponseGraph>();
        }
        return responseGraphs;
    }

    public void setResponseGraphs(ArrayList<ResponseGraph> responseGraphs) {
        this.responseGraphs = responseGraphs;
    }

    public void setParameterGraph(ParameterGraph parameterGraph) {
        if(this.parameterGraphs == null){
            this.parameterGraphs = new ArrayList<ParameterGraph>();
        }
        this.parameterGraphs.add(parameterGraph);
    }

    public void setResponseGraph(ResponseGraph responseGraph) {
        if(this.responseGraphs == null){
            this.responseGraphs = new ArrayList<ResponseGraph>();
        }
        this.responseGraphs.add(responseGraph);
    }
}
