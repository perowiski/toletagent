package controllers;

import authority.Secured;
import com.google.gson.Gson;
import models.*;
import models.Subscription;
import models_enums.PaymentType;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import pojos.*;
import services.DBFilter;
import services.DBService;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by peter on 3/8/17.
 */
@Security.Authenticated(Secured.class)
@Transactional
public class AdminSubscriptionController extends Controller{

    private final DBService db;
    private final FormFactory formFactory;

    @Inject
    public AdminSubscriptionController(FormFactory formFactory, DBService db) {
        this.formFactory = formFactory;
        this.db = db;
    }

    public Result pendingSubscriptions(){
        Map<String, Object> modelMap = new HashMap<String, Object>();
        Param param = Utility.getParam();
        DBFilter filter = DBFilter.get();
        String search = request().getQueryString("search");
        if(Utility.isNotEmpty(search)) {
            filter.brS()
                    .field("agent.name").like(search)
                    .or().field("agent.email").like(search)
                    .brE();
        }
        filter.field("isPending", true);
        List<Subscription> subscriptionList = db.find(Subscription.class, filter, param);
        Long total = DBService.Q.count(Subscription.class, filter);
        modelMap.put("subscriptionList", subscriptionList);
        modelMap.put("total", total);
        return ok(views.html.pendingSubscriptions.render(modelMap));
    }

    public Result pendingTopUps(){
        Map<String, Object> modelMap = new HashMap<String, Object>();
        Param param = Utility.getParam();
        DBFilter filter = DBFilter.get();
        String search = request().getQueryString("search");
        if(Utility.isNotEmpty(search)) {
            filter.brS()
                    .field("subscription.agent.name").like(search)
                    .or().field("subscription.agent.email").like(search)
                    .brE();
        }
        filter.field("isPending", true);
        List<SubscriptionPaymentTopUp> subscriptionPaymentTopUpList
                = db.find(SubscriptionPaymentTopUp.class, filter, param);
        Long total = DBService.Q.count(SubscriptionPaymentTopUp.class, filter);
        modelMap.put("subscriptionPaymentTopUpList", subscriptionPaymentTopUpList);
        modelMap.put("total", total);
        return ok(views.html.pendingTopUps.render(modelMap));
    }

    public Result subscriptions(){
        Map<String, Object> modelMap = new HashMap<>();
        Param param = Utility.getParam();
        DBFilter filter = DBFilter.get();
        String search = request().getQueryString("search");
        if(Utility.isNotEmpty(search)) {
            filter.brS()
                    .field("name").like(search)
                    .or().field("email").like(search)
                    .brE();
        }
        List<Agent> agentList = db.find(Agent.class, filter, param);
        Long total = DBService.Q.count(Agent.class, filter);

        modelMap.put("agentList", agentList);
        modelMap.put("total", total);
        return ok(views.html.subscriptions.render(modelMap));
    }

    public Result subscriptionHistory(Long id){
        Map<String, Object> modelMap = new HashMap<>();
        Agent agent = db.findOne(Agent.class, id);
        Param param = Utility.getParam();
        param.setSort("created DESC");
        DBFilter filter = DBFilter.get();
        filter.field("agent", agent);
        filter.field("isPending", false);
        List<Subscription> subscriptionList = db.find(Subscription.class, filter, param);
        Long total = DBService.Q.count(Subscription.class, filter);
        modelMap.put("subscriptionList", subscriptionList);
        modelMap.put("agent", agent);
        modelMap.put("total", total);
        return ok(views.html.subscriptionHistory.render(modelMap));
    }

    public Result subscriptionTopUps(Long id){
        Map<String, Object> modelMap = new HashMap<>();
        Subscription subscription = db.findOne(Subscription.class, id);
        Param param = Utility.getParam();
        param.setSort("created DESC");
        DBFilter filter = DBFilter.get();
        filter.field("subscription", subscription);
        filter.field("isPending", false);
        List<SubscriptionPaymentTopUp> subscriptionPaymentTopUpList
                = db.find(SubscriptionPaymentTopUp.class, filter, param);
        Long total = DBService.Q.count(SubscriptionPaymentTopUp.class, filter);
        modelMap.put("subscription", subscription);
        modelMap.put("subscriptionPaymentTopUpList", subscriptionPaymentTopUpList);
        modelMap.put("total", total);
        return ok(views.html.subscriptionTopUp.render(modelMap));
    }

    public Result activatePendingSubscription(){
        DynamicForm requestData = formFactory.form().bindFromRequest();
        Long subscriptionId = Long.parseLong(requestData.get("subscriptionId"));
        Subscription pendingSubscription = db.findOne(Subscription.class, subscriptionId);
        pendingSubscription.setIsPending(false);
        pendingSubscription.setIsActive(true);

        if(!pendingSubscription.getName().equalsIgnoreCase("Basic")){
            Date today = new Date();
            pendingSubscription.setActivationDate(today);
            pendingSubscription.setExpiryDate(addDays(today, pendingSubscription.getDuration()));
        }

        Agent agent = pendingSubscription.getAgent();
        if(agent.getCurrentSubscription() != null){
            rollOverExistingSubscriptionProducts(agent.getCurrentSubscription(), pendingSubscription);
            DBFilter filter = DBFilter.get();
            filter.field("isPending", false);
            filter.field("subscription", agent.getCurrentSubscription());
            SubscriptionPaymentTopUp existingSubscriptionPaymentTopUp = null;
            List<SubscriptionPaymentTopUp> existingSubscriptionPaymentTopUps
                    = db.find(SubscriptionPaymentTopUp.class, filter, "created DESC");
            if(!existingSubscriptionPaymentTopUps.isEmpty()){
                existingSubscriptionPaymentTopUp = existingSubscriptionPaymentTopUps.get(0);
                rollOverExistingSubscriptionProductTopUps(pendingSubscription, existingSubscriptionPaymentTopUp);
            }
        }
        agent.setCurrentSubscription(pendingSubscription);
        db.save(pendingSubscription);
        db.merge(agent);
        flash().put("activationMsg", "Plan was successfully activated.");
        return redirect(routes.AdminSubscriptionController.pendingSubscriptions());
    }

