package com.sample.api;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.logging.Logger;

import org.apache.commons.collections.map.LRUMap;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import java.util.Map;

public class PentahoExecutor {

	protected static Logger log = Logger.getLogger(PentahoExecutor.class.getName());
	
	private Map metadataMap = Collections.synchronizedMap(new LRUMap(10));
	
	public PentahoExecutor() throws KettleException{
		init();
	}
	
	private void init() throws KettleException{
		synchronized (this.getClass()){
		  System.setProperty("KETTLE_PLUGIN_CLASSES","org.pentaho.di.trans.steps.xmloutput.XMLOutputMeta");
		  EnvUtil.environmentInit();
		  KettleEnvironment.init(false);
		}
	}
	
	public void execute(String name, Map<String, String> params, OutputStream os) throws KettleException{
		TransMeta transmeta = getTransMeta(name);
		Trans trans = createTrans(transmeta,params,os);
		executeTrans(trans);
	}
	
   public void executeJob(String name, Map<String, String> params) throws KettleException{
		JobMeta metaData = new JobMeta("res:"+name,null);
		Job job = new Job(null,metaData);
		for(String key : params.keySet()){
			if(null == job.getJobMeta().getParameterValue(key)){
				job.getJobMeta().addParameterDefinition(key, params.get(key),"");
			}
			job.getJobMeta().setParameterValue(key, params.get(key));
		}
		job.copyParametersFrom(job.getJobMeta());
		job.activateParameters();
		job.start();
		job.waitUntilFinished();
		
		if(job.getErrors()> 0) {
			log.warning("Error executing job: " +name+ " Paramters: " + params + "Results: " +job.getResult().getLogText());
			throw new RuntimeException("Error executing job: " +name+ " Paramters: " + params + "Results: " +job.getResult().getLogText());
		}
	}
   
   private TransMeta getTransMeta(String name) throws KettleException, KettleMissingPluginsException{
	   if(metadataMap.containsKey(name))
		   return (TransMeta)metadataMap.get(name);
	   else
		   return createTransMeta(name);
   }
   
   private TransMeta createTransMeta(String name) throws KettleException, KettleMissingPluginsException{
	   
	   System.out.println("********** calling Trabsmeta " +name);
	   TransMeta transmeta = new TransMeta("res:"+name);
	   metadataMap.put(name, transmeta);
	   return transmeta;
   }
   
   private Trans createTrans(TransMeta metaData, Map <String, String> params, OutputStream baos) throws UnknownParamException {
	   Trans trans = new Trans(metaData);
	   PrintWriter out = new PrintWriter(baos);
	   trans.setServletPrintWriter(out);
	   if (params != null){
		   for(String paramName: params.keySet()) {
			   trans.setParameterValue(paramName, params.get(paramName));
		   }
	   }
	   return trans;
   }
   
   private void executeTrans(Trans trans) throws KettleException, KettleMissingPluginsException{
	   trans.execute(null);
	   trans.waitUntilFinished();
	   if(trans.getErrors() >0){
		   log.severe("Error executing transformation");
	   }
   }
}
