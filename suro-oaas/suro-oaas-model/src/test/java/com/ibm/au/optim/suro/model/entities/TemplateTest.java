package com.ibm.au.optim.suro.model.entities;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.au.jaws.data.DataValidationException;

/**
 * Class <b>TemplateTest</b>. This class inherits from {@link EntityTest} and extends
 * the test suite defined for {@link Entity} instances with specific methods to test
 * {@link Template} instances.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class TemplateTest extends EntityTest {


	/**
	 * This method tests the behaviour of the {@link Template#Template()}
	 * constructor. The expected behaviour is that once an instance is
	 * initialised with the default constructor all the fields are set to
	 * {@literal null}.
	 */
	@Test
	@Override
    public void testDefaultConstructor() {
    	
        Template template = new Template();
        
        Assert.assertNull(template.getModelId());
        Assert.assertNull(template.getId());
        Assert.assertNull(template.getDescription());
        Assert.assertNull(template.getLabel());
        Assert.assertNull(template.getParameters());
        Assert.assertNull(template.getAttachments());
    }

	
	/**
	 * This method test the implemented behaviour of the getter and setter
	 * of the <i>label</i> property. The expected behaviour is that when a
	 * {@link Template} instance is created the <i>label</i> property is set
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
	 * when a {@link Template} instance is created the <i>description</i> 
	 * property is set to {@literal null}. Moreover, the value set via the 
	 * setter should be the same value returned by the getter. There are no 
	 * restrictions on the value that can be assigned to the property.
	 */
	@Test
	public void testGetSetDescription() {
		
		// Test 1. null by default.
		//
		Template template = new Template();
		Assert.assertNull(template.getDescription());
		
		// Test 2. can we assign a value and retrieve it?
		//
		String expected = "This is the description of the template.";
		template.setDescription(expected);
		String actual = template.getDescription();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		template.setDescription(null);
		Assert.assertNull(template.getDescription());
	}

	/**
	 * This method test the implemented behaviour of the getter and setter
	 * of the <i>modelId</i> property. The expected behaviour is that when
	 * a {@link Template} instance is created the <i>modelId</i> property
	 * is set to {@literal null}. Moreover, the value set via the setter 
	 * should be the same value returned by the getter. There are no 
	 * restrictions on the value that can be assigned to the property.
	 */
	@Test
	public void testGetSetModelId() {
		
		// Test 1. null by default.
		//
		Template template = new Template();
		Assert.assertNull(template.getModelId());
		
		// Test 2. can we assign a value and retrieve it?
		//
		String expected = "27ade23cbf174aa234323f";
		template.setModelId(expected);
		String actual = template.getModelId();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		template.setModelId(null);
		Assert.assertNull(template.getModelId());
		
		
	}
	
	/**
	 * This method test the implemented behaviour of the getter and setter
	 * of the <i>parameters</i> property. The expected behaviour is that when
	 * a {@link Template} instance is created the <i>parameters</i> property
	 * is set to {@literal null}. Moreover, the value set via the setter 
	 * should be the same value returned by the getter. There are no 
	 * restrictions on the value that can be assigned to the property.
	 */
	@Test
    public void testGetSetParameters() {

		
		// Test 1. null by default.
		//
		Template template = new Template();
		Assert.assertNull(template.getParameters());

		// Test 2. we add an empty list of parameters.
		//
        List<TemplateParameter> expected = new ArrayList<TemplateParameter>();
        template.setParameters(expected);
        List<TemplateParameter> actual = template.getParameters();
        Assert.assertNotNull(actual);
        Assert.assertEquals(0, actual.size());
        
        // Test 3. we add a bunch of parameters
        //
        expected = new ArrayList<TemplateParameter>();
        
        TemplateParameter tp1 = new TemplateParameter("P1");
        expected.add(tp1);
        
        TemplateParameter tp2 = new TemplateParameter("P2");
        expected.add(tp2);
        
        template.setParameters(expected);
        actual = template.getParameters();
        
        // NOTE: we do not make an equality test on the lists implementation
        //       because the interface does not enforce that this has to be
        //       same list instance passed in, what we care about is that the
        //		 content of the list is the same.
        //
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertTrue(actual.contains(tp1));
        Assert.assertTrue(actual.contains(tp2));
        
        // Test 4. can we set null?
        //
        template.setParameters(null);
        Assert.assertNull(template.getParameters());

    }
	
	/**
	 * This method test the implemented behaviour of the {@link Template#getParameter(String)}
	 * method. The method is expected to retrieve a specific parameter named as the argument
	 * passed to the method if that parameter exists. Moreover, it is expected that the method
	 * throws {@link IllegalArgumentException} if the parameter name is {@literal null} or an
	 * empty string.
	 */
	@Test
	public void testGetParameter() {

		// Test 1. By default there are no parameters added.
		//
		Template template = new Template();
		TemplateParameter parameter = template.getParameter("thisIsNotAParameter");
		Assert.assertNull(parameter);
		
		
		// Test 2. We setup the parameter collection with a list
		// 		   of parameters, and check that all the parameters
		//		   in that list are present.
		List<TemplateParameter> parameters = new ArrayList<TemplateParameter>();
		
		TemplateParameter tp1 = new TemplateParameter("tp1");
		parameters.add(tp1);
		
		TemplateParameter tp2 = new TemplateParameter("tp2");
		parameters.add(tp2);
		
		template.setParameters(parameters);
		
		
		// let's check tp1.
		//
		TemplateParameter actual = template.getParameter(tp1.getName());
		Assert.assertEquals(tp1, actual);
		
		// let's check tp2.
		//
		actual = template.getParameter(tp2.getName());
		Assert.assertEquals(tp2, actual);
		
		
		// Test 3. We then set the list of parameters to null, and we
		//         should not have any parameter left there.
		//
		template.setParameters(null);
		actual = template.getParameter(tp1.getName());
		Assert.assertNull(actual);
		actual = template.getParameter(tp2.getName());
		Assert.assertNull(actual);
		
		
		// Test 4. We pass an invalid parameter name.
		//
		//
		
		// 4a. null name.
		//
		try {
			
			template.getParameter(null);
			Assert.fail("Template.getParameter(null) should throw IllegalArgumentException, parameter name cannot be null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go..
		}
		
		// 4b. empty parameter name
		//
		try {
			
			template.getParameter("");
			Assert.fail("Template.getParameter('') should throw IllegalArgumentException, parameter name cannot be empty.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go..
		}
		
	}
	
	/**
	 * This method tests the implementation of the {@link Template#populate(Run)} method. The expected
	 * behaviour is for the {@link Template} instance to complement the information that is currently
	 * set in the {@link Run}. Before complementing the {@link Run} the templates also validates its
	 * content. Hence, for the purposes of testing we will both test the validation process and further
	 * the process of complementing the values.
	 */
	@Test
	public void testPopulateWithRun() {
	
		// Test 1. calling the populate method with a null run.
		//

		Template template = new Template();
		
		try {
			
			template.populate(null);
			Assert.fail("Template.populate(null) should throw IllegalArgumentException.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go.
		}
		

		// Test 2. the template has a null template identifier..
		
		Run run = new Run();
		run.setTemplateId("eee12312313cefb21a");
		
		
		try {
			
			template.populate(run);
			Assert.fail("Template.populate(Run) should throw IllegalStateException when the unique identifier is not set for the template.");
			
		} catch(IllegalStateException ilex) {
			
			// good to go..
		}
		
		String expectedTemplateId = "23231dfe351a33bc45";
		template.setId(expectedTemplateId);
		
		// Test 3. the template has a null model identifier.
		//

		// we set the template identifier, so that we can
		// proceed with the validation and check the model
		// identifier.
		//
		run.setTemplateId(expectedTemplateId);		   
		
		try {
			
			template.populate(run);
			Assert.fail("Template.populate(Run) should throw IllegalStateException if the modelId is set to null].");
			
		} catch(IllegalStateException dvex) {
			
			// ok, good to go.
		}

		
		
		String expectedModelId = "321321df44eabc32cc5";
		
		// Test 4. the run does not have a modelId that 
		//		   matches the model identifier declared
		//         by the template. (e.g. null).
		//
		template.setModelId(expectedModelId);
		run.setModelId(null);
		
		try {
			
			template.populate(run);
			Assert.fail("Template.populate(Run) should throw DataValidationException if the modelId is different [template: " + template.getModelId() + ", run: " + run.getModelId() + "].");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
			
		}
		
		run.setModelId("222221cde34acd34fe");
		try {
			
			template.populate(run);
			Assert.fail("Template.populate(Run) should throw DataValidationException if the modelId is different [template: " + template.getModelId() + ", run: " + run.getModelId() + "].");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
			
		}
		
		
		// now we set the model identifier to be the same 
		// and this execution will pass.
		//
		template.setModelId(expectedModelId);
		run.setModelId(expectedModelId);
		Run actual = template.populate(run);
		Assert.assertNotNull(actual);
		Assert.assertNotNull(actual.getParameters());
		Assert.assertEquals(0, actual.getParameters().size());
		
		
		List<TemplateParameter> templateParameters = new ArrayList<TemplateParameter>();
		
		TemplateParameter tp1fixed = new TemplateParameter("tp1", 12, true);
		templateParameters.add(tp1fixed);
		TemplateParameter tp2open = new TemplateParameter("tp2", "text", false);
		templateParameters.add(tp2open);
		
		template.setParameters(templateParameters);
		
		
		// Test 5. We have a run with parameters that are not defined in the  template.
		//

		Parameter tp4 = new Parameter("tp4", 45);
		List<Parameter> runParameters  = new ArrayList<Parameter>();
		runParameters.add(tp4);
		run.setParameters(runParameters);
		
		try {
			
			template.populate(run);
			Assert.fail("Template.populate(Run) should throw DataValidationException, if the run contains a parameter not dedined in the template.");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
		}
		
		// Test 6. We have a run with a fixed parameter.
		//
		runParameters.remove(tp4);
		Parameter rp1fixed = new Parameter(tp1fixed.getName(), 34);
		runParameters.add(rp1fixed);
		run.setParameters(runParameters);
		
		try {
			
			template.populate(run);
			Assert.fail("Template.populate(Run) should throw DataValidationException, if the run contains a parameter dedined fixed the template.");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
		}
		
		// Test 7. We have a run with multiple occurrences of the same parameter.
		//
		runParameters.remove(rp1fixed);
		
		Parameter rp1 = new Parameter(tp2open.getName(), "script");
		runParameters.add(rp1);
		
		Parameter rp2 = new Parameter(tp2open.getName(), "book");
		runParameters.add(rp2);
		
		run.setParameters(runParameters);

		try {
			
			template.populate(run);
			Assert.fail("Template.populate(Run) should throw DataValidationException, if the run contains multiple occurrences of parameters defined in the template.");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
		}
		
		// Test 8. Sunny day test, only single occurrences of the allowed parameters (open ones).
		//
		// we set it back to a single occurrence of the
		// open parameter and we should be able to pass
		// the test.
		//
		runParameters.remove(rp2);
		run.setParameters(runParameters);
		
		
		actual = template.populate(run);
		Assert.assertNotNull(actual);
		List<Parameter> completed = actual.getParameters();
		
		// we now check that the following characteristics:
		// 
		
		// 1. there should be as many parameters as in the list
		//	  of template parameters.
		//
		Assert.assertEquals(templateParameters.size(), completed.size());
		
		// 2. the fixed parameters should be present with their original
		//    values. We will look for tp1fixed.
		//
		Parameter p = actual.getParameter(tp1fixed.getName());
		Assert.assertNotNull(p);
		Assert.assertEquals(tp1fixed.getValue(), p.getValue());
		
		// 3. the open parameters should be present with the value set in
		//    the run originally. We will look for tp2open (rp2) and check 
		//    that the value is equal to the value set in the run.
		//
		p = actual.getParameter(rp2.getName());
		Assert.assertNotNull(p);
		Assert.assertEquals(rp2.getName(), p.getName());
		
	}
	
	/**
	 * This method tests the implementation of the {@link Template#validate(Run)} method. The expected 
	 * behaviour for the method is throwing a {@link DataValidationException} if any of the following 
	 * conditions are verified:
	 * <ul>
	 * <li>the {@link Run} instance has multiple occurrences of the same parameters.</li>
	 * <li>the {@link Run} instance has occurrences of parameters defined fixed in the template</li>
	 * <li>the {@link Run} instance has occurrences of parameters not defined in the template</li>
	 * <li>the {@link Run#getTemplateId()} method returns an identifier that does not match 
	 * the value returned by {@link Template#getId()}</li>
	 * <li>the {@link Run} instance has different model identifier (applies when one not null)</li>
	 * </ul>
	 * Moreover, {@link IllegalArgumentException} should be thrown if <i>run</i> is {@literal null} and
	 * {@link IllegalStateException} is thrown if the template has the <i>id</i> or the <i>modelId</i>
	 * property set to {@literal null}.
	 */
	@Test
	public void testValidateWithRun() {

		// Test 1. calling the method with a null run.
		//
		
		Template template = new Template();
		
		try {
			
			template.validate(null);
			Assert.fail("Template.validate(null) should throw IllegalArgumentException.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go.
		}
		
		
		// Test 2. the template has a null template identifier..
		
		Run run = new Run();
		run.setTemplateId("eee12312313cefb21a");
		
		
		try {
			
			template.validate(run);
			Assert.fail("Template.validate(Run) should throw IllegalStateException when the unique identifier is not set for the template.");
			
		} catch(IllegalStateException ilex) {
			
			// good to go..
		}
		
		String expectedTemplateId = "23231dfe351a33bc45";
		template.setId(expectedTemplateId);


		// Test 2. the run does not have a templateId that
		//		   matches the current identifier of the
		//		   template.
		
		try {
			
			template.validate(run);
			Assert.fail("Template.validate(Run) should throw DataValidationException if the templateId is different [template: " + template.getId() + ", run: " + run.getModelId() + "].");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
		}
		
		
		// Test 3. the template has a null model identifier.
		//

		// we set the template identifier, so that we can
		// proceed with the validation and check the model
		// identifier.
		//
		run.setTemplateId(expectedTemplateId);		   
		
		try {
			
			template.validate(run);
			Assert.fail("Template.validate(Run) should throw IllegalStateException if the modelId is set to null].");
			
		} catch(IllegalStateException dvex) {
			
			// ok, good to go.
		}

		
		
		String expectedModelId = "321321df44eabc32cc5";
		
		// Test 4. the run does not have a modelId that 
		//		   matches the model identifier declared
		//         by the template. (e.g. null).
		//
		template.setModelId(expectedModelId);
		run.setModelId(null);
		
		try {
			
			template.validate(run);
			Assert.fail("Template.validate(Run) should throw DataValidationException if the modelId is different [template: " + template.getModelId() + ", run: " + run.getModelId() + "].");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
			
		}
		
		run.setModelId("222221cde34acd34fe");
		try {
			
			template.validate(run);
			Assert.fail("Template.validate(Run) should throw DataValidationException if the modelId is different [template: " + template.getModelId() + ", run: " + run.getModelId() + "].");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
			
		}
		
		
		// now we set the model identifier to be the same 
		// and this execution will pass.
		//
		template.setModelId(expectedModelId);
		run.setModelId(expectedModelId);
		template.validate(run);
		
		
		List<TemplateParameter> templateParameters = new ArrayList<TemplateParameter>();
		
		TemplateParameter tp1fixed = new TemplateParameter("tp1", 12, true);
		templateParameters.add(tp1fixed);
		TemplateParameter tp2open = new TemplateParameter("tp2", "text", false);
		templateParameters.add(tp2open);
		
		template.setParameters(templateParameters);
		
		
		// Test 5. We have a run with parameters that are not defined in the  template.
		//

		Parameter tp4 = new Parameter("tp4", 45);
		List<Parameter> runParameters  = new ArrayList<Parameter>();
		runParameters.add(tp4);
		run.setParameters(runParameters);
		
		try {
			
			template.validate(run);
			Assert.fail("Template.validate(Run) should throw DataValidationException, if the run contains a parameter not dedined in the template.");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
		}
		
		// Test 6. We have a run with a fixed parameter.
		//
		runParameters.remove(tp4);
		Parameter rp1fixed = new Parameter(tp1fixed.getName(), 34);
		runParameters.add(rp1fixed);
		run.setParameters(runParameters);
		
		try {
			
			template.validate(run);
			Assert.fail("Template.validate(Run) should throw DataValidationException, if the run contains a parameter dedined fixed the template.");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
		}
		
		// Test 7. We have a run with multiple occurrences of the same parameter.
		//
		runParameters.remove(rp1fixed);
		
		Parameter rp1 = new Parameter(tp2open.getName(), "script");
		runParameters.add(rp1);
		
		Parameter rp2 = new Parameter(tp2open.getName(), "book");
		runParameters.add(rp2);
		
		run.setParameters(runParameters);

		try {
			
			template.validate(run);
			Assert.fail("Template.validate(Run) should throw DataValidationException, if the run contains multiple occurrences of parameters defined in the template.");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
		}
		
		// Test 8. Sunny day test, only single occurrences of the allowed parameters (open ones).
		//
		// we set it back to a single occurrence of the
		// open parameter and we should be able to pass
		// the test.
		//
		runParameters.remove(rp2);
		run.setParameters(runParameters);
		
		template.validate(run);
		
	}

    /**
     * This method tests the implemented behaviour of {@link Template#clone()}.
     * The method is expected to clone complex properties and to shallow
     * copy immutable properties or simple ones.
     */
	@Override
	@Test
    public void testClone() {
		
    	super.testClone();
    
    	// ok let's take care of the template specific properties.
    	
    	Template expected = new Template();
    	expected.setId(UUID.randomUUID().toString());
    	expected.setModelId(UUID.randomUUID().toString());
    	expected.setLabel("This is a label.");
    	expected.setDescription("This is a description");
    	
    	List<TemplateParameter> parameters = new ArrayList<TemplateParameter>();
    	parameters.add(new TemplateParameter("t1", 34, false));
    	parameters.add(new TemplateParameter("t2", true, true));
    	parameters.add(new TemplateParameter("t3", 0.634, false));
    	parameters.add(new TemplateParameter("t4", "text", true));
    	
    	expected.setParameters(parameters);
    	
    	Template actual = (Template) expected.clone();
    	this.equals(expected, actual);
	}
	

    /**
     * This implementation of the method returns a {@link Template}
     * instance for the purpose of testing the {@link Entity} portion
     * of a {@link Template}.
     * 
     * @return	a {@link Template} instance.
     */
	@Override
    protected Entity createEntity() {
    	
    	return new Template();
    }
	
	/**
	 * This method extends {@link EntityTest#equals(Entity, Entity)} and performs
	 * the comparison field by field of the {@link Template} instances. For the
	 * <i>parameters</i> property the method first check whether the one of the
	 * {@literal null} and if both are not {@literal null}, first the size of the
	 * collection is assessed, and then the content will be checked by looking at
	 * whether all the parameters of one, are contained in the other.
	 * 
	 * @param expected	a {@link Entity} instance, must be not {@literal null}
	 * 					and of type {@link Template}.
	 * @param actual	a {@link Entity} instance, must be not {@literal null}
	 * 					and of type {@link Template}.
	 */
	@Override
	protected void equals(Entity expected, Entity actual) {
	
		super.equals(expected, actual);
		
		Template te = (Template) expected;
		Template ta = (Template) actual;
		
		Assert.assertEquals(te.getLabel(), ta.getLabel());
		Assert.assertEquals(te.getDescription(), ta.getDescription());
		Assert.assertEquals(te.getModelId(),  ta.getModelId());
		
		List<TemplateParameter> parameters = te.getParameters();
		
		if (parameters != null) {
			
			List<TemplateParameter> targets = ta.getParameters();
			Assert.assertNotNull(targets);
			Assert.assertEquals(parameters.size(), targets.size());
			
			for(TemplateParameter tp : parameters) {
				
				TemplateParameter found = ta.getParameter(tp.getName());
				Assert.assertNotNull(found);
				
				Assert.assertEquals(tp.getName(), found.getName());
				Assert.assertEquals(tp.getValue(), found.getValue());
				Assert.assertEquals(tp.isFixed(), found.isFixed());
			}
			
		} else {
			
			Assert.assertNull(ta.getParameters());
		}
	}


}
