package ntou.soselab.swagger.neo4j.domain.service;

import ntou.soselab.swagger.neo4j.domain.relationship.Endpoint;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Resource extends ConcreteService{

    ArrayList<String> schemes;
    String host;
    String basePath;
    String id;
    String title;
    String description;
    String logo;
    String provider;
    String version;
    String swaggerUrl;
    ArrayList<String> authentications;
    ArrayList<String> consumes;
    ArrayList<String> produces;


    public Resource() {
        super();
    }

    public Resource(ArrayList<String> schemes, String host, String basePath, String id, String title, String description, String logo, String provider, String version, String swaggerUrl, ArrayList<String> authentications, ArrayList<String> consumes, ArrayList<String> produces) {
        this.schemes = schemes;
        this.host = host;
        this.basePath = basePath;
        this.id = id;
        this.title = title;
        this.description = description;
        this.logo = logo;
        this.provider = provider;
        this.version = version;
        this.swaggerUrl = swaggerUrl;
        this.authentications = authentications;
        this.consumes = consumes;
        this.produces = produces;
    }

    public ArrayList<String> getSchemes() {
        return schemes;
    }

    public void setSchemes(ArrayList<String> schemes) {
        this.schemes = schemes;
    }

    public void setScheme(String scheme) {
        if(schemes == null){
            schemes = new ArrayList<String>();
        }
        schemes.add(scheme);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSwaggerUrl() {
        return swaggerUrl;
    }

    public void setSwaggerUrl(String swaggerUrl) {
        this.swaggerUrl = swaggerUrl;
    }

    public ArrayList<String> getAuthentications() {
        return authentications;
    }

    public void setAuthentications(ArrayList<String> authentications) {
        this.authentications = authentications;
    }

    public void setAuthentication(String authentication) {
        if(authentications == null){
            authentications = new ArrayList<String>();
        }
        authentications.add(authentication);
    }

    public ArrayList<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(ArrayList<String> consumes) {
        this.consumes = consumes;
    }

    public void setConsume(String consume) {
        if(consumes == null){
            consumes = new ArrayList<String>();
        }
        consumes.add(consume);
    }

    public ArrayList<String> getProduces() {
        return produces;
    }

    public void setProduces(ArrayList<String> produces) {
        this.produces = produces;
    }

    public void setProduce(String produce) {
        if(produces == null){
            produces = new ArrayList<String>();
        }
        produces.add(produce);
    }

    @Override
    public String toString() {
        return "Resource [title=" + title + ", provider=" + provider + "]";
    }

    @Relationship(type = "endpoint", direction = Relationship.OUTGOING)
    Set<Endpoint> endpoints = new HashSet<>();

    public Set<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Endpoint endpoint) {
        this.endpoints.add(endpoint);
    }
}
