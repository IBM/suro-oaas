/**
 * 
 */
package com.ibm.au.optim.suro.model.admin.feedback.impl.redmine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.jaws.web.core.runtime.impl.InMemoryRuntimeContext;
import com.ibm.au.optim.suro.model.admin.feedback.Issue;
import com.ibm.au.optim.suro.model.admin.feedback.IssueException;
import com.ibm.au.optim.suro.model.admin.feedback.IssueType;
import com.ibm.au.optim.suro.model.admin.feedback.impl.SimpleIssue;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.jaws.web.core.runtime.RuntimeContext;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentFacade;
import com.ibm.au.jaws.web.core.runtime.impl.PropertiesParameterSource;

/**
 * Class <b>RedmineIssueManagerTest</b>. This class tests the behaviour of the {@link RemineIssueManager}
 * component and particularly it verifies that all the various parameters are properly configured and can
 * be used to issue tickets to a <i>Redmine</i> installation.
 * 
 * 
 * @author Christian Vecchiola
 *
 */
public class RedmineIssueManagerTest {
	
	/**
	 * A {@link Logger} implementation that is used to collect the log messages for this class.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(RedmineIssueManagerTest.class);
	
	/**
	 * <p>
	 * Class <b>SSLDebugRedmineIssueManager</b>. This class extends the {@link RedmineIssueManager}
	 * and provides an additional level of functionality to support testing, which enables to dump
	 * information about the supported or enabled SSL protocols and ciphers used for SSL connections.
	 * </p>
	 * <p>
	 * This capability is added by overriding the {@link RedmineIssueManager#getFactory(SSLContext)}
	 * method and inspecting the returned instance. The class does not add any change to the issue
	 * submission behaviour or the configuration of the instance.
	 * </p>
	 * 
	 * @author Christian Vecchiola
	 *
	 */
	private class SSLDebugRedmineIssueManager extends RedmineIssueManager {
		
		/**
		 * A {@literal boolean} flag that controls whether the instance should
		 * inspect the {@link SSLConnectionSocketFactory} used by the HTTP client
		 * to expose the details about supported protocols and ciphers.
		 */
		private boolean debug;

		/**
		 * Initialises an instance of the {@link SSLDebugRedmineIssueManager} with the
		 * given behaviour for debugging SSL connections.
		 * 
		 * @param debug	a {@literal boolean} value indicating whether the SSL settings
		 * 				for the SSL connections should be inspected to list all the 
		 * 				relevant details ({@literal true}) or not ({@literal false}).
		 */
		public SSLDebugRedmineIssueManager(boolean debug) {
			this.debug = debug;
		}
		
		/** 
		 * <p>
		 * This method overrides the original method simply to add an instruction that
		 * enables the inspection of the SSL properties (protocols and ciphers) that
		 * are used by the {@link SSLConnectionSocketFactory} class, used to generate
		 * SSL connections for the HTTP client.
		 * </p>
		 * <p>
		 * This method invokes the base class version of the method and before returning
		 * what is created, conditionally inspect the supported protocols and ciphers 
		 * used by the factory in case the value of the debug switch is set to {@code true}.
		 * </p>
		 * 
		 * @param sslContext	an instance of the {@link SSLContext} that is used to 
		 * 						control the SSL connections that will be created by the
		 * 						returned factory.
		 * 
		 * @return 	an instance of {@link SSLConnectionFactory} that will be used to 
		 * 			generate the SSL connections.
		 */
		@Override
		protected SSLConnectionSocketFactory getFactory(SSLContext sslContext) {
			
			SSLConnectionSocketFactory factory = super.getFactory(sslContext);
			
			if (this.debug == true) {
				
				this.checkFactory(factory);
			}
			
			return factory;
		}
		
		/**
		 * This is an extension method for debugging the configuration of the {@link SSLConnectionSocketFactory} used to 
		 * talk to the remote SSL endpoint. This can be used to troubleshooting the SSL connections on top of the information
		 * that are obtained with <code>-Djava.net.ssl.debug=all</code>.
		 * 
		 * @param factory	a {@link SSLConnectionSocketFactory} instance that is used to build the {@link HttpClient}
		 * 					implementation used to connect to the remote endpoint via SSL.
		 */
		protected void checkFactory(SSLConnectionSocketFactory factory) {
			
			try {
				
				System.out.println(">> SSL CHECK START <<");
				
				SSLSocket socket = (SSLSocket) factory.createSocket(null);

				String[] options = socket.getSupportedProtocols();
				this.dumpOptions("Supported Protocols", options);
		        
		        options = socket.getEnabledProtocols();
		        this.dumpOptions("Enabled Protocols", options);

		        options = socket.getSupportedCipherSuites();
		        this.dumpOptions("Supported Ciphers", options);
		        
		        options = socket.getEnabledCipherSuites();
		        this.dumpOptions("Enabled Ciphers", options);
		        
				
			} catch (IOException ex) {
				
				System.out.println("Could not check SSLConnectionSocketFactory error while creating test socket [Msg: " + ex.getMessage() + ", Type: " + ex.getClass().getName() + "].");
			
			} finally {
				
				System.out.println(">> SSL CHECK END <<");
			}
			
		}
		
		/**
		 * This method simply dumps the collection of the options (one per line) onto the console
		 * preceeded by the given <i>header</i> string passed as a parameter. The method also adds
		 * at the end of the header the {@link String} <code>: n</code> where <i>n</i> is the value
		 * returned by {@link Array#length} invoked on <i>options</i>. 
		 * 
		 * @param header	a {@link String} representing the header written before dumping the content
		 * 					of the array.
		 * @param options	a {@link String} array representing the set of options to inspect.
		 */
		protected void dumpOptions(String header, String[] options) {
			
			System.out.println(header + ": " + options.length);
			for(int i=0; i<options.length; i++){
				
				System.out.println("  " + options[i]);
			}
		}
	}
	
	/**
	 * Class <b>InspectableRedmineIssueManager</b>. This class extends the {@link RedmineIsueManager}
	 * instance in order to provide support for testing. For the majority of the testing methods we
	 * do not need to go through real HTTP request but we simply need to check that the request is
	 * properly configured with the parameters that we have set in order to ensure that the client 
	 * work properly. This class overrides the {@link RedmineIssueManager#execute(HttpPost)} method 
	 * and provides an extension method that can be overridden to inspect the request and verify that
	 * the request is properly arranged.
	 * 
	 * @author Christian Vecchiola
	 *
	 */
	private abstract class InspectableRedmineIssueManager extends RedmineIssueManager {

		/**
		 * This method overrides the original implementation of that submits the request to the
		 * <i>Redmine</i> installation and rewire the call to a callback that can be used to 
		 * verify the content of the request, and generate a fake response.
		 */
		@Override
		protected HttpResponse execute(HttpPost post) {
		
			return this.inspectRequest(post);
		
		}
		/**
		 * This method is left to implementors of concrete classes inheriting from this one. The
		 * purpose of this method is to inspect the request and validate that it has been properly
		 * composed and configured to issue a ticket to the <i>Redmine</i> installation. 
		 * 
		 * @param post	a {@link HttpPost} instance represeting the request of ticket submission.
		 * 
		 * @return	a {@link HttpResponse} instance that can be used to generate a fake response
		 * 			for the caller.
		 */
		protected abstract HttpResponse inspectRequest(HttpPost post);
		
		/** 
		 * This method creates a fake response with the given status code information and the
		 * expected response to the request, as if it was posted to a real <i>Redmine<i>
		 * installation.
		 * 
		 * @param source		a {@link RedmineIssue} instance representing the content of the
		 * 						POST request converted from its original JSON format. It can be
		 * 						{@literal null}. If {@literal null} no body is added to the
		 * 						response, if not {@literal null} the method creates the inserted
		 * 						version of the instance.
		 * @param statusCode	a {@literal int} indicating the value of the status code of the
		 * 						response to create.
		 * @param reasonPhrase	a {@link String} representing the reason phrase that is passed to
		 * 						the response and further details the outcome of the operation.
		 * 
		 * @return	a {@link HttpResponse} implementation containing the specified status code and
		 * 			reason phrase with eventually a JSON content reflecting what it has been added
		 * 			to the <i>Redmine</i> installation.
		 */
		protected HttpResponse createResponse(HttpPost post, int statusCode, String reasonPhrase, RedmineIssue source) {
		
			
			BasicHttpResponse response = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), statusCode, reasonPhrase);
			response.setHeader("Content-Type", "application/json");
			
			if (source != null) {
			
				try {
					
					Map<String, Object> issue = this.createInsertedIssue(source);
					
					String tmp = this.mapper.writeValueAsString(issue);
					
					StringEntity entity = new StringEntity(tmp);
					
					response.setEntity(entity);
					
				
				} catch(IOException error) {
					
					LOGGER.error("Could not read the request content.", error);
					
					response.setStatusCode(500);
					response.setReasonPhrase("Internal server error.");
					
				}
			
			}
			
