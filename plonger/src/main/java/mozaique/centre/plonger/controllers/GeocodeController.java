package mozaique.centre.plonger.controllers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import mozaique.centre.plonger.models.Place;
import mozaique.centre.plonger.models.WorkingHour;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Controller
public class GeocodeController {
   @GetMapping(path = "/geocode" )
   public String getGeocode(@RequestParam(required = false, value = "") String filter,Model model) throws IOException {
       OkHttpClient client = new OkHttpClient();

        String encodedAddress = URLEncoder.encode("centre de plongée " + filter + " tunisie", "UTF-8");
       Request request = new Request.Builder()
               .url("https://api.hasdata.com/scrape/google-maps/search?q="+ encodedAddress)
               .get()
               .addHeader("x-api-key", "977bc94f-3855-4370-9a41-f0c8743c321e")
               .addHeader("Content-Type", "application/json")
               .build();
       ResponseBody responseBody = client.newCall(request).execute().body();
       JSONObject userJson = new JSONObject(responseBody.string());
       if (!userJson.has("localResults")) {
        model.addAttribute("places", new ArrayList<>());
        return "list";
       }
       JSONArray result=userJson.getJSONArray("localResults");
       List<Place> listPlace = new ArrayList<>();
        for(int i=0;i<result.length();i++){
                Place p=new Place();
                Iterator<String> keys = result.getJSONObject(i).keys();
                while (keys.hasNext()) {
                        String key = keys.next();
                        switch (key) {
                                case "placeId": p.setId(result.getJSONObject(i).getString("placeId"));break;
                                case "title": p.setTitle(result.getJSONObject(i).getString("title")); break;
                                case "address": p.setAddress(result.getJSONObject(i).getString("address")); break;
                                case "thumbnail":p.setPhotoUrl(result.getJSONObject(i).getString("thumbnail")); break;
                                case "type":p.setType(result.getJSONObject(i).getString("type")); break;
                                case "phone": p.setPhoneNumber(result.getJSONObject(i).getString("phone")); break;
                                case "rating": p.setRating(result.getJSONObject(i).getDouble("rating")); break;
                                case "reviews": p.setReviews(result.getJSONObject(i).getInt("reviews")); break;
                                case "website": p.setWebsite(result.getJSONObject(i).getString("website")); break;
                                case "gpsCoordinates": {
                                        JSONObject gpsCoordinates = new JSONObject(result.getJSONObject(i).get("gpsCoordinates").toString());
                                        p.setLatitude(gpsCoordinates.getDouble("latitude"));
                                        p.setLongitude(gpsCoordinates.getDouble("longitude"));
                                } break;
                                case "serviceOptions":{
                                        @SuppressWarnings("unchecked")
                                        List<String> serviceOptions = result.getJSONObject(i).getJSONArray("serviceOptions").toList();
                                        p.setServiceOptions(serviceOptions);
                                        
                                } break;
                                case "workingHours":{
                                        JSONObject workingHours = new JSONObject(result.getJSONObject(i).get("workingHours").toString());
                                        p.setTimeZone(workingHours.getString("timezone"));
                                        JSONArray days=workingHours.getJSONArray("days");
                                        List<WorkingHour> work=new ArrayList<>();
                                        for(int j=0;j<days.length();j++){
                                                WorkingHour w=new WorkingHour();
                                                w.setDay(days.getJSONObject(j).getString("day"));
                                                w.setTime(days.getJSONObject(j).getString("time"));
                                                w.setDate(days.getJSONObject(j).getString("date"));
                                                work.add(w);
                                        }
                                        p.setWorkingHours(work);

                                } break;
                        
                        }
                
                }

                listPlace.add(p);
        }
       model.addAttribute("places", listPlace);
       return "list";
   }

   
}