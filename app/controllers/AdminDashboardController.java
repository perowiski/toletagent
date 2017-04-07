package controllers;

import authority.Secured;
import models.*;
import org.apache.commons.lang3.time.DateUtils;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Param;
import services.ApiSave;
import services.DBFilter;
import services.DBService;
import services.Exec;
import views.html.*;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author seyi
 */

@Security.Authenticated(Secured.class)
@Transactional
public class AdminDashboardController extends Controller {
    private final DBService db;
    private final FormFactory formFactory;
    private static Form<AgentCredit> cForm;

    @Inject
    public AdminDashboardController(FormFactory formFactory, DBService db) {
        this.formFactory = formFactory;
        this.db = db;
        cForm = this.formFactory.form(AgentCredit.class);
    }

    public Result index() {
        return ok(index.render());
    }

    public Result agent(Long id) {
        Agent agent = db.findOne(Agent.class, id);
        List<Agent> list = Arrays.asList(agent);
        return ok(agents.render(list, 1L, cForm));
    }

    public Result agentList() {
        Param param = Utility.getParam();
        param.setSort("created DESC");

        DBFilter filter = DBFilter.get();

        String search = request().getQueryString("search");
        if(Utility.isNotEmpty(search)) {
            filter.brS()
                    .field("name").like(search)
                    .or().field("email").like(search)
                    .brE();
        }

        List<Agent> list = db.find(Agent.class, filter, param);
        Long total = db.count(Agent.class, filter);

        return ok(agents.render(list, total, cForm));
    }

    public Result propertyLeadList() {
        Param param = Utility.getParam();
        param.setSort("created DESC");

        DBFilter filter = DBFilter.get();

        String search = request().getQueryString("search2");
        if(Utility.isNotEmpty(search)) {
            filter.brS()
                    .field("userName").like(search)
                    .or().field("userEmail").like(search)
                    .or().field("userPhone").like(search)
                    .or().field("pid").like(search)
                    .brE();
        }
        List<PropertyLead> list;
        Long total;

        String id = request().getQueryString("id");
        if(Utility.isNumeric(id)) {
            filter.field("id", Long.valueOf(id));
            list = db.find(PropertyLead.class, filter);
            total = 1L;
        } else {
            list = db.find(PropertyLead.class, filter, param);
            total = db.count(PropertyLead.class, filter);
        }

        return ok(propertyLeads.render(list, total));
    }

    public Result agentLeadList() {
        Param param = Utility.getParam();
        param.setSort("created DESC");

        DBFilter filter = DBFilter.get();

        String search = request().getQueryString("search2");
        if(Utility.isNotEmpty(search)) {
            filter.brS()
                    .field("name").like(search)
                    .or().field("email").like(search)
                    .or().field("phone").like(search)
                    .brE();
        }

        List<AgentLead> list = db.find(AgentLead.class, filter, param);
        Long total = db.count(AgentLead.class, filter);

        return ok(agentLeads.render(list, total));
    }

    public Result runApiPull() {
        String d = request().getQueryString("do");
        String start = request().getQueryString("start_date");
        String end = request().getQueryString("end_date");

        if("agents".equals(d)) {
            ApiSave.crawlAgents(start, end);
        }

        if ("agentLeads".equals(d)) {
            ApiSave.crawlAgentLeads(start, end);
        }

        if ("propertyLeads".equals(d)) {
            ApiSave.crawlPropertyLeads(start, end);
        }

        if("location".equals(d)) {
            ApiSave.crawlLocation();
        }

        return ok("success");
    }

    public Result runApiPullAll() {
        String d = request().getQueryString("do");
        String t = request().getQueryString("times");
        int times = Integer.valueOf(t);
        Exec.Ex.execute(() -> {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy:HH:mm:ss");
            Date endTime = new Date();
            Date startTime = DateUtils.addHours(endTime, -12);
            String start = format.format(startTime);
            String end = format.format(endTime);
            int i = 1;
            while (i <= times) {
                if ("agentLeads".equals(d)) {
                    ApiSave.crawlAgentLeads(start, end);
                }
                if ("propertyLeads".equals(d)) {
                    ApiSave.crawlPropertyLeads(start, end);
                }
                i++;

                endTime = startTime;
                startTime = DateUtils.addHours(endTime, -12);
                start = format.format(startTime);
                end = format.format(endTime);
            }
        });

        return ok("success");
    }



}
