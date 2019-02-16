package ntou.soselab.swagger.cluster;

import ntou.soselab.swagger.algo.ParametersSimilarityOfTwoServices;
import ntou.soselab.swagger.neo4j.domain.service.Operation;
import ntou.soselab.swagger.neo4j.domain.service.Parameter;
import ntou.soselab.swagger.neo4j.domain.service.Response;
import ntou.soselab.swagger.neo4j.repositories.service.OperationRepository;
import ntou.soselab.swagger.neo4j.repositories.service.ParameterRepository;
import ntou.soselab.swagger.neo4j.repositories.service.ResourceRepository;
import ntou.soselab.swagger.neo4j.repositories.service.ResponseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class Similarity {
    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    OperationRepository operationRepository;

    @Autowired
    ParameterRepository parameterRepository;

    @Autowired
    ResponseRepository responseRepository;

    @Autowired
    ParametersSimilarityOfTwoServices parametersSimilarityOfTwoServices;

    Logger log = LoggerFactory.getLogger(Similarity.class);

    public void findSameEnpoint(Long resourceId1, Long resourceId2) {
//        Resource resource1 =resourceRepository.findResourceById(6661L);
//        Resource resource2 =resourceRepository.findResourceById(4802L);

        HashMap<HashMap<String,String>, Double> similarityResult = new HashMap<>();
        HashMap<HashMap<String,String>, Double> mashupResult = new HashMap<>();

        for(Operation operation1 : operationRepository.findOperationsByResource(resourceId1)) {
            ArrayList<Parameter> parameters1 = new ArrayList<>(parameterRepository.findParametersByOperation(operation1.getNodeId()));
            ArrayList<Response> responses1 = new ArrayList<>(responseRepository.findSuccessResponsesByOperation(operation1.getNodeId()));
            double similarityMaxScore = 0.0;
            double mashupMaxScore = 0.0;
            String pathName = "";

            for(Operation operation2 : operationRepository.findOperationsByResource(resourceId2)) {
                ArrayList<Parameter> parameters2 = new ArrayList<>(parameterRepository.findParametersByOperation(operation2.getNodeId()));
                ArrayList<Response> responses2 = new ArrayList<>(responseRepository.findSuccessResponsesByOperation(operation2.getNodeId()));
                //log.info("service1 :{} ---- service2 :{}", operation1.getOperationAction(), operation2.getOperationAction());
                double inputMatchScore = parametersSimilarityOfTwoServices.calculateServiceInputScore(parameters1, parameters2);
                log.info("Input Similarity Score :{}", inputMatchScore);

                // 每個 input 參數皆有配對到，才會去算 output 分數，若不相似則去計算 mashup
                if(inputMatchScore != 0.0) {
                    double outputMatchScore = parametersSimilarityOfTwoServices.calculateServiceOutputScore(responses1, responses2);

                    // 若 output 都有配對到則放入到 similarity 的結果內，若不相同 則放入到 mashup 的結果內
                    if(outputMatchScore != 0.0) {
                        double sumScore = (inputMatchScore + outputMatchScore) / 2;
                        if (sumScore > similarityMaxScore) {
                            similarityMaxScore = sumScore;
                            pathName = operationRepository.findPathByOperation(operation2.getNodeId()).getPath();
                        }
                    }else if(outputMatchScore == 0.0) {
                        double sumScore = inputMatchScore;
                        if (sumScore > mashupMaxScore) {
                            mashupMaxScore = sumScore;
                            pathName = operationRepository.findPathByOperation(operation2.getNodeId()).getPath();
                        }
                    }
                }else if(inputMatchScore == 0.0) {
                    double outputMatchScore = parametersSimilarityOfTwoServices.calculateServiceOutputScore(responses1, responses2);

                    if(outputMatchScore != 0.0) {
                        double sumScore = outputMatchScore;
                        if (sumScore > mashupMaxScore) {
                            mashupMaxScore = sumScore;
                            pathName = operationRepository.findPathByOperation(operation2.getNodeId()).getPath();
                        }
                    }
                }

                // similarity 分數高於 0.6 則放入結果列表
                if(similarityMaxScore > 0.6) {
                    HashMap<String,String> serviceName = new HashMap<>();
                    serviceName.put(operationRepository.findPathByOperation(operation1.getNodeId()).getPath(), pathName);
                    similarityResult.put(serviceName, similarityMaxScore);
                }

                if(mashupMaxScore > 0.6) {
                    HashMap<String,String> serviceName = new HashMap<>();
                    serviceName.put(operationRepository.findPathByOperation(operation1.getNodeId()).getPath(), pathName);
                    mashupResult.put(serviceName, mashupMaxScore);
                }
            }
        }

        for(HashMap<String, String> str : similarityResult.keySet()) {
            for(String name : str.keySet()) {
                log.info("Similarity service1 :{} ---- service2 :{} --------------> score :{}", name, str.get(name), similarityResult.get(str));
            }
        }

        for(HashMap<String, String> str : mashupResult.keySet()) {
            for(String name : str.keySet()) {
                log.info("Mashup service1 :{} ---- service2 :{} --------------> score :{}", name, str.get(name), mashupResult.get(str));
            }
        }
    }
}
