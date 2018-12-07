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
    String description;
    String format;
    boolean required;

    public Response(String name, String media_type, String description, String format, boolean required) {
        this.name = name;
        this.media_type = media_type;
        this.description = description;
        this.format = format;
        this.required = required;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
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
