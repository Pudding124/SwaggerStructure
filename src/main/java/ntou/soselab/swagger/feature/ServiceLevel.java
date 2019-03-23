package ntou.soselab.swagger.feature;

import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.models.auth.SecuritySchemeDefinition;
import io.swagger.parser.SwaggerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ServiceLevel {
    Logger log = LoggerFactory.getLogger(ServiceLevel.class);

    public ArrayList<String> parseSwaggerService(Swagger swagger){
        boolean httpsFlag = false;

        // Service Level check
        int operationQuantity = 0;
        List<Scheme> schemes = null;
        Map<String, SecuritySchemeDefinition> securityDefinitions = null;

        // save restful feture
        ArrayList<String> feture = new ArrayList<>();

        schemes = swagger.getSchemes();
        securityDefinitions = swagger.getSecurityDefinitions();

        if(schemes != null){
            for(Scheme scheme : schemes){
                if(scheme.toValue().toLowerCase().equals("https")){
                    feture.add("HTTPS support");
                    httpsFlag = true;
                    break;
                }
            }
        }

        if(securityDefinitions != null){
            for(String key : securityDefinitions.keySet()){
                SecuritySchemeDefinition securitySchemeDefinition = securityDefinitions.get(key);
                if(securitySchemeDefinition.getType().equals("basic")){
                    feture.add("User authentication");
                    break;
                }else if(securitySchemeDefinition.getType().equals("apiKey")){
                    feture.add("User authentication");
                    break;
                }else if(securitySchemeDefinition.getType().equals("oauth2")){
                    feture.add("User authentication");
                    break;
                }
            }
        }

        for(String p : swagger.getPaths().keySet()){
            if (swagger.getPaths().get(p).getDelete() != null) {
                operationQuantity++;
            }
            if (swagger.getPaths().get(p).getGet() != null) {
                operationQuantity++;
            }
            if (swagger.getPaths().get(p).getPost() != null) {
                operationQuantity++;
            }
            if (swagger.getPaths().get(p).getPut() != null) {
                operationQuantity++;
            }
            if (swagger.getPaths().get(p).getPatch() != null) {
                operationQuantity++;
            }
        }
        if(operationQuantity >= 20){
            feture.add("At most 20 operations");
        }

        return feture;
    }
}

