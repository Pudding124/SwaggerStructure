package ntou.soselab.swagger.cluster;

import ntou.soselab.swagger.algo.CosineSimilarity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MashupTest {

    Logger log = LoggerFactory.getLogger(MashupTest.class);

    @Autowired
    Mashup mashup;

    CosineSimilarity cosineSimilarity = new CosineSimilarity();

    //@Test
    public void hashmapSortTest() {
        HashMap<String, Integer> wordCounts = new HashMap<>();
        wordCounts.put("USA", 100);
        wordCounts.put("jobs", 200);
        wordCounts.put("software", 50);
        wordCounts.put("technology", 70);
        wordCounts.put("opportunity", 200);
        mashup.getMoreUseWord(wordCounts, 2);
    }


    //@Test
    public void cosineSimilarityScoreTest() {
        double[] target = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        double[] compare = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double score = cosineSimilarity.cosineSimilarity(target, compare);
        log.info("CS Score :{}", score);

        double[] target2 = {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
        double[] compare2 = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double score2 = cosineSimilarity.cosineSimilarity(target2, compare2);
        log.info("CS Score :{}", score2);

        double[] target3 = {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
        double[] compare3 = {0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        double score3 = cosineSimilarity.cosineSimilarity(target3, compare3);
        log.info("CS Score :{}", score3);

        double[] target4 = {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
        double[] compare4 = {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double score4 = cosineSimilarity.cosineSimilarity(target4, compare4);
        log.info("CS Score :{}", score4);
    }

    @Test
    public void mashupFlow() {
        mashup.findAllClusterGroupLDA(32357L);
    }

}