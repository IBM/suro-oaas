/**
 * 
 */
package com.ibm.au.optim.suro.model.admin.feedback.impl.redmine;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;

/**
 * Class <b>RedmineIssueTest</b>. This class tests the {@link Issue} implementation
 * provided by the class {@link RedmineIssue} and the specific features implemented
 * in that class. A {@link RedmineIssue} instance is meant to represent a subset of
 * the feature of an issue in <i>Redmine</i> in particular those that are relevent
 * for simple bug tracking and feature request.
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class RedmineIssueTest {
	
	/**
	 * A random number generator for the attributes of {@link RedmineIssue} instances.
	 */
	protected static Random rdn = new Random();


	/**
	 * Tests the behaviour of the getter and setter for the <i>priorityId</i> property
	 * of the {@link RedmineIssue} class. By default is set to the constant value 
	 * {@literal RedmineIssue#REDMINE_ISSUE_NOT_DEFINED} and once set it should be 
	 * equal to what is retrieved by the getter.
	 */
	@Test
	public void testGetSetPriorityId() {
		
		RedmineIssue issue = new RedmineIssue();
		
		int actual = issue.getPriorityId();
		
		// 1. test that is equal to default.
		//
		Assert.assertEquals("Property 'priorityId' should be set to " + RedmineIssue.REDMINE_ISSUE_NOT_DEFINED + " by default.", actual, RedmineIssue.REDMINE_ISSUE_NOT_DEFINED);
		
		// 2. setting a value
		//
		int expected = rdn.nextInt();
		issue.setPriorityId(expected);
		actual = issue.getPriorityId();
		
		Assert.assertEquals("Property 'priorityId' does not match expected value.", actual, expected);
		
		// 3. "" edge case.
		//
		expected = RedmineIssue.REDMINE_ISSUE_NOT_DEFINED;
		issue.setPriorityId(expected);
		actual = issue.getPriorityId();
		
		Assert.assertEquals("Property 'priorityId' does not match expected value [edge case: " + RedmineIssue.REDMINE_ISSUE_NOT_DEFINED + "].", actual, expected);
		
	}

	/**
	 * Tests the behaviour of the getter and setter for the <i>categoryId</i> property
	 * of the {@link RedmineIssue} class. By default is set to the constant value 
	 * {@literal RedmineIssue#REDMINE_ISSUE_NOT_DEFINED} and once set it should be 
	 * equal to what is retrieved by the getter.
	 */
	@Test
	public void testGetSetCategoryId() {
		
		
		RedmineIssue issue = new RedmineIssue();
		
		int actual = issue.getCategoryId();
		
		// 1. test that is equal to default.
		//
		Assert.assertEquals("Property 'categoryId' should be set to " + RedmineIssue.REDMINE_ISSUE_NOT_DEFINED + " by default.", actual, RedmineIssue.REDMINE_ISSUE_NOT_DEFINED);
		
		// 2. setting a value
		//
		int expected = rdn.nextInt();
		issue.setCategoryId(expected);
		actual = issue.getCategoryId();
		
		Assert.assertEquals("Property 'categoryId' does not match expected value.", actual, expected);
		
		// 3. "" edge case.
		//
		expected = RedmineIssue.REDMINE_ISSUE_NOT_DEFINED;
		issue.setCategoryId(expected);
		actual = issue.getCategoryId();
		
		Assert.assertEquals("Property 'categoryId' does not match expected value [edge case: " + RedmineIssue.REDMINE_ISSUE_NOT_DEFINED + "].", actual, expected);
	}

	/**
	 * Tests the behaviour of the getter and setter for the <i>projectId</i> property
	 * of the {@link RedmineIssue} class. By default is set to the constant value 
	 * {@literal RedmineIssue#REDMINE_ISSUE_NOT_DEFINED} and once set it should be 
	 * equal to what is retrieved by the getter.
	 */
	@Test
	public void testGetSetProjectId() {

		RedmineIssue issue = new RedmineIssue();
		
		int actual = issue.getProjectId();
		
		// 1. test that is equal to default.
		//
		Assert.assertEquals("Property 'projectId' should be set to " + RedmineIssue.REDMINE_ISSUE_NOT_DEFINED + " by default.", actual, RedmineIssue.REDMINE_ISSUE_NOT_DEFINED);
		
		// 2. setting a value
		//
		int expected = rdn.nextInt();
		issue.setProjectId(expected);
		actual = issue.getProjectId();
		
		Assert.assertEquals("Property 'projectId' does not match expected value.", actual, expected);
		
		// 3. "" edge case.
		//
		expected = RedmineIssue.REDMINE_ISSUE_NOT_DEFINED;
		issue.setProjectId(expected);
		actual = issue.getProjectId();
		
		Assert.assertEquals("Property 'projectId' does not match expected value [edge case: " + RedmineIssue.REDMINE_ISSUE_NOT_DEFINED + "].", actual, expected);
	}

	/**
	 * Tests the behaviour of the getter and setter for the <i>trackerId</i> property
	 * of the {@link RedmineIssue} class. By default is set to the constant value 
	 * {@literal RedmineIssue#REDMINE_ISSUE_NOT_DEFINED} and once set it should be 
	 * equal to what is retrieved by the getter.
	 */
	@Test
	public void testGetSetTrackerId() {
		
		
		RedmineIssue issue = new RedmineIssue();
		
		int actual = issue.getTrackerId();
		
		// 1. test that is equal to default.
		//
		Assert.assertEquals("Property 'trackerId' should be set to " + RedmineIssue.REDMINE_ISSUE_NOT_DEFINED + " by default.", actual, RedmineIssue.REDMINE_ISSUE_NOT_DEFINED);
		
		// 2. setting a value
		//
		int expected = rdn.nextInt();
		issue.setTrackerId(expected);
		actual = issue.getTrackerId();
		
		Assert.assertEquals("Property 'trackerId' does not match expected value.", actual, expected);
		
		// 3. "" edge case.
		//
		expected = RedmineIssue.REDMINE_ISSUE_NOT_DEFINED;
		issue.setTrackerId(expected);
		actual = issue.getTrackerId();
		
		Assert.assertEquals("Property 'trackerId' does not match expected value [edge case: " + RedmineIssue.REDMINE_ISSUE_NOT_DEFINED + "].", actual, expected);
	}

	/**
	 * Tests the behaviour of the getter and setter for the <i>statusId</i> property
	 * of the {@link RedmineIssue} class. By default is set to the constant value 
	 * {@literal RedmineIssue#REDMINE_ISSUE_NOT_DEFINED} and once set it should be 
	 * equal to what is retrieved by the getter.
	 */
	@Test
	public void testGetSetStatusId() {
		
		
		RedmineIssue issue = new RedmineIssue();
		
		int actual = issue.getStatusId();
		
		// 1. test that is equal to default.
		//
		Assert.assertEquals("Property 'statusId' should be set to " + RedmineIssue.REDMINE_ISSUE_NOT_DEFINED + " by default.", actual, RedmineIssue.REDMINE_ISSUE_NOT_DEFINED);
		
		// 2. setting a value
		//
		int expected = rdn.nextInt();
		issue.setStatusId(expected);
		actual = issue.getStatusId();
		
		Assert.assertEquals("Property 'statusId' does not match expected value.", actual, expected);
		
		// 3. "" edge case.
		//
		expected = RedmineIssue.REDMINE_ISSUE_NOT_DEFINED;
		issue.setStatusId(expected);
		actual = issue.getStatusId();
		
		Assert.assertEquals("Property 'statusId' does not match expected value [edge case: " + RedmineIssue.REDMINE_ISSUE_NOT_DEFINED + "].", actual, expected);

	}
	/**
	 * Tests the behaviour of the getter and setter for the <i>subject</i> property
	 * of the {@link RedmineIssue} class. By default is set to {@literal null} and once
	 * set it should be equal to what is retrieved by the getter.
	 */
	@Test
	public void testGetSetSubject() {
		
		RedmineIssue issue = new RedmineIssue();
		
		String actual = issue.getSubject();
		
		// 1. test that is equal to null.
		//
		Assert.assertNull("Property 'subject' should be set to null by default.", actual);
		
		// 2. setting a value
		//
		String expected = "This is a random description for an issue.";
		issue.setSubject(expected);
		actual = issue.getSubject();
		
		Assert.assertEquals("Property 'subject' does not match expected value.", actual, expected);
		
		// 3. "" edge case.
		//
		expected = "";
		issue.setSubject(expected);
		actual = issue.getSubject();
		
		Assert.assertEquals("Property 'subject' does not match expected value [edge case: ''].", actual, expected);
		
		// 4. null edge case
		//
		expected = null;
		issue.setSubject(expected);
		actual = issue.getSubject();
		
		Assert.assertNull("Property 'subject' does not match expected value [edge case: null].", actual);
		
		
	}
	
	/**
	 * Tests the behaviour of the getter and setter for the <i>description</i> property
	 * of the {@link RedmineIssue} class. By default is set to {@literal null} and once
	 * set it should be equal to what is retrieved by the getter.
	 */
	@Test
	public void testGetSetDescription() {
	
		RedmineIssue issue = new RedmineIssue();
		
		String actual = issue.getDescription();
		
		// 1. test that is equal to null.
		//
		Assert.assertNull("Property 'description' should be set to null by default.", actual);
		
		// 2. setting a value
		//
		String expected = "This is a random description for an issue.";
		issue.setDescription(expected);
		actual = issue.getDescription();
		
		Assert.assertEquals("Property 'description' does not match expected value.", actual, expected);
		
		// 3. "" edge case.
		//
		expected = "";
		issue.setDescription(expected);
		actual = issue.getDescription();
		
		Assert.assertEquals("Property 'description' does not match expected value [edge case: ''].", actual, expected);
		
		// 4. null edge case
		//
		expected = null;
		issue.setDescription(expected);
		actual = issue.getDescription();
		
		Assert.assertNull("Property 'description' does not match expected value [edge case: null].", actual);
		
		
	}
	
	/**
	 * Test the default implementation of the constructor. The default implementation
	 * of the constructor is empty. This means that all the properties of the {@link
	 * RedmineIssue} instances should be set to their corresponding default values.
	 */
	@Test
	public void testDefaultConstructor() {
		
		RedmineIssue issue = new RedmineIssue();
		
		Assert.assertEquals("Property 'projectId' should be set to default value.", issue.getProjectId(), RedmineIssue.REDMINE_ISSUE_NOT_DEFINED);
		Assert.assertEquals("Property 'categoryId' should be set to default value.", issue.getCategoryId(), RedmineIssue.REDMINE_ISSUE_NOT_DEFINED);
		Assert.assertEquals("Property 'priorityId' should be set to default value.", issue.getPriorityId(), RedmineIssue.REDMINE_ISSUE_NOT_DEFINED);
		Assert.assertEquals("Property 'statusId' should be set to default value.", issue.getStatusId(), RedmineIssue.REDMINE_ISSUE_NOT_DEFINED);
		Assert.assertEquals("Property 'trackerId' should be set to default value.", issue.getTrackerId(), RedmineIssue.REDMINE_ISSUE_NOT_DEFINED);
		
		Assert.assertNull("Property 'subject' should be set to default value (null).", issue.getSubject());
		Assert.assertNull("Property 'description' should be set to default value (null).", issue.getDescription());
	}
	
	
	/**
	 * Tests that the deserialisation and the serialisation through Jackson work as expected. The
	 * test is not meant to test JSON deserialisation made by the Jackson library, but whether we
	 * have annotated the class {@link RedmineIssue} properties appropriately.
	 */
	@Test
	public void testJSONDeserialization() throws IOException {
		
		// 1. Test 1: we deserialise a valid JSON and we check that all the values
		//			  are mapped properly.
		//
		String fileName = "issue-correct.json";
		
		InputStream input = this.getClass().getClassLoader().getResourceAsStream(fileName);
		String issueText = this.read(input);
		
		ObjectMapper mapper = new ObjectMapper();
		
		RedmineIssue actual = (RedmineIssue) mapper.readValue(issueText, RedmineIssue.class);
		@SuppressWarnings("unchecked")
		Map<String,Object> expected = (Map<String,Object>) mapper.readValue(issueText, new TypeReference<Map<String,Object>>() {});
		
		// ok if we got here, we are sure that we have deserialised the
		// instance. Now we need to check that all the values that we
		// expect are there.
		
		String text = (String) expected.get("subject");
		Assert.assertEquals("The 'subject' property has not been mapped correctly.", text, actual.getSubject());
		text = (String) expected.get("description");
		Assert.assertEquals("The 'description' property has not been mapped correctly.", text, actual.getDescription());
		int value = (int) expected.get("project_id");
		Assert.assertEquals("The 'projectId' property has not been mapped  correctly.", value, actual.getProjectId());
		value = (int) expected.get("priority_id");
		Assert.assertEquals("The 'priorityId' property has not been mapped  correctly.", value, actual.getPriorityId());
		value = (int) expected.get("category_id");
		Assert.assertEquals("The 'categoryId' property has not been mapped  correctly.", value, actual.getCategoryId());
		value = (int) expected.get("status_id");
		Assert.assertEquals("The 'statusId' property has not been mapped  correctly.", value, actual.getStatusId());
		value = (int) expected.get("tracker_id");
		Assert.assertEquals("The 'trackerId' property has not been mapped  correctly.", value, actual.getTrackerId());
		
		
		// 2. Test 2: we deserialise an invalid JSON and we ensure that we have an exception
		//            thrown.
		
		try {
			
			fileName = "issue-incorrect.json";
			input = this.getClass().getClassLoader().getResourceAsStream(fileName);
			actual = (RedmineIssue) mapper.readValue(input, RedmineIssue.class);
			
			Assert.fail("Invalid JSON document has been deserialised into a RedmineIssue instance.");
			
		} catch(JsonMappingException jmex) {
			
			// ok all good, this means that we are ok.
		}
		
		
	}
	/**
	 * This method tests the {@link RedmineIssue#clone()} method.
	 */
	@Test
	public void testClone() {
		
		RedmineIssue expected = new RedmineIssue();
		expected.setDescription(null);
		expected.setSubject("this is a subject");
		expected.setCategoryId(RedmineIssue.REDMINE_ISSUE_NOT_DEFINED);
		expected.setPriorityId(RedmineIssueTest.rdn.nextInt());
		expected.setProjectId(RedmineIssueTest.rdn.nextInt());
		expected.setStatusId(RedmineIssueTest.rdn.nextInt());
		expected.setTrackerId(RedmineIssueTest.rdn.nextInt());
		
		
		RedmineIssue actual = expected.clone();
		
		
		Assert.assertEquals("Subject property is not the same,", expected.getSubject(), actual.getSubject());
		Assert.assertNull("Description property is not the same.", actual.getDescription());
		Assert.assertEquals("Project id property is not the same.", expected.getProjectId(), actual.getProjectId());
		Assert.assertEquals("Priority id property is not the same.", expected.getPriorityId(), actual.getPriorityId());
		Assert.assertEquals("Status id property is not the same.", expected.getStatusId(), actual.getStatusId());
		Assert.assertEquals("Category id property is not the same.", expected.getCategoryId(), actual.getCategoryId());
		Assert.assertEquals("Tracker id property is not the same.", expected.getTrackerId(), actual.getTrackerId());
		
		
		
		
	}
	
	/**
	 * This method reads the content of an input stream into a string.
	 * 
	 * @param input	a {@link InputStream} instance that contains the text that 
	 * 				will be read into a {@link String}.
	 * 
	 * @return	a {@link String} representing the text read from the input stream.
	 * 
	 * @throws IOException	if there is any error while reading the stream.
	 */
	private String read(InputStream input) throws IOException {
		
		try {
			
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    byte[] buffer = new byte[1024];
		    int length = 0;
		    while ((length = input.read(buffer)) != -1) {
		        baos.write(buffer, 0, length);
		    }
		    byte[] content = baos.toByteArray();
		    return new String(content);
	    
		} finally {
			
			input.close();
		}
	}

}
