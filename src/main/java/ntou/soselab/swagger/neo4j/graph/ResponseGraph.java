package ntou.soselab.swagger.neo4j.graph;

import ntou.soselab.swagger.neo4j.domain.relationship.Output;
import ntou.soselab.swagger.neo4j.domain.service.Response;

public class ResponseGraph {
    Response response;
    Output output;

    public ResponseGraph(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Output getOutput() {
        return output;
    }
    public void setOutput(Output output) {
        this.output = output;
    }
}
