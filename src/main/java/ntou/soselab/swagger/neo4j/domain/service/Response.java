package ntou.soselab.swagger.neo4j.domain.service;

import org.neo4j.ogm.annotation.NodeEntity;

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

}
