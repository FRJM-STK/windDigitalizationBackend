package com.ge.predix.solsvc.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import com.ge.predix.solsvc.boot.Application;

/**
 * An example of creating a Rest api using Spring Annotations @RestController.
 * 
 * 
 * 
 * @author predix
 */
@RestController
public class HelloController
{

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	private static final String URL = "jdbc:postgresql://10.72.6.143:5432/de18cf678ae7f4872a19f2a55c2983269";
	private static final String USER = "u29fc273d52204df6a3f18ca46a67d034";
	private static final String PASSWORD = "f365409705974ec19c5cd9b00a6303e4";

    /**
     * 
     */
    public HelloController()
    {
        super();
    }

    /**
     * Sample Endpoint which returns a Welcome Message
     * 
     * @param echo
     *            - the string to echo back
     * @return -
     */
    @SuppressWarnings("nls")
    @RequestMapping(value = "/echo", method = RequestMethod.GET)
    public String index(@RequestParam(value = "echo", defaultValue = "echo this text") String echo)
    {
        return "Greetings from Predix Spring Boot! echo=" + echo + " " + (new Date());
    }

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public String health()
    {
        return String.format("{\"status\":\"up\", \"date\": \" " + new Date() + "\"}");
    }

    @SuppressWarnings("nls")
    @RequestMapping(value = "/componentsList", method = RequestMethod.GET)
    public String getComponentsList()
    {
    	Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        StringBuffer response = new StringBuffer("{");

        try {
            
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            pst = con.prepareStatement("select * from cumulus.component order by name");
            rs = pst.executeQuery();

            while (rs.next()) {
            	response.append("\"" + rs.getInt(1) + "\":\"" + rs.getString(2) + "\",");
            }
            response.deleteCharAt(response.length()-1);
            response.append("}");

        } catch (SQLException ex) {
        	log.info("Connection Failed!.");
        	log.info(ex.getMessage());
        } finally {

            try {
            	log.info("Closing connection!.");
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            	log.info("Connection closure Failed!.");
            }
        }
        
        return response.toString();
    }

    @SuppressWarnings("nls")
    @RequestMapping(value = "/OemsList", method = RequestMethod.GET)
    public String getOemsList()
    {
    	Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        StringBuffer response = new StringBuffer("{");

        try {
            
        	con = DriverManager.getConnection(URL, USER, PASSWORD);
            pst = con.prepareStatement("select * from cumulus.oem order by name");
            rs = pst.executeQuery();

            while (rs.next()) {
            	response.append("\"" + rs.getInt(1) + "\":\"" + rs.getString(2) + "\",");
            }
            response.deleteCharAt(response.length()-1);
            response.append("}");

        } catch (SQLException ex) {
        	log.info("Connection Failed!.");
        	log.info(ex.getMessage());
        } finally {

            try {
            	log.info("Closing connection!.");
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            	log.info("Connection closure Failed!.");
            }
        }
        
        return response.toString();
    }

    @SuppressWarnings("nls")
    @RequestMapping(value = "/sources", method = RequestMethod.GET)
    public String sources(@RequestParam(value = "components", required = false) String components, @RequestParam(value = "oems", required = false) String oems)
    {
       log.info("Connecting!.");
        
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        StringBuffer query = new StringBuffer();
        
        query.append("SELECT DISTINCT  supplier.id supplier_id, supplier.name supplier_name, supplier.suffix, supplier.address, supplier.latitude, ");
        query.append("supplier.longitude, component.name component_name ");
        query.append("FROM cumulus.supplier, cumulus.source, cumulus.component, cumulus.provider, cumulus.spec, cumulus.turbine, cumulus.family, cumulus.oem ");  
        query.append("WHERE (supplier.id = provider.supplier AND source.component = component.id AND source.supplier = supplier.id ");
        query.append("AND component.id = provider.component AND provider.spec = spec.id AND spec.turbine = turbine.id AND turbine.family = family.id ");
        query.append("AND family.oem = oem.id) ");
        
        if (!StringUtils.isEmpty(components)){
        query.append("AND ( component.id IN (" + components + ") ) ");
        }
        
        if (!StringUtils.isEmpty(oems)){
        	query.append("AND ( oem.id IN (" + oems + ") ) ");
        }
        
        query.append("ORDER BY supplier.id;");
        
        StringBuffer response = new StringBuffer("[");

        try {
            
        	con = DriverManager.getConnection(URL, USER, PASSWORD);
            pst = con.prepareStatement(query.toString());
            rs = pst.executeQuery();

            while (rs.next()) {
            	response.append("{\"id\":" + rs.getInt(1) + ",");
            	response.append("\"name\":\"" + rs.getString(2) + "\",");
            	response.append("\"suffix\":\"" + rs.getString(3) + "\",");
            	response.append("\"address\":\"" + rs.getString(4) + "\",");
            	response.append("\"latitude\":\"" + rs.getString(5) + "\",");
            	response.append("\"longitude\":\"" + rs.getString(6) + "\",");
            	response.append("\"component\":\"" + rs.getString(7) + "\"},");
            }
            
            if (response.length() > 1){
            	response.deleteCharAt(response.length()-1);
            }

        } catch (SQLException ex) {
        	log.info("Connection Failed!.");
        	log.info(ex.getMessage());
        } finally {

            try {
            	log.info("Closing connection!.");
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            	log.info("Connection closure Failed!.");
            }
        }
        
        response.append("]");
        
        return response.toString();
    }

}
