package ntou.soselab.swagger.neo4j.repositories.service;

import ntou.soselab.swagger.neo4j.domain.service.Operation;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface OperationRepository extends GraphRepository<Operation> {
}

