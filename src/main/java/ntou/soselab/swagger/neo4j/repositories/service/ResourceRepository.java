package ntou.soselab.swagger.neo4j.repositories.service;

import ntou.soselab.swagger.neo4j.domain.service.Resource;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface ResourceRepository extends GraphRepository<Resource> {
}
