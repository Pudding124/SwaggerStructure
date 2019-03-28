package ntou.soselab.swagger.swagger;

import ntou.soselab.swagger.algo.CosineSimilarity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CSTest {
    Logger log = LoggerFactory.getLogger(CSTest.class);

    @Test
    public void CosineSimiliarityTest() {
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        double[] docVector1 = {1,1,1,1};
        double[] docVector2 = {1,1,0,0};

        log.info("分數 :{}", cosineSimilarity.cosineSimilarity(docVector1, docVector2));
    }

    @Test
    public void CollectionWord() {
        ArrayList<String> target = new ArrayList<>();
        ArrayList<String> compare = new ArrayList<>();
        target.add("a");
        target.add("e");
        target.add("c");
        compare.add("d");
        compare.add("e");
        compare.add("e");
        compare.add("f");

        calculateTwoMatrixVectorsAndCosineSimilarity(target, compare);
    }

    public void calculateTwoMatrixVectorsAndCosineSimilarity(ArrayList<String> targetVector, ArrayList<String> compareVector) {
        ArrayList<String> allWord = new ArrayList<>();

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

        log.info("target :{}", target);
        log.info("compare :{}", compare);
    }
}
