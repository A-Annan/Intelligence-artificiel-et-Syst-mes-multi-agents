package Agents;

import Containers.ConsumerContainer;
import com.sun.nio.sctp.AbstractNotificationHandler;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;


public class ConsumerAgent extends GuiAgent {

    private transient ConsumerContainer gui;
    @Override
    protected void setup() {
        if (getArguments().length !=0)
        gui = (ConsumerContainer) getArguments()[0];
        gui.setConsumerAgent(this);
        addBehaviour(new CyclicBehaviour() {
             @Override
             public void action() {
                 ACLMessage message  = receive();


                 if (message != null) {
                             gui.logMsg(message);
                     switch (message.getPerformative()){
                         case ACLMessage.CONFIRM:
                             break;
                     }
                 }
                 else block();
             }
         });

    }

    @Override
    public void onGuiEvent(GuiEvent params) {
        if (params.getType() == 1){
            String livre =  params.getParameter(0).toString();
            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
            message.setContent(livre);
            message.addReceiver(new AID("Acheteur",AID.ISLOCALNAME));
            send(message);

        }
    }
}