			return response;
		}
		
		/**
		 * This method retrieves the content of the request body and converts it into a {@link RedmineIssue} instance.
		 * 
		 * @param post		a {@link HttpPost} instance containing the information about the POST request made by the
		 * 					client. 
		 * 
		 * @return	a {@link RedmineIssue} instance that represents the mapped instance, or {@literal null} if there
		 * 			was any error in trying to perform the conversion.
		 */
		protected RedmineIssue getPostedIssue(HttpPost post)  {
			
			RedmineIssue source = null;
			
			try {
			
				InputStream json = post.getEntity().getContent();
			
				@SuppressWarnings("unchecked")
				Map<String, RedmineIssue> baggage = (Map<String,RedmineIssue>) this.mapper.readValue(json, new TypeReference<Map<String,RedmineIssue>>() {});
				source = baggage.get("issue");
			
			} catch(IOException ioex) {
				
				LOGGER.error("Error while reading the content of the post request.", ioex);
				
			}
			
			return source;
		}
		/**
		 * This method created a {@link Map} implementation that contains the equivalent
		 * inserted issue that matches the given <i>source</i> issue.
		 * 
		 * @param source	an instance of {@link RedmineIssue} representing the issue to
		 * 					add to the <i>Redmine</i> installation.
		 * 
		 * @return  a {@link Map} implementation containing the JSON document that is returned
		 * 			to the client that submits a request for adding an issue.
		 */
		private Map<String, Object> createInsertedIssue(RedmineIssue source) {
			
			Map<String,Object> createdIssue = new HashMap<String,Object>();
			createdIssue.put("id", 321);
			createdIssue.put("subject", source.getSubject());
			createdIssue.put("description", source.getDescription());
			createdIssue.put("done_ratio", 0);
			
			int value = source.getTrackerId();
			createdIssue.put("tracker", this.getMap(this.getTrackerFor(value), value));
			
			value = source.getPriorityId();
			createdIssue.put("priority", this.getMap(this.getPriorityFor(value), value));
			
			value = source.getProjectId();
			createdIssue.put("project", this.getMap("Test project", value));
			
			value = source.getStatusId();
			createdIssue.put("status", this.getMap(this.getStatusFor(value), value));
			
			createdIssue.put("author", this.getMap("Test author", 1));
			
			// timing information
			//
			
			Date date = new Date();
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault());
			String tmp = formatter.format(date);
			createdIssue.put("created_on", tmp);
			createdIssue.put("updated_on", tmp);
			
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			createdIssue.put("start_date", formatter.format(date));
			
			Map<String,Object> issue = new HashMap<String,Object>();
			issue.put("issue", createdIssue);
			return issue;
		}
		
		/**
		 * Gets the name of the tracker that corresponds to the value 
		 * that has been passed as an argument to the method. 
		 * 
		 * @param value	a {@literal int} value representing the unique
		 * 				identifier of the tracker.
		 * 
		 * @return  a {@link String} representing the name of the tracker.
		 * 			The current implementation only returns "Bug".
		 */
		protected String getTrackerFor(int value) {
		
			return "Bug";
		}
		/**
		 * Gets the name of the priority that corresponds to the value 
		 * that has been passed as an argument to the method. 
		 * 
		 * @param value	a {@literal int} value representing the unique
		 * 				identifier of the priority.
		 * 
		 * @return  a {@link String} representing the name of the priority.
		 * 			The current implementation only returns "High".
		 */
		protected String getPriorityFor(int value) {
			
			return "High";
		}
		
		/**
		 * Gets the name of the status that corresponds to the value 
		 * that has been passed as an argument to the method. 
		 * 
		 * @param value	a {@literal int} value representing the unique
		 * 				identifier of the status.
		 * 
		 * @return  a {@link String} representing the name of the status.
		 * 			The current implementation only returns "New".
		 */
		protected String getStatusFor(int value) {
			return "New";
		}
		/**
		 * This method creates a {@link Map} instance that contains two 
		 * properties: <i>id</i> whose value is set to <i>value</i>, and
		 * <i>name</i> whose value is set to <i>name</i>.
		 * 
		 * @param name		a {@link String} representing the value of the
		 * 					<i>name</i> property in the map that is created.
		 * 
		 * @param value		a {@literal int} representing the value of the
		 * 					<i>value</i> property in the map that is created.
		 * 
		 * @return an {@link HashMap} instance that
		 */
		protected Map<String,Object> getMap(String name, Object value) {
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id",  value);
			map.put("name", name);
			
			return map;
		}
		
	}
	
	public void testBind() {
		
	}
	
	public void testRelease() {
		
	}
	/**
	 * This method tests the implementation of the logic behind the {@link RedmineIssueManager#REDMINE_ISSUE_TEMPLATE_PATH}.
	 * More precisely it does test the following cases:
	 * <ul>
	 * <li>when a template is specified via the property aforementioned, that specific template will be used</li>
	 * <li>when a non-existing template is specified, the default template will be used</li>
	 * <li>when no template is specified the default template will be used</li>
	 * </ul>
	 * 
	 */
	@Test
	public void testTemplatePath() {
		
		Properties properties = this.getSourceParameters("test-issues-template-path.properties");
		String resPath = properties.getProperty(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH);
		String localPath = null;
		
		Environment environment = this.getEnvironment(properties);
		
		Long envToken = null;
		
		try {
			
			envToken = environment.bind();
			
			// we copy to the local file system the resource template file and we we set the new
			// path to the corresponding property so that it can be properly loaded.
			//
			localPath = this.copyResource(resPath);
			properties.setProperty(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH, localPath);
			
			// we also load into local memory a copy to facilitate the verification of the test
			// scenarios
			//
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<Map<String, RedmineIssue>> reference = new TypeReference<Map<String,RedmineIssue>>() {};
			
			Map<String,RedmineIssue> expected = mapper.readValue(new File(localPath), reference);
			
			
			Issue bug = new SimpleIssue("Test Bug", "This is a test bug.", IssueType.BUG);
			Issue feature = new SimpleIssue("Test Feature", "This is a feature.", IssueType.FEATURE);
			Issue task = new SimpleIssue("Test Task", "This is a task.", IssueType.TASK);
			
			// Test 1. We load the template issue and we verify that the issue templates defined
			//         in the file are used, rather than the default ones.
			//
			this.doSubmitIssue(expected, bug, environment);
			this.doSubmitIssue(expected, feature, environment);
			this.doSubmitIssue(expected, task, environment);
			
			
			
			// Test 2. We delete the template file that is mapped to the property and we ensure that
			//         the default is used.
			//
			//
			File f = new File(localPath);
			f.delete();
			InputStream input = this.getClass().getClassLoader().getResourceAsStream(RedmineIssueManager.DEFAULT_TEMPLATE_PATH);
			expected = mapper.readValue(input, reference);
			
			this.doSubmitIssue(expected, bug, environment);
			this.doSubmitIssue(expected, feature, environment);
			this.doSubmitIssue(expected, task, environment);
			
			
			// Test 3. We create a directory and we should read the default.
			//
			//
			f.mkdir();
			
			this.doSubmitIssue(expected, bug, environment);
			this.doSubmitIssue(expected, feature, environment);
			this.doSubmitIssue(expected, task, environment);
			
			f.delete();
			
			
			// Test 4. We remote the property and we ensure that the default
			//         is read.
			//
			
			properties.remove(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH);

			this.doSubmitIssue(expected, bug, environment);
			this.doSubmitIssue(expected, feature, environment);
			this.doSubmitIssue(expected, task, environment);
			
			
		} catch(IOException ioex) {
			
			LOGGER.error("Test failed because of I/O error.", ioex);
			Assert.fail("Test failed because of I/O error.");
			
		} finally {
			
			// just ensure that the file we have locally copied from the
			// resources is deleted.
			//
			if (localPath != null) {
				
				File f = new File(localPath);
				if (f.exists()  == true) {
					f.delete();
				}
			}
			
			if (envToken != null) {
				
				environment.release(envToken);
			}
		}
	}
	/**
	 * This method tests that the following conditions:
	 * <ul>
	 * <li>
	 * the method {@link raises a {@link ParameterMissing} exception when this information
	 * is missing from the configuration properties.
	 * </li>
	 * <li>
	 * when specified to a specific value, the method generates a request against the defined
	 * url, plus the additional components that are required to target the issues section.
	 * </li>
	 * </ul>
	 * To verify the second condition we create an instance of {@link InspectableRedmineIssueManager}
	 * and we override the method {@link InspectableRedmineIssueManager#inspectRequest(HttpPost)} in
	 * order to verify the parameters of the request and eventually raise an issue.
	 */
	@Test
	public void testApiUrl() {
		
		
		// Step 0. We get the configuration that we need in order to initialise the test.
		//
		Properties properties = this.getSourceParameters("test-api-url.properties");
		EnvironmentFacade<?, ?> environment = this.getEnvironment(properties);
		Long envToken = environment.bind();
		

		// Test 1. We check that when the property {@link RedmineIssueManager#REDMINE_API_URL}
		//         is not set we do get an exception at bind time.
		
		final String apiUrl = (String) properties.remove(RedmineIssueManager.REDMINE_API_URL);
		final boolean hasTrailingSlash = apiUrl.endsWith("/");
		
		try {
		
			
			
			RedmineIssueManager manager = new RedmineIssueManager();
			Long token = null;
			try {
				
				token = manager.bind(environment);
				
				Assert.assertNull("A RedmineIssueManager instance should not be bound if the parameter '" + RedmineIssueManager.REDMINE_API_URL + "' is missing.", token);
				
				
			} finally {
				
				if (token != null) {
					
					manager.release(token);
				}
			}
			
			
			
			// Test 2. We check that when the request is properly set up, the request is
			//         issued to the selected end point.
			//
			InspectableRedmineIssueManager inspectable = new InspectableRedmineIssueManager() {
	
				protected HttpResponse inspectRequest(HttpPost post) {
					
					URI uri = post.getURI();
					
					String expected = ((hasTrailingSlash == true) ? apiUrl : apiUrl  + "/");
					expected = String.format(RedmineIssueManager.REDMINE_API_TEMPLATE, expected);
					
					Assert.assertEquals("API url is different from expected.", expected, uri.toString());
	
					RedmineIssue source = this.getPostedIssue(post);
					
					return this.createResponse(post, 201, "Created.", source);
				}
			};
			
			properties.setProperty(RedmineIssueManager.REDMINE_API_URL, apiUrl);
	
		
			// this method will take care of triggering the submission
			// and therefore executing the validation method that has
			// been plugged in the inspectable instance.
			//
			this.doSubmitIssue(inspectable, new SimpleIssue("Test Issue"), environment, null);
			

			// Test 3. We add a trailing slash if missing or remove it if is present.
			//
			final String anotherApiUrl = (hasTrailingSlash ? apiUrl.substring(0, apiUrl.length() - 1) : apiUrl + "/");
			properties.setProperty(RedmineIssueManager.REDMINE_API_URL, anotherApiUrl);
			
			
			 inspectable = new InspectableRedmineIssueManager() {
				
				protected HttpResponse inspectRequest(HttpPost post) {
				
					URI uri = post.getURI();
					
					String expected = ((hasTrailingSlash == false) ? anotherApiUrl : anotherApiUrl  + "/");
					expected = String.format(RedmineIssueManager.REDMINE_API_TEMPLATE, expected);
					
					Assert.assertEquals("API url is different from expected.", expected, uri.toString());

					RedmineIssue source = this.getPostedIssue(post);
					
					return this.createResponse(post, 201, "Created.", source);
				}
			};
			
			this.doSubmitIssue(inspectable, new SimpleIssue("Test Issue"), environment, null);
			
		
		} finally {
			
			if (envToken != null) {
				
				environment.release(envToken);
			}
		}
		
		

		
		
	}
	/**
	 * This method tests that the property {@link RedmineIssueManager#REDMINE_API_KEY} is
	 * properly used to compose the request made to the <i>Redmine</i> installation. The
	 * following cases are explored:
	 * <ul>
	 * <li>the property is not set, and the expected behaviour is not to find the header
	 * containing the information about the API key</li>
	 * <li>the property is set, and the expected behaviour is to find the header containing
	 * the information about the API key, with the value that was set.</li>
	 * </ul>
	 */
	@Test
	public void testApiKey() {
		
		// Step 0. We get the configuration that we need in order to initialise the test.
		//
		Properties properties = this.getSourceParameters("test-api-key.properties");
		EnvironmentFacade<?, ?> environment = this.getEnvironment(properties);
		Long envToken = environment.bind();
		
		try {
		
			// Test 1. We check that when the property {@link RedmineIssueManager#REDMINE_API_KEY}
			//         is not set we do not find any header.
			
			final String expected = properties.getProperty(RedmineIssueManager.REDMINE_API_KEY);
			
			InspectableRedmineIssueManager inspectable = new InspectableRedmineIssueManager() {
	
				protected HttpResponse inspectRequest(HttpPost post) {
					
					Header header = post.getFirstHeader(RedmineIssueManager.REDMINE_API_KEY_HEADER);
					String actual = header.getValue();
					
					Assert.assertEquals("API key header does not match.", expected, actual);
					
					RedmineIssue source = this.getPostedIssue(post);
	
					return this.createResponse(post, source == null ? 500 : 201, source == null ? "Internal server error" : "Created.", source);
				}
			};
			
			this.doSubmitIssue(inspectable, new SimpleIssue("test"), environment, null);
			
			
			// Test 2. We check that when the property it is not set, we do not get any default
			//		   apiKey.
			
			properties.remove(RedmineIssueManager.REDMINE_API_KEY);
			
			inspectable = new InspectableRedmineIssueManager() {
	
				protected HttpResponse inspectRequest(HttpPost post) {
					
					Header header = post.getFirstHeader(RedmineIssueManager.REDMINE_API_KEY_HEADER);
					Assert.assertNull("API key header should not be present.", header);

					RedmineIssue source = this.getPostedIssue(post);
	
					return this.createResponse(post, source == null ? 500 : 201, source == null ? "Internal server error" : "Created.", source);
				}
			};
			
			this.doSubmitIssue(inspectable, new SimpleIssue("test"), environment, null);

		
		} finally {
			
			if (envToken != null) {
				
				environment.release(envToken);
			}
		}

	}
	/**
	 * <p>
	 * This method tests the behaviour of the configuration parameter {@link RedmineIssueManager#REDMINE_ISSUE_BUG}.
	 * This parameter is used to override the default value that is mapped for the identifier of the <i>Bug</i> 
	 * tracker in the target <i>Redmine</i> installation by default is set to {@link RedmineIssueManager#DEFAULT_BUG_ID}.
	 * </p>
	 * <p>
	 * The method tests two scenarios:
	 * <ul>
	 * <li>
	 * The {@link RedmineIssueManager#REDMINE_ISSUE_BUG} property, is set. Therefore, when we a {@link IssueType#BUG} is
	 * submitted, this should correspond to an instance of {@link RedmineIssue} whose value of {@link RedmineIssue#getTrackerId()}
	 * is equal to the property value set.
	 * </li>
	 * <li>
	 * The {@link RedmineIssueManager#REDMINE_ISSUE_BUG} property is not set. Therefore, when a {@link IssueType#BUG} is submitted
	 * this should correspond to an instance of {@link RedmineIssue} whose value of {@link RedmineIssue#getTrackerId()} is equal to
	 * the the property {@link RedmineIssueManager#DEFAULT_BUG_ID}.
	 * </li>
	 * </p>
	 */
	@Test
	public void testIssueBug() {
		

		this.testItemType("test-issue-bug.properties", IssueType.BUG, RedmineIssueManager.REDMINE_ISSUE_BUG, RedmineIssueManager.DEFAULT_BUG_ID);
		
	}
	/**
	 * <p>
	 * This method tests the behaviour of the configuration parameter {@link RedmineIssueManager#REDMINE_ISSUE_FEATURE}.This parameter 
	 * is used to override the default value that is mapped for the identifier of the <i>Feature</i> tracker in the target <i>Redmine</i> 
	 * installation by default is set to {@link RedmineIssueManager#DEFAULT_FEATURE_ID}.
	 * </p>
	 * <p>
	 * The method tests two scenarios:
	 * <ul>
	 * <li>
	 * The {@link RedmineIssueManager#REDMINE_ISSUE_FEATURE} property, is set. Therefore, when we a {@link IssueType#FEATURE} is
	 * submitted, this should correspond to an instance of {@link RedmineIssue} whose value of {@link RedmineIssue#getTrackerId()}
	 * is equal to the property value set.
	 * </li>
	 * <li>
	 * The {@link RedmineIssueManager#REDMINE_ISSUE_FEATURE} property is not set. Therefore, when a {@link IssueType#FEATURE} is submitted
	 * this should correspond to an instance of {@link RedmineIssue} whose value of {@link RedmineIssue#getTrackerId()} is equal to
	 * the the property {@link RedmineIssueManager#DEFAULT_FEATURE_ID}.
	 * </li>
	 * </p>
	 */
	@Test
	public void testIssueFeature() {

		this.testItemType("test-issue-feature.properties", IssueType.FEATURE, RedmineIssueManager.REDMINE_ISSUE_FEATURE, RedmineIssueManager.DEFAULT_FEATURE_ID);		
	}
	/**
	 * <p>
	 * This method tests the behaviour of the configuration parameter {@link RedmineIssueManager#REDMINE_ISSUE_WORK_ITEM}.This parameter 
	 * is used to override the default value that is mapped for the identifier of the <i>Work Item</i> tracker in the target <i>Redmine</i> 
	 * installation by default is set to {@link RedmineIssueManager#DEFAULT_WORK_ITEM_ID}.
	 * </p>
	 * <p>
	 * The method tests two scenarios:
	 * <ul>
	 * <li>
	 * The {@link RedmineIssueManager#REDMINE_ISSUE_WORK_ITEM} property, is set. Therefore, when we a {@link IssueType#TASK} is
	 * submitted, this should correspond to an instance of {@link RedmineIssue} whose value of {@link RedmineIssue#getTrackerId()}
	 * is equal to the property value set.
	 * </li>
	 * <li>
	 * The {@link RedmineIssueManager#REDMINE_ISSUE_WORK_ITEM} property is not set. Therefore, when a {@link IssueType#TASK} is submitted
	 * this should correspond to an instance of {@link RedmineIssue} whose value of {@link RedmineIssue#getTrackerId()} is equal to
	 * the the property {@link RedmineIssueManager#DEFAULT_WORK_ITEM_ID}.
	 * </li>
	 * </p>
	 */
	@Test
	public void testIssueWorkItem() {

		this.testItemType("test-issue-work-item.properties", IssueType.TASK, RedmineIssueManager.REDMINE_ISSUE_WORK_ITEM, RedmineIssueManager.DEFAULT_WORK_ITEM_ID);	
	}
	/**
	 * This method tests the implementation of the logic controlled by the property {@link RedmineIssueManager#REDMINE_ISSUE_CATEGORY_ID}. 
	 * The expected behaviour is the following: 
	 * <ul>
	 * <li>when the property is set to a specific value, all the issues submitted to the <i>Redmine</i> installation, should have the property
	 * accessed by {@link RedmineIssue#getCategoryId()} equal to that value</i>
	 * <li>when the property is not set, the issues submitted to the <i>Redmine</i> installation, should have the property accessed by
	 * {@link RedmineIssue#getCategoryId()} equal to the original value that matches the one set in the template configured with the
	 * {@link RedmineIssueManager} instance</li>
	 * </ul>
	 * The method uses a custom property file <i>test-issue-category-id.properties</i> and a corresponding issues template file
	 * <i>templates/test-issue-category-id.json</i> that are both store in the test resources folder.
	 */
	@Test
	public void testIssueCategoryId() {
		
		this.testItemOverrideProperty("test-issue-category-id.properties", RedmineIssueManager.REDMINE_ISSUE_CATEGORY_ID, "getCategoryId");
		
	}
	/**
	 * This method tests the implementation of the logic controlled by the property {@link RedmineIssueManager#REDMINE_ISSUE_PRIORITY_ID}. 
	 * The expected behaviour is the following: 
	 * <ul>
	 * <li>when the property is set to a specific value, all the issues submitted to the <i>Redmine</i> installation, should have the property
	 * accessed by {@link RedmineIssue#getPriorityId()} equal to that value</i>
	 * <li>when the property is not set, the issues submitted to the <i>Redmine</i> installation, should have the property accessed by
	 * {@link RedmineIssue#getPriorityId()} equal to the original value that matches the one set in the template configured with the
	 * {@link RedmineIssueManager} instance</li>
	 * </ul>
	 * The method uses a custom property file <i>test-issue-priority-id.properties</i> and a corresponding issues template file
	 * <i>templates/test-issue-priority-id.json</i> that are both store in the test resources folder.
	 */
	@Test
	public void testIssuePriorityId() {
		
		this.testItemOverrideProperty("test-issue-priority-id.properties", RedmineIssueManager.REDMINE_ISSUE_PRIORITY_ID, "getPriorityId");
	}
	/**
	 * This method tests the implementation of the logic controlled by the property {@link RedmineIssueManager#REDMINE_ISSUE_PROJECT_ID}. 
	 * The expected behaviour is the following: 
	 * <ul>
	 * <li>when the property is set to a specific value, all the issues submitted to the <i>Redmine</i> installation, should have the property
	 * accessed by {@link RedmineIssue#getProjectId()} equal to that value</i>
	 * <li>when the property is not set, the issues submitted to the <i>Redmine</i> installation, should have the property accessed by
	 * {@link RedmineIssue#getProjectId()} equal to the original value that matches the one set in the template configured with the
	 * {@link RedmineIssueManager} instance</li>
	 * </ul>
	 * The method uses a custom property file <i>test-issue-project-id.properties</i> and a corresponding issues template file
	 * <i>templates/test-issue-project-id.json</i> that are both store in the test resources folder.
	 */
	@Test
	public void testIssueProjectId() {
		
		this.testItemOverrideProperty("test-issue-project-id.properties", RedmineIssueManager.REDMINE_ISSUE_PROJECT_ID, "getProjectId");
		
	}
	/**
	 * This method tests the implementation of the logic controlled by the property {@link RedmineIssueManager#REDMINE_ISSUE_STATUS_ID}. 
	 * The expected behaviour is the following: 
	 * <ul>
	 * <li>when the property is set to a specific value, all the issues submitted to the <i>Redmine</i> installation, should have the property
	 * accessed by {@link RedmineIssue#getStatusId()} equal to that value</i>
	 * <li>when the property is not set, the issues submitted to the <i>Redmine</i> installation, should have the property accessed by
	 * {@link RedmineIssue#getStatusId()} equal to the original value that matches the one set in the template configured with the
	 * {@link RedmineIssueManager} instance</li>
	 * </ul>
	 * The method uses a custom property file <i>test-issue-status-id.properties</i> and a corresponding issues template file
	 * <i>templates/test-issue-status-id.json</i> that are both store in the test resources folder.
	 */
	@Test
	public void testIssueStatusId() {
		
		this.testItemOverrideProperty("test-issue-status-id.properties", RedmineIssueManager.REDMINE_ISSUE_STATUS_ID, "getStatusId");
		
	}
	/**
	 * <p>
	 * This method tests the implementation of the logic that is controlled by the property {@link RedmineIssueManager#REDMINE_ISSUE_TRACKER_ID}.
	 * This property acts as a filter to restrict the types of issues that can be submitted to the <i>Redmine</i> installation to only those of
	 * the type whose tracker identifier is specified by the property. If there is no property all the types of issues are allowed.
	 * </p>
	 * <p>
	 * Therefore, the method performs two tests:
	 * <ul>
	 * <li>it first sets the property and then verifies that only those issues of the type indicated are submitted successfully</li>
	 * <li>it then removes the property and verifies that all the types of issues are being submitted</li>
	 * </ul>
	 */
	@Test
	public void testIssueTrackerId() {
		
		// Step 0. We get the configuration that we need in order to initialise the test.
		//
		Properties properties = this.getSourceParameters("test-issue-tracker-id.properties");
		
		String path = properties.getProperty(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH);
		EnvironmentFacade<?, ?> environment = this.getEnvironment(properties);
		
		try {
			
			// this is necessary in order to be sure that the resource template
			// file used for the test, is used as template as well so that we
			// can verify that the property that customise the issue type is 
			// properly set.
			//
			path = this.copyResource(path);
			properties.put(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH, path);
			

			Long envToken = environment.bind();
			
			try {
				
				
				// Step 1. 	We check now which tracker has been selected and we ensure that
				//			the component throws exceptions if try to submit issues of other
				//			types.
				
				int values[]  = new int[] { RedmineIssueManager.DEFAULT_BUG_ID, RedmineIssueManager.DEFAULT_FEATURE_ID, RedmineIssueManager.DEFAULT_WORK_ITEM_ID };
				for(int i=0; i<values.length; i++) {
					
					final int expected = values[i];
					
					properties.setProperty(RedmineIssueManager.REDMINE_ISSUE_TRACKER_ID, "" + expected);
					
					InspectableRedmineIssueManager inspectable = new InspectableRedmineIssueManager() {

						@Override
						protected HttpResponse inspectRequest(HttpPost post) {
							
							// we just need to verify here that the issue has
							// been submitted, for the rest we are not interested
							// in the content of the issue.
							//
							RedmineIssue source = this.getPostedIssue(post);
							
							int actual = source.getTrackerId();
							
							Assert.assertEquals("The property '" + RedmineIssueManager.REDMINE_ISSUE_TRACKER_ID + "' did not filter issues during submission.", expected, actual);
							
							HttpResponse response = this.createResponse(post, source == null ? 500 : 201, source == null ? "Internal server error." : "Created.", source);
							return response;
						}
						
					};
					
					// we construct an array, of SimpleIssue. The first
					// should pass, the other two no, because they are 
					// of different type.
					
					SimpleIssue[] issues = new SimpleIssue[3];
					SimpleIssue bug = new SimpleIssue("This is a bug", "This is a test bug.", IssueType.BUG);
					SimpleIssue feature = new SimpleIssue("This is a feature", "This is a test feature.", IssueType.FEATURE);
					SimpleIssue task = new SimpleIssue("This is a task", "This is a test task.", IssueType.TASK);
					
					switch(expected) {
					
						case RedmineIssueManager.DEFAULT_BUG_ID: 
							
							issues[0] = bug;
							issues[1] = feature;
							issues[2] = task;
							
						break;
						case RedmineIssueManager.DEFAULT_FEATURE_ID:
							
							issues[0] = feature;
							issues[1] = task;
							issues[2] = bug;
							
						break;
						case RedmineIssueManager.DEFAULT_WORK_ITEM_ID:
							
							issues[0] = task;
							issues[1] = bug;
							issues[2] = feature;
							
						break;
					}
					
					// the first one has always to pass.
					//
					this.doSubmitIssue(inspectable, issues[0], environment, null);
					// the other two not.
					//
					this.doSubmitIssueWithException(inspectable, issues[1], environment, "Issues of type " + issues[1].getType() + " should trigger IssueException.", null);
					this.doSubmitIssueWithException(inspectable, issues[2], environment, "Issues of type " + issues[2].getType() + " should trigger IssueException.", null);
										
				}
				
				
				
				
				// Step 2.	We remove the property and we ensure that all the types of issues
				//			are submitted.
				
				properties.remove(RedmineIssueManager.REDMINE_ISSUE_TRACKER_ID);
				
				// 0. we define the inspectable, which really has only the responsibility to
				//	  close the loop with the caller.
				InspectableRedmineIssueManager inspectable = new InspectableRedmineIssueManager() {

					@Override
					protected HttpResponse inspectRequest(HttpPost post) {
						
						// we just need to verify here that the issue has
						// been submitted, for the rest we are not interested
						// in the content of the issue.
						//
						RedmineIssue source = this.getPostedIssue(post);
						
						HttpResponse response = this.createResponse(post, source == null ? 500 : 201, source == null ? "Internal server error." : "Created.", source);
						return response;
					}
					
				};
				
				
				// 1. we try with bug first.
				//
				SimpleIssue issue = new SimpleIssue("This is a bug", "This is a test bug", IssueType.BUG);
				this.doSubmitIssue(inspectable, issue, environment, null);
				
				// 2. we then try with feature.
				//
				issue = new SimpleIssue("This is a feature", "This is a test feature.", IssueType.FEATURE);
				this.doSubmitIssue(inspectable, issue, environment, null);
				
				// 3. we finally try with task.
				//
				issue = new SimpleIssue("This is a task", "This is a test task.", IssueType.TASK);
				this.doSubmitIssue(inspectable, issue, environment, null);
				
				
				
			} finally {
			
				if (envToken != null) {
					
					environment.release(envToken);
				}
			}
			
		} catch(IOException ioex) {
			
			LOGGER.error("Precondition failed, could not save the test issues template file to a temporary file.", ioex);
			Assert.fail("Precondition failed, could materialise the test issues template file.");
		}
		
	}
	
	/**
	 * This method tests the behaviour of the method {@link RedmineIssueManager#submitIssue(Issue)} and verifies the 
	 * following conditions:
	 * <ul>
	 * <li>A call to the method when the instance is not bound, should throw a {@link IllegalStateException}.</li>
	 * <li>A call to the method with a {@literal null} argument, should raise a {@link IllegalArgumentException}.</li>
	 * <li>A call to the method with an unmapped tracker should throw a {@link IssueException}.</li> 
	 * <li>A call to the method with a tracker that is not allowed, should throw a {@link IssueException}.</li>
	 * <li>A call to the method with a tracker that has a {@literal null} template, should throw a {@link IssueException}.</li>
	 * <li>An error while submitting the issue to the remote <i>Redmine</i> installation, should throw a {@link IssueException}.</li>
	 * <li>A return code different from 201 from the remote <i>Redmine</i> installation, should throw a {@link IssueException}.</li>
	 * </ul>
	 * In case of an issue that is properly configured, the remote <i>Redmine</i> installation should return a successful
	 * message (201) and the detailed issue added to the tracker.
	 */
	@Test
	public void testSubmitIssue() throws IssueException {
		
		Issue bug = new SimpleIssue("Test Bug,", "This is a test bug.", IssueType.BUG);
		
		
		// Test 1. RedmineIssueManager.submitIssue(...) invoked before binding, should throw IllegalStateException
		//
		RedmineIssueManager manager = new RedmineIssueManager();
		try {
			
			manager.submitIssue(bug);
			Assert.fail("Unbound RedmineIssueManager should throw IllegalStateException, when called before being bound.");
			
		} catch(IllegalStateException isex) {
			
			LOGGER.debug("IllegalStateException caught, test passed.");
		} 
		
		
		// ok now we need to load the environment because we need it to test the
		// other scenarios.
		//
		Properties properties = this.getSourceParameters("test-submit-issue.properties");
		
		// we save this for later.
		//
		String templatePath = (String) properties.remove(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH);

		Environment environment = this.getEnvironment(properties);
		Long envToken = null;
		
		
		try {
			
			envToken = environment.bind();

			
			// Test 2. RedmineIssueManager.submitIssue(...) invoked with a null argument, should throw IllegalArgumentException
			//
			
			Long token = null;
			
			try {
				
				Issue task = null;
				token = manager.bind(environment);
				
				manager.submitIssue(task);
				Assert.fail("Unbound RedmineIssueManager should throw IllegalArgumentException, when called with a null argument.");
				
			} catch(IllegalArgumentException isex) {
				
				LOGGER.debug("IllegalArgumentException caught, test passed.");
			} finally {
				
				if (token != null) {
					
					manager.release(token);
				}
			}
		

			
			// Test 3. RedmineIssueManager.submitIssue(...) invoked with a unmapped tracker, should throw IssueException
			//

			// with the default settings, the OTHER issue type is not mapped.
			// we can use this for testing the scenario.
			
			this.doSubmitIssueWithException(manager, 
											new SimpleIssue("Unmapped", "This is an issue with an unmapped type.", IssueType.OTHER), 
											environment, "An issue with an unmapped tracker should throw IssueException (type: " + IssueType.OTHER + ").", 
											null);
		
			
			
			// Test 4. RedmineIssueManager.submitIssue(....) invoked with a type that maps to a tracker that is not allowed, 
			//		   should throw IssueException
			
			// we allow only features
			//
			properties.setProperty(RedmineIssueManager.REDMINE_ISSUE_TRACKER_ID, "2");

			// we refresh the manager instance
			//
			manager = new RedmineIssueManager();
			this.doSubmitIssueWithException(manager, bug, environment, "An issue with a not allowed type, should throw IssueException.", null);
			
			
			// Test 5. RedmineIssueManager.submitIssue(....) invoked with an issue that has a type mapping to null template
			//         should throw an IssueException.
			//
			properties.remove(RedmineIssueManager.REDMINE_ISSUE_TRACKER_ID);
			
			String localPath = this.copyResource(templatePath);
			
			// we set back the local template path. This template has a null template for the
			// bug template (the "1" that maps the bug is not present as a key) and for the
			// feature template (the "2" maps to a null) instance.
			//
			properties.setProperty(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH, localPath);
			
			this.doSubmitIssueWithException(manager, bug, environment, "An issue with a type that is not mapped but allowed should throw IssueException.", null);
			
			this.doSubmitIssueWithException(manager,
											new SimpleIssue("Test Feature", "This is a test feature.", IssueType.FEATURE), 
											environment, "An issue with a type that is mapped to a null template should throw IssueException.", null);
			
			
			properties.remove(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH);
			
			// Test 6. RedmineIssueManager.submitIssue(....) invoked with a valid Issue, but whose execution generates
			//         an error in the interaction with the remote Redmine installation, should throw an IssueException
			
			InspectableRedmineIssueManager inspectable = new InspectableRedmineIssueManager() {
				
				@Override
				protected HttpResponse inspectRequest(HttpPost post) {
					
					// we throw an exception, to simulate that there is an error
					// 
					
					throw new RuntimeException("Error while communicating with the Redmine installation.");
				}
				
			};
			
			this.doSubmitIssueWithException(inspectable, bug, environment, "An exception with the remote Redmine installation should throe IssueException.", null);
			
			
			// Test 7. RedmineIssueManager.submitIssue(....) invoked with a valid Issue, but whose exceution generates
			//		   an HTTP code different from 201, it should generate an error.
			
			
			inspectable = new InspectableRedmineIssueManager() {
				
				@Override
				protected HttpResponse inspectRequest(HttpPost post) {
					
					// we throw an exception, to simulate that there is an error
					// 
					
					return this.createResponse(post, 404, "Not Found.", null);
				}
				
			};
			
			this.doSubmitIssueWithException(inspectable, bug, environment, "A return code different from 201 from the Redmine installation should throw IssueException.", null);

			
			
			// Test 8. RedmineIssueManager.submitIssue(...) invoked with a valid issue should end with no exceptions 
			//		   if the remote Redmine installation returns 201.
			
			
			inspectable = new InspectableRedmineIssueManager() {
				
				@Override
				protected HttpResponse inspectRequest(HttpPost post) {
					
					// we throw an exception, to simulate that there is an error
					// 
					RedmineIssue source = this.getPostedIssue(post);
					
					return this.createResponse(post, 201, "Created.", source);
				}
				
			};
			
			this.doSubmitIssue(inspectable, bug, environment, null);
			
			
		} catch(IOException ioex) {
			
			LOGGER.error("Test failed, could not save the resource issue template file into the local file system.", ioex);
			Assert.fail("Test failed because of I/O errors while storing template file into the local file system.");
			
		} finally {
			
			
			if (envToken != null) {
				
				environment.release(envToken);
			}
		}

	}
	
	/**
	 * <p>
	 * This method tests the implemented behaviour of the {@link RedmineIssueManager#REDMINE_API_SSL_TRUST_PATH}. When
	 * set, this parameter is used to load a custom trust store that is then used to verify the server certificate that
	 * is sent by the <i>Redmine</i> installation.
	 * </p>
	 * <p>
	 * The scenarios tested in this method are the following:
	 * <ul>
	 * <li>
	 * when the parameter is not specified, the manager will use the default trust store and will not be able to accept
	 * the server certificate sent by the <i>Redmine</i> installation prepared for the test that has a certificate signed
	 * by a CA not present in the default store.
	 * </li>
	 * <li>
	 * when the parameter i specified, the manager will use the specified one that contains the root certificate that
	 * signed the certificate sent by the <i>Redmine</i> installation prepared for the test. In this case the submission
	 * of the test should be successful.
	 * </li>
	 * </ul>
	 * </p>
	 */
	// TODO: add this test again when the SSL issues are solved - Java 8 Bug SNI ?
	// @Test
	public void testSSLwithTrustStore() {
		
		
		LOGGER.info("SSLwithTrustStore - Default Store");
		
		// this will be used to start all the environments.
		//
		Long envToken = null;


		SimpleIssue testIssue = new SimpleIssue("Sample Issue", "This is the description of the sample issue.", IssueType.BUG);
		
		// 1. We do not provide anything, the connection to the server should fail because
		//    we do not have the certificate registered.
		
		Properties properties = this.getSourceParameters("test-ssl-ca-signed.properties");
		
		String redmineTrustStore = (String) properties.remove(RedmineIssueManager.REDMINE_API_SSL_TRUST_PATH);
		String redmineTrustPwd  = (String) properties.remove(RedmineIssueManager.REDMINE_API_SSL_TRUST_PASSWORD);
		
		properties.remove(RedmineIssueManager.REDMINE_API_SSL_STRICT);
		
		Environment env = this.getEnvironment(properties);

		try {
			
			envToken = env.bind();
			
			// we use this in case we want to debug in details the SSL, but otherwise
			// it is completely identical to the base class RedmineIssueManager. If we
			// want to debug the code, set debug to true.
			// 
			RedmineIssueManager manager = new SSLDebugRedmineIssueManager(false);
			
			this.doSubmitIssueWithException(manager, testIssue, env, 
											"RedmineIssyeManager.submitIssue(Issue) should have failed when connecting to a server whose Root CA is not present in trust store.", 
											null);
		
		} finally {
			
			if (envToken != null) {
				
				env.release(envToken);
			}
		}
		

		LOGGER.info("SSLwithTrustStore - Custom Store");
		
		// 2. We do add a trust store and we set the proper password and we check that this
		//    time we should be able to accept the certificate that is sent by the server
		//    whose root CA is in our local turust store.
		
		String localTrustStore = null;
		try {
			
			// we need to take the resource file copy to local disk and fix the path to where we
			// it has been saved (temporary location) so that we can make the test portable. Once
			// we have copied the file we update the environment's underlying properties set.
			//
			localTrustStore = this.copyResource(redmineTrustStore);

			properties.setProperty(RedmineIssueManager.REDMINE_API_SSL_TRUST_PATH, localTrustStore);
			properties.setProperty(RedmineIssueManager.REDMINE_API_SSL_TRUST_PASSWORD, redmineTrustPwd);
			
			try {

				env = this.getEnvironment(properties);
				envToken = env.bind();
				
				// we use this in case we want to debug in details the SSL, but otherwise
				// it is completely identical to the base class RedmineIssueManager. If we
				// want to debug the code, set debug to true.
				// 
				RedmineIssueManager manager = new SSLDebugRedmineIssueManager(false);
				this.doSubmitIssue(manager, testIssue, env, "RedmineIssueManager.submitIssue(Issue): must not fail with a local trust store whose CA root is present [Message: %1$s]");
				
			} finally {
				
				if (envToken != null) {
					
					env.release(envToken);
					envToken = null;
				}
			}
			
		} catch(IOException ioex) {
			
			Assert.fail("Could not copy '" + redmineTrustStore + "' to local file system, test aborted [Message: " + ioex.getMessage() + "].");	
		
		} finally {

			if (localTrustStore != null) {
				
				File f = new File(localTrustStore);
				if (f.exists() == true) {	
					
					f.delete();
				}
			}
		}
	}
	
	/**
	 * <p>
	 * This method tests the implemented behaviour of the property {@link RedmineIssueManager#REDMINE_API_SSL_TRUST_PASSWORD}. This property
	 * is meant to be used with {@link RedmineIssueManager#REDMINE_API_SSL_TRUST_PATH} and contains the information about the password that
	 * is used to open the trust store.
	 * </p>
	 * <p>
	 * This method tests the following scenarios:
	 * <ul>
	 * <li>the trust store is provided but no password is given and then an exception will be generated because the trust store cannot be read.</li>
	 * <li>the trust store is provided but a wrong password is given, then an exception will be generated because the trust store cannot be read.</li>
	 * <li>the trust store is provived with the right password and the manager should be able to submit the issue with no problems.</li>
	 * </ul>
	 * </p>
	 */
	// TODO: add this test again when the SSL issues are solved - Java 8 Bug SNI ?
	// @Test
	public void testSSLwithTrustPassword() {
	
		// this will be used to start all the environments.
		//
		Long envToken = null;


		SimpleIssue testIssue = new SimpleIssue("Sample Issue", "This is the description of the sample issue.", IssueType.BUG);
		
		// 1. We do not provide anything, the connection to the server should fail because
		//    we do not have the certificate registered.
		
		Properties properties = this.getSourceParameters("test-ssl-ca-signed.properties");

		String redmineTrustStore = properties.getProperty(RedmineIssueManager.REDMINE_API_SSL_TRUST_PATH);
		String redmineTrustPwd  = (String) properties.remove(RedmineIssueManager.REDMINE_API_SSL_TRUST_PASSWORD);
		

		Environment env = this.getEnvironment(properties);
		
		String localTrustStore = null;
		
		try {
			
			// we need to take the resource file copy to local disk and fix the path to where we
			// it has been saved (temporary location) so that we can make the test portable. Once
			// we have copied the file we update the environment's underlying properties set.
			//
			localTrustStore = this.copyResource(redmineTrustStore);
			properties.setProperty(RedmineIssueManager.REDMINE_API_SSL_TRUST_PATH, localTrustStore);
			
			envToken = env.bind();
			
			// we use this in case we want to debug in details the SSL, but otherwise
			// it is completely identical to the base class RedmineIssueManager. If we
			// want to debug the code, set debug to true.
			// 
			RedmineIssueManager manager = new SSLDebugRedmineIssueManager(false);

			Long token = null;
			try {
				
				token = manager.bind(env);
				
				if (token != null) {
				
					Assert.fail("RedmineIssueManager.bind(Environment): should fail when no password is provided for trust store.");
				
				} else {
					
					LOGGER.info("RedmineIssueManager.bind(Environment): successfully returned null token on password missing.");
				}
				
			} finally {
				
				if (token != null) {
					
					manager.release(token);
					token = null;
				}
			}
			
			// 2. We do provide a wrong password, and try again. In this case we should raise an exception
			//    because we're still not able to read the trust store.
			
			
			String newPwd = redmineTrustPwd + "312131";
			properties.setProperty(RedmineIssueManager.REDMINE_API_SSL_TRUST_PASSWORD, newPwd);
			
			// we use this in case we want to debug in details the SSL, but otherwise
			// it is completely identical to the base class RedmineIssueManager. If we
			// want to debug the code, set debug to true.
			// 
			manager = new SSLDebugRedmineIssueManager(false);
			try {
				
				token = manager.bind(env);
				
				if (token != null) {
					
					Assert.fail("RedmineIssueManager.bind(Environment): should fail when a wrong password is provided for a trust store.");
				
				} else {
					
					LOGGER.info("RedmineIssueManager.bind(Environment): has successfully thrown an exception when trying to configure a trust store requiring a password (pwd=[wrong]).");
				}
				
			} finally {
				
				if (token != null) {
					
					manager.release(token);
					token = null;
				}
			}
			
			
			// 3. We do provide the right password, and in this case we should be able to submit the issue
			//    because we can read the trust store, which is configured with the certificate root used
			//    to sign the server certficate of the test installation.
			
			properties.setProperty(RedmineIssueManager.REDMINE_API_SSL_TRUST_PASSWORD, redmineTrustPwd);
			
			// we use this in case we want to debug in details the SSL, but otherwise
			// it is completely identical to the base class RedmineIssueManager. If we
			// want to debug the code, set debug to true.
			// 
			manager = new SSLDebugRedmineIssueManager(false);			
			this.doSubmitIssue(manager, testIssue, env, "RedmineIssueManager.submitIssue(Issue): should be able to successfully submit an issue with a properly configured trust store (pwd=[right]) [Message: %1$s].");
			
		} catch(IOException ioex) {
			
			Assert.fail("Could not copy '" + redmineTrustStore + "' to local file system, test aborted [Message: " + ioex.getMessage() + "].");
			
		} finally {
			
			if (envToken != null) {
				
				env.release(envToken);
				envToken = null;
			}
			
			
			if (localTrustStore != null) {
				
				File f = new File(localTrustStore);
				if (f.exists() == true) {	
					
					f.delete();
				}
			}
		}
	}
	
	
	/**
	 * <p>
	 * This method tests the implemented behaviour of the property {@link RedmineIssueManager#REDMINE_API_SSL_STRICT}. This
	 * flag controls whether the {@link RedmineIssueManager} instance should accept self-signed certificates or not. The
	 * method tests the following scenarios:
	 * <ul>
	 * <li>
	 * a <i>Redmine</i> installation providing self-signed certificates is used for testing, and the parameter is not specified.
	 * The default behaviour is to reject self-signed certificates and throw an exception.
	 * </li>
	 * <li>
	 * a <i>Redmine</i> installation providing a self-signed certificate is used for testing, and the parameter is set to {@literal true}.
	 * The behaviour should be the same as the one in the previous case.
	 * </li>
	 * <li>
	 * a <i>Redmine</i> installation providing a self-signed certificate is used for testing, and the parameter is set to {@literal false}.
	 * The manager should accept the certificate and successfully submit the issue.
	 * </li>
	 * </ul>
	 * The three settings for the flag are repeated with a trust store containing a CA root certificate that is used to sign the
	 * certificate sent by the test <i>Redmine</i> installation. Because we do have a local copy of the CA root, in this case the
	 * value of strict does not matter.
	 * </p>
	 */
	// TODO: add this test again when the SSL issues are solved - Java 8 Bug SNI ?
	// @Test
	public void testSSLwithTrustStrict() {
		
		// this will be used to start all the environments.
		//
		Long envToken = null;

		SimpleIssue testIssue = new SimpleIssue("Sample Issue", "This is the description of the sample issue.", IssueType.BUG);	
		
		// 1. We do not provide anything, the connection to the server should fail because
		//    we do not have the certificate registered.
		
		Properties properties = this.getSourceParameters("test-ssl-self-signed.properties");
		
		String redmineTrustStore = properties.getProperty(RedmineIssueManager.REDMINE_API_SSL_TRUST_PATH);
		Environment env = this.getEnvironment(properties);
		String localTrustStore = null;
		
		try {
			
			// we need to take the resource file copy to local disk and fix the path to where we
			// it has been saved (temporary location) so that we can make the test portable. Once
			// we have copied the file we update the environment's underlying properties set.
			//
			localTrustStore = this.copyResource(redmineTrustStore);
			properties.setProperty(RedmineIssueManager.REDMINE_API_SSL_TRUST_PATH, localTrustStore);
			
			envToken = env.bind();
			
			// 1.a - STRICT set to default(true) - submission should fail for self-signed server certificates.
			//
			properties.remove(RedmineIssueManager.REDMINE_API_SSL_STRICT);
			RedmineIssueManager manager = new RedmineIssueManager();
			this.doSubmitIssueWithException(manager, testIssue, env, "RedmineIssueManager.submitIssue(Issue): should fail when strict is default and the certificate is self-signed [Message: %1$s].", null);
			
			// 1.b - STRICT set to true - submission should fail for self-signed server certificates.
			//
			properties.put(RedmineIssueManager.REDMINE_API_SSL_STRICT, true);
			manager = new RedmineIssueManager();
			this.doSubmitIssueWithException(manager, testIssue, env, "RedmineIssueManager.submitIssue(Issue): should fail when strict is true and the certificate is self-signed [Message: %1$s].", null);
			
			// 1.c - STRICT set to false - submission should be successful for self-signed certificates.
			//
			properties.put(RedmineIssueManager.REDMINE_API_SSL_STRICT, false);
			
			// we use this in case we want to debug in details the SSL, but otherwise
			// it is completely identical to the base class RedmineIssueManager. If we
			// want to debug the code, set debug to true.
			// 
			manager = new SSLDebugRedmineIssueManager(false);
			this.doSubmitIssue(manager, testIssue, env, "RedmineIssueManager.submitIssue(Issue): should complete successfully when strict is set to false and the certificate is self-signed [MEssage: %1$s].");
			
			
		} catch(IOException ioex) {
			
			Assert.fail("Could not copy '" + redmineTrustStore + "' to local file system, test aborted [Message: " + ioex.getMessage() + "].");
			
		} finally {
			
			if (envToken != null) {
				
				env.release(envToken);
				envToken = null;
			}
			if (localTrustStore != null) {
				
				File f = new File(localTrustStore);
				if (f.exists() == true) {	
					
					f.delete();
				}
			}
		}
		
		// 2. We repeat the test with a server with proper certificate whose CA root is stored in the used trust store.
		//    in this case all the attempt should pass despite the value of the strict flag.
		//
		
		properties = this.getSourceParameters("test-ssl-ca-signed.properties");
		
		redmineTrustStore = properties.getProperty(RedmineIssueManager.REDMINE_API_SSL_TRUST_PATH);
		env = this.getEnvironment(properties);
		localTrustStore = null;
		
		try {
			
			// we need to take the resource file copy to local disk and fix the path to where we
			// it has been saved (temporary location) so that we can make the test portable. Once
			// we have copied the file we update the environment's underlying properties set.
			//
			localTrustStore = this.copyResource(redmineTrustStore);
			properties.setProperty(RedmineIssueManager.REDMINE_API_SSL_TRUST_PATH, localTrustStore);
			
			envToken = env.bind();
			
			// 2.a - STRICT set to default(true) - submission should fail for self-signed server certificates.
			//
			properties.remove(RedmineIssueManager.REDMINE_API_SSL_STRICT);
			RedmineIssueManager manager = new RedmineIssueManager();
			this.doSubmitIssue(manager, testIssue, env, "RedmineIssueManager.submitIssue(Issue): should complete successfully when strict is default and the certificate is CA signed [Message: %1$s].");
			
			// 2.b - STRICT set to true - submission should fail for self-signed server certificates.
			//
			properties.put(RedmineIssueManager.REDMINE_API_SSL_STRICT, true);
			manager = new RedmineIssueManager();
			this.doSubmitIssue(manager, testIssue, env, "RedmineIssueManager.submitIssue(Issue): should complete successfully when strict is true and the certificate is CA signed [Message: %1$s].");
			
			// 2.c - STRICT set to false - submission should be successful for self-signed certificates.
			//
			properties.put(RedmineIssueManager.REDMINE_API_SSL_STRICT, false);
			
			
			// we use this in case we want to debug in details the SSL, but otherwise
			// it is completely identical to the base class RedmineIssueManager. If we
			// want to debug the code, set debug to true.
			// 
			manager = new SSLDebugRedmineIssueManager(false);
			this.doSubmitIssue(manager, testIssue, env, "RedmineIssueManager.submitIssue(Issue): should complete successfully when strict is set to false and the certificate is CA signed [Message: %1$s].");
			
			
		} catch(IOException ioex) {
			
			Assert.fail("Could not copy '" + redmineTrustStore + "' to local file system, test aborted [Message: " + ioex.getMessage() + "].");
			
		} finally {
			
			if (envToken != null) {
				
				env.release(envToken);
				envToken = null;
			}
			if (localTrustStore != null) {
				
				File f = new File(localTrustStore);
				if (f.exists() == true) {	
					
					f.delete();
				}
				
				localTrustStore = null;
			}
		}
	}
	
	/**
	 * This method tests the implementation of the logic that is controlled by <i>propertyName</i>. This identifies 
	 * a parameter that can be used in the configuration settings in order to override the default mapping to the
	 * predefined template for the given issue type. If this property is set, the corresponding issue template file
	 * is expected to have a matching entry for that value, and that entry will be used to create the instance of
	 * {@link RedmineIssue} that is submitted to the <i>Redmine</i> installation. The method executes two simple
	 * tests to verify that the logic for this property is properly implemented:
	 * <ul>
	 * <li>The first scenario sets the value of the property and checks whether the corresponding tracker identifier
	 * value is set for the {@link RedmineIssue} instance submitted to the <i>Redmine</i> installation.</li>
	 * <li>The second scenario removes the value of the property and checks whether the corresponding track identifier
	 * remains the one that representing the default setting for the given issue type.</li>
	 * </ul>
	 * 
	 * @param testCaseFile		a {@link String} representing the path to the resource file containing the initialisation
	 * 							properties for the {@link RedmineIssueManager} instance under test.
	 * @param type				a {@link IssueType} value indicating the specific type of issue being tested.
	 * @param propertyName		a {@link String} instance representing the corresponding property that is used to 
	 * 							control the mapping of the issue of type <i>type</i>
	 * @param defaultValue		a {@literal int} representing the defaut value for the mapping of issues of type <i>type</i>.
	 */
	private void testItemType(String testCaseFile, IssueType type, String propertyName, final int defaultValue) {
		
		// Step 0. We get the configuration that we need in order to initialise the test.
		//
		Properties properties = this.getSourceParameters(testCaseFile);
		
		String path = properties.getProperty(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH);
		
		try {
			
			// this is necessary in order to be sure that the resource template
			// file used for the test, is used as template as well so that we
			// can verify that the property that customise the issue type is 
			// properly set.
			//
			path = this.copyResource(path);
			properties.put(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH, path);
			
			
			
			EnvironmentFacade<?, ?> environment = this.getEnvironment(properties);
			Long envToken = environment.bind();
			
			final String issueType = type.toString();
	
			try {
				
				// Step 1. We get a value for the bug tracker has it has been set in the
				//         properties file for the test.
				
				String val = properties.getProperty(propertyName);
				final int expected = Integer.parseInt(val);
				
				InspectableRedmineIssueManager inspectable = new InspectableRedmineIssueManager() {
					
					protected HttpResponse inspectRequest(HttpPost post) {
						
	
						RedmineIssue source = this.getPostedIssue(post);
						int actual = source.getTrackerId();
		
						Assert.assertEquals("Tracker Id (" + issueType + ") is different from value set.", expected, actual);
		
						return this.createResponse(post, source == null ? 500 : 201, source == null ? "Internal server error" : "Created.", source);
					}
				};
				
				this.doSubmitIssue(inspectable, new SimpleIssue("test", "description", type), environment, null);
				
				
				
				// Step 2. We remove the value for the bug tracker and we check that the value matches
				//         the default value.
				
				properties.remove(propertyName);
				
				
				inspectable = new InspectableRedmineIssueManager() {
					
					protected HttpResponse inspectRequest(HttpPost post) {
						
	
						RedmineIssue source = this.getPostedIssue(post);
						int actual = source.getTrackerId();
		
						Assert.assertEquals("Tracker Id (" + issueType + ") is different from default value.", defaultValue, actual);
		
						return this.createResponse(post, source == null ? 500 : 201, source == null ? "Internal server error" : "Created.", source);
					}
				};
				
				this.doSubmitIssue(inspectable, new SimpleIssue("test", "description", type), environment, null);
				
				
				
				
			} finally {
				
				if (envToken != null) {
					
					environment.release(envToken);
				}
			}
		
		} catch(IOException ioex) {
			
			LOGGER.error("Could not copy template file to local directory [path: " + path + "].", ioex);
			Assert.fail("Could not execute the test, precodition failed: could not copy template file to local directory.");
		} 
	}
	/**
	 * This method checks that the {@link RedmineIssueManager} instance implement the proper logic for the given 
	 * <i>propertyName</i>. This maps to a configuration settings that alters the default value returned by the
	 * method <i>methodName</i>, for all types of issues submitted to the <i>Redmine</i> installation. For all the
	 * types of issues that are managed by the instance, the method performs this two tests:
	 * <ul>
	 * <li>if the value of <i>propertyName</i> is set via the configuration, this shall override the original value
	 * of the corresponding property in the template that is accessed by invoking <i>methodName</i></li>
	 * <li>if the value of <i>propertyName</i> is not set via the configuration, the original value of the corresponding
	 * property should be the one submitted to the <i>Redmine</i> installation.
	 * </ul>
	 * 
	 * @param testCaseFile		a {@link String} representing the path to the resource files that contains the 
	 * 							configuration properties for the {@link RedmineIssueManager} instance under test.
	 * @param propertyName		a {@link String} representing the name of the property to test.
	 * @param methodName		a {@link String} representing the name of the method that is used to access the 
	 * 							corresponding property that is controlled by <i>propertyName</i>.
	 */
	private void testItemOverrideProperty(String testCaseFile, String propertyName, String methodName) {
		
		// Step 0. We get the configuration that we need in order to initialise the test.
		//
		Properties properties = this.getSourceParameters(testCaseFile);
		
		String path = properties.getProperty(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH);
		
		try {
			
			// this is necessary in order to be sure that the resource template
			// file used for the test, is used as template as well so that we
			// can verify that the property that customise the issue type is 
			// properly set.
			//
			path = this.copyResource(path);
			properties.put(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH, path);
			

			// we keep the property value into a local variable so that we can check
			// whether it has been passed when the issue has been submitted to the
			// installation of Redmine.
			//
			String val = properties.getProperty(propertyName);
			final int expectedOverride = Integer.parseInt(val);
			
			
			// we now read those items to be sure that we have a reference point
			// that we can use to compare against the submissions we make and 
			// verify whether the policy being tested has been applied correctly.
			
			ObjectMapper mapper = new ObjectMapper();
			final Map<String, RedmineIssue> issues = mapper.readValue(new File(path), new TypeReference<Map<String,RedmineIssue>>() {});
			final String mName = methodName;
			final String pName = propertyName; 
			
			EnvironmentFacade<?, ?> environment = this.getEnvironment(properties);
			Long envToken = environment.bind();
			
			InspectableRedmineIssueManager inspectable = null;
			
			Map<String, IssueType> types = new HashMap<String,IssueType>();
			types.put(RedmineIssueManager.DEFAULT_BUG_ID + "", IssueType.BUG);
			types.put(RedmineIssueManager.DEFAULT_FEATURE_ID + "", IssueType.FEATURE);
			types.put(RedmineIssueManager.DEFAULT_WORK_ITEM_ID + "", IssueType.TASK);
	
			try {
				
				// Step 1.  One round with the override value. This should be the one that
				//			is submitted to the Redmine installation.
				//
				for(Entry<String,RedmineIssue> entry : issues.entrySet()) {
					
					inspectable = new InspectableRedmineIssueManager() {
						
						protected HttpResponse inspectRequest(HttpPost post)  {
							
							try {
								
								// we retrieve the instance that we need to check
								// and we invoke the method that gives us access
								// to the submitted value of the tested property.
								//
								RedmineIssue source = this.getPostedIssue(post);
								Method getter = source.getClass().getMethod(mName);
								
								Object result = getter.invoke(source);
								
								// we cast the value back to the integer and then
								// we extract the underlying value.
								//
								Integer theValue = (Integer) result;
								int actual = theValue.intValue();
								
								Assert.assertEquals("The value of '" + pName + "' has not been set for issue of type '" + source.getTrackerId() + "'.", expectedOverride, actual);
				
								return this.createResponse(post, source == null ? 500 : 201, source == null ? "Internal server error" : "Created.", source);
							
							} catch(IllegalAccessException |  IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
								
								return this.createResponse(post, 500, "Internal server error.", null);
							}
						}
					};
					
					String key = entry.getKey();
					IssueType type = types.get(key);

					this.doSubmitIssue(inspectable, new SimpleIssue("test", "description", type), environment, null);
					
				}
				
				// Step 2.	One round with the original value. This is achieved by removing
				//			the value from the properties and re-running the test. In this
				//			case we expect to obtain the original value.
				for(Entry<String,RedmineIssue> entry : issues.entrySet()) {
					
					final String key = entry.getKey();

					
					inspectable = new InspectableRedmineIssueManager() {
						
						protected HttpResponse inspectRequest(HttpPost post) {
							
							try {
								
								// we retrieve the instance that we need to check
								// and we invoke the method that gives us access
								// to the submitted value of the tested property.
								//
								RedmineIssue source = this.getPostedIssue(post);
								Method getter = source.getClass().getMethod(mName);
								Object result = getter.invoke(source);
								
								// we cast the value back to the integer and then
								// we extract the underlying value.
								//
								int actual = ((Integer) result).intValue();
								
								RedmineIssue template = this.issues.get(key);
								getter = template.getClass().getMethod(mName);
								result = getter.invoke(template);
								int expected = ((Integer) result).intValue();
								
								Assert.assertEquals("The original value of '" + mName + "' has not been set for issue of type '" + source.getTrackerId() + "'.", expected, actual);
				
								return this.createResponse(post, source == null ? 500 : 201, source == null ? "Internal server error" : "Created.", source);
							
							} catch(IllegalAccessException |  IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
							
								return this.createResponse(post, 500, "Internal server error.", null);
							}
							
							
 						}
					};
					
					IssueType type = types.get(key);

					this.doSubmitIssue(inspectable, new SimpleIssue("test", "description", type), environment, null);
					
				}
				
				
			} finally {
				
				if (envToken != null) {
					
					environment.release(envToken);
				}
			}
		
		} catch(IOException ioex) {
			
			LOGGER.error("Could not copy template file to local directory [path: " + path + "].", ioex);
			Assert.fail("Could not execute the test, precodition failed: could not copy template file to local directory.");
		} 
	}
	/**
	 * This method creates a temporary local file that is used to store the content
	 * of the resource file indicated by <i>source</i>.
	 * 
	 * @param source	a {@link String} representing the path to the file to copy
	 * 					in the test resources.
	 * 
	 * @return a {@link String} representing the path to the temporary file.
	 * 
	 * @throws IOException	if there is any error while performing the operation.
	 */
	private String copyResource(String source) throws IOException {
		
		Path srcPath = Paths.get(source);
		Path tmpPath = Files.createTempFile(srcPath.getFileName().toString(), ".json");
		
		InputStream input = this.getClass().getClassLoader().getResourceAsStream(source);
		Files.copy(input, tmpPath, StandardCopyOption.REPLACE_EXISTING);
		
		String target = tmpPath.toAbsolutePath().toString();
		
		return target;
	}
	
	/**
	 * This method checks that the given <i>issue</i> when submitted to the remote <i>Redmine</i> installation,
	 * matches the template that corresponds to its type and that is stored in <i>reference</i>. The method invokes
	 * {@link RedmineIssueManagerTest#doSubmitIssue(RedmineIssueManager, Issue, Environment)} with an instance of
	 * {@link InspectableRedmineIssueManager} that is configured to verify the aforementioned condition.
	 * 
	 * @param reference	a {@link Map} implementation that maps for each tracker the settings for the {@link RedmineIssue}
	 * 					properties that needs to be used.
	 * @param issue		a {@link Issue} implementation that is used to invoke {@link RedmineIssueManager#submitIssue(Issue)}.
	 * @param env		an {@link Environment} implementation that is used to bind the {@link RedmineIssueManager} instance
	 * 					used for the test.
	 */
	private void doSubmitIssue(Map<String,RedmineIssue> reference, Issue issue, Environment env) {
		
		final Map<String,RedmineIssue> expected = reference;
		final IssueType type = issue.getType();
		final String key = (type == IssueType.BUG ? "1" : (type == IssueType.FEATURE ? "2" : "3"));
		
		InspectableRedmineIssueManager inspectable = new InspectableRedmineIssueManager() {
			
			protected HttpResponse inspectRequest(HttpPost post) {

				RedmineIssue actual = this.getPostedIssue(post);
				
				RedmineIssue expectedBug = expected.get(key);
				
				Assert.assertEquals("Issue template path (type: " + key + ") - Project Id is different.", expectedBug.getProjectId(), actual.getProjectId());
				Assert.assertEquals("Issue template path (type: " + key + ") - Priority Id is different.", expectedBug.getPriorityId(), actual.getPriorityId());
				Assert.assertEquals("Issue template path (type: " + key + ") - Status Id is different.", expectedBug.getStatusId(), actual.getStatusId());
				Assert.assertEquals("Issue template path (type: " + key + ") - Category Id is different.", expectedBug.getCategoryId(), actual.getCategoryId());
				Assert.assertEquals("Issue template path (type: " + key + ") - Tracker Id is different.", expectedBug.getTrackerId(), actual.getTrackerId());
				
				return this.createResponse(post, 201, "Created.", actual);
			}
			
		};
		
		this.doSubmitIssue(inspectable, issue, env, null);
		
	}
	
	
	/**
	 * <p>
	 * This is a helper method that is used to safely submit an issue by using the given
	 * instance of {@link RemdineIssueManager}. The intent of the method is to test the
	 * successful submission of an issue.
	 * </p>
	 * <p>
	 * The method also performs the binding and release of the <i>manager</i> instance 
	 * with the given implementation of {@link Environment} <i>env</i>.
	 * </p>
	 * 
	 * @param manager	a {@link RedmineIssueManager} implementation that will be used to
	 * 					submit <i>issue</i>.
	 * @param issue		a {@link Issue} implementation that is used for testing purposes.
	 * @param env		a {@link Environment} implementation that will be used to bind
	 * 					before (and then release after) the submitting the issue.
	 */
	private void doSubmitIssue(RedmineIssueManager manager, Issue issue, Environment env, String failMessage) {
			
		Long token = manager.bind(env);
		if (token == null) {
			
			Assert.fail("RedmineIssueManager.bind(Environment) returned null token.");
		}
		
		
		try {
			
			
			manager.submitIssue(issue);
			
		} catch(IssueException iex) {
			
			if (failMessage == null) {
				
				failMessage = "Successful submission should not fail (IssueException: %1$s).";
			}
			
			Throwable cause = iex.getCause();
			
			String mex = (cause != null ? (iex.getMessage() + " (Cause: " + cause.getMessage() + ")") : (iex.getMessage()));
				
			Assert.fail(String.format(failMessage, mex));
			
		} finally {
			
			if (token != null) {
				
				manager.release(token);
			}
			
		}
	}

	/**
	 * This method submits the given <i>issue</i> by using the passed instance of the <i>manager</i> and verifies that it
	 * receives an {@link IssueException}, optionally by also checking the <i>expectedMessage</i> if not {@literal null}.
	 * The method also manages the {@link RedmineIssueManager#bind(Environment)} and {@link RedmineIssueManager#release(long)}
	 * operations by using the passed implementation of {@link Environment} <i>env</i>.
	 * 
	 * @param manager			a {@link RedmineIssueManager} instance under test.
	 * @param issue				a {@link Issue} instance that is used for the submission.
	 * @param env				a {@link Environment} implementation containing the configuration parameters for the 
	 * 							manager instance.
	 * @param expectedMessage	a {@link String} instance, which if not {@literal null} is used as expected message for the
	 * 							{@link IssueException} raised.
	 */
	private void doSubmitIssueWithException(RedmineIssueManager manager, Issue issue, Environment env, String failMessage, String expectedMessage) {

		
		Long token = null;
		// the next two, we need to expect an issue exception.
		//
		try {
			
			token = manager.bind(env);
			manager.submitIssue(issue);
			
			Assert.fail(failMessage);
			
		} catch(IssueException iex) {
			
			// ok, good to go, we expected this to be the
			// case.
			
			if (expectedMessage != null) {
				
				String actualMessage = iex.getMessage();
				Assert.assertEquals("Got IssueException, but message is different from expected.", expectedMessage,  actualMessage);
			}
			
		} finally {
			
			if (token != null) {
				
				manager.release(token);
			}
		}
	}
	/**
	 * This method provides an implementation of the {@link Environment} interface
	 * that is used to create an instance of {@link RedmineIssueTestManager}. The 
	 * implementation of {@link Environment} that is returned to the caller is of
	 * type {@link EnvironmentFacade} and the {@link ParameterSource} component is
	 * configured with a path to the resource file that contains the initialisation
	 * properties that need to be set up in the {@link Environment} instance to
	 * perform the test.
	 * 
	 * @param testCaseFile	a {@link String} representing the path to the resource
	 * 						file containing the properties that are required for the
	 * 						test.
	 * 
	 * @return	an {@link Environment} implementation that can be used to bind
	 * 			{@link RedmineIssueManager} instances for the purpose of testing.
	 * 			The set of initialisation properties set up is defined by the 
	 * 			content of the properties file that is passed as argument.
	 */
	private EnvironmentFacade<?,?> getEnvironment(String testCaseFile) {

		Properties properties = this.getSourceParameters(testCaseFile);
		return this.getEnvironment(properties);

	}
	/**
	 * This method provides an implementation of the {@link Environment} interface
	 * that is used to create an instance of {@link RedmineIssueTestManager}. The 
	 * implementation of {@link Environment} that is returned to the caller is of
	 * type {@link EnvironmentFacade} and the {@link ParameterSource} component is
	 * configured with a path to the resource file that contains the initialisation
	 * properties that need to be set up in the {@link Environment} instance to
	 * perform the test.
	 * 
	 * @param properties 	a {@link Properties} instance that contains the initialisation
	 * 						parameters that will be configured in the {@link Environment}
	 * 						instance.	
	 * @return	an {@link EnvironmentFacade} implementation where the {@link ParameterSource}
	 * 			component is based upon {@link PropertiesParameterSource} configured with
	 * 			the given <i>properties</i> parameter, and {@link RuntimeContext} is set
	 * 			to an instance of {@link InMemoryRuntimeContext}.
	 */
	private EnvironmentFacade<?,?> getEnvironment(Properties properties) {
		
		PropertiesParameterSource source = new PropertiesParameterSource(properties);
		RuntimeContext runtime = (RuntimeContext) new InMemoryRuntimeContext(); 
		EnvironmentFacade<PropertiesParameterSource, RuntimeContext> facade = new EnvironmentFacade<PropertiesParameterSource, RuntimeContext>(source, runtime);
		
		
		return facade;
	}
	/**
	 * This method retrieves the collection of properties that are stored
	 * in the given <i>testCaseFile</i> within the test resources.
	 * 
	 * @param testCaseFile	a {@link String} representing the path to the
	 * 						resource file that contains the properties
	 * 						that will be used for testing.
	 * 
	 * @return  a {@link Properties} instance that contains the collection of
	 * 			properties that have been read from the resource file 
	 * 			<i>testCaseFile</i>.	
	 */
	private Properties getSourceParameters(String testCaseFile)  {
		
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(testCaseFile);
		
		try {
			
			Properties properties = new Properties();
			properties.load(stream);
			
			return properties;
			
		} catch(IOException error) {			
			LOGGER.error("Could not load the properties for the test: " + testCaseFile, error);
			
			return null;
		}
		
		
	}
	
	
}