    private void rollOverExistingSubscriptionProducts(Subscription currentSubscription,
                                                      Subscription pendingSubscription){
        List<Subscription.Product> currentSubscriptionProducts = currentSubscription.getProducts();
        List<Subscription.Product> pendingSubscriptionProducts = pendingSubscription.getProducts();
        for(Subscription.Product currentSubscriptionProduct : currentSubscriptionProducts){
            for(Subscription.Product pendingSubscriptionProduct : pendingSubscriptionProducts){
                if(currentSubscriptionProduct.getProductType() == pendingSubscriptionProduct.getProductType()){
                    if(currentSubscriptionProduct.getRemainder() != null){
                        pendingSubscriptionProduct.setRemainder(pendingSubscriptionProduct.getRemainder()
                         + currentSubscriptionProduct.getRemainder());
                    }
                }
            }
        }
    }

    private void rollOverExistingSubscriptionProductTopUps(Subscription pendingSubscription,
                                                           SubscriptionPaymentTopUp existingSubscriptionPaymentTopUp){
        List<Subscription.Product> pendingSubscriptionProducts = pendingSubscription.getProducts();
        List<Subscription.Product> existingSubscriptionProductTopUps
                = existingSubscriptionPaymentTopUp.getProducts();
        for(Subscription.Product pendingSubscriptionProduct : pendingSubscriptionProducts){
            for(Subscription.Product existingSubscriptionProductTopUp : existingSubscriptionProductTopUps){
                if(pendingSubscriptionProduct.getProductType() == existingSubscriptionProductTopUp.getProductType()){
                    if(existingSubscriptionProductTopUp.getRemainder() != null){
                        pendingSubscriptionProduct.setRemainder(pendingSubscriptionProduct.getRemainder()
                        + existingSubscriptionProductTopUp.getRemainder());
                    }
                }
            }
        }
    }

    public Result activatePendingTopUp(){
        DynamicForm requestData = formFactory.form().bindFromRequest();
        Long subscriptionPaymentTopUpId = Long.parseLong(requestData.get("subscriptionPaymentTopUpId"));
        SubscriptionPaymentTopUp pendingSubscriptionPaymentTopUp =
                db.findOne(SubscriptionPaymentTopUp.class, subscriptionPaymentTopUpId);
        Agent agent = pendingSubscriptionPaymentTopUp.getSubscription().getAgent();
        Subscription currentSubscription = agent.getCurrentSubscription();

        List<Subscription.Product> pendingSubscriptionPaymentTopUpProducts
                = pendingSubscriptionPaymentTopUp.getProducts();

        DBFilter filter = DBFilter.get();
        filter.field("isPending", false);
        filter.field("subscription", currentSubscription);

        List<SubscriptionPaymentTopUp> mostRecentSubscriptionPaymentTopUpList
                = db.find(SubscriptionPaymentTopUp.class, filter, "created DESC");

        SubscriptionPaymentTopUp mostRecentSubscriptionPaymentTopUp = null;
        if(!mostRecentSubscriptionPaymentTopUpList.isEmpty()){
            mostRecentSubscriptionPaymentTopUp = mostRecentSubscriptionPaymentTopUpList.get(0);

            List<Subscription.Product> mostRecentSubscriptionPaymentTopUpProducts
                    = mostRecentSubscriptionPaymentTopUp.getProducts();

            for(Subscription.Product pendingSubscriptionPaymentTopUpProduct : pendingSubscriptionPaymentTopUpProducts){
                for(Subscription.Product mostRecentSubscriptionPaymentTopUpProduct : mostRecentSubscriptionPaymentTopUpProducts){
                    if(pendingSubscriptionPaymentTopUpProduct.getProductType() == mostRecentSubscriptionPaymentTopUpProduct.getProductType()){
                        if(mostRecentSubscriptionPaymentTopUpProduct.getRemainder() != null){
                            pendingSubscriptionPaymentTopUpProduct.setRemainder(pendingSubscriptionPaymentTopUpProduct.getRemainder()
                                    + mostRecentSubscriptionPaymentTopUpProduct.getRemainder() );
                        }
                    }
                }
            }
        }

        pendingSubscriptionPaymentTopUp.setIsPending(false);
        db.save(pendingSubscriptionPaymentTopUp);
        flash().put("activationMsg", "Top Up was successfully activated.");
        return redirect(routes.AdminSubscriptionController.pendingTopUps());
    }

    private Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

}
