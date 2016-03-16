/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.managedbeans;

import controller.util.JsfUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import model.geojson.GeoJSONFeatures;
import org.json.JSONObject;

/**
 *
 * @author nelson
 */
@ManagedBean
@ViewScoped
public class InjuriesCountMB {

    private Date initialDate;
    private Date endDate;
    private String initialDateStr = "2012/01/01";
    private String endDateStr = "2013/01/01";
    private SimpleDateFormat sm = new SimpleDateFormat("yyyy/MM/dd");
    private int fatalInjuriesSelectedCategory = 1;
    private int nonfatalInjuriesSelectedCategory = 3;
    
    //Spatial fields
    private String selectedAreaPoints = "XYZ";
    
    //GeoJSON fields
    private JSONObject fatalInjuriesFeatures;
    private JSONObject nonFatalInjuriesFeatures;
    private GeoJSONFeatures geoJSONInjuries = new GeoJSONFeatures();
    
    //Chart fields
    private JSONObject categoriesData;
    private JSONObject seriesData;

    /**
     * Creates a new instance of CountInjuriesMB
     */
    public InjuriesCountMB() {
    }

    @PostConstruct
    public void init() {
        try {
            initialDate = sm.parse(initialDateStr);
            endDate = sm.parse(endDateStr);
        } catch (ParseException ex) {
            Logger.getLogger(InjuriesCountMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        injuriesVisualizationProcess();
    }

    public void injuriesVisualizationProcess() {
        initialDateStr = sm.format(initialDate);
        endDateStr = sm.format(endDate);
        fatalInjuriesFeatures = geoJSONInjuries.processGeoJSONFatalInjuries(fatalInjuriesSelectedCategory, initialDateStr, endDateStr);
        nonFatalInjuriesFeatures = geoJSONInjuries.processGeoJSONNonFatalInjuries(nonfatalInjuriesSelectedCategory, initialDateStr, endDateStr);
        JsfUtil.addSuccessMessage("Mapa creado exitosamente");
    }
    
    public void statisticsGenerationProcess(){
    
    }
    
    /*
        Getters and Setters
    */
    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public String getInitialDateStr() {
        return initialDateStr;
    }

    public void setInitialDateStr(String initialDateStr) {
        this.initialDateStr = initialDateStr;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public int getFatalInjuriesSelectedCategory() {
        return fatalInjuriesSelectedCategory;
    }

    public void setFatalInjuriesSelectedCategory(int fatalInjuriesSelectedCategory) {
        this.fatalInjuriesSelectedCategory = fatalInjuriesSelectedCategory;
    }

    public int getNonfatalInjuriesSelectedCategory() {
        return nonfatalInjuriesSelectedCategory;
    }

    public void setNonfatalInjuriesSelectedCategory(int nonfatalInjuriesSelectedCategory) {
        this.nonfatalInjuriesSelectedCategory = nonfatalInjuriesSelectedCategory;
    }

    public JSONObject getFatalInjuriesFeatures() {
        return fatalInjuriesFeatures;
    }

    public void setFatalInjuriesFeatures(JSONObject fatalInjuriesFeatures) {
        this.fatalInjuriesFeatures = fatalInjuriesFeatures;
    }
    
    public JSONObject getNonFatalInjuriesFeatures() {
        return nonFatalInjuriesFeatures;
    }

    public void setNonFatalInjuriesFeatures(JSONObject nonFatalInjuriesFeatures) {
        this.nonFatalInjuriesFeatures = nonFatalInjuriesFeatures;
    }

    public String getSelectedAreaPoints() {
        return selectedAreaPoints;
    }

    public void setSelectedAreaPoints(String selectedAreaPoints) {
        this.selectedAreaPoints = selectedAreaPoints;
    }
    
    public JSONObject getCategoriesData() {
        return categoriesData;
    }

    public void setCategoriesData(JSONObject categoriesData) {
        this.categoriesData = categoriesData;
    }

    public JSONObject getSeriesData() {
        return seriesData;
    }

    public void setSeriesData(JSONObject seriesData) {
        this.seriesData = seriesData;
    }

}
