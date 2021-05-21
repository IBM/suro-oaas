package com.ibm.au.optim.suro.docloud;

import com.ibm.au.optim.oaas.test.MockController;
import com.ibm.au.optim.oaas.test.MockServer;
import com.ibm.au.optim.suro.core.SuroCore;
import com.ibm.au.optim.suro.core.controller.BasicDataSetController;
import com.ibm.au.optim.suro.core.controller.BasicModelController;
import com.ibm.au.optim.suro.core.controller.SuroRunController;
import com.ibm.au.optim.suro.model.control.Core;
import com.ibm.au.optim.suro.model.control.DataSetController;
import com.ibm.au.optim.suro.model.control.ModelController;
import com.ibm.au.optim.suro.model.control.RunController;
import com.ibm.au.optim.suro.model.control.job.JobController;
import com.ibm.au.optim.suro.model.store.*;
import com.ibm.au.optim.suro.model.store.impl.*;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;

import java.util.Properties;

/**
 * @author Peter Ilfrich
 */
public abstract class DOCloudTest {


    protected Environment createEnvironment(int numberOfExecutors, boolean bind, Properties baseProperties) {
    	
        Properties properties = this.createProperties(numberOfExecutors, baseProperties);

        Environment env = EnvironmentHelper.mockEnvironment(properties);
        env.setAttribute(Core.CORE_INSTANCE, new SuroCore());


        RunController rc = new SuroRunController();
        RunRepository rr = new TransientRunRepository();
        rc.setRepository(rr);

        RunDetailsRepository or = new TransientOptimizationResultRepository();
        ModelRepository mr = new TransientModelRepository();
        DataSetRepository dsr = new TransientDataSetRepository();

        ModelController mc = new BasicModelController();
        mc.setRepository(mr);
        DataSetController dsc = new BasicDataSetController();
        dsc.setRepository(dsr);

        env.setAttribute(RunController.RUN_CONTROLLER_INSTANCE, rc);
        env.setAttribute(ModelController.MODEL_CONTROLLER_INSTANCE, mc);
        env.setAttribute(DataSetController.DATA_SET_CONTROLLER_INSTANCE, dsc);
        env.setAttribute(RunRepository.RUN_REPOSITORY_INSTANCE, rr);
        env.setAttribute(RunDetailsRepository.DETAILS_REPOSITORY_INSTANCE, or);
        env.setAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE, mr);
        env.setAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE, dsr);
        env.setAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE, new TransientTemplateRepository());

        DOCloudJobController jc = new DOCloudJobController();
        if (bind) {
            jc.bind(env);
            ((SuroCore) env.getAttribute(Core.CORE_INSTANCE)).bind(env);
        }

        env.setAttribute(JobController.JOB_CONTROLLER_INSTANCE, jc);
        MockController control = new MockController(MockServer.DEFAULT_PORT);

        // reset DOCloud
        control.reset();

        return env;
    }


    protected Environment createEnvironment(int numberOfExecutors, boolean bind) {
    	
        Properties properties = createProperties(numberOfExecutors, null);
        return this.createEnvironment(numberOfExecutors, bind, properties);

    }

    protected Properties createProperties(int numberOfExecutors, Properties properties) {
        
    	if (properties == null) {
            properties = new Properties();
        }
        
        if (properties.getProperty(DOCloudJobController.CFG_DOCLOUD_API_URL) == null) {
        
        	properties.setProperty(DOCloudJobController.CFG_DOCLOUD_API_URL, "http://localhost:" + MockServer.DEFAULT_PORT);
        }
        
        if (properties.getProperty(DOCloudJobController.CFG_DOCLOUD_API_TOKEN) == null) {
           
        	properties.setProperty(DOCloudJobController.CFG_DOCLOUD_API_TOKEN, "interpwetrust");
        }
        
        if (properties.getProperty(DOCloudJobController.CFG_DOCLOUD_MAX_CONCURRENT) == null) {
         
        	properties.setProperty(DOCloudJobController.CFG_DOCLOUD_MAX_CONCURRENT, String.valueOf(numberOfExecutors));
        }
        
        return properties;
    }
}
