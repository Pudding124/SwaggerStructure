package ntou.soselab.swagger.neo4j.repositories.service;

import ntou.soselab.swagger.neo4j.domain.service.Response;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResponseRepository extends GraphRepository<Response> {

    @Query("MATCH (n:Resource)-[a:endpoint]-(m:Path)-[s:action]-(r:Operation)-[w:output]-(x:StatusCode {statusCode : \"200\"})-[y:have]-(z:Response) WHERE id(n)= {id} RETURN z")
    List<Response> findResponsesByResource(@Param("id") Long id);
}

