package Agents;

import Containers.AchteurGui;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class AcheteurAgent extends GuiAgent {

    protected AchteurGui achteurGui;
    protected  AID vendeur[];

    @Override
    protected void setup() {

        if (getArguments().length == 1)
            achteurGui = (AchteurGui)getArguments()[0];
            achteurGui.acheteurAgent = this;

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
                private List<ACLMessage> messageList = new ArrayList<>();
            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                                        MessageTemplate.or(
                                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                                MessageTemplate.or(
                                        MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
                                        MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
                                )
                        ));

                ACLMessage aclMessage = receive(messageTemplate);
                if (aclMessage != null){
                    switch (aclMessage.getPerformative()){
                        case ACLMessage.REQUEST:
                                ACLMessage message = new ACLMessage(ACLMessage.CFP);
                                message.setContent(aclMessage.getContent());
                            for (int i = 0; i < vendeur.length; i++) {
                                message.addReceiver(vendeur[i]);
                            }
                            send(message);
                            achteurGui.logMsg(message);
                            break;
                        case ACLMessage.PROPOSE:
                            messageList.add(aclMessage);
                            achteurGui.logMsg(aclMessage);
                            System.out.println(messageList.size()+" "+vendeur.length);
                            if (messageList.size() == vendeur.length){
                                ACLMessage meilleurOffre = messageList.get(0);
                                double min = Double.parseDouble(meilleurOffre.getContent());
                                for (ACLMessage aclMessage1: messageList){
                                    double price = Double.parseDouble(aclMessage1.getContent());
                                    System.out.println(aclMessage1.getSender().getName());
                                    if (min > price){
                                        meilleurOffre = aclMessage1;
                                        min = price;
                                    }
                                }
                            ACLMessage offreAccept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                offreAccept.setContent(meilleurOffre.getContent());
                                offreAccept.addReceiver(meilleurOffre.getSender());
                            achteurGui.logMsg(offreAccept);
                                System.out.println("Propose");
                            send(offreAccept);

                            }
                            break;
                        case ACLMessage.CONFIRM:
                            ACLMessage aclMessagereply = new ACLMessage(ACLMessage.INFORM);
                            aclMessagereply.addReceiver(new AID("ConsumerAgent",AID.ISLOCALNAME));
                            aclMessagereply.setContent(aclMessage.getContent());
                            send(aclMessagereply);
                            break;
                        case ACLMessage.REFUSE:
                            break;

                    }
                      achteurGui.logMsg(aclMessage);
                } else block();
            }
        });

        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,6000) {
            @Override
            protected void onTick() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();

                serviceDescription.setType("Transaction");
                serviceDescription.setName("Vente-livre");
                dfAgentDescription.addServices(serviceDescription);

                try {
                    DFAgentDescription[] result = DFService.search(myAgent,dfAgentDescription);
                    vendeur = new AID[result.length];
                    for (int i = 0; i < result.length; i++) {
                        vendeur[i] = result[i].getName();
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}
