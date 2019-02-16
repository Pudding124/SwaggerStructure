package ntou.soselab.swagger.cluster;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SimilarityTest {

    @Autowired
    Similarity similarity;

    @Test
    public void searchSameEndpoint() {
        Long resourceId1 = 4104L;
        Long resourceId2 = 7081L;
        similarity.findSameEnpoint(resourceId1, resourceId2);
    }
}
