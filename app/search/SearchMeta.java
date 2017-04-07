package search;

import controllers.Utility;
import pojos.Param;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by seyi on 7/27/16.
 */
public class SearchMeta {
    public String title = "";
    public String description = "";
    public String keywords = "";

    public String canonical = "";
    public String location = "";

    public SearchMeta(SearchReq req, Param param) {
        location = Utility.isNotEmpty(req.area)? Utility.capitalize(req.area) + " ":"";
        location += Utility.isNotEmpty(req.axis)? Utility.capitalize(req.axis) + " ":"";
        location += Utility.isNotEmpty(req.state)? Utility.capitalize(req.state) +" Nigeria,": " Nigeria,";

        req.faci.forEach(faci -> {
            title += Utility.capitalize(faci) + " ";
        });

        boolean noType = true;

        if(Utility.isNotEmpty(req.use)) {
            String use = Utility.capitalize(req.use);
            title += use + " ";
            keywords += use + ", ";
        }

        if(req.beds != null && req.beds > 0) {
            String beds = " "+req.beds + " Bedroom";
            title += beds + " ";
            keywords += beds + ", ";
        }

        if(Utility.isNotEmpty(req.type)) {
            String type = req.type;
            if(Utility.isNotEmpty(type)) {
                type = Arrays.asList(type.split(",")).stream().map(en -> Utility.capitalize(en.trim())).collect(Collectors.joining(", "));
            } else {
                type = "";
            }
            String propType = propType(req,type);
            type = propType;

            title += type + " ";
            keywords += type + ", ";
            noType = false;
        } else {
            title += " Properties & Houses ";
        }

        if(Utility.isNotEmpty(req.mode)) {
            title += "for " + req.mode + " ";
            //keywords += title + ", ";
            String propMode = req.mode.equals("rent")?" let": req.mode;
            //Utility.isNotEmpty(req.type)?cleanType(req):"Real Estate"
            keywords += (req !=null && Utility.isNotEmpty(req.type) ? cleanType(req) : "Real Estate") + " for " + propMode + " in " + location+" ";
            keywords += title + "";
        }

        boolean hasAxis=false;

        if(Utility.isNotEmpty(req.axis)) {
            hasAxis=true;
            String axis = req.axis;
            if(Utility.isNotEmpty(axis)) {
                axis = Arrays.asList(axis.split(",")).stream().map(en -> Utility.capitalize(en.trim())).collect(Collectors.joining(", "));
            } else {
                axis = "";
            }

            String area = req.area;
            if(Utility.isNotEmpty(area)) {
                area = Arrays.asList(area.split(",")).stream().map(en -> Utility.capitalize(en.trim())).collect(Collectors.joining(", "));
            } else {
                area = "";
            }

            title += "in " + area + " " + axis + " ";
            if(noType) {
                /*keywords += "Real Estate to "+ (("rent".equals(req.mode)) ? "let": req.mode) +" in Nigeria, " +
                        "Property & Houses For "+ req.mode +" in "+ axis +", "+ area + " " +axis +", " +
                        "Lagos Nigeria - Flats, houses, Apartments, land, commercial property, office space, " +
                        "self contain, bedroom flat, mini flat, bq, shop, apartment, ";*/
                keywords += "Real Estate to "+ (("rent".equals(req.mode)) ? "let": req.mode) +" in Nigeria, " +
                        "Property & Houses For "+ req.mode +" in "+ location+" ";
            }

            keywords += area + " " + axis + " ";
        }

        String in = "";
        if(!hasAxis){
            in = "in ";
        }

        String append = " " +in+ Utility.capitalize(req.state) + " Nigeria";
        title += append + " ";
        keywords += moreKeyWords(req, append) +", "+ location.replace("Nigeria","")+" Flats, houses, Apartments, land, commercial property, office space, self contain, Nigerian Real Estate & Property";

        if(param.getPage() > 0) {
            title += "| Page " + param.getPage();
            //keywords += "Page " + param.getPage();
        }

        description = ((req.type != null) ? cleanType(req)+" ": "Real Estate") + (("rent".equals(req.mode)) ? " to let": " for sale") +
                " in " +location+
                " " + title.trim() + ", Nigerian Real estate and Property";

        if(Utility.isNotEmpty(req.state)) {
            canonical += "/" + req.state;
        }

        if(Utility.isNotEmpty(req.axis)) {
            canonical += "/" + req.axis;
        }

        if(Utility.isNotEmpty(req.area)) {
            canonical += "/" + req.area;
        }

        if(Utility.isNotEmpty(req.type)) {
            canonical += "/" + req.type;
        }

        if(req.beds != null && req.beds > 0) {
            canonical += "?beds=" + req.beds;
        }

    }

    public String propType(SearchReq req,String type) {
        if(req != null && Utility.isNotEmpty(req.type)) {
            if(req.beds != null && req.beds == 1 && req.type.equals("flat-apartment")){
                return "Mini Flat";
            } else if (req.type.equals("boys-quarters")) {
                return type + ", bq";
            } else if (req.type.equals("land")) {
                return type + ", Plots, Acres , Hectares of Land";
            }
        }
        return type;
    }

    public String cleanType(SearchReq req) {
        if(req != null && Utility.isNotEmpty(req.type)) {
            String type = req.type;
            if(req.type.equals("flat-apartment") && req.beds != null && req.beds == 1) {
                type = "mini-flat";
            }
            type = type.contains("-") ? type.replace("-"," ") : type;
            if(type.length() > 1) {
                String firstChar = type.substring(0,1).toUpperCase();
                String otherChars = type.substring(1,type.length()).toLowerCase();
                return firstChar + otherChars;
            }
        }
        return req.type;
    }

    public String moreKeyWords(SearchReq req, String keywords) {
        if(req != null && Utility.isNotEmpty(req.type)) {
            if(req.type.equals("self-contain")) {
                keywords += ", a room Self Contain " + location;
            }
        }
        return keywords;
    }
}
