package com.sample.api;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DataProcessController {

	protected static Logger log = Logger.getLogger(PentahoExecutor.class.getName());
	private PentahoExecutor pentahoExecutor;
	private static final String SERVCEINFO  = "process/name=sampletrans.ktr&param1=value1&param2=value2";
	
	/*
	 * Test service to execute a trasformation
	 */
	@RequestMapping("/testprocess")
    public String runTestProcess(@RequestParam(value="name", defaultValue="sampletrans.ktr") String name) {
		String message = "Completed successfully";
		
		 ByteArrayOutputStream os = new ByteArrayOutputStream();
	        try 
	        {
	        	pentahoExecutor = new PentahoExecutor();
	            pentahoExecutor.execute(name, null, os);	
	        }catch (Exception e){
	        	message = e.getMessage()+SERVCEINFO;
	        	log.severe(message);
	        }
		return message;
    }

	/*
	 * Service to execute a trasformation or a job with paramters
	 */
	
    @RequestMapping(value="/process", method=RequestMethod.GET)
    @ResponseBody
    public String executeProcess(HttpServletRequest request, HttpServletResponse response) {
    	response.setContentType("text/plain");
    	response.setCharacterEncoding("UTF-8");
    	String message = "Completed successfully";
    	
        Map<String, String[]> parameters = request.getParameterMap();        

        String [] vals = null;
        Map<String, String> params = new HashMap<String, String>();
        String trans = null;
        for (String key: parameters.keySet()) {
        	
        	vals = parameters.get(key);
        	
          for(String val: vals)	{
        	  if(key.equals("name"))
        		  trans = val;
        	  else {
        		  params.put(key, val);     		  
        	  }
          }
        	
        }
        
        message="";
        System.out.println("************trans name : " +trans);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try 
        {
        	pentahoExecutor = new PentahoExecutor();
        	if(trans.endsWith("ktr"))
        		pentahoExecutor.execute(trans, params, os);
        	else if (trans.endsWith("kjb"))
        		pentahoExecutor.executeJob(trans, params);
        }catch (Exception e){
        	message = e.getMessage()+SERVCEINFO;
        	log.severe(message);
        }
        
        return message.equals("")?os.toString() : message;
    }
}
