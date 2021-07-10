package com.ss.TIW_2021project.business.utils;

import java.util.ArrayList;
import java.util.List;

public class PathUtils {

    public static String pathToHomePage = "/home.html";
    public static String pathToLoginPage = "/login.html";

    public static String serv1 = "/GetHomeProducts";
    public static String serv2 = "/GetOrders";
    public static String serv3 = "/login";
    public static String serv4 = "/logout";
    public static String serv5 = "/PlaceOrder";
    public static String serv6 = "/SetProductDisplayed";
    public static String serv7 = "/signup";
    public static String serv8 = "/GetSearchProducts";

    private PathUtils() {

    }

    public static List<String> getAllPath(String contextPath) {
        List<String> paths = new ArrayList<>();

        paths.add(contextPath.concat("/"));
        paths.add(contextPath.concat(pathToHomePage));
        paths.add(contextPath.concat(pathToLoginPage));
        paths.add(contextPath.concat(serv1));
        paths.add(contextPath.concat(serv2));
        paths.add(contextPath.concat(serv3));
        paths.add(contextPath.concat(serv4));
        paths.add(contextPath.concat(serv5));
        paths.add(contextPath.concat(serv6));
        paths.add(contextPath.concat(serv7));
        paths.add(contextPath.concat(serv8));

        return paths;
    }

}
