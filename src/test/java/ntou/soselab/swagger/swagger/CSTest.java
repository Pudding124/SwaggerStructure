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

    //@Test
    public void CosineSimiliarityTest() {
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        double[] docVector1 = {1, 2, 2, 1, 1, 1, 0};
        double[] docVector2 = {1, 2, 2, 1, 1, 2, 1};

        log.info("分數 :{}", cosineSimilarity.cosineSimilarity(docVector1, docVector2));
    }

    //@Test
    public void CollectionWord() {
        // 416527 - 157782
        ArrayList<String> target = new ArrayList<>();
        ArrayList<String> compare = new ArrayList<>();
        target.add("softwar");
        target.add("configur");
        target.add("updat");
        compare.add("configur");
        compare.add("compon");
        compare.add("proactiv");
        compare.add("detect");
        compare.add("client");

        calculateTwoMatrixVectorsAndCosineSimilarity(target, compare);

        ArrayList<String> target2 = new ArrayList<>();
        ArrayList<String> compare2 = new ArrayList<>();
        target2.add("updat");
        target2.add("creat");
        target2.add("softwar");
        target2.add("configur");
        target2.add("uri");
        target2.add("singl");
        target2.add("configur");
        target2.add("softwar");
        target2.add("updat");
        target2.add("specif");
        target2.add("updat");
        target2.add("delet");
        target2.add("softwar");
        target2.add("configur");
        target2.add("softwar");
        target2.add("updat");
        target2.add("account");
        target2.add("configur");

        compare2.add("detect");
        compare2.add("compon");
        compare2.add("proactiv");
        compare2.add("applic");
        compare2.add("configur");
        compare2.add("insight");
        compare2.add("proactiv");
        compare2.add("detect");
        compare2.add("configur");
        compare2.add("updat");
        compare2.add("detect");
        compare2.add("configur");
        compare2.add("proactiv");

        calculateTwoMatrixVectorsAndCosineSimilarity(target2, compare2);
    }

    @Test
    public void CollectionWord2() {
        ArrayList<String> target = new ArrayList<>();
        ArrayList<String> compare = new ArrayList<>();
//        target.add("I");
//        target.add("like");
//        target.add("see");
//        target.add("tv");
//        target.add("no");
        target.add("like");
        target.add("see");
        target.add("movie");

        compare.add("I");
        compare.add("no");
        compare.add("like");
        compare.add("see");
        compare.add("tv");
        compare.add("also");
        compare.add("no");
//        compare.add("like");
//        compare.add("see");
//        compare.add("movie");

        calculateTwoMatrixVectorsAndCosineSimilarity(target, compare);
    }

    public void calculateTwoMatrixVectorsAndCosineSimilarity(ArrayList<String> targetVector, ArrayList<String> compareVector) {
        CosineSimilarity cosineSimilarity = new CosineSimilarity();

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
        double score = cosineSimilarity.cosineSimilarity(target, compare);
        log.info("target :{}", target);
        log.info("compare :{}", compare);
        log.info("score :{}", score);
    }
}
