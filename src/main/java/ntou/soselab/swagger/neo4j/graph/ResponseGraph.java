package ntou.soselab.swagger.neo4j.graph;

import ntou.soselab.swagger.neo4j.domain.service.Response;

public class ResponseGraph {
    Response response;

    public ResponseGraph(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
