package ntou.soselab.swagger.web.recommand;

import ntou.soselab.swagger.algo.ParametersSimilarityOfTwoServices;
import ntou.soselab.swagger.neo4j.domain.service.Operation;
import ntou.soselab.swagger.neo4j.domain.service.Parameter;
import ntou.soselab.swagger.neo4j.domain.service.Resource;
import ntou.soselab.swagger.neo4j.domain.service.Response;
import ntou.soselab.swagger.neo4j.repositories.service.OperationRepository;
import ntou.soselab.swagger.neo4j.repositories.service.ParameterRepository;
import ntou.soselab.swagger.neo4j.repositories.service.ResourceRepository;
import ntou.soselab.swagger.neo4j.repositories.service.ResponseRepository;
import org.neo4j.ogm.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class ServiceRecommand {

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

    Logger log = LoggerFactory.getLogger(ServiceRecommand.class);

    @CrossOrigin
    @RequestMapping(value = "/getOASRecommand/{id}", method = RequestMethod.GET)
    public String  getRecommandResult(@PathVariable("id")Long resourceId) {

        Service service = new Service();
        ArrayList<Similarity> similaritys = new ArrayList<>();
        ArrayList<Mashup> mashups = new ArrayList<>();

        for(Operation operation1 : operationRepository.findOperationsByResource(resourceId)) { //7081
            ArrayList<Parameter> parameters1 = new ArrayList<>(parameterRepository.findParametersByOperation(operation1.getNodeId()));
            ArrayList<Response> responses1 = new ArrayList<>(responseRepository.findSuccessResponsesByOperation(operation1.getNodeId()));

            for(Operation operation2 : operationRepository.findOperationsByResource(4104L)) {
                ArrayList<Parameter> parameters2 = new ArrayList<>(parameterRepository.findParametersByOperation(operation2.getNodeId()));
                ArrayList<Response> responses2 = new ArrayList<>(responseRepository.findSuccessResponsesByOperation(operation2.getNodeId()));
                //log.info("service1 :{} ---- service2 :{}", operation1.getOperationAction(), operation2.getOperationAction());
                double inputMatchScore = parametersSimilarityOfTwoServices.calculateServiceInputScore(parameters1, parameters2);
                double inputMashupMatchScore = parametersSimilarityOfTwoServices.calculateServiceInputOutputScore(parameters1, responses2);
                double outputMashupMatchScore = parametersSimilarityOfTwoServices.calculateServiceOutputInputScore(responses1, parameters2);
                double similarityMaxScore = 0.0;
                double mashupMaxScore = 0.0;
                log.info("Input Similarity Score :{}", inputMatchScore);
                log.info("Input Mashup Score :{}", inputMashupMatchScore);
                log.info("Output Mashup Score :{}", outputMashupMatchScore);

                // 判斷 Input 或是 Output 相似
                int flag = 0;

                // 每個 input 參數皆有配對到，才會去算 output 分數，若不相似則去計算 mashup
                if(inputMatchScore != 0.0) {
                    double outputMatchScore = parametersSimilarityOfTwoServices.calculateServiceOutputScore(responses1, responses2);

                    // 若 output 都有配對到則放入到 similarity 的結果內，若不相同 則放入到 mashup 的結果內
                    if(outputMatchScore != 0.0) {
                        double sumScore = (inputMatchScore + outputMatchScore) / 2;
                        if (sumScore > similarityMaxScore) {
                            similarityMaxScore = sumScore;
                        }
                    }else if(outputMatchScore == 0.0) {
                        double sumScore = inputMatchScore;
                        if (sumScore > mashupMaxScore) {
                            mashupMaxScore = sumScore;
                        }
                        // input 相同
                        flag = 1;
                    }
                }else if(inputMatchScore == 0.0) {
                    double outputMatchScore = parametersSimilarityOfTwoServices.calculateServiceOutputScore(responses1, responses2);

                    if(outputMatchScore != 0.0) {
                        double sumScore = outputMatchScore;
                        if (sumScore > mashupMaxScore) {
                            mashupMaxScore = sumScore;
                        }
                        // output 相同
                        flag = 2;
                    }
                }

                // similarity 分數高於 0.6 則放入結果列表
                if(similarityMaxScore > 0.0) {
                    Similarity similarity = new Similarity();
                    similarity.setScore(similarityMaxScore);
                    similarity.setTargetEndpoint(operationRepository.findPathByOperation(operation1.getNodeId()).getPath());
                    similarity.setTargetOperation(operation1.getOperationAction());
                    Resource resource = operationRepository.findResourceByOperation(operation2.getNodeId());
                    similarity.setCompareOASId(resource.getNodeId());
                    similarity.setCompareOASName(resource.getTitle());
                    similarity.setCompareEndpoint(operationRepository.findPathByOperation(operation2.getNodeId()).getPath());
                    similarity.setCompareOperation(operation2.getOperationAction());
                    similaritys.add(similarity);
                }

                if(mashupMaxScore > 0.0) {
                    Mashup mashup = new Mashup();
                    if(flag == 1) {
                        mashup.setCategory("Same Input");
                    }else if(flag == 2) {
                        mashup.setCategory("Same Output");
                    }
                    mashup.setScore(mashupMaxScore);
                    mashup.setTargetEndpoint(operationRepository.findPathByOperation(operation1.getNodeId()).getPath());
                    mashup.setTargetOperation(operation1.getOperationAction());
                    Resource resource = operationRepository.findResourceByOperation(operation2.getNodeId());
                    mashup.setCompareOASId(resource.getNodeId());
                    mashup.setCompareOASName(resource.getTitle());
                    mashup.setCompareEndpoint(operationRepository.findPathByOperation(operation2.getNodeId()).getPath());
                    mashup.setCompareOperation(operation2.getOperationAction());
                    mashups.add(mashup);
                }

                if(inputMashupMatchScore > 0.0) {
                    Mashup mashup = new Mashup();
                    mashup.setCategory("Input Match");
                    mashup.setScore(inputMashupMatchScore);
                    mashup.setTargetEndpoint(operationRepository.findPathByOperation(operation1.getNodeId()).getPath());
                    mashup.setTargetOperation(operation1.getOperationAction());
                    Resource resource = operationRepository.findResourceByOperation(operation2.getNodeId());
                    mashup.setCompareOASId(resource.getNodeId());
                    mashup.setCompareOASName(resource.getTitle());
                    mashup.setCompareEndpoint(operationRepository.findPathByOperation(operation2.getNodeId()).getPath());
                    mashup.setCompareOperation(operation2.getOperationAction());
                    mashups.add(mashup);
                }

                if(outputMashupMatchScore > 0.0) {
                    Mashup mashup = new Mashup();
                    mashup.setCategory("Output Match");
                    mashup.setScore(outputMashupMatchScore);
                    mashup.setTargetEndpoint(operationRepository.findPathByOperation(operation1.getNodeId()).getPath());
                    mashup.setTargetOperation(operation1.getOperationAction());
                    Resource resource = operationRepository.findResourceByOperation(operation2.getNodeId());
                    mashup.setCompareOASId(resource.getNodeId());
                    mashup.setCompareOASName(resource.getTitle());
                    mashup.setCompareEndpoint(operationRepository.findPathByOperation(operation2.getNodeId()).getPath());
                    mashup.setCompareOperation(operation2.getOperationAction());
                    mashups.add(mashup);
                }
            }
        }
        service.setSimilaritys(similaritys);
        service.setMashups(mashups);
        JSONObject jsonObjectMary = new JSONObject(service);
        log.info("ans :{}",jsonObjectMary);
        return jsonObjectMary.toString();
    }
}