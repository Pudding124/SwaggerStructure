package ntou.soselab.swagger.neo4j.domain.service;

import ntou.soselab.swagger.neo4j.domain.relationship.Action;
import ntou.soselab.swagger.neo4j.domain.relationship.Endpoint;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

public class Path extends ConcreteService {

    String path;

    public Path() {}

    public Path(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Relationship(type = "endpoint", direction = Relationship.INCOMING)
    Set<Endpoint> endpoints = new HashSet<>();

    public Set<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Endpoint endpoint) {
        this.endpoints.add(endpoint);
    }

    @Relationship(type = "action", direction = Relationship.OUTGOING)
    Set<Action> actions = new HashSet<>();

    public Set<Action> getActions() {
        return actions;
    }

    public void setActions(Action action) {
        this.actions.add(action);
    }
}