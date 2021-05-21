package com.ibm.au.optim.suro.model.entities;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;


/**
 * Class <b>RunTest</b>. This class extends {@link EntityTest} and
 * verifies the implemented behaviour of {@link Run}. A {@link Run}
 * instance is characterised by the a collection of {@link Parameter}
 * instance that define the settings of the execution and a collection
 * of properties that define its status and metadata.
 * 
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class RunTest extends EntityTest {
	
	/**
	 * A {@literal double} value that is used to execute comparisons
	 * between {@literal double} values.
	 */
	private static final double DELTA = 0.000000000000000000001;
	
	/**
	 * A {@link Random} instance used to generate random numbers for
	 * the purpose of testing.
	 */
	private static final Random random = new Random();

	
	/**
	 * This method tests the behaviour of the {@link Run#Run()} constructor.
	 * The constructor is expected to initialises every property to the 
	 * default value defined by the underlying type.
	 */
	@Test
    public void testDefaultConstructor() {
		
        Run run = new Run();
        Assert.assertNull(run.getId());

        Assert.assertNull(run.getModelId());
        Assert.assertNull(run.getTemplateId());
        Assert.assertNull(run.getDataSetId());

        Assert.assertNull(run.getParameters());
        Assert.assertNull(run.getAttachments());
        
        Assert.assertNull(run.getLabel());
        Assert.assertNull(run.getDescription());
        
        Assert.assertNull(run.getJobId());
        Assert.assertNull(run.getJobStatus());
        Assert.assertNull(run.getSolveStatus());
        Assert.assertEquals(RunStatus.NEW, run.getStatus());

        Assert.assertEquals(Run.DEFAULT_GAP, run.getFinalGap(), RunTest.DELTA);
        Assert.assertEquals(Run.DEFAULT_GAP, run.getMinGap(), RunTest.DELTA);
        
        Assert.assertEquals(run.getStartTime(), 0);
        Assert.assertEquals(run.getRunTime(), 0);
        Assert.assertEquals(run.getMaxRunTime(), 0);
    }
	
	/**
	 * This method test the implemented behaviour of the getter and setter
	 * of the <i>label</i> property. The expected behaviour is that when a
	 * {@link Run} instance is created the <i>label</i> property is set
	 * to {@literal null}. Moreover, the value set via the setter should be
	 * the same value returned by the getter. There are no restrictions on 
	 * the value that can be assigned to the property.
	 */
	@Test
	public void testGetSetLabel() {
	
		// Test 1. null by default.
		//
		Template template = new Template();
		Assert.assertNull(template.getLabel());
		
		// Test 2. can we assign a value and retrieve it?
		//
		String expected = "thisIsALabel";
		template.setLabel(expected);
		String actual = template.getLabel();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		template.setLabel(null);
		Assert.assertNull(template.getLabel());
		
		
	}
	/**
	 * This method test the implemented behaviour of the getter and setter
	 * of the <i>description</i> property. The expected behaviour is that 
	 * when a {@link Run} instance is created the <i>description</i> 
	 * property is set to {@literal null}. Moreover, the value set via the 
	 * setter should be the same value returned by the getter. There are no 
	 * restrictions on the value that can be assigned to the property.
	 */
	@Test
	public void testGetSetDescription() {
		
		// Test 1. null by default.
		//
		Run run = new Run();
		Assert.assertNull(run.getDescription());
		
		// Test 2. can we assign a value and retrieve it?
		//
		String expected = "This is the description of the run.";
		run.setDescription(expected);
		String actual = run.getDescription();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		run.setDescription(null);
		Assert.assertNull(run.getDescription());
	}

	/**
	 * This method test the implemented behaviour of the getter and setter
	 * of the <i>modelId</i> property. The expected behaviour is that when
	 * a {@link Run} instance is created the <i>modelId</i> property is set
	 * to {@literal null}. Moreover, the value set via the setter should be 
	 * the same value returned by the getter. There are no restrictions on 
	 * the value that can be assigned to the property.
	 */
	@Test
	public void testGetSetModelId() {
		
		// Test 1. null by default.
		//
		Run run = new Run();
		Assert.assertNull(run.getModelId());
		
		// Test 2. can we assign a value and retrieve it?
		//
		String expected = "27ade23cbf174aa234323f";
		run.setModelId(expected);
		String actual = run.getModelId();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		run.setModelId(null);
		Assert.assertNull(run.getModelId());
		
		
	}

	/**
	 * This method test the implemented behaviour of the getter and setter
	 * of the <i>templateId</i> property. The expected behaviour is that when
	 * a {@link Run} instance is created the <i>templateId</i> property is set
	 * to {@literal null}. Moreover, the value set via the setter should be 
	 * the same value returned by the getter. There are no restrictions on 
	 * the value that can be assigned to the property.
	 */
	@Test
	public void testGetSetTemplateId() {
		
		// Test 1. null by default.
		//
		Run run = new Run();
		Assert.assertNull(run.getTemplateId());
		
		// Test 2. can we assign a value and retrieve it?
		//
		String expected = "27ade23cbf174aa234323f";
		run.setTemplateId(expected);
		String actual = run.getTemplateId();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		//
		run.setTemplateId(null);
		Assert.assertNull(run.getTemplateId());
	}

	/**
	 * This method test the implemented behaviour of the getter and setter
	 * of the <i>dataSetId</i> property. The expected behaviour is that when
	 * a {@link Run} instance is created the <i>dataSetId</i> property is set
	 * to {@literal null}. Moreover, the value set via the setter should be 
	 * the same value returned by the getter. There are no restrictions on 
	 * the value that can be assigned to the property.
	 */
	@Test
	public void testGetSetDataSetId() {
		
		// Test 1. null by default.
		//
		Run run = new Run();
		Assert.assertNull(run.getDataSetId());
		
		// Test 2. can we assign a value and retrieve it?
		//
		String expected = "27ade23cbf174aa234323f";
		run.setDataSetId(expected);
		String actual = run.getDataSetId();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		//
		run.setDataSetId(null);
		Assert.assertNull(run.getDataSetId());
		
	}


	/**
	 * This method test the implemented behaviour of the getter and setter
	 * of the <i>jobId</i> property. The expected behaviour is that when
	 * a {@link Run} instance is created the <i>jobId</i> property is set
	 * to {@literal null}. Moreover, the value set via the setter should be 
	 * the same value returned by the getter. There are no restrictions on 
	 * the value that can be assigned to the property.
	 */
	@Test
	public void testGetSetJobId() {

		// Test 1. null by default.
		//
		Run run = new Run();
		Assert.assertNull(run.getJobId());
		
		// Test 2. can we assign a value and retrieve it?
		//
		String expected = "27ade23cbf174aa234323f";
		run.setJobId(expected);
		String actual = run.getJobId();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		//
		run.setJobId(null);
		Assert.assertNull(run.getJobId());
	}

	
	/**
	 * This method test the implemented behaviour of the getter and setter of
	 * the <i>runStatus</i> property. The expected behaviour is that when a 
	 * {@link Run} instance is created the <i>runStatus</i> property is set to 
	 * {@literal Run#NEW}. Moreover, the value set via the setter should be the 
	 * same value returned by the getter. There is no restriction on the value 
	 * that can be assigned to the property.
	 */
	@Test
	public void testGetSetRunStatus() {
		
		
		RunStatus[] statuses = RunStatus.values();
		
		// Test 1. null by default.
		//
		Run run = new Run();
		Assert.assertEquals(RunStatus.NEW, run.getStatus());
		
		// Test 2. can we assign a value and retrieve it?
		//
		RunStatus expected = statuses[RunTest.random.nextInt(statuses.length)];
		run.setStatus(expected);
		RunStatus actual = run.getStatus();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		//		   we should throw an IllegalArgumentException 
		//
		
		try {
		
			run.setStatus(null);
			Assert.fail("Run.setStatus(null) should throw IllegalArgumentException.");
		
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go...
		}
		
		
	}


	/**
	 * This method test the implemented behaviour of the getter and setter of
	 * the <i>jobStatus</i> property. The expected behaviour is that  when a
	 * a {@link Run} instance is created the <i>jobStatus</i> property is set 
	 * to {@literal null}. Moreover, the value set via the setter should be the 
	 * same value returned by the getter. There are no restrictions on the value 
	 * that can be assigned to the property.
	 */
	@Test
	public void testGetSetJobStatus() {
		
		JobStatus[] statuses = JobStatus.values();
		
		// Test 1. null by default.
		//
		Run run = new Run();
		Assert.assertNull(run.getJobStatus());
		
		// Test 2. can we assign a value and retrieve it?
		//
		JobStatus expected = statuses[RunTest.random.nextInt(statuses.length)];
		run.setJobStatus(expected);
		JobStatus actual = run.getJobStatus();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		//
		run.setJobStatus(null);
		Assert.assertNull(run.getJobStatus());	
	}

	/**
	 * This method test the implemented behaviour of the getter and setter
	 * of the <i>solveStatus</i> property. The expected behaviour is that 
	 * when a {@link Run} instance is created the <i>jobId</i> property is 
	 * set to {@literal null}. Moreover, the value set via the setter should 
	 * be the same value returned by the getter. There are no restrictions on 
	 * the value that can be assigned to the property.
	 */
	@Test
	public void testGetSetSolveStatus() {
		
		// Test 1. null by default.
		//
		Run run = new Run();
		Assert.assertNull(run.getSolveStatus());
		
		// Test 2. can we assign a value and retrieve it?
		//
		String expected = "OPTIMAL_SOLUTION";
		run.setSolveStatus(expected);
		String actual = run.getSolveStatus();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		//
		run.setSolveStatus(null);
		Assert.assertNull(run.getSolveStatus());
	}


	/**
	 * This method test the implemented behaviour of the getter and setter of
	 * the <i>minGap</i> property. The expected behaviour is that when a {@link
	 * Run} instance is created the <i>minGap</i> property is set to {@literal 
	 * Run#DEFAULT_GAP}. Moreover, the value set via the setter should be the 
	 * same value returned by the getter. There is no restriction on the value 
	 * that can be assigned to the property.
	 */
	@Test
	public void testGetSetMinGap() {

		// Test 1. DEFAULT_GAP by default.
		//
		Run run = new Run();
		Assert.assertEquals(Run.DEFAULT_GAP, run.getMinGap(), RunTest.DELTA);
		
		// Test 2. can we assign a value and retrieve it?
		//
		double expected = RunTest.random.nextDouble();
		run.setMinGap(expected);
		double actual = run.getMinGap();
		Assert.assertEquals(expected, actual, RunTest.DELTA);
		
	}

	/**
	 * This method test the implemented behaviour of the getter and setter of
	 * the <i>finalGap</i> property. The expected behaviour is that when a 
	 * {@link Run} instance is created the <i>finalGap</i> property is set to 
	 * {@literal Run#DEFAULT_GAP}. Moreover, the value set via the setter should
	 * be the same value returned by the getter. There is no restriction on the 
	 * value that can be assigned to the property.
	 */
	@Test
	public void testGetSetFinalGap() {

		// Test 1. DEFAULT_GAP by default.
		//
		Run run = new Run();
		Assert.assertEquals(Run.DEFAULT_GAP, run.getFinalGap(), RunTest.DELTA);
		
		// Test 2. can we assign a value and retrieve it?
		//
		double expected = RunTest.random.nextDouble();
		run.setFinalGap(expected);
		double actual = run.getFinalGap();
		Assert.assertEquals(expected, actual, RunTest.DELTA);
		
	}
	
	/**
	 * This method test the implemented behaviour of the getter and setter of
	 * the <i>maxRunTime</i> property. The expected behaviour is that when a 
	 * {@link Run} instance is created the <i>maxRunTime</i> property is set to 
	 * zero. Moreover, the value set via the setter should be the same value 
	 * returned by the getter. There is no restriction on the value that can be 
	 * assigned to the property.
	 */
	@Test
	public void testGetSetMaxRunTime() {

		// Test 1. 0 by default.
		//
		Run run = new Run();
		Assert.assertEquals(0, run.getMaxRunTime());
		
		// Test 2. can we assign a value and retrieve it?
		//
		long expected = RunTest.random.nextLong();
		run.setMaxRunTime(expected);
		long actual = run.getMaxRunTime();
		Assert.assertEquals(expected, actual);
	}
	
	/**
	 * This method test the implemented behaviour of the getter and setter of
	 * the <i>runTime</i> property. The expected behaviour is that when a 
	 * {@link Run} instance is created the <i>runTime</i> property is set to 
	 * zero. Moreover, the value set via the setter should be the same value 
	 * returned by the getter. There is no restriction on the value that can be 
	 * assigned to the property.
	 */
	@Test
	public void testGetSetRunTime() {

		// Test 1. 0 by default.
		//
		Run run = new Run();
		Assert.assertEquals(0, run.getRunTime());
		
		// Test 2. can we assign a value and retrieve it?
		//
		long expected = RunTest.random.nextLong();
		run.setRunTime(expected);
		long actual = run.getRunTime();
		Assert.assertEquals(expected, actual);
		
	}

	
	/**
	 * This method test the implemented behaviour of the getter and setter of
	 * the <i>startTime</i> property. The expected behaviour is that when a 
	 * {@link Run} instance is created the <i>startTime</i> property is set to 
	 * zero. Moreover, the value set via the setter should be the same value 
	 * returned by the getter. There is no restriction on the value that can be 
	 * assigned to the property.
	 */
	@Test
	public void testGetSetStartTime() {

		// Test 1. 0 by default.
		//
		Run run = new Run();
		Assert.assertEquals(0, run.getStartTime());
		
		// Test 2. can we assign a value and retrieve it?
		//
		long expected = RunTest.random.nextLong();
		run.setStartTime(expected);
		long actual = run.getStartTime();
		Assert.assertEquals(expected, actual);
		
	}
	
	
	/**
	 * This method test the implemented behaviour of the getter and setter of the 
	 * <i>parameters</i> property. The expected behaviour is that when a {@link Run} 
	 * instance is created the <i>parameters</i> property is set to {@literal null}. 
	 * Moreover, the value set via the setter should be the same value returned by 
	 * the getter. There are no restrictions on the value that can be assigned to the 
	 * property.
	 */
	@Test
    public void testGetSetParameters() {

		
		// Test 1. null by default.
		//
		Run run = new Run();
		Assert.assertNull(run.getParameters());

		// Test 2. we add an empty list of parameters.
		//
        List<Parameter> expected = new ArrayList<Parameter>();
        run.setParameters(expected);
        List<Parameter> actual = run.getParameters();
        Assert.assertNotNull(actual);
        Assert.assertEquals(0, actual.size());
        
        // Test 3. we add a bunch of parameters
        //
        expected = new ArrayList<Parameter>();
        
        Parameter p1 = new Parameter("p1");
        expected.add(p1);
        
        Parameter p2 = new Parameter("p2");
        expected.add(p2);
        
        run.setParameters(expected);
        actual = run.getParameters();
        
        // NOTE: we do not make an equality test on the lists implementation
        //       because the interface does not enforce that this has to be
        //       same list instance passed in, what we care about is that the
        //		 content of the list is the same.
        //
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertTrue(actual.contains(p1));
        Assert.assertTrue(actual.contains(p2));
        
        // Test 4. can we set null?
        //
        run.setParameters(null);
        Assert.assertNull(run.getParameters());

    }
	
	/**
	 * This method test the implemented behaviour of the {@link Run#getParameter(String)}
	 * method. The method is expected to retrieve a specific parameter named as the argument
	 * passed to the method if that parameter exists. Moreover, it is expected that the method
	 * throws {@link IllegalArgumentException} if the parameter name is {@literal null} or an
	 * empty string.
	 */
	@Test
	public void testGetParameter() {

		// Test 1. By default there are no parameters added.
		//
		Run run = new Run();
		Parameter parameter = run.getParameter("thisIsNotAParameter");
		Assert.assertNull(parameter);
		
		
		// Test 2. We setup the parameter collection with a list
		// 		   of parameters, and check that all the parameters
		//		   in that list are present.
		List<Parameter> parameters = new ArrayList<Parameter>();
		
		Parameter p1 = new Parameter("p1");
		parameters.add(p1);
		
		Parameter p2 = new Parameter("p2");
		parameters.add(p2);
		
		run.setParameters(parameters);
		
		
		// let's check tp1.
		//
		Parameter actual = run.getParameter(p1.getName());
		Assert.assertEquals(p1, actual);
		
		// let's check tp2.
		//
		actual = run.getParameter(p2.getName());
		Assert.assertEquals(p2, actual);
		
		
		// Test 3. We then set the list of parameters to null, and we
		//         should not have any parameter left there.
		//
		run.setParameters(null);
		actual = run.getParameter(p1.getName());
		Assert.assertNull(actual);
		actual = run.getParameter(p2.getName());
		Assert.assertNull(actual);
		
		
		// Test 4. We pass an invalid parameter name.
		//
		//
		
		// 4a. null name.
		//
		try {
			
			run.getParameter(null);
			Assert.fail("Run.getParameter(null) should throw IllegalArgumentException, parameter name cannot be null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go..
		}
		
		// 4b. empty parameter name
		//
		try {
			
			run.getParameter("");
			Assert.fail("Run.getParameter('') should throw IllegalArgumentException, parameter name cannot be empty.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go..
		}
		
	}

    /**
     * This method tests the implemented behaviour of {@link Run#clone()}.
     * The method is expected to clone complex properties and to shallow
     * copy immutable properties or simple ones.
     */
	@Override
	@Test
    public void testClone() {
    	
		super.testClone();
    	
		// the above has taken care of defaults, values, id, revisions, and attachments.
		// now we consider the Run specific attributes.
		//
		
		Run expected = new Run();
		
		// identifiers.
		//
		expected.setId(UUID.randomUUID().toString());
		expected.setTemplateId(UUID.randomUUID().toString());
		expected.setModelId(UUID.randomUUID().toString());
		expected.setDataSetId(UUID.randomUUID().toString());
		expected.setJobId(UUID.randomUUID().toString());
		
		// labels and descriptions
		//
		expected.setLabel("This is a label.");
		expected.setDescription("This is a description.");
		
		// Statuses
		//
		expected.setStatus(RunStatus.CREATE_JOB);
		expected.setJobStatus(JobStatus.PROCESSED);
		expected.setSolveStatus("OPTIMAL_SOLUTION");
		
		// Gap and Runtime
		//
		expected.setMinGap(0.01);
		expected.setFinalGap(0.001);
		expected.setMaxRunTime(420010);
		expected.setStartTime(213213131);
		expected.setRunTime(29292);
		
		
		// Parameters
		//
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new Parameter("p1", 23));
		parameters.add(new Parameter("p2", "up"));
		parameters.add(new Parameter("p3", 4.5));
		parameters.add(new Parameter("p4", true));
		expected.setParameters(parameters);
		
		Run actual = (Run) expected.clone();
		
		this.equals(expected, actual);
		
    }
	
	
	/**
	 * This method extends {@link Entity#equals(Entity, Entity)} and complements
	 * it with all the field by field checks to validate all the properties that
	 * are specific to {@link Run} instances.
	 * 
	 * @param expected	a {@link Entity} reference. It must be not {@literal null}
	 * 					and of type {@link Run}.
	 * @param actual	a {@link Entity} reference. It must be not {@literal null}
	 * 					and of type {@link Run}.
	 */
	protected void equals(Entity expected, Entity actual) {
		
		
		super.equals(expected, actual);
		
		Run re = (Run) expected;
		Run ra = (Run) actual;
		
		Assert.assertEquals(re.getLabel(), ra.getLabel());
		Assert.assertEquals(re.getDescription(), ra.getDescription());
		
		Assert.assertEquals(re.getModelId(), ra.getModelId());
		Assert.assertEquals(re.getTemplateId(), ra.getTemplateId());
		Assert.assertEquals(re.getDataSetId(), ra.getDataSetId());
		Assert.assertEquals(re.getJobId(), ra.getJobId());
		
		Assert.assertEquals(re.getMinGap(),ra.getMinGap(), RunTest.DELTA);
		Assert.assertEquals(re.getFinalGap(), ra.getFinalGap(), RunTest.DELTA);
		
		Assert.assertEquals(re.getMaxRunTime(), ra.getMaxRunTime());
		Assert.assertEquals(re.getStartTime(), ra.getStartTime());
		Assert.assertEquals(re.getRunTime(), ra.getRunTime());
		
		Assert.assertEquals(re.getStatus(), ra.getStatus());
		Assert.assertEquals(re.getJobStatus(), ra.getJobStatus());
		Assert.assertEquals(re.getSolveStatus(), ra.getSolveStatus());
		
		List<Parameter> parameters = re.getParameters();
		if (parameters != null) {
			
			List<Parameter> targets = ra.getParameters();
			Assert.assertNotNull(targets);
			Assert.assertEquals(parameters.size(), targets.size());
			
			for(Parameter p : parameters) {
				
				Parameter found = ra.getParameter(p.getName());
				Assert.assertNotNull(found);
				Assert.assertEquals(p.getName(), found.getName());
				Assert.assertEquals(p.getValue(), found.getValue());
			}
			
		} else {
			
			Assert.assertNull(ra.getParameters());
		}
		
		
	}
	

    /**
     * This implementation of the method returns a {@link Run} instance
     * for the purpose of testing the {@link Entity} portion of a {@link 
     * Run}.
     * 
     * @return	a {@link Run} instance.
     */
	@Override
    protected Entity createEntity() {
    	
    	return new Run();
    }

}
