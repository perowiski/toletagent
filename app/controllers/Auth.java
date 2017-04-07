package controllers;

import models.Admin;
import models.Agent;
import models.Permission;
import play.mvc.Http;
import services.CacheService;
import services.DBService;

import java.util.Set;

/**
 * @author seyi
 */

public class Auth {
    
    public static final String ADMIN = "admin";
    public static final String AGENT = "account";

    public static Agent getAgent() {
        String id = Http.Context.current().session().get(AGENT);
        if(id != null) {
            Agent agent = (Agent) CacheService.C.get(AGENT + id);
            if (agent == null) {
                agent =  DBService.Q.findOne(Agent.class, id);
                if (agent != null) {
                    cacheAgent(agent);
                }
            }
            return agent;
        }
        return null;
    }

    public static void setAgent(Agent agent) {
        Http.Context.current().session().put(AGENT, agent.getId().toString());
        cacheAgent(agent);
    }

    public static void cacheAgent(Agent agent) {
        CacheService.C.set(ADMIN+agent.getId(), agent);
    }

    public static boolean isAdmin() {
        return getAdmin() != null;
    }

    public static boolean isAgent() {
        return getAgent() != null;
    }

    public static Admin getAdmin() {
        String id = Http.Context.current().session().get(ADMIN);
        if(id != null) {
            Admin admin = (Admin) CacheService.C.get(ADMIN + id);
            if (admin == null) {
                admin =  DBService.Q.findOne(Admin.class, id);
                if (admin != null) {
                    CacheService.C.set(ADMIN+admin.getId(), admin);
                }
            }
            return admin;
        }
        return null;
    }

    public static void setAdmin(Admin admin) {
        Http.Context.current().session().put(ADMIN, admin.getId().toString());
        CacheService.C.set(ADMIN+admin.getId(), admin);
    }

    public static boolean hasPerm(Permission perm) {
        Admin admin = getAdmin();
        if(admin != null) {
            Set<Permission> permissions = admin.permissions();
            if(permissions.contains(Permission.SUPER)) {
                return true;
            }
            return permissions.contains(perm);
        }
        return false;
    }
    
    public static boolean hasAnyPerm(Permission... perms) {
        Admin admin = getAdmin();
        if(admin != null) {
            Set<Permission> permissions = admin.permissions();
            if(permissions.contains(Permission.SUPER)) {
                return true;
            }
            for (Permission perm : perms) {
                if (permissions.contains(perm)) {
                    return true;
                }
            }
        }
        return false;
    }
}
