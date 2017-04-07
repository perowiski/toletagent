package controllers;

import authority.SecuredAgent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import models.*;
import models_enums.PaymentType;
import models_enums.ProductType;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import pojos.Param;
import pojos.SubscriptionForm;
import pojos.TellerPaymentDetail;
import pojos.TopUpForm;
import services.DBFilter;
import services.DBService;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by peter on 3/8/17.
 */
@Security.Authenticated(SecuredAgent.class)
@Transactional
public class AgentSubscriptionController extends Controller{

    private final DBService db;
    private final FormFactory formFactory;

    @Inject
    public AgentSubscriptionController(FormFactory formFactory, DBService db) {
        this.formFactory = formFactory;
        this.db = db;
    }

    public Result index(){
        Map<String, Object> modelMap = new HashMap<>();
        Agent agent = Auth.getAgent();
        Subscription subscription = agent.getCurrentSubscription();
        DBFilter filter = DBFilter.get();
        filter.field("isPending", false);
        filter.field("subscription", subscription);
        SubscriptionPaymentTopUp subscriptionPaymentTopUp = null;
        List<SubscriptionPaymentTopUp> subscriptionPaymentTopUpList = db.find(SubscriptionPaymentTopUp.class, filter, "created DESC");
        if(!subscriptionPaymentTopUpList.isEmpty()){
            subscriptionPaymentTopUp = subscriptionPaymentTopUpList.get(0);
        }
        DBFilter filter2 = DBFilter.get();
        filter2.field("isPending", true);
        filter2.field("agent", agent);
        Subscription pendingSubscription = null;
        List<Subscription> pendingSubscriptionList = db.find(Subscription.class, filter2, "created DESC");
        if(!pendingSubscriptionList.isEmpty()){
            pendingSubscription = pendingSubscriptionList.get(0);
        }
        DBFilter filter3 = DBFilter.get();
        filter3.field("isPending", true);
        filter3.field("subscription", subscription);
        SubscriptionPaymentTopUp pendingSubscriptionPaymentTopUp = null;
        List<SubscriptionPaymentTopUp> pendingSubscriptionPaymentTopUpList = db.find(SubscriptionPaymentTopUp.class, filter3, "created DESC");
        if(!pendingSubscriptionPaymentTopUpList.isEmpty()){
            pendingSubscriptionPaymentTopUp = pendingSubscriptionPaymentTopUpList.get(0);
        }
        System.out.println(pendingSubscriptionPaymentTopUp);
        modelMap.put("subscription", subscription);
        modelMap.put("subscriptionPaymentTopUp", subscriptionPaymentTopUp);
        modelMap.put("pendingSubscription", pendingSubscription);
        modelMap.put("pendingSubscriptionPaymentTopUp", pendingSubscriptionPaymentTopUp);
        return ok(views.html.profile.subscription.render(modelMap));
    }

    public Result topUpProduct(){
        Agent agent = Auth.getAgent();
        Subscription subscription = agent.getCurrentSubscription();
        List<Plan> plans = db.find(Plan.class, DBFilter.get());
        Plan plan = db.findOne(Plan.class, DBFilter.get().field("name", subscription.getName()));
        DBFilter filter = DBFilter.get();
        filter.field("plan", plan);
        filter.field("maximum").notNull();
        System.out.println(plan.id);
        List<Product> products = db.find(Product.class, filter);

        DBFilter filter3 = DBFilter.get();
        filter3.field("isPending", true);
        filter3.field("subscription", subscription);
        SubscriptionPaymentTopUp pendingSubscriptionPaymentTopUp = null;
        List<SubscriptionPaymentTopUp> pendingSubscriptionPaymentTopUpList = db.find(SubscriptionPaymentTopUp.class, filter3, "created DESC");
        if(!pendingSubscriptionPaymentTopUpList.isEmpty()){
            pendingSubscriptionPaymentTopUp = pendingSubscriptionPaymentTopUpList.get(0);
        }

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("plans", plans);
        modelMap.put("products", products);
        modelMap.put("subscription", subscription);
        modelMap.put("pendingSubscriptionPaymentTopUp", pendingSubscriptionPaymentTopUp);
        return ok(views.html.profile.topup.render(modelMap));
    }

    public Result changePlan(){
        Agent agent = Auth.getAgent();
        Subscription subscription = agent.getCurrentSubscription();
        List<Plan> plans = db.find(Plan.class, DBFilter.get());
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("plans", plans);
        modelMap.put("subscription", subscription);
        DBFilter filter = DBFilter.get();
        filter.field("isPending", true);
        filter.field("agent", agent);
        Subscription pendingSubscription = null;
        List<Subscription> pendingSubscriptionList = db.find(Subscription.class, filter, "created DESC");
        if(!pendingSubscriptionList.isEmpty()){
            pendingSubscription = pendingSubscriptionList.get(0);
        }
        modelMap.put("pendingSubscription", pendingSubscription);
        return ok(views.html.profile.changePlan.render(modelMap));
    }

    public Result renewPlan(){
        Agent agent = Auth.getAgent();
        Subscription subscription = agent.getCurrentSubscription();
        Plan plan = db.findOne(Plan.class, DBFilter.get().field("name", subscription.name));
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("plan", plan);
        modelMap.put("subscription", subscription);
        return ok(views.html.profile.renewPlan.render(modelMap));
    }


