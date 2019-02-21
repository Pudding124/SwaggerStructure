package ntou.soselab.swagger.cluster;

import ntou.soselab.swagger.algo.CosineSimilarity;
import ntou.soselab.swagger.neo4j.domain.service.Operation;
import ntou.soselab.swagger.neo4j.domain.service.Resource;
import ntou.soselab.swagger.neo4j.repositories.service.OperationRepository;
import ntou.soselab.swagger.neo4j.repositories.service.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClusterMashup {

    Logger log = LoggerFactory.getLogger(ClusterMashup.class);

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    OperationRepository operationRepository;

    CosineSimilarity cosineSimilarity = new CosineSimilarity();


    public void findAllClusterGroupLDA(Long id) {
        // record all finish search group
        ArrayList<String> finishSearchCluster = new ArrayList<>();

        // record cluster represent word
        HashMap<String, HashMap<String, Integer>> clusterWord = new HashMap<>();

        for(Resource resource : resourceRepository.findAll()) {
            // avoid same cluster search twice
            if(finishSearchCluster.contains(resource.getClusterGroup())) {
                log.info("This OAS is finish search");
            }else {
                String cluster = resource.getClusterGroup();

                // record same OAS resource and operation appear word and time
                HashMap<String, Integer> OASWord = new HashMap<>();

                // search this resource all same cluster id
                for(Resource resource1 : resourceRepository.findResourcesBySameCluster(cluster)) {

                    if(resourceRepository.findResourcesBySameCluster(cluster).size() == 1) {
                        // not search no cluster OAS
                        log.info("this OAS only has itself");
                        break;
                    }

                    Long resource1Id = resource1.getNodeId();

                    if(resource1.getOriginalWord() != null && !resource1.getOriginalWord().isEmpty()) {
                        ArrayList<String> resourceLDA = resource1.getOriginalWord();
                        for(String word : resourceLDA) {
                            if(OASWord.containsKey(word)) {
                                OASWord.put(word, OASWord.get(word)+1);
                            }else {
                                OASWord.put(word, 1);
                            }
                        }
                    }

                    for(Operation operation : operationRepository.findOperationsByResource(resource1Id)) {
                        if(operation.getOriginalWord() != null && !operation.getOriginalWord().isEmpty()) {
                            ArrayList<String> operationLDA = operation.getOriginalWord();
                            for(String word : operationLDA) {
                                if(OASWord.containsKey(word)) {
                                    OASWord.put(word, OASWord.get(word)+1);
                                }else {
                                    OASWord.put(word, 1);
                                }
                            }
                        }
                    }
                }
                // add OAS cluster add word
                clusterWord.put(cluster, OASWord);
                // add OAS cluster number
                finishSearchCluster.add(cluster);
            }
        }

        compareTagetClusterAndOtherCluster(clusterWord, id);

//        for(String clusterId : clusterWord.keySet()) {
//            log.info("ClusterId :{} --> Cluster Group :{}", clusterId, clusterWord.get(clusterId));
//        }
    }

    public void compareTagetClusterAndOtherCluster(HashMap<String, HashMap<String, Integer>> clusterWord, Long id) {

        Resource resource = resourceRepository.findResourceById(id);
        // get target cluster word
        HashMap<String, Integer> target = clusterWord.get(resource.getClusterGroup());

        // get target top 10 word
        ArrayList<String> targetWord = getMoreUseWord(target, 10);

        for(String clusterNumber : clusterWord.keySet()) {
            if(!clusterNumber.equals(resource.getClusterGroup())) {

                //get other top 10 word
                ArrayList<String> otherWord = getMoreUseWord(clusterWord.get(clusterNumber), 10);

                double score = calculateTwoMatrixVectorsAndCosineSimilarity(targetWord, otherWord);
                if(score >= 0.0) {
                    log.info("Mashup Cluster :{} --> Score :{}", clusterNumber, score);
                }

            }
        }

    }

    public ArrayList<String> getMoreUseWord(HashMap<String, Integer> list, int rank) {
        ArrayList<String> rankList = new ArrayList<>();

        // sort hashmap
        HashMap<String, Integer> sortedByCount = sortByValue(list);

        for(String word : sortedByCount.keySet()) {
//            log.info("Word :{}, Time :{}", word, list.get(word));
            if(rank>=1) rankList.add(word);
            if(rank<1) break;
            rank--;
        }
        return rankList;
    }

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> wordCounts) {
        return wordCounts.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public double calculateTwoMatrixVectorsAndCosineSimilarity(ArrayList<String> targetVector, ArrayList<String> compareVector) {

        ArrayList<String> allWord = new ArrayList<>();
        double cosineSimilarityScore = 0.0;

        // record all appear word
        for(String str : targetVector){
            if(!allWord.contains(str)) {
                allWord.add(str);
            }
        }
        for(String str : compareVector){
            if(!allWord.contains(str)) {
                allWord.add(str);
            }
        }


        // calculate two matrix vector
        double[] target = new double[allWord.size()];
        double[] compare = new double[allWord.size()];
        for(int i = 0;i < allWord.size();i++){
            boolean flag = true;
            for(String str : targetVector){
                if(allWord.get(i).equals(str)) {
                    target[i]++;
                    flag = false;
                }
            }
            if(flag) target[i] = 0.0;
        }

        for(int i = 0;i < allWord.size();i++){
            boolean flag = true;
            for(String str : compareVector){
                if(allWord.get(i).equals(str)) {
                    compare[i]++;
                    flag = false;
                }
            }
            if(flag) compare[i] = 0.0;
        }
        cosineSimilarityScore = cosineSimilarity.cosineSimilarity(target, compare);

        return cosineSimilarityScore;
    }
}