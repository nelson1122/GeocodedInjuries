/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.geojson;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.dao.GeoDBConnection;
import model.vo.GeoreferencedInjuries;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author nelson
 */
public class GeoJSONFeatures {
    
    private GeoDBConnection geoInjuries;
    private JSONObject fatalInjuriesRoot;
    private JSONObject nonFatalInjuriesRoot;
    
    public GeoJSONFeatures(){
        geoInjuries = new GeoDBConnection("localhost", "5432", "delitos", "postgres", "postgres1");
        fatalInjuriesRoot = new JSONObject();
        nonFatalInjuriesRoot = new JSONObject();
    }

    public JSONObject processGeoJSONFatalInjuries(int selectedCategory, String initialDateStr, String endDateStr) {

        List<GeoreferencedInjuries> geoData = geoInjuries.listFatalInjuries(selectedCategory, initialDateStr, endDateStr);
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
            fatalInjuriesRoot.put("features", featuresArray);
            fatalInjuriesRoot.put("type", "FeatureCollection");

        } catch (Exception ex) {
            Logger.getLogger(GeoJSONFeatures.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return fatalInjuriesRoot;
    }
    
    public JSONObject processGeoJSONNonFatalInjuries(int selectedCategory, String initialDateStr, String endDateStr) {

        List<GeoreferencedInjuries> geoData = geoInjuries.listNonFatalInjuries(selectedCategory, initialDateStr, endDateStr);
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
            nonFatalInjuriesRoot.put("features", featuresArray);
            nonFatalInjuriesRoot.put("type", "FeatureCollection");

        } catch (Exception ex) {
            Logger.getLogger(GeoJSONFeatures.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return nonFatalInjuriesRoot;
    }

}
