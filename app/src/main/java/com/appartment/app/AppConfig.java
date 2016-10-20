package com.appartment.app;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ripulchhabra on 25/09/16.
 */
public class AppConfig {
    // Base Url
    private static String BASE_URL = "http://s442410310.onlinehome.us";
    // Server user login url
    public static String URL_LOGIN = BASE_URL+"/osos/OSOS/index.php/Verifylogin/login";
    public static String URL_TICKETS = BASE_URL+"/osos/OSOS/index.php/ticket/viewTicketByUserId";
    public static String URL_UPLOAD_DATA = BASE_URL+"/osos/OSOS/index.php/ticket/Comments";
    public static Integer PHOTO_CAPTURE_LIMIT = 2;

    // priority background Color
    public static String getPriorityBackground(String priority) {
        priority = priority.toLowerCase().trim();
        switch (priority) {
            case "normal" : return "#FFb266";
            case "high" : return "#FF0000";
            case "low" : return "#008000";
            case "critical" : return "#FF1100";
            default : return "#FFFF00";
        }
    }

    // priority background Color
    public static String getPriorityTextColor(String priority) {
        priority = priority.toLowerCase().trim();
        switch (priority) {
            case "normal" : return "#000000";
            case "high" : return "#FFFFFF";
            case "low" : return "#FFFFFF";
            default : return "#000000";
        }
    }
}
