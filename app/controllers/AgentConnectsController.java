package controllers;

import authority.SecuredAgent;
import models.Agent;
import models.AgentConnect;
import models.AgentConnectReply;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Param;
import services.DBFilter;
import services.DBService;
import views.html.profile.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Security.Authenticated(SecuredAgent.class)
@Transactional
public class AgentConnectsController extends Controller {

    private final DBService db;
    private final FormFactory formFactory;

    @Inject
    public AgentConnectsController(DBService db, FormFactory formFactory) {
        this.db = db;
        this.formFactory = formFactory;
    }

    public Result sent() {
        Agent agent = Auth.getAgent();

        Map<String, Object> map = new HashMap<>();
        Param param = Utility.getParam();
        param.setSort("id DESC");
        DBFilter filter = DBFilter.get();

        filter.field("fromAgent.id", agent.id);
        filter.field("toAgent").isNull();

        List<AgentConnect> connects = db.find(AgentConnect.class, filter, param);
        Long total = db.count(AgentConnect.class, filter);

        map.put("page", "Sent");
        map.put("connects", connects);
        map.put("total", total);
        return ok(agentConnects.render(map));
    }

    public Result received() {
        Agent agent = Auth.getAgent();

        Map<String, Object> map = new HashMap<>();
        Param param = Utility.getParam();
        param.setSort("id DESC");
        DBFilter filter = DBFilter.get();

        boolean normal = false;

        if("testagent".equals(agent.username)) {
            filter.field("toAgent").isNull();
        } else {
            filter.field("toAgent.id", agent.id);
            normal = true;
        }

        List<AgentConnect> connects = db.find(AgentConnect.class, filter, param);

        if(normal) {
            for (AgentConnect connect : connects) {
                if(!connect.seen) {
                    connect.seen = true;
                    db.save(connect);
                }
            }
        }

        Long total = db.count(AgentConnect.class, filter);

        map.put("page", "Received");

        map.put("connects", connects);
        map.put("total", total);
        return ok(agentConnects.render(map));
    }

    public static Long notSeen() {
        Agent agent = Auth.getAgent();
        DBFilter filter = DBFilter.get();
        filter.field("toAgent.id", agent.id);
        filter.field("seen", false);
        return  DBService.Q.count(AgentConnect.class, filter);
    }

    private static ExecutorService Ex = Executors.newFixedThreadPool(1000);

    public static void sendConnects(AgentConnect connect, String state, List<String> axises) {
        DBFilter filter = DBFilter.get();

        connect.states.add(Long.valueOf(state));
        for(String axis: axises) {
            connect.axises.add(Long.valueOf(axis));
        }
        DBService.Q.save(connect);

        Ex.execute(() -> {
            final Map<Long, Agent> agents = Selectors.selectAgents();

            System.out.println("Started sending agent connects");
            DBService.J.withTransaction(() -> {
                agents.forEach((id, agent) -> {
                    if(agent.axises != null && !agent.axises.isEmpty()) {
                        for(Long axis: connect.axises) {
                            if(agent.axises.contains(axis)) {
                                AgentConnect c = new AgentConnect();
                                c.fromAgent = connect.fromAgent;
                                c.type = connect.type;
                                c.pid = connect.pid;
                                c.brief = connect.brief;
                                c.request = connect.request;
                                c.message = connect.message;
                                c.created = connect.created;
                                c.toAgent = agent;
                                DBService.Q.save(c);
                            }
                        }
                    }
                });

                AgentConnect conn = DBService.Q.findOne(AgentConnect.class, connect.id);
                conn.success = true;
                DBService.Q.save(conn);
            });
        });



    }

    public Result reply(Long id) {
        DynamicForm form = formFactory.form().bindFromRequest();

        Agent agent = Auth.getAgent();
        AgentConnect connect = db.findOne(AgentConnect.class, id);

        AgentConnectReply reply = new AgentConnectReply();
        reply.agent = agent;
        reply.connect = connect;
        reply.message = form.get("message");
        db.save(reply);
        return ok("success");
    }

    public Result replies(Long id) {
        Agent agent = Auth.getAgent();
        Param param = Utility.getParam();
        param.setSort("id DESC");
        DBFilter filter = DBFilter.get();
        filter.field("connect.id").eq(id);
        filter.field("connect.fromAgent.id").eq(agent.id);

        List<AgentConnectReply> replies = db.find(AgentConnectReply.class, filter, param);
        return ok(agentRepliesModal.render(replies));
    }

    public Result replier(Long id) {
        AgentConnectReply reply = db.findOne(AgentConnectReply.class, id);
        reply.pulled = true;
        db.save(reply);
        return ok(replierModal.render(reply));
    }

    public static Long replyCount(Long id) {
        Agent agent = Auth.getAgent();
        Param param = Utility.getParam();
        param.setSort("id DESC");
        DBFilter filter = DBFilter.get();
        filter.field("connect.id").eq(id);
        filter.field("connect.fromAgent.id").eq(agent.id);

        return DBService.Q.count(AgentConnectReply.class, filter);
    }
}