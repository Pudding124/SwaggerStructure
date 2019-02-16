package ntou.soselab.swagger.web.oaspage;

import java.util.ArrayList;

public class PathInfo {

    String endpoint;
    ArrayList<OperationInfo> operations;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public ArrayList<OperationInfo> getOperations() {
        return operations;
    }

    public void setOperations(ArrayList<OperationInfo> operations) {
        this.operations = operations;
    }
}
