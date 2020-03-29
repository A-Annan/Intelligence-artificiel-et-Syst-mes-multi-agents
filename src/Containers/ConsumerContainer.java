package Containers;

import Agents.ConsumerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
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

public class ConsumerContainer extends Application {
    ObservableList<String> observableList;

    private ConsumerAgent consumerAgent;

    public ConsumerAgent getConsumerAgent() {
        return consumerAgent;
    }

    public void setConsumerAgent(ConsumerAgent consumerAgent) {
        this.consumerAgent = consumerAgent;
    }

    public static void main(String[] args) {

        launch(args);
    }

    public void startContainer(){

        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();

        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer container = runtime.createAgentContainer(profile);
        try {
            AgentController agentController  = container.createNewAgent("ConsumerAgent","Agents.ConsumerAgent",new Object[] {this});
            agentController.start();
        } catch (ControllerException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        HBox hBox = new HBox();
        Label label = new Label("livre");
        TextField textField =  new TextField();
        Button button =  new Button("acheter");
        hBox.getChildren().addAll(label,textField,button);
        VBox vBox = new VBox();
         observableList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<String>(observableList);
        vBox.getChildren().addAll(listView);
        BorderPane borderPane = new BorderPane();
        hBox.setPadding(new Insets(10,10,10,10));
        borderPane.setCenter(vBox);
        borderPane.setTop(hBox);

        button.setOnAction(evt->{
            String livre = textField.getText();
//            observableList.add(livre);
            GuiEvent event = new GuiEvent(this,1);
            event.addParameter(livre);
            consumerAgent.onGuiEvent(event);
        });
        stage.setTitle("Consumer");
        Scene scene = new Scene(borderPane,600,400);
        stage.setScene(scene);
        stage.show();
    }

    public void logMsg(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableList.add(aclMessage.getContent() + " from: "+aclMessage.getSender()+" performative : "+aclMessage.getPerformative());
        });
    }
}
