package ntou.soselab.swagger.swagger;

import ntou.soselab.swagger.algo.CosineSimilarity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CSTest {
    Logger log = LoggerFactory.getLogger(CSTest.class);

    @Test
    public void CosineSimiliarityTest() {
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        double[] docVector1 = {1,1,1,0};
        double[] docVector2 = {0,0,1,1};

        log.info("分數 :{}", cosineSimilarity.cosineSimilarity(docVector1, docVector2));
    }
}
