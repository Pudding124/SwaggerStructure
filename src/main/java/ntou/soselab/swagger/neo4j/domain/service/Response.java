package ntou.soselab.swagger.neo4j.domain.service;

import ntou.soselab.swagger.neo4j.domain.relationship.Have;
import ntou.soselab.swagger.neo4j.domain.relationship.Output;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Response extends ConcreteService {

    String name;
    String media_type;

    public Response(Response response) {
        this.name = response.getName();
        this.media_type = response.getMedia_type();
    }

    public Response() {
        super();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMedia_type() {
        return media_type;
    }
    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    @Relationship(type = "have", direction = Relationship.INCOMING)
    Set<Have> haves = new HashSet<>();

    public Set<Have> getHaves() {
        return haves;
    }
    public void setHave(Have have) {
        this.haves.add(have);
    }

}
