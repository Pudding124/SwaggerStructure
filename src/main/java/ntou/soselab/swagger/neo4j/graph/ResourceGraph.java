package ntou.soselab.swagger.neo4j.graph;

import ntou.soselab.swagger.neo4j.domain.service.Resource;

import java.util.ArrayList;

public class ResourceGraph {

    Resource resource;
    ArrayList<PathGraph> pathGraphs;

    public ResourceGraph(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ArrayList<PathGraph> getPathGraphs() {
        return pathGraphs;
    }

    public void setPathGraphs(ArrayList<PathGraph> pathGraphs) {
        this.pathGraphs = pathGraphs;
    }

    public void setPathGraph(PathGraph pathGraph) {
        if(this.pathGraphs == null){
            this.pathGraphs = new ArrayList<PathGraph>();
        }
        this.pathGraphs.add(pathGraph);
    }
}
