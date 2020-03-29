package Containers;

import Agents.AcheteurAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class AchteurGui extends Application {

    ObservableList<String> observableList;
    public AcheteurAgent acheteurAgent;


    @Override
    public void start(Stage stage) throws Exception {
        satrCountainer();

        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();
        observableList = FXCollections.observableArrayList();
        ListView<String> listView =  new ListView<String>(observableList);
        vBox.getChildren().add(listView);
        borderPane.setTop(vBox);
        Scene scene = new Scene(borderPane,400,300);
        stage.setTitle("Achteur");
        stage.setScene(scene);
        stage.show();
    }

    private void satrCountainer() {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();

        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        try {
            AgentController agentController = agentContainer.createNewAgent("Acheteur","Agents.AcheteurAgent",new Object[]{this});
            agentController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }


    public  void logMsg(ACLMessage aclMessage){
        Platform.runLater(()->{
        this.observableList.add(aclMessage.getContent() + " from: "+aclMessage.getSender().getName()      +" performative : "+aclMessage.getPerformative());
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
