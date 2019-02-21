package ntou.soselab.swagger.cluster;

import ntou.soselab.swagger.web.recommand.ServiceRecommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SimilarityTest {

    @Autowired
    ServiceRecommand serviceRecommand;

    @Test
    public void searchSameEndpoint() {
        Long resourceId1 = 28161L;
        Long resourceId2 = 7081L;
        serviceRecommand.getRecommandResult(resourceId1);
    }
}
