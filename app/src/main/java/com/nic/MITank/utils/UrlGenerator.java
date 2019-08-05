package com.nic.MITank.utils;


/**
 * Created by Achanthi Sundar  on 21/03/16.
 */
public class UrlGenerator {



    public static String getLoginUrl() {
//        return "http://10.163.19.140:81/rdweb/project/webservices_forms/login_service/login_services.php";
        return "https://tnrd.gov.in/project/webservices_forms/login_service/login_services.php";
    }

    public static String getServicesListUrl() {
//        return "http://10.163.19.140:81/rdweb/project/webservices_forms/master_services/master_services.php";
        return "https://tnrd.gov.in/project/webservices_forms/master_services/master_services.php";
    }

    public static String getTankPondListUrl() {
//        return "http://10.163.19.140:81/rdweb/project/webservices_forms/mi_tank/mi_tank_services.php";
        return "https://tnrd.gov.in/project/webservices_forms/mi_tank/mi_tank_services.php";
    }


    public static String getTnrdHostName() {
//        return "10.163.19.140";
        return "tnrd.gov.in";
    }



}
