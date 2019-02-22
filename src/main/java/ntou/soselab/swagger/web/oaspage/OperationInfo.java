package ntou.soselab.swagger.web.oaspage;

import org.json.JSONObject;

import java.util.ArrayList;

public class OperationInfo {

    String operation;
    String description;
    ArrayList<String> features;
    ArrayList<InputParameterInfo> inputParameters;
    ArrayList<JSONObject> statusCode;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<String> features) {
        this.features = features;
    }

    public ArrayList<InputParameterInfo> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(ArrayList<InputParameterInfo> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public ArrayList<JSONObject> getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(ArrayList<JSONObject> statusCode) {
        this.statusCode = statusCode;
    }
}
