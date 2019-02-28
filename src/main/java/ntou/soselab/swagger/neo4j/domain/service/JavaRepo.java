package ntou.soselab.swagger.neo4j.domain.service;

import java.util.ArrayList;

public class JavaRepo extends ConcreteService {

    private String repoName;

    private String repoUrl;

    private String javaDocumentName;

    private String javaDocumentUrl;

    private ArrayList<String> methodName;

    private int score;

    public JavaRepo() {

    }

    public JavaRepo(String repoName, String repoUrl, String javaDocumentName, String javaDocumentUrl, ArrayList<String> methodName, int score) {
        this.repoName = repoName;
        this.repoUrl = repoUrl;
        this.javaDocumentName = javaDocumentName;
        this.javaDocumentUrl = javaDocumentUrl;
        this.methodName = methodName;
        this.score = score;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getJavaDocumentName() {
        return javaDocumentName;
    }

    public void setJavaDocumentName(String javaDocumentName) {
        this.javaDocumentName = javaDocumentName;
    }

    public String getJavaDocumentUrl() {
        return javaDocumentUrl;
    }

    public void setJavaDocumentUrl(String javaDocumentUrl) {
        this.javaDocumentUrl = javaDocumentUrl;
    }

    public ArrayList<String> getMethodName() {
        return methodName;
    }

    public void setMethodName(ArrayList<String> methodName) {
        this.methodName = methodName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
