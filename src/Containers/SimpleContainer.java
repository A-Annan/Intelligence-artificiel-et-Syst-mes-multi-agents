package Containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

public class SimpleContainer  {
    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();

        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer container = runtime.createAgentContainer(profile);
        try {
            container.start();
        } catch (ControllerException e) {
            e.printStackTrace();
        }

    }
}