    public Result planInfo(Long id){
        Plan p = db.findOne(Plan.class, id);
        Plan plan = new Plan();
        plan.setId(p.getId());
        plan.setName(p.getName());
        plan.setPrice(p.getPrice());
        plan.setDuration(p.getDuration());
        plan.setCreated(p.getCreated());
        return ok(Json.toJson(plan));
    }

    public Result paymentTypes(){
        Map<String, String> paymentTypes = PaymentType.select();
        String out = paymentTypes.remove("Free");
        return ok(Json.toJson(paymentTypes));
    }

    public Result subscriptionTopUps(Long id){
        Map<String, Object> modelMap = new HashMap<>();
        Subscription subscription = db.findOne(Subscription.class, id);
        Param param = Utility.getParam();
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

    public Result subscribe(){
        Form<SubscriptionForm> form = formFactory.form(SubscriptionForm.class);
        SubscriptionForm subscriptionForm = form.bindFromRequest().get();
        Long planId = subscriptionForm.getPlanId();
        Agent agent = Auth.getAgent();
        DBFilter filter = DBFilter.get();
        filter.field("id", planId);
        Plan plan = db.findOne(Plan.class, filter);
        Subscription subscription = new Subscription();
        subscription.setName(plan.getName());
        subscription.setPrice(plan.getPrice());
        subscription.setDuration(plan.getDuration());
        subscription.setIsPending(true);
        subscription.setIsActive(false);
        if(subscriptionForm.getPaymentType().equals("Teller")){
            TellerPaymentDetail tellerPaymentDetail = new TellerPaymentDetail();
            tellerPaymentDetail.setAmountPaid(Double.valueOf(subscriptionForm.getAmountPaid()));
            tellerPaymentDetail.setTellerNumber(subscriptionForm.getTellerNumber());
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = formatter.parse(subscriptionForm.getPaymentDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                tellerPaymentDetail.setPaymentDate(calendar);
                subscription.setPaymentDetail(tellerPaymentDetail);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (PaymentType paymentType : PaymentType.values()){
            if(paymentType.toString().equals(subscriptionForm.getPaymentType())){
                subscription.setPaymentType(paymentType);
            }
        }
        List<Product> planProducts =  plan.getProducts();
        List<Subscription.Product> subscriptionProducts = new ArrayList<>();
        for(Product planProduct : planProducts){
            Subscription.Product subscriptionProduct = new Subscription.Product();
            subscriptionProduct.setRemainder((planProduct.getMaximum() != null)? planProduct.getMaximum() : null);
            subscriptionProduct.setMaximum((planProduct.getMaximum() != null)? planProduct.getMaximum() : null);
            subscriptionProduct.setProductType(planProduct.getProductType());
            subscriptionProducts.add(subscriptionProduct);
        }

        subscription.setProducts(subscriptionProducts);
        subscription.setAgent(agent);
        db.save(subscription);
        db.Q.merge(agent);
        return redirect(routes.AgentSubscriptionController.index());
    }

    public Result topUp(){
        Map<String, String[]> requestParameters = request().body().asFormUrlEncoded();
        Set<String> properties = requestParameters.keySet();
        Agent agent = Auth.getAgent();
        Subscription subscription = agent.getCurrentSubscription();
        SubscriptionPaymentTopUp subscriptionPaymentTopUp = new SubscriptionPaymentTopUp();
        subscriptionPaymentTopUp.setIsPending(true);
        Form<TopUpForm> form = formFactory.form(TopUpForm.class);
        TopUpForm topUpForm = form.bindFromRequest().get();
        Plan plan = db.findOne(Plan.class, DBFilter.get().field("name", subscription.getName()));
        ArrayList<Subscription.Product> topUpProducts = new ArrayList<>();
        for(ProductType productType : ProductType.values()){
            if(properties.contains(productType.name)){
                Subscription.Product product = new Subscription.Product();
                Double unitPrice = getUnitPrice(productType, plan);
                String unit = requestParameters.get(productType.name)[0];
                product.setUnitPrice(unitPrice);
                product.setMaximum(Integer.valueOf(unit));
                product.setRemainder(Integer.valueOf(unit));
                product.setProductType(productType);
                topUpProducts.add(product);
            }
        }
        if(topUpForm.getPaymentType().equals("Teller")){
            TellerPaymentDetail tellerPaymentDetail = new TellerPaymentDetail();
            tellerPaymentDetail.setAmountPaid(Double.valueOf(topUpForm.getAmountPaid()));
            tellerPaymentDetail.setTellerNumber(topUpForm.getTellerNumber());
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = formatter.parse(topUpForm.getPaymentDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                tellerPaymentDetail.setPaymentDate(calendar);
                subscriptionPaymentTopUp.setPaymentDetail(tellerPaymentDetail);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (PaymentType paymentType : PaymentType.values()){
            if(paymentType.toString().equals(topUpForm.getPaymentType())){
                subscriptionPaymentTopUp.setPaymentType(paymentType);
            }
        }
        subscriptionPaymentTopUp.setProducts(topUpProducts);
        subscriptionPaymentTopUp.setSubscription(subscription);
        db.save(subscriptionPaymentTopUp);
        return redirect(routes.AgentSubscriptionController.index());
    }


    private Double getUnitPrice(ProductType productType, Plan plan){
        System.out.println(productType);
        System.out.println(plan.name);
        DBFilter filter = DBFilter.get();
        filter.field("productType", productType);
        filter.field("plan", plan);
        Product product = db.findOne(Product.class, filter);
        return product.getUnitPrice();
    }

}
