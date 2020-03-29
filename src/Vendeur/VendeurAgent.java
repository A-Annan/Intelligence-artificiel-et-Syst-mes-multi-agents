package Vendeur;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

public class VendeurAgent extends GuiAgent {


    VendeurGui vendeurGui;
    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    @Override
    protected void setup() {
        if (getArguments().length == 1)
            vendeurGui = (VendeurGui) getArguments()[0];
        vendeurGui.vendeurAgent = this;

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CFP),MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
                ACLMessage aclMessage = receive(messageTemplate);
                if (aclMessage != null){
                    vendeurGui.logMsg(aclMessage);

                    switch (aclMessage.getPerformative()) {
                        case ACLMessage.CFP:
                            ACLMessage reply =aclMessage.createReply();
                            reply.setContent(String.valueOf(500+new Random().nextInt(1000)));
                            reply.setPerformative(ACLMessage.PROPOSE);
                            send(reply);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            ACLMessage replymsg = new ACLMessage(ACLMessage.CONFIRM);
                            replymsg.addReceiver(aclMessage.getSender());
                            replymsg.setContent(aclMessage.getContent());
                            send(replymsg);
                            System.out.println("ok");
                            break;

                    }
                } else block();
            }

        });
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                dfAgentDescription.setName(getAID());
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("Transaction");
                serviceDescription.setName("Vente-livre");
                dfAgentDescription.addServices(serviceDescription);

                try {
                    DFService.register(myAgent,dfAgentDescription);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
