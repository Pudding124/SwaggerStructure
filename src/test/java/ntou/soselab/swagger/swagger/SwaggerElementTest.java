package ntou.soselab.swagger.swagger;

import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import ntou.soselab.swagger.neo4j.domain.service.Resource;
import ntou.soselab.swagger.transformation.SwaggerToNeo4jTransformation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SwaggerElementTest {
    Logger log = LoggerFactory.getLogger(SwaggerElementTest.class);

    @Autowired
    SwaggerToNeo4jTransformation swaggerToNeo4jTransformation;

    @Test
    public void readOneFile() {
        try {
            // do something
            String document = readLocalSwagger("./src/main/resources/swagger document/" + "ebay.com_buy-feed_v1_beta.9.0.json");
            if(document != null){
                swaggerToNeo4jTransformation.parseSwaggerDocument(document);
            }else{
                log.error("error read swagger local file");
            }
        } catch (Exception e) {
            log.error("error parsing");
            log.error(e.toString());
        }
    }

    // For testing
    public String readLocalSwagger(String path) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, "UTF-8");
        } catch (IOException e) {
            System.err.println("read swagger error");
            return null;
        }

    }
}
