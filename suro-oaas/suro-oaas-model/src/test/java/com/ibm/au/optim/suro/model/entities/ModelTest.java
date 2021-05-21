package com.ibm.au.optim.suro.model.entities;

import com.ibm.au.optim.suro.model.entities.Objective;
import com.ibm.au.jaws.data.DataValidationException;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.ModelParameter;
import com.ibm.au.optim.suro.model.entities.ModelParameter.ParameterType;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSource;
import com.ibm.au.optim.suro.model.entities.mapping.MappingSpecification;
import com.ibm.au.optim.suro.model.entities.mapping.MappingType;
import com.ibm.au.optim.suro.model.entities.mapping.OutputMapping;
import com.ibm.au.optim.suro.model.entities.mapping.ValueMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

/**
 * Class <b>ModelTest</b>. This class tests and verifies the implemented
 * behaviour of the {@link Model} class. A model is defined by a label,
 * an identifier, a collection of objectives, and a set of parameters.
 * 
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class ModelTest extends EntityTest {

	
	/**
	 * This method tests the behaviour of the {@link Model#Model(String)}
	 * constructor. The method verifies that all the properties of the
	 * instance return the default initialisation values:
	 * <ul>
	 * <li><i>id</i>: {@literal null}</li>
	 * <li><i>revision</i>: {@literal null}</li>
	 * <li><i>attachments</i>: {@literal null}</li>
	 * <li><i>defaultModel</i>: {@literal false}</li>
	 * <li><i>objectives</i>: {@literal null}</li>
	 * <li><i>parameters</i>: {@literal null}</li>
	 * <li><i>outputMapping</i>: {@literal null}</li>
	 * </ul>
	 * The only property that should not return {@literal null} is the 
	 * <i>label</i>property. This is meant to be initialised with the
	 * value passed to the constructor.
	 */
	@Test
    public void testConstructorWithLabel() {
		
		String expectedLabel = "test";
		
		// we can override the model
		
        Model model = new Model(expectedLabel);
        
        // Entity related properties...
        //
        Assert.assertNull(model.getId());
        Assert.assertNull(model.getRevision());
        Assert.assertNull(model.getAttachments());
        
        // Model related properties....
        //
        Assert.assertEquals(expectedLabel, model.getLabel());
        Assert.assertFalse(model.isDefaultModel());
        Assert.assertNull(model.getObjectives());
        Assert.assertNull(model.getParameters());
        Assert.assertNull(model.getOutputMappings());
        Assert.assertNull(model.getAttachments());
        
        
        // we also need to test the constraints on the value
        // of the label property.
        
        try {
        	
        	model = new Model(null, true);
        	
        	Assert.fail("Model(null,boolean) should throw IllegalArgumentException.");
        	
        } catch(IllegalArgumentException ilex) { 
        	// all good. 
        }
        
        try {
        	
        	model = new Model("", true);
        	
        	Assert.fail("Model('',boolean) should throw IllegalArgumentException.");
        	
        } catch(IllegalArgumentException ilex) { 
        	// all good. 
        }
    }

	/**
	 * This method tests the behaviour of the {@link Model#Model(String, boolean)}
	 * constructor. The method verifies that all the properties of the instance 
	 * return the default initialisation values:
	 * <ul>
	 * <li><i>id</i>: {@literal null}</li>
	 * <li><i>revision</i>: {@literal null}</li>
	 * <li><i>attachments</i>: {@literal null}</li>
	 * <li><i>objectives</i>: {@literal null}</li>
	 * <li><i>parameters</i>: {@literal null}</li>
	 * <li><i>outputMapping</i>: {@literal null}</li>
	 * </ul>
	 * The only two properties that should not return {@literal null} are <i>label</i>
	 * and <i>defaultModel</i> that should return the initialisation values passed to
	 * the constructor.
	 */
	@Test
	public void testConstructorWithLabelandDefault() {
		
		String expectedLabel = "test";
		boolean expectedDefault = true;
		
		// we can override the model
		
        Model model = new Model(expectedLabel, expectedDefault);
        
        // Entity related properties...
        //
        Assert.assertNull(model.getId());
        Assert.assertNull(model.getRevision());
        Assert.assertNull(model.getAttachments());
        
        // Model related properties....
        //
        Assert.assertEquals(expectedLabel, model.getLabel());
        Assert.assertEquals(expectedDefault, model.isDefaultModel());
        Assert.assertNull(model.getObjectives());
        Assert.assertNull(model.getParameters());
        Assert.assertNull(model.getOutputMappings());
        Assert.assertNull(model.getAttachments());
        
        
        // we now try with expectedDefault = false;
        
        expectedDefault = false;
		
		// we can override the model
		
        model = new Model(expectedLabel, expectedDefault);
        
        // Entity related properties...
        //
        Assert.assertNull(model.getId());
        Assert.assertNull(model.getRevision());
        Assert.assertNull(model.getAttachments());
        
        // Model related properties....
        //
        Assert.assertEquals(expectedLabel, model.getLabel());
        Assert.assertEquals(expectedDefault, model.isDefaultModel());
        Assert.assertNull(model.getObjectives());
        Assert.assertNull(model.getParameters());
        Assert.assertNull(model.getOutputMappings());
        Assert.assertNull(model.getAttachments());
        
        
        // we also need to test the constraints on the value
        // of the label property.
        
        try {
        	
        	model = new Model(null, true);
        	
        	Assert.fail("Model(null,boolean) should throw IllegalArgumentException.");
        	
        } catch(IllegalArgumentException ilex) { 
        	// all good. 
        }
        
        try {
        	
        	model = new Model("", true);
        	
        	Assert.fail("Model('',boolean) should throw IllegalArgumentException.");
        	
        } catch(IllegalArgumentException ilex) { 
        	// all good. 
        }
        
	}

    /**
     * This method tests the implemented behaviour of the getter and setter
     * for the <i>defaultModel</i> property. By default it should be set to
     * {@literal false}. Moreover, the value set with the setter should be
     * retrieved by the getter.
     */
	@Test
    public void testGetSetDefaultModel() {
    	

    	// Test 1. By default it should be false.
    	//
    	Model model = new Model("Model Test");
    	Assert.assertFalse(model.isDefaultModel());

    	// Test 2. Setting it to true
    	//
        model.setDefaultModel(true);
        Assert.assertTrue(model.isDefaultModel());

    	// Test 3. Setting it to false
    	//
        model.setDefaultModel(false);
        Assert.assertFalse(model.isDefaultModel());
    }
    

    /**
     * This method tests the implemented behaviour of the getter and setter
     * for the <i>zero</i> property. By default it should be set to
     * zero. Moreover, the value set with the setter should be retrieved by
     * the getter.
     */
	@Test
    public void testGetSetModelVersion() {
    	

		
		// Test 1. zero by default.
		//
		Model model = new Model("Model test");
		Assert.assertEquals(0, model.getModelVersion());
		
		// Test 2. can we assign a value and retrieve it?
		//
		int expected = 1;
		model.setModelVersion(expected);
		int actual = model.getModelVersion();
		Assert.assertEquals(expected, actual);
    }
    
	/**
	 * This method test the implemented behaviour of the getter and setter
	 * of the <i>label</i> property. The expected behaviour is that when a
	 * {@link Model} instance is created the <i>label</i> property is set
	 * to the value passed to the constructor. Moreover, the value set via 
	 * the setter should be the same value returned by the getter. The
	 * <i>label</i> cannot be a {@literal null} value, an empty string, or
	 * a {@link String} composed by spaces only.
	 */
	@Test
	public void testGetSetLabel() {
	
		// Test 1. null by default.
		//
		String expectedLabel = "Test Model";
		
		Model model = new Model(expectedLabel);
		Assert.assertEquals(expectedLabel, model.getLabel());
		
		// Test 2. can we assign a value and retrieve it?
		//
		expectedLabel = "thisIsALabel";
		model.setLabel(expectedLabel);
		String actual = model.getLabel();
		Assert.assertEquals(expectedLabel, actual);
		
		// Test 3. can we assign a null value?
		
		try {
			
			model.setLabel(null);
			Assert.fail("Model.setLabel(null) should throw IllegalArgumentException.");
		
		} catch(IllegalArgumentException ilex) {
			
			// good to go..
		}
		
		// Test 4. empty string?
		
		try {
			
			model.setLabel("");
			Assert.fail("Model.setLabel('') should throw IllegalArgumentException.");
		
		} catch(IllegalArgumentException ilex) {
			
			// good to go..
		}

		// Test 4. string composed by spaces?
		
		try {
			
			model.setLabel("   ");
			Assert.fail("Model.setLabel('   ') should throw IllegalArgumentException.");
		
		} catch(IllegalArgumentException ilex) {
			
			// good to go..
		}
		
	}
	
	/**
	 * This method test the implemented behaviour of the getter and setter
	 * of the <i>description</i> property. The expected behaviour is that 
	 * when a {@link Model} instance is created the <i>description</i> 
	 * property is set to {@literal null}. Moreover, the value set via the 
	 * setter should be the same value returned by the getter. There are no 
	 * restrictions on the value that can be assigned to the property.
	 */
	@Test
	public void testGetSetDescription() {
		
		// Test 1. null by default.
		//
		Model model = new Model("Model test");
		Assert.assertNull(model.getDescription());
		
		// Test 2. can we assign a value and retrieve it?
		//
		String expected = "This is the description of the model.";
		model.setDescription(expected);
		String actual = model.getDescription();
		Assert.assertEquals(expected, actual);
		
		// Test 3. can we assign a null value?
		model.setDescription(null);
		Assert.assertNull(model.getDescription());
	}
    
	/**
	 * This method tests the implemented behaviour of the setter and getter methods
	 * for the <i>outputMapping</i> property. The property is expected to be set to
	 * {@literal null} once the instance is initialised and the value set with the
	 * setter it is expected to be returned by the getter. Because we are dealing 
	 * with a collection of elements, we will check size and content, not references
	 * to the collection instance, which may change.
	 */
	@Test
    public void testGetSetOutputMapping() {

		// Test 1. Output mappings are null, once they instance
		//		   has been initialised.	
		//
    	Model model = new Model("Model test");
    	Assert.assertNull(model.getOutputMappings());
    	
    	
    	// Test 2. We add an empty list and we expect to have an
    	//		   and empty list back.

        List<OutputMapping> expected = new ArrayList<>();
        model.setOutputMappings(expected);
        List<OutputMapping> actual = model.getOutputMappings();
        Assert.assertEquals(expected.size(), actual.size());
        
        
        // Test 3. We create a list of mappings and we check whether
        //		   we get them back.
        //
        OutputMapping om1 = new OutputMapping("test.json", MappingType.COMPLEX);
        OutputMapping om2 = new OutputMapping("solution.json", MappingType.JSON_CATEGORY); 
        expected.add(om1);
        expected.add(om2);
        
        model.setOutputMappings(expected);
        actual = model.getOutputMappings();
        Assert.assertEquals(expected.size(), actual.size());
        for(OutputMapping om : expected) {
        	
        	Assert.assertTrue(actual.contains(om));
        }
        
        // Test 4. Can we accept null?
        //
        model.setOutputMappings(null);
        Assert.assertNull(model.getOutputMappings());
    	
    }
	
	/**
	 * This method tests the implemented behaviour of {@link Model#validate(Parameter)}. The 
	 * method is expected to throw {@link IllegalArgumentException} if the argument is {@literal
	 * null} and {@link DataValidationException} if the parameter does not match one of those
	 * declared in the model, or is not compatible with the constraints defined for the parameter
	 * in the model.
	 */
	@Test
	public void testValidateParameter() {
		

		
		Model model = new Model("Model test");
		
		// Test 1. IllegalArgumentException with a parameter equal to null.
		//
		
		try {
			
			model.validate((Parameter) null);
			Assert.fail("Model.validate(null) should throw IllegalArgumentException when the parameter argument is set to null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok.. good to go
		}
		
		// Test 2. DataValidationException with a parameter that is not declared in the model (list null).
		//
		
		Parameter p1 = new Parameter("p1", 45);
		
		try {
			
			model.validate(p1);
			Assert.fail("Model.validate(Parameter) should throw DataValidationException when the parameter is not declared (model parameter list: null).");
			
		} catch(DataValidationException dvex) {
			
			// ok... good to go.
		}
		
		// Test 3. DataValidationException with a parameter that is not declared in the model (list empty).
		//
		model.setParameters(new ArrayList<ModelParameter>());
		
		try {
			
			model.validate(p1);
			Assert.fail("Model.validate(Parameter) should throw DataValidationException when the parameter is not declared (model parameter list: empty).");
			
		} catch(DataValidationException dvex) {
			
			// ok... good to go.
		}
		
		// Test 4. DataValidationException with a parameter that is not declared in the model (list non empty).
		//
		List<ModelParameter> modelParams = new ArrayList<ModelParameter>();
		ModelParameter mp2 = new ModelParameter("p2", true, ParameterType.BOOLEAN, null, null);
		modelParams.add(mp2);
		model.setParameters(modelParams);
		
		try {
			
			model.validate(p1);
			Assert.fail("Model.validate(Parameter) should throw DataValidationException when the parameter is not declared (model parameter list: not empty).");
			
		} catch(DataValidationException dvex) {
			
			// ok... good to go.
		}
		
		// Test 5. DataValidationException with a parameter that is declared but a different type.
		//
		ModelParameter mp1 = new ModelParameter("p1", "text", ParameterType.STRING, null, null);
		modelParams.add(mp1);
		model.setParameters(modelParams);

		try {
			
			model.validate(p1);
			Assert.fail("Model.validate(Parameter) should throw DataValidationException when the parameter is declared with a different type.");
			
		} catch(DataValidationException dvex) {
			
			// ok... good to go.
		}
		
		// Test 6. DataValidationException with a parameter that is declared of the same type but out of range.
		//
		mp1.setType(ParameterType.INT);
		mp1.setRange(new Object[] { 100, 200 });
		mp1.setValue(150);
		
		modelParams.clear();
		modelParams.add(mp1);
		modelParams.add(mp2);
		
		model.setParameters(modelParams);

		try {
			
			model.validate(p1);
			Assert.fail("Model.validate(Parameter) should throw DataValidationException when the parameter is declared and the parameter value is out of range.");
			
		} catch(DataValidationException dvex) {
			
			// ok... good to go.
		}
		
		// Test 7. sunny day testing, this should pass. Value of the right type and in range.
		//
		p1.setValue(190);
		model.validate(p1);
		
		// Test 8. We add a double parameter, and check whether we can manage to accept integer values.
		//
		ModelParameter mp3 = new ModelParameter("p3", 45.3, ParameterType.DOUBLE, null, null);
		modelParams.add(mp3);
		
		Parameter p3 = new Parameter("p3", 23);
		
		model.setParameters(modelParams);
		model.validate(p3);
		
		
	}
    
	/**
	 * This method tests the implemented behaviour of the {@link Model#populate(Template)}
	 * method. This method is expected to first validate the template instance and then feed
	 * the values of the template with those of the model if they are not declared. Because
	 * the method needs to implement the semantics of the validation we need to the validation
	 * again in this method.
	 */
	@Test
    public void testPopulateWithTemplate() {
    	// Test 1. IllegalArgumentException, when passing null.
    	//
    	Model model = new Model("Model test");
    	
    	try {
    		
    		model.populate((Template) null);
    		Assert.fail("Model.populate(null) should throw IllegalArgumentException with a null template.");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, good to go..
    	}
    	
    	
    	// Test 2. IllegalStateException, when calling validate
    	// 		   when the model identifier is null.
    	
    	model.setId(null);
    	
    	Template template = new Template();
    	template.setId("31de43532cb21af321ccd");
    	
    	try {
    		
    		model.populate(template);
    		Assert.fail("Model.populate(template) should throw an IllegalStateException when invoked on a model with a null identifier.");
    		
    	} catch(IllegalStateException ilex) {
    		
    		// ok, good to go..
    	}
    	
    	// Test 3. We set the model and we check whether
    	//
    	model.setId("21dc432afe34352c9800de");
    	
    	try {
    		
    		model.populate(template);
    		Assert.fail("Model.populate(template) should throw a DataValidationException when invoked with a template whose model id is different from the one in the model.");
    		
    	} catch(DataValidationException dvex) {
    		
    		// ok, good to go...
    	}
    	
    	// Test 4. We set the model id of the template to null.
    	//
    	template.setId(null);

    	try {
    		
    		model.populate(template);
    		Assert.fail("Model.populate(template) should throw a DataValidationException when invoked with a template whose model identifier is null.");
    		
    	} catch(DataValidationException dvex) {
    		
    		// ok, good to go...
    	}
    	
    	// Test 5. We check the edge scenario, where there are no
    	//         parameters in the model, and there are no parameters
    	//		   in the template, the validation should pass.
    	
    	// 5a: null, null
    	//
    	template.setModelId(model.getId());
    	Template populated = model.populate(template);
    	// we need to check that the parameter list is empty.
    	List<TemplateParameter> actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(0, actual.size());
    	
    	// 5b: null, [empty]
    	//
    	template.setParameters(new ArrayList<TemplateParameter>());
    	populated = model.populate(template);
    	// we need to check that the parameter list is empty.
    	actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(0, actual.size());
    	
    	// 5c: [empty], [empty]
    	//
    	model.setParameters(new ArrayList<ModelParameter>());
    	populated = model.populate(template);
    	// we need to check that the parameter list is empty.
    	actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(0, actual.size());
    	
    	// 5d: [empty], null
    	//
    	template.setParameters(null);
    	populated = model.populate(template);
    	// we need to check that the parameter list is empty.
    	actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(0, actual.size());
    	
    	
    	// Test 6. We need to check whether we throw a validation exception
    	//		   when we add some parameters to the template.
    	
    	List<TemplateParameter> params = new ArrayList<TemplateParameter>();
    	TemplateParameter tp1 = new TemplateParameter("p1", 23, false);			// integer parameter
    	params.add(tp1);
    	TemplateParameter tp2 = new TemplateParameter("p2", "text", true);		// string parameter
    	params.add(tp2);
    	TemplateParameter tp3 = new TemplateParameter("p3", 45.1, true);		// double parameter
    	params.add(tp3);
    	TemplateParameter tp4 = new TemplateParameter("p4", true, false);		// boolean parameter
    	params.add(tp4);
    	template.setParameters(params);
    	
    	try {
    		
    		model.populate(template);
    		Assert.fail("Model.populate(template) should throw DataValidationException, when the model declares no parameters and the template has some.");
    		
    	} catch(DataValidationException ilex) {
    		
    		// ok, good to go..
    	}
    	
    	// Test 7. We check whether we put a parameter that does not have
    	//		   the same type as the one declared by the model.
    	//
    	params.remove(tp2);
    	params.remove(tp3);
    	params.remove(tp4);
    	template.setParameters(params);
    	
    	List<ModelParameter> parameters = new ArrayList<ModelParameter>();
    	ModelParameter mp1 = new ModelParameter("p1", true, ParameterType.BOOLEAN, null, null);
    	parameters.add(mp1);
    	model.setParameters(parameters);
    	
    	try {
    		
    		model.populate(template);
    		Assert.fail("Model.populate(template) should throw DataValidationException when the template parameter does not match the type of the corresponding in the model.");
    		
    	} catch(DataValidationException dvex) {
    	
    		// ok, good to go...
    	}
    	
    	// Test 8. We check whether we put a parameter that matches the type, but we add it
    	//		   twice.
    	//
    	params.remove(tp1);
    	tp1.setValue(false);
    	params.add(tp1);
    	params.add(tp1);
    	
    	try {
    		
    		model.populate(template);
    		Assert.fail("Model.populate(template) should throw DataValidationException when the template has the same valid parameter declared twice.");
    		
    	} catch(DataValidationException dvex) {
    		
    		// ok, good to go
    	}
    	
    	// Test 9. We do a couple of more checks for range parameters to be sure that the
    	//		   validation works as expected.
    	
    	mp1.setType(ParameterType.DOUBLE);
    	mp1.setValue(34.2);
    	mp1.setRange(new Object[] {1.2, 100.4});
    	
    	parameters.remove(mp1);
    	parameters.add(mp1);
    	model.setParameters(parameters);
    	
    	params.clear();
    	tp1.setValue(200.0);
    	params.add(tp1);
    	template.setParameters(params);
    	
    	try {
    		
    		model.populate(template);
    		Assert.fail("Model.populate(template) shoudl throw DataValidationException when the template has a parameter that is out of range.");
    		
    	} catch(DataValidationException dvex) {
    		
    		// ok, good to go...
    	}
    	
    	// Test 10. We check with a value that is compatible...
    	//          The test should pass, because the integer value
    	//          can be converted into a double and it is in range.
    	//
    	params.clear();
    	tp1.setValue(10);
    	params.add(tp1);
    	
    	template.setParameters(params);
    	populated = model.populate(template);
    	
    	// here the list should contain one parameter and should be set to the value
    	// defined in the template.
    	actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(1, actual.size());
    	
    	TemplateParameter fp = populated.getParameter(tp1.getName());
    	Assert.assertEquals(tp1.getValue(), fp.getValue());
    	
    	
    	// Test 11. This is the sunny day scenario, we throw a bunch of parameters 
    	//			in the model, which are all compatible with the parameter definition
    	//			we initially created.
    	
    	parameters.clear();
    	
    	mp1.setType(ParameterType.INT);
    	mp1.setRange(null);
    	mp1.setValue(2000);
    	parameters.add(mp1);
    	
    	ModelParameter mp2 = new ModelParameter("p2", "this is text", ParameterType.STRING, null, null); 
    	parameters.add(mp2);
    	ModelParameter mp3 = new ModelParameter("p3", 100, ParameterType.DOUBLE, null, null);
    	parameters.add(mp3);
    	ModelParameter mp4 = new ModelParameter("p4", false, ParameterType.BOOLEAN, null, null);
    	parameters.add(mp4);
    	model.setParameters(parameters);
    	
    	params.clear();
    	params.add(tp1);
    	params.add(tp2);
    	params.add(tp3);
    	params.add(tp4);
    	template.setParameters(params);
    	
    	populated = model.populate(template);
    	
    	// here we need to check that the paameters have all the original values defined
    	// in the template.
    	//
    	actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(4, actual.size());
    	
    	// recheck that the values are the original ones.
    	//
    	fp = populated.getParameter(tp1.getName());
    	Assert.assertNotNull(fp);
    	Assert.assertEquals(tp1.getValue(), fp.getValue());
    	
    	fp = populated.getParameter(tp2.getName());
    	Assert.assertNotNull(fp);
    	Assert.assertEquals(tp2.getValue(), fp.getValue());
    	
    	fp = populated.getParameter(tp3.getName());
    	Assert.assertNotNull(fp);
    	Assert.assertEquals(tp3.getValue(), fp.getValue());
    	
    	fp = populated.getParameter(tp4.getName());
    	Assert.assertNotNull(fp);
    	Assert.assertEquals(tp4.getValue(), fp.getValue());
    	
    	
    	
    	// Test 12. We add another parameter to the model and check whether the validation still
    	//          passes.
    	ModelParameter mp5 = new ModelParameter("p5", "this is text", ParameterType.STRING, null, null); 
    	parameters.add(mp5);
    	model.setParameters(parameters);
    	
    	populated = model.populate(template);
    	
    	// here we need to check that the paameters have all the original values defined
    	// in the template.
    	//
    	actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(5, actual.size());
    	
    	// recheck that the values are the original ones.
    	//
    	fp = populated.getParameter(tp1.getName());
    	Assert.assertNotNull(fp);
    	Assert.assertEquals(tp1.getValue(), fp.getValue());
    	
    	fp = populated.getParameter(tp2.getName());
    	Assert.assertNotNull(fp);
    	Assert.assertEquals(tp2.getValue(), fp.getValue());
    	
    	fp = populated.getParameter(tp3.getName());
    	Assert.assertNotNull(fp);
    	Assert.assertEquals(tp3.getValue(), fp.getValue());
    	
    	fp = populated.getParameter(tp4.getName());
    	Assert.assertNotNull(fp);
    	Assert.assertEquals(tp4.getValue(), fp.getValue());
    	
    	fp = populated.getParameter(mp5.getName());
    	Assert.assertNotNull(fp);
    	Assert.assertEquals(mp5.getValue(), fp.getValue());
    }
	
	/**
     * This method tests the implemented behaviour of the {@link Model#populate(Template, Run)}
     * method. This method is expected to perform the following tasks:
     * <ul>
     * <li>validate the template to ensure that is compliant with model definition.</li>
     * <li>populate the parameters the run with the missing parameters that are in the template</li>
     * <li>populate the parameters that are in the run with the missing parameters that are in the model</li>
     * </ul>
     * At the end of the process if successful, the run instance should have all the parameters that
     * are in the model with all the assigned values. The following exceptions are expected to be 
     * thrown:
     * <ul>
     * <li>{@link IllegalArgumentException} if any of the two parameters are {@literal null}.</li>
     * <li>{@link IllegalStateException} if the model (strategy) identifier is {@literal null}.</li>
     * <li>{@link DataValidationException} if there is any validation error.</li>
     * </ul>
     * To verify the full semantics of the method, we need to replicate the tests that have been
     * already implemented in the method that test {@link Model#validate(Template)} and also
     * {@link Template#populate(Run)}.
     */
    @Test
    public void testPopulateWithTemplateAndRun() {
    	
    	
    	// ---------------------------------------------------------------------------------- //
    	//  SECTION 1: BASIC VALIDATION                                                       //
    	// ---------------------------------------------------------------------------------- //
    	
    	// Test 1. IllegalArgumentException, when passing null.
    	//
    	Model model = new Model("Model test");
    	
    	// 1a. [null, null]
    	//
    	try {
    		
    		model.populate(null, null);
    		Assert.fail("Model.validate(null,null) should throw IllegalArgumentException with null parameters.");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, good to go..
    	}
    	
    	// 1b. [null, Run]
    	//
    	try {
    		
    		model.populate(null, new Run());
    		Assert.fail("Model.populate(null,Run) should throw IllegalArgumentException with a null template.");
    		
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, good to go..
    	}
    	
    	// 1c. [Template, null]
    	//
    	model.setId(null);
    	
    	Template template = new Template();
    	template.setId("31de43532cb21af321ccd");
    	
    	try {
    		
    		model.populate(template, null);
    		Assert.fail("Model.populate(Template,null) should throw IllegalArgumentException with a null run.");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, good to go..
    	}
    	
    	
    	
    	// Test 2. IllegalStateException, when calling validate
    	// 		   when the model identifier is null.
    	

    	
    	try {
    		
    		model.populate(template, new Run());
    		Assert.fail("Model.populate(Template,Run) should throw an IllegalStateException when invoked on a model with a null identifier.");
    		
    	} catch(IllegalStateException ilex) {
    		
    		// ok, good to go..
    	}
    	
	
    	
    	// ---------------------------------------------------------------------------------- //
    	//  SECTION 2: TEMPLATE VALIDATION                                                    //
    	// ---------------------------------------------------------------------------------- //    	
    	
    	
    	
    	// Test 3. We set the model and we check whether
    	//
    	model.setId("21dc432afe34352c9800de");
    	
    	try {
    		
    		model.populate(template, new Run());
    		Assert.fail("Model.populate(Template,Run) should throw a DataValidationException when invoked with a template whose model id is different from the one in the model.");
    		
    	} catch(DataValidationException dvex) {
    		
    		// ok, good to go...
    	}
    	
    	// Test 4. We set the model id of the template to null.
    	//
    	template.setId(null);

    	try {
    		
    		model.populate(template, new Run());
    		Assert.fail("Model.populate(Template,Run) should throw a DataValidationException when invoked with a template whose model identifier is null.");
    		
    	} catch(DataValidationException dvex) {
    		
    		// ok, good to go...
    	}
    	
    	Run run = new Run();
    	
    	// Test 5. We check the edge scenario, where there are no
    	//         parameters in the model, and there are no parameters
    	//		   in the template, the validation should pass.

    	template.setModelId(model.getId());
    	template.setId("324133dce3ab6f54d3");
    	run.setTemplateId(template.getId());
    	run.setModelId(model.getId());
    	
    	// 5a: null, null
    	//
    	Run populated = model.populate(template,run);
    	List<Parameter> actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(0, actual.size());
    	
    	// 5b: null, [empty]
    	//
    	template.setParameters(new ArrayList<TemplateParameter>());
    	populated = model.populate(template,run);
    	actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(0, actual.size());
    	
    	// 5c: [empty], [empty]
    	//
    	model.setParameters(new ArrayList<ModelParameter>());
    	populated = model.populate(template,run);
    	actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(0, actual.size());
    	
    	
    	// 5d: [empty], null
    	//
    	template.setParameters(null);
    	populated = model.populate(template,run);
    	actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(0, actual.size());
    	
    	
    	
    	// Test 6. We need to check whether we throw a validation exception
    	//		   when we add some parameters to the template.
    	
    	List<TemplateParameter> params = new ArrayList<TemplateParameter>();
    	
    	TemplateParameter tp1 = new TemplateParameter("p1", 23, false);			// integer parameter
    	TemplateParameter tp2 = new TemplateParameter("p2", "text", true);		// string parameter
    	TemplateParameter tp3 = new TemplateParameter("p3", 45.1, true);		// double parameter
    	TemplateParameter tp4 = new TemplateParameter("p4", true, false);		// boolean parameter
    	
    	params.add(tp1);
    	params.add(tp2);
    	params.add(tp3);
    	params.add(tp4);
    	
    	template.setParameters(params);
    	
    	try {
    		
    		model.populate(template, run);
    		Assert.fail("Model.populate(Template,Run) should throw DataValidationException, when the model declares no parameters and the template has some.");
    		
    	} catch(DataValidationException ilex) {
    		
    		// ok, good to go..
    	}
    	
    	// Test 7. We check whether we put a parameter that does not have
    	//		   the same type as the one declared by the model.
    	//
    	params.remove(tp2);
    	params.remove(tp3);
    	params.remove(tp4);
    	template.setParameters(params);
    	
    	List<ModelParameter> modelParameters = new ArrayList<ModelParameter>();
    	ModelParameter mp1 = new ModelParameter("p1", true, ParameterType.BOOLEAN, null, null);
    	modelParameters.add(mp1);
    	model.setParameters(modelParameters);
    	
    	try {
    		
    		model.populate(template,run);
    		Assert.fail("Model.populate(Template,Run) should throw DataValidationException when the template parameter does not match the type of the corresponding in the model.");
    		
    	} catch(DataValidationException dvex) {
    	
    		// ok, good to go...
    	}
    	
    	// Test 8. We check whether we put a parameter that matches the type, but we add it
    	//		   twice.
    	//
    	params.remove(tp1);
    	tp1.setValue(false);
    	params.add(tp1);
    	params.add(tp1);
    	
    	try {
    		
    		model.populate(template,run);
    		Assert.fail("Model.populate(Template,Run) should throw DataValidationException when the template has the same valid parameter declared twice.");
    		
    	} catch(DataValidationException dvex) {
    		
    		// ok, good to go
    	}
    	
    	// Test 9. We do a couple of more checks for range parameters to be sure that the
    	//		   validation works as expected.
    	
    	mp1.setType(ParameterType.DOUBLE);
    	mp1.setValue(34.2);
    	mp1.setRange(new Object[] {1.2, 100.4});
    	
    	modelParameters.remove(mp1);
    	modelParameters.add(mp1);
    	model.setParameters(modelParameters);
    	
    	params.clear();
    	tp1.setValue(200.0);
    	params.add(tp1);
    	template.setParameters(params);
    	
    	try {
    		
    		model.populate(template,run);
    		Assert.fail("Model.populate(Template,Run) should throw DataValidationException when the template has a parameter that is out of range.");
    		
    	} catch(DataValidationException dvex) {
    		
    		// ok, good to go...
    	}
    	
    	// ---------------------------------------------------------------------------------- //
    	//  SECTION 3: RUN VALIDATION                                              			  //
    	// ---------------------------------------------------------------------------------- //     
    	
    	ModelParameter mp2 = new ModelParameter("p2", "this is text", ParameterType.STRING, null, null);
    	ModelParameter mp3 = new ModelParameter("p3", 100, ParameterType.DOUBLE, null, null);
    	ModelParameter mp4 = new ModelParameter("p4", false, ParameterType.BOOLEAN, null, null);
    	
    
    	// we reset the template and the model parameters lists. 
    	//
    	model.setParameters(new ArrayList<ModelParameter>());
    	template.setParameters(new ArrayList<TemplateParameter>());
		
		// Test 11. the run does not have a modelId that matches the model identifier declared
		//          by the template. (e.g. null).
		//

    	run = new Run();
		String expectedTemplateId = "23231dfe351a33bc45";
		
		// setting a template id
		//
		template.setId(expectedTemplateId);
		run.setTemplateId(expectedTemplateId);		   

		// setting the model id
		//
		template.setModelId(model.getId());
		run.setModelId(null);
		
		try {
			
			model.populate(template, run);
			Assert.fail("Model.populate(Template,Run) should throw DataValidationException if the modelId is different [template: " + template.getModelId() + ", run: " + run.getModelId() + "].");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
			
		}
		
		run.setModelId("222221cde34acd34fe");
		try {
			
			model.populate(template, run);
			Assert.fail("Model.populate(Template, Run) should throw DataValidationException if the modelId is different [template: " + template.getModelId() + ", run: " + run.getModelId() + "].");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
			
		}
		
		// Test 12. If we have the model identifier that matches we should be able to
		//          pass the test.
		//
		
		// now we set the model identifier to be the same 
		// and this execution will pass.
		//
		run.setModelId(model.getId());
		populated = model.populate(template, run);
		Assert.assertNotNull(populated);
		Assert.assertNotNull(populated.getParameters());
		Assert.assertEquals(0, populated.getParameters().size());
		
		
		// Test 13. We now add some parameters that are both in the model and in the 
		//          template, and we then we check whether the validation of the Run
		//			works. 

		
		modelParameters.clear();
		modelParameters.add(mp1);	// double 	:	[1.2, 100.4]: 34.2
		modelParameters.add(mp2);	// string	: 	"this is text"
		modelParameters.add(mp3);   // double	: 	100.0
		modelParameters.add(mp4);	// boolean	:	false
		
		model.setParameters(modelParameters);
		
		
		List<TemplateParameter> templateParameters = new ArrayList<TemplateParameter>();
	
		tp1.setValue(100.0);
		templateParameters.add(tp1);	// tp1: 100.0	false
		templateParameters.add(tp2);	// tp2: "text"	true
		templateParameters.add(tp3);	// tp3: 45.1	true
		templateParameters.add(tp4);	// tp4: true	false
		
		template.setParameters(templateParameters);
		
		
		// 13a. We have a run with parameters that are not defined in the  template.
		//

		Parameter p = new Parameter("p5", 45);
		List<Parameter> runParameters  = new ArrayList<Parameter>();
		runParameters.add(p);
		run.setParameters(runParameters);
		
		try {
			
			model.populate(template, run);
			Assert.fail("Model.populate(Template, Run) should throw DataValidationException, if the run contains a parameter not dedined in the template.");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
		}
		
		// 13b. We have a run with a fixed parameter.
		//
		runParameters.remove(p);
		p = new Parameter(tp2.getName(), 34);
		runParameters.add(p);
		run.setParameters(runParameters);
		
		try {
			
			model.populate(template, run);
			Assert.fail("Model.populate(Template, Run) should throw DataValidationException, if the run contains a parameter dedined fixed the template.");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
		}
		
		// 13c. We have a run with multiple occurrences of the same parameter.
		//
		runParameters.remove(p);
		runParameters.add(new Parameter(tp1.getName(), 23.0));
		runParameters.add(new Parameter(tp1.getName(), 75.2));
		
		run.setParameters(runParameters);

		try {
			
			model.populate(template, run);
			Assert.fail("Model.populate(Template,Run) should throw DataValidationException, if the run contains multiple occurrences of parameters defined in the template.");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
		}
		
		// 13d. We have a run parameters with an invalid type.
		//
		runParameters.clear();
		runParameters.add(new Parameter(tp1.getName(), "text"));
		run.setParameters(runParameters);
		
		try {
			
			model.populate(template, run);
			Assert.fail("Model.populate(Template,Run) should throw DataValidationException, if the run contains a parameter whose type is incompatible with the one declared in the model.");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
		}
		
		// 13d. We have a run parameters with a value out of range.
		//
		runParameters.clear();
		runParameters.add(new Parameter(tp1.getName(), 200.0));
		run.setParameters(runParameters);
		
		try {
			
			model.populate(template, run);
			Assert.fail("Model.populate(Template,Run) should throw DataValidationException, if the run contains a parameter that is out of range.");
			
		} catch(DataValidationException dvex) {
			
			// ok, good to go.
		}
    	
    	// ---------------------------------------------------------------------------------- //
    	//  SECTION 4: PASSING TESTS (SUNNY DAY)                                              //
    	// ---------------------------------------------------------------------------------- //    	
    	
    	// Test 10. We check with a value that is compatible...
    	//          The test should pass, because the integer value
    	//          can be converted into a double and it is in range.
    	//
    	params.clear();
    	tp1.setValue(10);
    	params.add(tp1);
    	template.setParameters(params);
    	run.setParameters(new ArrayList<Parameter>());

    	// we set this because we need to check that the template 
    	// passes the validation, and then we finally get all the
    	// model parameters as expected in the run.
    	//
    	run.setModelId(model.getId());
    	run.setTemplateId(template.getId());
    	
    	populated = model.populate(template, run);
    	Assert.assertNotNull(populated);
    	actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	

    	Assert.assertEquals(model.getParameters().size(), actual.size());
    	
    	Parameter found = populated.getParameter(tp1.getName());
    	Assert.assertNotNull(found);
    	Assert.assertEquals(tp1.getValue(), found.getValue());
    	
    	
    	// Test 11. This is the sunny day scenario, we throw a bunch of parameters 
    	//			in the model, which are all compatible with the parameter definition
    	//			we initially created.
    	
    	modelParameters.clear();
    	
    	mp1.setType(ParameterType.INT);
    	mp1.setRange(null);
    	mp1.setValue(2000);
    	modelParameters.add(mp1);
    	
    	modelParameters.add(mp2);
    	modelParameters.add(mp3);
    	modelParameters.add(mp4);
    	model.setParameters(modelParameters);
    	
    	params.clear();
    	params.add(tp1);
    	params.add(tp2);
    	params.add(tp3);
    	params.add(tp4);
    	template.setParameters(params);
    	
    	populated = model.populate(template, run);
    	
    	
    	// ok we should not have the following conditions.
    	//
    	// 1. we should have 4 parameters (the same as the template and the model).
    	// 2. p1 should come from the template and set to 10
    	// 3. p2 should come from the template and set to "text"
    	// 4. p3 should come from the template and set to 45.1
    	// 5. p4 should come from the template and set to true
    	

    	Assert.assertNotNull(populated);
    	actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(model.getParameters().size(), actual.size());
    	
    	// let's check the single parameters
    	//
    	// p1.
    	//
    	found = populated.getParameter(tp1.getName());
    	Assert.assertNotNull(found);
    	Assert.assertEquals(10, found.getValue());
    	
    	// p2.
    	//
    	found = populated.getParameter(tp2.getName());
    	Assert.assertNotNull(found);
    	Assert.assertEquals(tp2.getValue(), found.getValue());
    	
    	// p3.
    	//
    	found = populated.getParameter(tp3.getName());
    	Assert.assertNotNull(found);
    	Assert.assertEquals(tp3.getValue(), found.getValue());
    	
    	// p4.
    	//
    	found = populated.getParameter(tp4.getName());
    	Assert.assertNotNull(found);
    	Assert.assertEquals(tp4.getValue(), found.getValue());
    	
    	
    	
    	// Test 12. We add another parameter to the model and check whether the validation still
    	//          passes.
    	List<Parameter> runParams = new ArrayList<Parameter>();
    	runParams.add(new Parameter("p1", 11));
    	run.setParameters(runParams);
    	
    	ModelParameter mp5 = new ModelParameter("p5", "this is text", ParameterType.STRING, null, null); 
    	modelParameters.add(mp5);
    	model.setParameters(modelParameters);
    	
    	populated = model.populate(template, run);
    	
    	// we should have the following condition if the method is implemented
    	// correctly:
    	//
    	// 1. we should have 5 parameters (the same as the model)
    	// 2. p1 should come from the run and set to 11
    	// 3. p2 should come from the template and set to "text"
    	// 4. p3 should come from the template and set to 45.1
    	// 5. p4 should come from the template and set to true
    	// 6. p5 should come from the model and set to 
    	//
    	
    	
    	Assert.assertNotNull(populated);
    	actual = populated.getParameters();
    	Assert.assertNotNull(actual);
    	Assert.assertEquals(model.getParameters().size(), actual.size());
    	
    	// let's check the single parameters
    	//
    	// p1.
    	//
    	found = populated.getParameter(tp1.getName());
    	Assert.assertNotNull(found);
    	Assert.assertEquals(11, found.getValue());
    	
    	// p2.
    	//
    	found = populated.getParameter(tp2.getName());
    	Assert.assertNotNull(found);
    	Assert.assertEquals(tp2.getValue(), found.getValue());
    	
    	// p3.
    	//
    	found = populated.getParameter(tp3.getName());
    	Assert.assertNotNull(found);
    	Assert.assertEquals(tp3.getValue(), found.getValue());
    	
    	// p4.
    	//
    	found = populated.getParameter(tp4.getName());
    	Assert.assertNotNull(found);
    	Assert.assertEquals(tp4.getValue(), found.getValue());
    	
    	// p5.
    	//
    	found = populated.getParameter(mp5.getName());
    	Assert.assertNotNull(found);
    	Assert.assertEquals(mp5.getValue(), found.getValue());
    	

    	
    }
    

    
    /**
     * This method tests the implemented behaviour of the {@link Model#validate(Template)}.
     * This method is expected to validate that the {@link Template} instance does belong
     * to the {@link Model} instance, and that all the parameters that are defined in the
     * template have a counter-part in the model and their assigned values match the type
     * declared for the corresponding parameter. Moreover, we will also check that when a
     * {@literal null} template is passed, an {@link IllegalArgumentException} is thrown
     * and when the value of the identifier of the model is set to {@literal null} the 
     * method throws an {@link IllegalStateException}. {@link DataValidationException} is
     * thrown when the unique identifier of the model is either {@literal null} or not
     * equal to the model identifier, or the parameters do not match.
     */
    @Test
    public void testValidateWithTemplate() {
    	
    	// Test 1. IllegalArgumentException, when passing null.
    	//
    	Model model = new Model("Model test");
    	
    	try {
    		
    		model.validate((Template) null);
    		Assert.fail("Model.validate(null) should throw IllegalArgumentException with a null template.");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, good to go..
    	}
    	
    	
    	// Test 2. IllegalStateException, when calling validate
    	// 		   when the model identifier is null.
    	
    	model.setId(null);
    	
    	Template template = new Template();
    	template.setId("31de43532cb21af321ccd");
    	
    	try {
    		
    		model.validate(template);
    		Assert.fail("Model.validate(Template) should throw an IllegalStateException when invoked on a model with a null identifier.");
    		
    	} catch(IllegalStateException ilex) {
    		
    		// ok, good to go..
    	}
    	
    	// Test 3. We set the model and we check whether
    	//
    	model.setId("21dc432afe34352c9800de");
    	
    	try {
    		
    		model.validate(template);
    		Assert.fail("Model.validate(Template) should throw a DataValidationException when invoked with a template whose model id is different from the one in the model.");
    		
    	} catch(DataValidationException dvex) {
    		
    		// ok, good to go...
    	}
    	
    	// Test 4. We set the model id of the template to null.
    	//
    	template.setId(null);

    	try {
    		
    		model.validate(template);
    		Assert.fail("Model.validate(Template) should throw a DataValidationException when invoked with a template whose model identifier is null.");
    		
    	} catch(DataValidationException dvex) {
    		
    		// ok, good to go...
    	}
    	
    	// Test 5. We check the edge scenario, where there are no
    	//         parameters in the model, and there are no parameters
    	//		   in the template, the validation should pass.
    	
    	// 5a: null, null
    	//
    	template.setModelId(model.getId());
    	model.validate(template);
    	
    	// 5b: null, [empty]
    	//
    	template.setParameters(new ArrayList<TemplateParameter>());
    	model.validate(template);
    	
    	// 5c: [empty], [empty]
    	//
    	model.setParameters(new ArrayList<ModelParameter>());
    	model.validate(template);
    	
    	// 5d: [empty], null
    	//
    	template.setParameters(null);
    	model.validate(template);
    	
    	
    	// Test 6. We need to check whether we throw a validation exception
    	//		   when we add some parameters to the template.
    	
    	List<TemplateParameter> params = new ArrayList<TemplateParameter>();
    	TemplateParameter tp1 = new TemplateParameter("p1", 23, false);			// integer parameter
    	params.add(tp1);
    	TemplateParameter tp2 = new TemplateParameter("p2", "text", true);		// string parameter
    	params.add(tp2);
    	TemplateParameter tp3 = new TemplateParameter("p3", 45.1, true);		// double parameter
    	params.add(tp3);
    	TemplateParameter tp4 = new TemplateParameter("p4", true, false);		// boolean parameter
    	params.add(tp4);
    	template.setParameters(params);
    	
    	try {
    		
    		model.validate(template);
    		Assert.fail("Model.validate(Template) should throw DataValidationException, when the model declares no parameters and the template has some.");
    		
    	} catch(DataValidationException ilex) {
    		
    		// ok, good to go..
    	}
    	
    	// Test 7. We check whether we put a parameter that does not have
    	//		   the same type as the one declared by the model.
    	//
    	params.remove(tp2);
    	params.remove(tp3);
    	params.remove(tp4);
    	template.setParameters(params);
    	
    	List<ModelParameter> parameters = new ArrayList<ModelParameter>();
    	ModelParameter mp1 = new ModelParameter("p1", true, ParameterType.BOOLEAN, null, null);
    	parameters.add(mp1);
    	model.setParameters(parameters);
    	
    	try {
    		
    		model.validate(template);
    		Assert.fail("Model.validate(Template) should throw DataValidationException when the template parameter does not match the type of the corresponding in the model.");
    		
    	} catch(DataValidationException dvex) {
    	
    		// ok, good to go...
    	}
    	
    	// Test 8. We check whether we put a parameter that matches the type, but we add it
    	//		   twice.
    	//
    	params.remove(tp1);
    	tp1.setValue(false);
    	params.add(tp1);
    	params.add(tp1);
    	
    	try {
    		
    		model.validate(template);
    		Assert.fail("Model.validate(Template) should throw DataValidationException when the template has the same valid parameter declared twice.");
    		
    	} catch(DataValidationException dvex) {
    		
    		// ok, good to go
    	}
    	
    	// Test 9. We do a couple of more checks for range parameters to be sure that the
    	//		   validation works as expected.
    	
    	mp1.setType(ParameterType.DOUBLE);
    	mp1.setValue(34.2);
    	mp1.setRange(new Object[] {1.2, 100.4});
    	
    	parameters.remove(mp1);
    	parameters.add(mp1);
    	model.setParameters(parameters);
    	
    	params.clear();
    	tp1.setValue(200.0);
    	params.add(tp1);
    	template.setParameters(params);
    	
    	try {
    		
    		model.validate(template);
    		Assert.fail("Model.validate(Template) shoudl throw DataValidationException when the template has a parameter that is out of range.");
    		
    	} catch(DataValidationException dvex) {
    		
    		// ok, good to go...
    	}
    	
    	// Test 10. We check with a value that is compatible...
    	//          The test should pass, because the integer value
    	//          can be converted into a double and it is in range.
    	//
    	params.clear();
    	tp1.setValue(10);
    	params.add(tp1);
    	
    	template.setParameters(params);
    	model.validate(template);
    	
    	
    	// Test 11. This is the sunny day scenario, we throw a bunch of parameters 
    	//			in the model, which are all compatible with the parameter definition
    	//			we initially created.
    	
    	parameters.clear();
    	
    	mp1.setType(ParameterType.INT);
    	mp1.setRange(null);
    	mp1.setValue(2000);
    	parameters.add(mp1);
    	
    	parameters.add(new ModelParameter("p2", "this is text", ParameterType.STRING, null, null));
    	parameters.add(new ModelParameter("p3", 100, ParameterType.DOUBLE, null, null));
    	parameters.add(new ModelParameter("p4", false, ParameterType.BOOLEAN, null, null));
    	model.setParameters(parameters);
    	
    	params.clear();
    	params.add(tp1);
    	params.add(tp2);
    	params.add(tp3);
    	params.add(tp4);
    	template.setParameters(params);
    	
    	model.validate(template);
    	
    	
    	// Test 12. We add another parameter to the model and check whether the validation still
    	//          passes.
    	
    	parameters.add(new ModelParameter("p5", "this is text", ParameterType.STRING, null, null));
    	model.setParameters(parameters);
    	
    	model.validate(template);
    	
    
    }
    
    /**
     * This method tests the implemented behaviour of the {@link Model#getParameter(String)}
     * method. The method is designed to retrieve the {@link ModelParameter} instance that
     * is mapped by the given name passed as argument. If the latter is {@literal null} or
     * an empty string, it should throw {@link IllegalArgumentException}.
     */
    @Test
    public void testGetParameter() {

		// Test 1. By default there are no parameters added.
		//
		Model model = new Model("Test Model");
		Parameter parameter = model.getParameter("thisIsNotAParameter");
		Assert.assertNull(parameter);
		
		
		// Test 2. We setup the parameter collection with a list
		// 		   of parameters, and check that all the parameters
		//		   in that list are present.
		List<ModelParameter> parameters = new ArrayList<ModelParameter>();
		
		ModelParameter mp1 = new ModelParameter("mp1", 23, ParameterType.INT, null, null);
		parameters.add(mp1);
		
		ModelParameter mp2 = new ModelParameter("mp2", true, ParameterType.BOOLEAN, null, null);
		parameters.add(mp2);
		
		model.setParameters(parameters);
		
		
		// let's check tp1.
		//
		ModelParameter actual = model.getParameter(mp1.getName());
		Assert.assertEquals(mp1, actual);
		
		// let's check tp2.
		//
		actual = model.getParameter(mp2.getName());
		Assert.assertEquals(mp2, actual);
		
		
		// Test 3. We then set the list of parameters to null, and we
		//         should not have any parameter left there.
		//
		model.setParameters(null);
		actual = model.getParameter(mp1.getName());
		Assert.assertNull(actual);
		actual = model.getParameter(mp2.getName());
		Assert.assertNull(actual);
		
		
		// Test 4. We pass an invalid parameter name.
		//
		//
		
		// 4a. null name.
		//
		try {
			
			model.getParameter(null);
			Assert.fail("Model.getParameter(null) should throw IllegalArgumentException, parameter name cannot be null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go..
		}
		
		// 4b. empty parameter name
		//
		try {
			
			model.getParameter("");
			Assert.fail("Model.getParameter('') should throw IllegalArgumentException, parameter name cannot be empty.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go..
		}
    	
    }
    
    /**
     * This method tests the behaviour of the getter and the setter defined for
     * the property <i>parameters</i>. This collection contains the list of the
     * {@link ModelParameter} instance defined for the model. The method will 
     * checkt that the parameters that are set, can be retrieved. The setter and
     * the getter can respectively accept and return a {@literal null} reference.
     */
    @Test
    public void testGetSetParameters() {
    	
		// Test 1. null by default.
		//
		Model model = new Model("TestModel");
		Assert.assertNull(model.getParameters());

		// Test 2. we add an empty list of parameters.
		//
        List<ModelParameter> expected = new ArrayList<ModelParameter>();
        model.setParameters(expected);
        List<ModelParameter> actual = model.getParameters();
        Assert.assertNotNull(actual);
        Assert.assertEquals(0, actual.size());
        
        // Test 3. we add a bunch of parameters
        //
        expected = new ArrayList<ModelParameter>();
        
        ModelParameter mp1 = new ModelParameter("mp1", 34, ParameterType.INT, null, null);
        expected.add(mp1);
        
        ModelParameter mp2 = new ModelParameter("mp2", true, ParameterType.BOOLEAN, null, null);
        expected.add(mp2);
        
        model.setParameters(expected);
        actual = model.getParameters();
        
        // NOTE: we do not make an equality test on the lists implementation
        
        //       because the interface does not enforce that this has to be
        //       same list instance passed in, what we care about is that the
        //		 content of the list is the same.
        //
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertTrue(actual.contains(mp1));
        Assert.assertTrue(actual.contains(mp2));
        
        // Test 4. can we set null?
        //
        model.setParameters(null);
        Assert.assertNull(model.getParameters());
    }
    
    /**
     * This method tests the implemented behaviour of the {@link Model#getObjectives()} method.
     * This method verifies that when a {@link Model} instance is initialised the objectives
     * collection is {@literal null}. Moreover, it checks that the collection of objectives can
     * be set and retrieved with the corresponding getters. The setter and the getter can accept
     * and retrieve {@literal null}.
     */
    @Test
    public void testGetObjectives() {
    	
		// Test 1. null by default.
		//
		Model model = new Model("TestModel");
		Assert.assertNull(model.getObjectives());

		// Test 2. we add an empty list of parameters.
		//
        List<Objective> expected = new ArrayList<Objective>();
        model.setObjectives(expected);
        List<Objective> actual = model.getObjectives();
        Assert.assertNotNull(actual);
        Assert.assertEquals(0, actual.size());
        
        // Test 3. we add a bunch of parameters
        //
        expected = new ArrayList<Objective>();
        
        Objective o1 = new Objective("o1", "Maximise throughput.", null);
        expected.add(o1);
        
        Objective o2 = new Objective("o2", "Minimise resonse time.", null);
        expected.add(o2);
        
        model.setObjectives(expected);
        actual = model.getObjectives();
        
        // NOTE: we do not make an equality test on the lists implementation
        
        //       because the interface does not enforce that this has to be
        //       same list instance passed in, what we care about is that the
        //		 content of the list is the same.
        //
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.size(), actual.size());
        Assert.assertTrue(actual.contains(o1));
        Assert.assertTrue(actual.contains(o2));
        
        // Test 4. can we set null?
        //
        model.setObjectives(null);
        Assert.assertNull(model.getObjectives());
    }
    
    /**
     * This method tests the implemented behaviour of the {@link Model#getObjective(String)}
     * method. The method is designed to retrieve the {@link objective} instance that is
     * mapped by the given name passed as argument. If the latter is {@literal null} or
     * an empty string, it should throw {@link IllegalArgumentException}.
     */
    @Test
    public void testGetObjective() {

		// Test 1. By default there are no objectives added.
		//
		Model model = new Model("Test Model");
		Objective objective = model.getObjective("thisIsNotAnObjective");
		Assert.assertNull(objective);
		
		
		// Test 2. We setup the objectives collection with a list
		// 		   of parameters, and check that all the parameters
		//		   in that list are present.
		List<Objective> objectives = new ArrayList<Objective>();
		
		Objective o1 = new Objective("o1", "Maximise throughput", null);
		objectives.add(o1);
		
		Objective o2 = new Objective("o2", "Minimise response", null);
		objectives.add(o2);
		
		model.setObjectives(objectives);
		
		
		// let's check tp1.
		//
		Objective actual = model.getObjective(o1.getName());
		Assert.assertEquals(o1, actual);
		
		// let's check tp2.
		//
		actual = model.getObjective(o2.getName());
		Assert.assertEquals(o2, actual);
		
		
		// Test 3. We then set the list of parameters to null, and we
		//         should not have any parameter left there.
		//
		model.setObjectives(null);
		actual = model.getObjective(o1.getName());
		Assert.assertNull(actual);
		actual = model.getObjective(o2.getName());
		Assert.assertNull(actual);
		
		
		// Test 4. We pass an invalid parameter name.
		//
		//
		
		// 4a. null name.
		//
		try {
			
			model.getObjective(null);
			Assert.fail("Model.getObjective(null) should throw IllegalArgumentException, objective name cannot be null.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go..
		}
		
		// 4b. empty parameter name
		//
		try {
			
			model.getObjective("");
			Assert.fail("Model.getObjective('') should throw IllegalArgumentException, objective name cannot be empty.");
			
		} catch(IllegalArgumentException ilex) {
			
			// ok good to go..
		}
    	
    }
    
    /**
     * This method tests the implemented behaviour of {@link Model#clone()}.
     * The method is expected to clone complex properties and to shallow
     * copy immutable properties or simple ones.
     */
    @Override
    @Test
    public void testClone() {
    	
    	super.testClone();
    	
    	// id, revision, and defaults have been taken care of...
    	// let's now take care of the model specific properties.
    	//
    	
    	Model expected = new Model("Model");
    	expected.setLabel("This is a label");
    	expected.setDescription("This is the description.");
    	expected.setId(UUID.randomUUID().toString());
    	expected.setDefaultModel(true);
    	expected.setModelVersion(3);
    	
    	List<ModelParameter> parameters = new ArrayList<ModelParameter>();
    	parameters.add(new ModelParameter("m1", 12, ParameterType.INT, null, "o1"));
    	parameters.add(new ModelParameter("m2", 2.3, ParameterType.DOUBLE, new Object [] { 0.0, null }, "o2"));
    	parameters.add(new ModelParameter("m3", "reverse", ParameterType.STRING, null, null));
    	parameters.add(new ModelParameter("m4", true, ParameterType.BOOLEAN, null, null));
    	expected.setParameters(parameters);
    	
    	List<Objective> objectives = new ArrayList<Objective>();
    	objectives.add(new Objective("o1", "Maximise throughout...", null));
    	objectives.add(new Objective("o2", "Maximise completion rate...", null));
    	expected.setObjectives(objectives);
    	
    	
    	List<OutputMapping> mappings = new ArrayList<OutputMapping>();
    	OutputMapping om1 = new OutputMapping();
    	om1.setFileName("mapping.mod");
    	om1.setMappingType(MappingType.TRANSFORMER);
    	om1.setTransformer("com.ibm.au.optim.suro.TestTransformer");
    	
    	OutputMapping om2 = new OutputMapping();
    	om2.setFileName("solution.json");
    	om2.setMappingType(MappingType.JSON_CATEGORY);
    	
    	MappingSource ms = new MappingSource("entries");
    	
    	MappingSpecification msr = new MappingSpecification();
    	msr.setLabels(new String[] { "lr1", "lr2", "lr3" });
    	msr.setEntryKeys(new String[] { "rk1", "rk2", "rk3" });
    	ms.setRow(msr);
    	
    	MappingSpecification msc = new MappingSpecification();
    	msc.setLabels(new String[] { "lc1", "lc2", "lc3" });
    	msc.setEntryKeys(new String[] { "ck1", "ck2", "ck3" });
    	ms.setColumn(msc);
    	
    	ValueMapping vm = new ValueMapping();
    	vm.setKeys(new String[] { "vk1", "vk2", "vk3", "vk4", "vk5"  });
    	ms.setValue(vm);
    	
    	
    	mappings.add(om1);
    	mappings.add(om2);
    	
    	expected.setOutputMappings(mappings);
    	
    	
    	Model actual = (Model) expected.clone();
    	this.equals(expected, actual);
    	
    }
    
    /**
     * This is the specialisation of the factory method for the
     * creation of {@link Entity} instances for the purpose of
     * testing. Since {@link Model} inherits from {@link Entity}
     * we can leverage the tests defined for the base class to
     * test the {@link Entity} portion of the {@link Model} type.
     * 
     * @return a {@link Model} instance.
     */
    @Override
    protected Entity createEntity() {
    	
    	return new Model("label", true);
    }
    
    /**
     * This method extends {@link EntityTest#equals(Entity, Entity)} with
     * the field by field checks of the {@link Model} specific properties.
     * For collections, the {@literal null} check is performed first, then
     * the size check, and then the check to see whether there is the same
     * content.
     * 
     * @param expected	a {@link Entity} instance. It must be not {@literal null}
     * 					and of type {@link Model}.
     * @param actual 	a {@link Entity} instance. It must be not {@literal null}
     * 					and of type {@link Model}.
     */
    @Override
    protected void equals(Entity expected, Entity actual) {
    	
    	super.equals(expected, actual);
    	
    	Model me = (Model) expected;
    	Model ma = (Model) actual;
    	
    	Assert.assertEquals(me.getLabel(), ma.getLabel());
    	Assert.assertEquals(me.getDescription(), ma.getDescription());
    	Assert.assertEquals(me.getModelVersion(), ma.getModelVersion());
    	Assert.assertEquals(me.isDefaultModel(), ma.isDefaultModel());
    	
    	// objectives
    	//
    	List<Objective> objectives = me.getObjectives();
    	if (objectives != null) {
    		
    		List<Objective> targets = ma.getObjectives();
			Assert.assertNotNull(targets);
    		Assert.assertEquals(objectives.size(),targets.size());
    		for(Objective objective : objectives) {
    			
    			Objective found = ma.getObjective(objective.getName());
    			Assert.assertNotNull(found);
    			
    			Assert.assertEquals(objective.getName(), found.getName());
    			Assert.assertEquals(objective.getLabel(), found.getLabel());
    			Assert.assertEquals(objective.getDescription(), found.getDescription());
    		}
    		
    	} else {
    		
    		Assert.assertNull(ma.getObjectives());
    	}
    	
    	// parameters
    	//
    	List<ModelParameter> parameters = me.getParameters();
    	if (parameters != null) {
    		
    		List<ModelParameter> targets = ma.getParameters();
			Assert.assertNotNull(targets);
    		Assert.assertEquals(parameters.size(),targets.size());
    		for(ModelParameter p : parameters) {
    			
    			ModelParameter found = ma.getParameter(p.getName());
    			Assert.assertNotNull(found);
    			
    			Assert.assertEquals(p.getName(), found.getName());
    			Assert.assertEquals(p.getValue(), found.getValue());
    			Assert.assertEquals(p.getType(), found.getType());
    			Assert.assertEquals(p.getLowerBound(), found.getLowerBound());
    			Assert.assertEquals(p.getUpperBound(), found.getUpperBound());
    		}
    		
    	} else {
    		
    		Assert.assertNull(ma.getObjectives());
    	}   
    	
    	// output mappings
    	//
    	
    	List<OutputMapping> mappings = me.getOutputMappings();
    	if (mappings != null) {
    		
    		List<OutputMapping> targets = ma.getOutputMappings();
			Assert.assertNotNull(targets);
    		Assert.assertEquals(mappings.size(),targets.size());
    		for(OutputMapping m : mappings) {
    				
    			boolean isMatch = true;
    			
    			for(OutputMapping mt : targets) {
    					
    				isMatch = m.equals(mt);
    				if (isMatch == true) {
    					break;
    				}
    			}
    			
    			Assert.assertTrue(isMatch);
    		}
    		
    	} else {
    		
    		Assert.assertNull(ma.getObjectives());
    	} 
    	
    	
    }
}
