package ntou.soselab.swagger.neo4j.domain.service;

import ntou.soselab.swagger.neo4j.domain.relationship.Action;
import ntou.soselab.swagger.neo4j.domain.relationship.Endpoint;
import ntou.soselab.swagger.neo4j.domain.relationship.Input;
import ntou.soselab.swagger.neo4j.domain.relationship.Output;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Operation extends ConcreteService{
    String description;
    String operationAction;
    ArrayList<String> originalWord;
    ArrayList<String> wordnetWord;
    ArrayList<String> feature;

    public Operation(){
        super();
    }

    public Operation(String description, String operationAction, ArrayList<String> originalWord, ArrayList<String> wordnetWord) {
        this.description = description;
        this.operationAction = operationAction;
        this.originalWord = originalWord;
        this.wordnetWord = wordnetWord;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperationAction() {
        return operationAction;
    }

    public void setOperationAction(String operationAction) {
        this.operationAction = operationAction;
    }

    public ArrayList<String> getOriginalWord() {
        return originalWord;
    }

    public void setOriginalWord(ArrayList<String> originalWord) {
        this.originalWord = originalWord;
    }

    public ArrayList<String> getWordnetWord() {
        return wordnetWord;
    }

    public void setWordnetWord(ArrayList<String> wordnetWord) {
        this.wordnetWord = wordnetWord;
    }

    public ArrayList<String> getFeature() {
        return feature;
    }

    public void setFeature(ArrayList<String> feature) {
        this.feature = feature;
    }

    @Relationship(type = "action", direction = Relationship.INCOMING)
    Set<Action> actions = new HashSet<>();

    public Set<Action> getActions() {
        return actions;
    }

    public void setActions(Action action) {
        this.actions.add(action);
    }

    @Relationship(type = "input", direction = Relationship.OUTGOING)
    Set<Input> inputs = new HashSet<>();

    public Set<Input> getInputs() {
        return inputs;
    }
    public void setInput(Input input) {
        this.inputs.add(input);
    }

    @Relationship(type = "output", direction = Relationship.OUTGOING)
    Set<Output> outputs = new HashSet<>();

    public Set<Output> getOutputs() {
        return outputs;
    }
    public void setOutput(Output output) {
        this.outputs.add(output);
    }
}
