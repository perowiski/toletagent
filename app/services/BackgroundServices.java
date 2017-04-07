package services;

import models.Agent;
import models.AgentClient;
import models.AgentInspection;
import models_enums.NotificationStatus;
import play.db.jpa.Transactional;

import java.util.List;

/**
 * Created by Onuche Idoko on 2/20/17.
 */
public class BackgroundServices {

    public static void inspectionNotifier(){

        //System.out.println("Notification Scanner...");

        DBService.J.withTransaction(() -> {
            List<Object[]> agentInspections = DBService.J.em().createNativeQuery("SELECT i.client_id, i.agent_id, i.dateOfInspection, i.id, TIMESTAMPDIFF(MINUTE, current_timestamp, i.dateOfInspection) FROM agent_inspections AS i WHERE TIMESTAMPDIFF(MINUTE, current_timestamp, i.dateOfInspection) <= 60 AND TIMESTAMPDIFF(MINUTE, current_timestamp, i.dateOfInspection) >= 0 AND i.notificationStatus = :status").setParameter("status", "WAITING").getResultList();

            for (Object[] i : agentInspections) {

                Agent agent = DBService.Q.findOne(Agent.class, i[1].toString());
                AgentClient agentClient = DBService.Q.findOne(AgentClient.class, i[0].toString());
                AgentInspection agentInspection = DBService.Q.findOne(AgentInspection.class, i[3].toString());

                String userMessage = "Hello " + agentClient.name + ",\n" +
                        "This is a reminder on the inspection you have with " + agent.name + " today, " + agentInspection.dateOfInspection + ".\n" +
                        "Kindly contact the agent " + agent.phone + " and " + agent.email + " to confirm or reschedule the inspection.\n" +
                        "Regards.";
                String agentMessage = "Hello " + agent.name + ",\n" +
                        "This is a reminder on the inspection you have with " + agent.name + " today, " + agentInspection.dateOfInspection + ".\n" +
                        "Kindly contact the client " + agentClient.phone + " and " + agentClient.email + " to confirm or reschedule the inspection.\n" +
                        "Regards.";

                MailService.sendAsync(agentClient.email, "Inspection Notification", userMessage);
                MailService.sendAsync(agent.email, "Inspection Notification", agentMessage);

                agentInspection.setNotificationStatus(NotificationStatus.SENT);
                DBService.Q.save(agentInspection);

                System.out.println("Message sent to " + agent.name + " and " + agentClient.name);
            }
        });
    }

}
