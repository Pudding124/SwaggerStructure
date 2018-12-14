package ntou.soselab.swagger.neo4j.repositories.service;

import ntou.soselab.swagger.neo4j.domain.service.Resource;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

public interface ResourceRepository extends GraphRepository<Resource> {

    @Query("MATCH (n:Resource) WHERE id(n)= {id} RETURN n")
    Resource findResourceById(@Param("id") Long id);

}
