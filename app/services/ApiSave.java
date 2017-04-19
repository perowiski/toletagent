package services;

import controllers.Auth;
import controllers.Utility;
import models.*;
import models_enums.PaymentType;
import models_enums.ProductType;
import pojos.AgentData;
import pojos.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by seyi on 12/2/16.
 */
public class ApiSave {
    public static void crawlAgents(String start, String end) {
        Agent[] agents = ApiPull.getAgents(start, end);
        for(Agent agent: agents) {
            try {
                DBService.Q.withTransaction(() -> {
                    saveAgent(agent);
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void saveAgent(Agent agent) {
        Agent o = DBService.Q.findOne(Agent.class, agent.id);
        if (o == null) {
            DBService.Q.persist(agent);
            enableDefaultPlan(agent);
            o = agent;
        } else {
            ApiSave.merge(o, agent);
            DBService.Q.merge(o);
        }

        Auth.cacheAgent(o);

        System.out.println(agent.name);
    }

    public static void enableDefaultPlan(Agent agent){
        DBFilter filter = DBFilter.get();
        filter.field("name", Utility.getBasicPlanName());
        Plan plan = DBService.Q.findOne(Plan.class, filter);
        Subscription subscription = new Subscription();
        subscription.setName(plan.getName());
        subscription.setPrice(plan.getPrice());
        subscription.setIsPending(false);
        subscription.setIsActive(true);
        subscription.setPaymentType(PaymentType.FREE);
        subscription.setDuration(plan.getDuration());

        Date today = new Date();
        subscription.setActivationDate(today);
        subscription.setExpiryDate(Utility.addDays(today, subscription.getDuration()));

        List<Product> planProducts =  plan.getProducts();
        ArrayList<Subscription.Product> subscriptionProducts = new ArrayList<>();
        for(Product planProduct : planProducts){
            Subscription.Product subscriptionProduct = new Subscription.Product();
            subscriptionProduct.setRemainder(planProduct.getMaximum());
            subscriptionProduct.setMaximum(planProduct.getMaximum());
            subscriptionProduct.setProductType(planProduct.getProductType());
            subscriptionProducts.add(subscriptionProduct);
        }
        subscription.setProducts(subscriptionProducts);
        subscription.setAgent(agent);
        DBService.Q.save(subscription);
        agent.setCurrentSubscription(subscription);
        DBService.Q.merge(agent);
        DBService.J.em().flush();
    }

    public static void merge(Agent o, Agent n) {
        o.id = n.id;
        o.name = n.name;
        o.username = n.username;
        o.email = n.email;
        o.phone = n.phone;
        o.logoUrl = n.logoUrl;
        o.type = n.type;
        o.active = n.active;
        o.balance = n.balance;
    }

    public static void fetchAgentDatas() {
        AgentData[] datas = ApiPull.getAgentDatas();
        if(datas != null && datas.length!=0) {
            for (AgentData data : datas) {
                try {
                    DBService.J.withTransaction( () -> {
                        if (data.agentId != null) {
                            Agent agent = DBService.J.em().find(Agent.class, data.agentId);
                            if(agent!=null) {
                                agent.states = data.states;
                                agent.axises = data.axises;
                                agent.areas = data.areas;
                                DBService.Q.merge(agent);
                                Auth.cacheAgent(agent);
                                System.out.println(agent.name);
                            }
                        }
                    });
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

    }

    public static void crawlPropertyLeads(String start, String end) {
        PropertyLead[] propertyLeads = ApiPull.getPropertyLeads(start, end);
        if(propertyLeads != null) {
            for (PropertyLead propertyLead : propertyLeads) {
                try {
                    DBService.Q.withTransaction(() -> {
                        savePropertyLead(propertyLead);
                    });
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static void savePropertyLead(PropertyLead propertyLead) {
        PropertyLead o = DBService.Q.findOne(PropertyLead.class, propertyLead.id);
        if (o == null) {
            DBService.Q.persist(propertyLead);
        } else {
            DBService.Q.merge(propertyLead);
        }
        System.out.println(propertyLead.userName);
    }

    public static void crawlAgentLeads(String start, String end) {
        AgentLead[] agentLeads = ApiPull.getAgentLeads(start, end);
        if(agentLeads != null) {
            for (AgentLead agentLead : agentLeads) {
                try {
                    DBService.Q.withTransaction(() -> {
                        saveAgentLead(agentLead);
                    });
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static void saveAgentLead(AgentLead agentLead) {
        AgentLead o = DBService.Q.findOne(AgentLead.class, agentLead.id);
        if (o == null) {
            DBService.Q.persist(agentLead);
        } else {
            DBService.Q.merge(agentLead);
        }
        System.out.println(agentLead.name);
    }

    public static void saveAgentLeadTest(AgentLead agentLead) {
        AgentLead o = DBService.Q.findOne(AgentLead.class, agentLead.id);
        if (o == null) {
            DBService.Q.persist(agentLead);
            Agent agent = DBService.Q.findOne(Agent.class, o.getAgentId());
            Subscription subscription = agent.currentSubscription;
            List<Subscription.Product> subscriptionProducts = subscription.getProducts();
            for(Subscription.Product subscriptionProduct: subscriptionProducts){
                if(subscriptionProduct.getProductType() == ProductType.FREE_LEADS){
                    if(subscriptionProduct.getRemainder() <= 0) {
                        subscriptionProduct.setRemainder(subscriptionProduct.getRemainder() - 1);
                    }else{
                        DBFilter filter = DBFilter.get();
                        filter.field("isPending", false);
                        filter.field("subscription", subscription);
                        List<SubscriptionPaymentTopUp> subscriptionPaymentTopUpList
                                = DBService.Q.find(SubscriptionPaymentTopUp.class, filter, "created DESC");
                        SubscriptionPaymentTopUp subscriptionPaymentTopUp = null;
                        if(!subscriptionPaymentTopUpList.isEmpty()){
                            subscriptionPaymentTopUp = subscriptionPaymentTopUpList.get(0);
                            List<Subscription.Product> topUpProducts = subscriptionPaymentTopUp.getProducts();
                            for(Subscription.Product topUpProduct : topUpProducts){
                                if(topUpProduct.getProductType() == ProductType.FREE_LEADS){
                                    if(topUpProduct.getRemainder() <= 0) {
                                        topUpProduct.setRemainder(topUpProduct.getRemainder() - 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            DBService.Q.merge(agentLead);
        }
        System.out.println(agentLead.name);
    }

    public static void crawlLocation() {
        Location.State[] locations = ApiPull.getLocation();
        for(Location.State state: locations) {
            try {
                DBService.Q.withTransaction(() -> {
                    EState st = DBService.Q.findOne(EState.class, state.id);
                    if (st == null) {
                        st = new EState();
                        setLocation(st, state);
                        DBService.Q.persist(st);
                    } else {
                        setLocation(st, state);
                        DBService.Q.merge(st);
                    }
                    System.out.println("State: " + st);
                    for(Location.Axis axis: state.axises) {
                        EAxis ax = DBService.Q.findOne(EAxis.class, axis.id);
                        if (ax == null) {
                            ax = new EAxis();
                            setLocation(ax, axis);
                            ax.state = st;
                            DBService.Q.persist(ax);
                        } else {
                            setLocation(ax, axis);
                            ax.state = st;
                            DBService.Q.merge(ax);
                        }
                        System.out.println("Axis: " + ax);

                        for(Location.Area area: axis.areas) {
                            EArea ar = DBService.Q.findOne(EArea.class, area.id);
                            if (ar == null) {
                                ar = new EArea();
                                setLocation(ar, area);
                                ar.axis = ax;
                                DBService.Q.persist(ar);
                            } else {
                                setLocation(ar, area);
                                ar.axis = ax;
                                DBService.Q.merge(ar);
                            }
                            System.out.println("Area: " + ar);
                        }
                    }
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void setLocation(EModel m, Location l) {
        m.id = l.id;
        m.name = l.name;
        m.hide = l.hidden;
    }
}