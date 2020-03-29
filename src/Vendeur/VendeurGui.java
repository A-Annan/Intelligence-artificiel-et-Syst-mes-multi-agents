package Vendeur;

import Agents.AcheteurAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VendeurGui extends Application {

    ObservableList<String> observableList;
    protected VendeurAgent vendeurAgent;
    AgentContainer agentContainer;

    TextField textField;

    @Override
    public void start(Stage stage) throws Exception {
        satrCountainer();

        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();
        observableList = FXCollections.observableArrayList();
        ListView<String> listView =  new ListView<String>(observableList);
        vBox.getChildren().add(listView);
        Button button = new Button("deploy");
        HBox hBox = new HBox();
        Label label= new Label("Agent name ");
        textField= new TextField();
        Button clear = new Button("Clear");
        clear.setOnAction((evt)->{
            this.observableList.clear();
        });
        hBox.getChildren().addAll(label,textField,button,clear);
        button.setOnAction((evt)->{
            try {
                String name = textField.getText();
                AgentController  agentController = agentContainer.createNewAgent(name,"Vendeur.VendeurAgent",new Object[]{this});
                agentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        });
        borderPane.setTop(hBox);
        borderPane.setCenter(vBox);
        hBox.setPadding(new Insets(10));
        Scene scene = new Scene(borderPane,400,300);
        stage.setTitle("Vendeur");
        stage.setScene(scene);
        stage.show();
    }

    private void satrCountainer() {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();

        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        agentContainer = runtime.createAgentContainer(profile);
        try {
            agentContainer.start();
        } catch (ControllerException e) {
            e.printStackTrace();
        }
    }


    public  void logMsg(ACLMessage aclMessage){
        Platform.runLater(()->{
        this.observableList.add(aclMessage.getContent() + " from: "+aclMessage.getSender().getName()+" performative : "+aclMessage.getPerformative());
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
