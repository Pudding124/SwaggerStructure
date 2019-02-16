package ntou.soselab.swagger.cluster;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClusterTest {

    @Autowired
    ReadClusterCSV readClusterCSV;

    @Test
    public void importClusterResultTest() {
        HashMap<Long, String> cluster = readClusterCSV.readClusterResult();
        readClusterCSV.saveClusterToNeo4j(cluster);
    }
}
