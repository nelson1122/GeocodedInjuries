/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 
package controller.managedbeans;

import controller.util.JsfUtil;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import model.dao.GeoDBConnection;
import model.vo.GeoreferencedInjuries;
import model.vo.Variable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@ManagedBean(name = "geoGSONMB")
@ViewScoped
public class GeoJSONMB implements Serializable {

    private GeoDBConnection geoInjuries;
    private JSONObject root;
    private String geoJSON;
    

    //charts
    private String xAxisData;
    private String series;
    private String murderSerie;
    private String suicideSerie;
    private String accidentalSerie;
    private String trafficSerie;
    private String selectedAreaPoints = "POINT(0 0),POINT(0 0),POINT(0 0), POINT(0 0)";

    public GeoJSONMB() {
    }

    @PostConstruct
    public void init() {
        geoInjuries = new GeoDBConnection("localhost", "5432", "delitos", "postgres", "postgres1");
        root = new JSONObject();
        try {
            initial = sm.parse(strInitial);
            end = sm.parse(strEnd);
        } catch (ParseException ex) {
            Logger.getLogger(GeoJSONMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        processFatalInjuries();
        processCountByYear();
    }

    public void processFatalInjuries() {

        strInitial = sm.format(initial);
        strEnd = sm.format(end);

        List<GeoreferencedInjuries> geoData = geoInjuries.listFatalInjuries(selectedCategory, strInitial, strEnd);
        try {
            JSONArray featuresArray = new JSONArray();
            for (int i = 0; i < geoData.size(); i++) {
                JSONArray coordinates = new JSONArray();
                coordinates.put(0, geoData.get(i).getX());
                coordinates.put(1, geoData.get(i).getY());

                JSONObject feature = new JSONObject();
                feature.put("type", "Point");
                feature.put("coordinates", coordinates);

                JSONObject properties = new JSONObject();
                properties.put("fatal_injury_id", geoData.get(i).getInjury_id());

                JSONObject geometry = new JSONObject();
                geometry.put("type", "Feature");
                geometry.put("geometry", feature);
                geometry.put("properties", properties);

                featuresArray.put(i, geometry);
            }
            root.put("features", featuresArray);
            root.put("type", "FeatureCollection");

            geoJSON = root.toString();

            JsfUtil.addSuccessMessage("Mapa creado exitosamente.");

        } catch (Exception ex) {
            Logger.getLogger(GeoJSONMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void processCountByYear() {

        String[] strI = strInitial.split("/");
        System.out.println(strI[0]);

        String[] strE = strEnd.split("/");
        System.out.println(strE[0]);
        int initialYear = Integer.parseInt(strI[0]);
        int endYear = Integer.parseInt(strE[0]);
        int difference = endYear - initialYear;
        JSONArray jsonYears = new JSONArray();
        JSONArray jsonMurders = new JSONArray();
        JSONArray jsonSuicides = new JSONArray();
        JSONArray jsonAccidentals = new JSONArray();
        JSONArray jsonTraffic = new JSONArray();

        List<Variable> chartData = geoInjuries.listCountbyYears(strInitial, strEnd, selectedAreaPoints);

        try {
            for (int i = 0; i < difference + 1; i++) {
                jsonYears.put(i, Integer.toString(initialYear + i));
            }

            for (int j = 0; j < chartData.size(); j++) {
                
                System.out.println(chartData.get(j).getColumn1()+" "+chartData.get(j).getColumn2()+" "+chartData.get(j).getScore());
                
                int riseYear = initialYear;
                
                for (int i = 0; i < difference + 1; i++) {
                    switch (chartData.get(j).getColumn1()) {
                        case "HOMICIDIO":
                            System.out.println("VALOR COMPARACION");
                            System.out.println(riseYear == chartData.get(j).getColumn2());
                            System.out.println("VALOR");
                            if (riseYear == chartData.get(j).getColumn2()) {
                                jsonMurders.put(i, chartData.get(j).getScore());
                            } else {
                                jsonMurders.put(i, 0);
                            }
                            break;
                        case "SUICIDIO":
                            if (chartData.get(j).getColumn2() == riseYear) {
                                jsonSuicides.put(i, chartData.get(j).getScore());
                            } else {
                                jsonSuicides.put(i, 0);
                            }
                            break;
                        case "MUERTE EN ACCIDENTE DE TRANSITO":
                            if (chartData.get(j).getColumn2() == riseYear) {
                                jsonAccidentals.put(i, chartData.get(j).getScore());
                            } else {
                                jsonAccidentals.put(i, 0);
                            }
                            break;

                        case "MUERTE ACCIDENTAL":
                            if (chartData.get(j).getColumn2() == riseYear) {
                                jsonTraffic.put(i, chartData.get(j).getScore());
                            } else {
                                jsonTraffic.put(i, 0);
                            }
                            break;
                    }
                    riseYear++;
                    System.out.println("INC= "+riseYear);
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(GeoJSONMB.class.getName()).log(Level.SEVERE, null, ex);
        }

        xAxisData = jsonYears.toString();
        murderSerie = jsonMurders.toString();
        suicideSerie = jsonSuicides.toString();
        accidentalSerie = jsonAccidentals.toString();
        trafficSerie = jsonTraffic.toString();

        JsfUtil.addSuccessMessage(selectedAreaPoints);

        //CREACION GRAFICO
    }

    public JSONObject getRoot() {
        return root;
    }

    public void setRoot(JSONObject root) {
        this.root = root;
    }

    public Date getInitial() {
        return initial;
    }

    public void setInitial(Date initial) {
        this.initial = initial;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getGeoJSON() {
        return geoJSON;
    }

    public void setGeoJSON(String geoJSON) {
        this.geoJSON = geoJSON;
    }

    public List<SelectItem> getCategories() {
        return categories;
    }

    public void setCategories(List<SelectItem> categories) {
        this.categories = categories;
    }

    public int getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(int selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    /*
    switch(selectedCategory){
            case 1: selectedCategory = "General"; break;
            case 12: selectedCategory = "Homicidios"; break;
            case 19: selectedCategory = "Suicidios"; break;
            case 5: selectedCategory = "No intencionales"; break;
            case 26: selectedCategory="Accidentes de transito"; break;
        }
     
    public String getxAxisData() {
        return xAxisData;
    }

    public void setxAxisData(String xAxisData) {
        this.xAxisData = xAxisData;
    }

    public String getSelectedAreaPoints() {
        return selectedAreaPoints;
    }

    public void setSelectedAreaPoints(String selectedAreaPoints) {
        this.selectedAreaPoints = selectedAreaPoints;
    }

    public String getMurderSerie() {
        return murderSerie;
    }

    public void setMurderSerie(String murderSerie) {
        this.murderSerie = murderSerie;
    }

    public String getSuicideSerie() {
        return suicideSerie;
    }

    public void setSuicideSerie(String suicideSerie) {
        this.suicideSerie = suicideSerie;
    }

    public String getAccidentalSerie() {
        return accidentalSerie;
    }

    public void setAccidentalSerie(String accidentalSerie) {
        this.accidentalSerie = accidentalSerie;
    }

    public String getTrafficSerie() {
        return trafficSerie;
    }

    public void setTrafficSerie(String trafficSerie) {
        this.trafficSerie = trafficSerie;
    }

}
*/