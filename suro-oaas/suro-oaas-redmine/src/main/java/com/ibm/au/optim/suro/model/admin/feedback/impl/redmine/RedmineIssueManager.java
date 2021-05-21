package com.ibm.au.optim.suro.model.admin.feedback.impl.redmine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.net.ssl.SSLContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
// HttpClient v4.5.1
//
// import org.apache.http.ssl.SSLContextBuilder;
// import org.apache.http.ssl.SSLContexts;
// import org.apache.http.ssl.TrustStrategy;
// import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//
// HttpClient v4.3.1
//
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.au.optim.suro.model.admin.feedback.Issue;
import com.ibm.au.optim.suro.model.admin.feedback.IssueException;
import com.ibm.au.optim.suro.model.admin.feedback.IssueManager;
import com.ibm.au.optim.suro.model.admin.feedback.IssueType;
import com.ibm.au.optim.suro.model.impl.AbstractSuroService;
import com.ibm.au.jaws.web.core.runtime.Environment;


/**
 * Class <b>RedmineIssueManager</b>. This class implements the {@link IssueManager} interface
 * to provide the binding capabilities to the Redmine Issue Management system. The implementation
 * enables submitting issues to a specific project in Redmine. The component is fully configurable
 * via {@link Environment} source parameters, which enable to:
 * <ul>
 * <li>select the API key if any to be used to submit the issue.</li>
 * <li>specify the remote url of the Redmine installation to be referred.</li>
 * <li>specify the templates to be used to submit the issue.</li>
 * <li>specify the project identifier that relates to the issue container.</li>
 * <li>specify the category identifier of the issue belongs to.</li>
 * <li>specify the trust store to be used to validate the server certificate in case of HTTPS.</li>
 * <li>specify the behaviour of the <i>SSL</i> connection (strict or not strict) with regards to self-signed certificates.</li>
 * </ul> 
 * 
 * @author Christian Vecchiola
 */
public class RedmineIssueManager extends AbstractSuroService implements IssueManager  {
	
	/**
	 * A {@link Logger} instance that can be used to log all the messages generate by the
	 * instances of this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RedmineIssueManager.class);
	
	/**
	 * A {@link String} contstant that contains the format of the endpoint that will be
	 * used to submit issues to the <i>Redmine</i> installation.
	 */
	public static final String REDMINE_API_TEMPLATE				=	"%sissues.json";
	/**
	 * A {@link String} constant that contains the header name for adding the API developer
	 * key that is used to submit the issues, if required. This is only required if the
	 * anonymous user or the non project member are not allowed to submit bugs.
	 */
	public static final String REDMINE_API_KEY_HEADER			=	"X-Redmine-API-Key";
	
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that
	 * is used to retrieve the information about the url of the <i>Redmine</i> installation 
	 * that will be used to submit issues.
	 */
	public static final String REDMINE_API_URL 					= 	"redmine.api.url";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that
	 * is used to retrieve the value of the API developer key (if needed) that will be used
	 * to build the authorization header for submitting issues.
	 */
	public static final String REDMINE_API_KEY					=	"redmine.api.key";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that
	 * is used to retrieve the value of the boolean flag that controls the behaviour of the
	 * SSL layer. If set to {@literal false} the SSL will accept self-signed certificates.
	 */
	public static final String REDMINE_API_SSL_STRICT			=	"redmine.api.ssl.strict";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that
	 * contains the location to the trust store that will be used to validate the certificate
	 * sent by the <i>Redmine</i> installation when establishing the connection.
	 */
	public static final String REDMINE_API_SSL_TRUST_PATH		=	"redmine.api.ssl.trust.path";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that
	 * contains the password to read the trust store that will be used to validate the certificate
	 * sent by the <i>Redmine</i> installation.
	 */
	public static final String REDMINE_API_SSL_TRUST_PASSWORD	=	"redmine.api.ssl.trust.password";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that
	 * contains the unique identifier of the project that needs to be used for all the issues 
	 * that are submitted to the <i>Redmine</i> installation by this instance.
	 */
	public static final String REDMINE_ISSUE_PROJECT_ID			=	"redmine.issue.project";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that
	 * contains the unique identifier of the status that needs to be used for all the issues 
	 * that are submitted to the <i>Redmine</i> installation by this instance.
	 */
	public static final String REDMINE_ISSUE_STATUS_ID			=	"redmine.issue.status";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that
	 * contains the unique identifier of the category that needs to be used for all the issues 
	 * that are submitted to the <i>Redmine</i> installation by this instance.
	 */
	public static final String REDMINE_ISSUE_CATEGORY_ID		=	"redmine.issue.category";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that
	 * contains the unique identifier of the priority level that needs to be used for all the 
	 * issues that are submitted to the <i>Redmine</i> installation by this instance.
	 */
	public static final String REDMINE_ISSUE_PRIORITY_ID		=	"redmine.issue.priority";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that
	 * contains the unique identifier of the tracker that will be accepted for issue submission.
	 * All the other trackers will trigger an exception when trying to submit an issue.
	 */
	public static final String REDMINE_ISSUE_TRACKER_ID			=	"redmine.issue.tracker";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that
	 * maps the path of the file that contains the issue templates that are used to submit
	 * issues to the <i>Redmine</i> installation.
	 */
	public static final String REDMINE_ISSUE_TEMPLATE_PATH		=	"redmine.issue.templatePath";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that 
	 * is used to set the tracker identifier of the tracker that will be used to compose the
	 * issue information that represents a <i>Bug</i>. 
	 */
	public static final String REDMINE_ISSUE_BUG				=	"redmine.issue.bug";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that 
	 * is used to set the tracker identifier of the tracker that will be used to compose the
	 * issue information that represents a <i>Feature</i>. 
	 */
	public static final String REDMINE_ISSUE_FEATURE			=	"redmine.issue.feature";
	/**
	 * A {@link String} constant that contains the name of the configuration parameter that 
	 * is used to set the tracker identifier of the tracker that will be used to compose the
	 * issue information that represents a <i>Work Item</i>. 
	 */
	public static final String REDMINE_ISSUE_WORK_ITEM			=	"redmine.issue.workItem";
	
	/**
	 * A {@link String} constant that contains the name of the default issue template file that
	 * can be used to create presets for the different types of issues that need to be submitted.
	 * This constant is used to identify the path in the library resources where the template
	 * file is located.
	 */
	public static final String DEFAULT_TEMPLATE_PATH			=	"issues-template.json";
	/**
	 * A {@literal int} constant that represent the unique identifier of the <i>Bug</i> tracker
	 * identifier.
	 */
	public static final int DEFAULT_BUG_ID						=	1;
	/**
	 * A {@literal int} constant that represent the unique identifier of the <i>Feature</i> tracker
	 * identifier.
	 */
	public static final int DEFAULT_FEATURE_ID					=	2;
	/**
	 * A {@literal int} constant that represent the unique identifier of the <i>Work Item</i> tracker
	 * identifier.
	 */
	public static final int DEFAULT_WORK_ITEM_ID				=	3;
	/**
	 * A {@literal int} constant that represent the value for the identifier not being set. We use this
	 * value to discriminate whether the identifiers for priority, tracker, category, and project are
	 * set in the {@link Environment} implementation. 
	 */
	public static final int ID_NOT_SET							=	0;
	
	/**
	 * A {@link String} constant that contains the name of the resource file containing the list of 
	 * protocols (one per row) that will be used for establishing SSL connections. 
	 */
	protected static final String DEFAULT_SSL_PROTOCOLS			=	"SSL_PROTOCOLS"; 

	/**
	 * This contains the mapping between issue types as defined by the interface {@link Issue} with
	 * the corresponding tracking identifiers that are used within the Redmine installation.
	 */
	protected static final Map<IssueType, Integer> TRACKER_MAP = new HashMap<IssueType, Integer>();
	
	/**
	 * A {@link String} representing the url to the <i>Redmine</i> installation that will be used for
	 * issue submission. This information is essential and cannot be omitted. 
	 */
	protected String redmineApiUrl = null;
	
	/**
	 * A {@link String} representing the API developer key that is used (if necessary) to submit 
	 * issues to the <i>Redmine</i> installation.
	 */
	protected String redmineApiKey = null;
	
	/**
	 * A {@literal int} representing the selected tracker identifier that is used to filter the
	 * values of issues that are submitted. 
	 */
	protected int trackerId = RedmineIssueManager.ID_NOT_SET;
	
	/**
	 * A {@link Map} that maps the value of each of the trackers to the corresponding {@link RedmineIssue}
	 * template that will be used to submit issues.
	 */
	protected Map<String, RedmineIssue> issues;
	
	/**
	 * A {@link Map} implementation that is used to map issue types to the corresponding tracker identifier types. This 
	 * mapping is used to enable to support a large variety of different types of trackers as the <i>Redmine</i> installation 
	 * does. The reason for introducing this indirection layer is to enable to re-map issues to any type of tracker that has 
	 * been defined in the targeted <i>Redmine</i> installation and not only the default <i>Bug</i>, <i>Feature</i>, ...
	 */
	protected Map<IssueType, String> types = null;
	
	/**
	 * A {@link HttpClient} implementation that is used to connect to the remote <i>Redmine</i> installation. This 
	 * component can be configured with all the security flags that are required to talk to the remote server if
	 * this is exposed through SSL.
	 */
	protected HttpClient client;
	
	/**
	 * A {@link ObjectMapper} instance that is used to convert {@link RedmineIssue} instances into their corresponding
	 * JSON format.
	 */
	protected ObjectMapper mapper = null;
	
	/**
	 * A {@link ClassLoader} instance that we use internally for loading
	 * resources.
	 */
	protected ClassLoader resources = this.getClass().getClassLoader();
	
	/**
	 * This method submits the given <i>issue</i> to the configured <i>Redmine</i> installation. The method first checks
	 * whether the <i>issue</i> is not {@literal null} and the checks its type by invoking {@link Issue#getType()} if the
	 * type is mapped to a corresponding tracker identifier used in <i>Redmine</i> the corresponding issue template is
	 * retrieved and the information is copied from <i>issue</i> into the template. The issue is then submitted to 
	 * <i>Redmine</i>.
	 * 
	 * @param issue	an instance of {@link Issue} containing the information about the issue to report. It cannot be {@literal null}.
	 * 
	 * @throws IllegalArgumentException		if <i>issue</i> is {@literal null}.
	 * @throws IllegalStateException		if the instance is not bound.
	 * @throws IssueException	if one of the following occurs:
	 * 							<ul>
	 * 							<li><i>issue</i> has a type that has not been mapped by a known tracker.</li>
	 * 							<li><i>issue</i> has a mapped type, but there is no template associated to the corresponding tracker.</li>
	 * 							<li><i>issue</i> has a mapped type and a template, but the selected tracker is not allowed because it
	 * 							is different from the configured tracker identifier via {@link RedmineIssueManager#REDMINE_ISSUE_TRACKER_ID}.</li>
	 * 							<li>an error occurs while trying to connect to <i>Redmine</i> and submit the issue.</li>
	 * 							<li><i>Redmine</i> does successfully process the submission of the issue (error code: != 2xx).</li>
	 * 							</ul>
	 */
	@Override
	public void submitIssue(Issue issue) throws IssueException {
		
		if (this.isBound() == false) {
			
			throw new IllegalStateException("Cannot submit an issue when the instance is unbound.");
		}

		// 1. check whether the issue is null.
		//
		if (issue == null) {
			
			throw new IllegalArgumentException("Parameter 'issue' cannot be null.");
		}
		
		// 2. ok now we need to check whether we need to submit
		//    that specific type of issue
		
		IssueType issueType = issue.getType();
		
		String tracker = this.types.get(issueType);
		if (tracker == null) {
			
			// ok we should throw an exception because we do not
			// have an associated tracker for the given issue type
			// to use 
			
			throw new IssueException("Cannot submit issues of type: " + issueType + ", no tracker associated with it.");
		}
	
		// 3. ok we have a tracker. Let's see whether we have a pre-configured tracker that limits the
		//    set of issues that can be submitted.
		//
		if (this.trackerId != RedmineIssueManager.ID_NOT_SET && !("" + this.trackerId).equals(tracker)) {
			// not allowed.
			//

			throw new IssueException("Cannot submit issues of type: " + issueType + ", associated tracker is not allowed [Allowed: " + this.trackerId + ", Mapped: " + tracker + "].");
		}
		
		// 4. ok we do have a tracker, we should check whether we have
		//    a mapped redmine issue.
		
		RedmineIssue redmineIssue = this.issues.get(tracker);
		
		if (redmineIssue == null) {
			
			throw new IssueException("Cannot submit issue of type: " + issueType + ", there is no template for associated tracker [Tracker:  " + tracker + "].");
		}
		
		// we clone it because, we do not want the templates
		// to get bloated with actual values.
		//
		redmineIssue = redmineIssue.clone();
		
		// 5. ok we have the issue now, we can set the information that
		//    are required to personalise the template for the specific
		//    issue.
		
		redmineIssue.setSubject(issue.getName());
		redmineIssue.setDescription(issue.getDescription());
		
		int httpCode;
		
		try {

			// supposed to return 201
			httpCode = this.submitIssue(redmineIssue);
			
			
		} catch(Exception ex) {
			
			throw new IssueException("Cannot submit issue - client server error.", ex);
		}
		
		if (httpCode != 201) {
			
			throw new IssueException("Cannot submit issue - client server error [code: " + httpCode + "].");
		}
	}
	
	/**
	 * <p>
	 * This method configures the component with the source parameters that are fed from the given {@link Environment} 
	 * implementation. The method first checks for the required information, such as the url of the <i>Redmine</i> 
	 * installation to target, which is mapped by {@link RedmineIssueManager#REDMINE_API_URL}. All the other parameter 
	 * are optional and can be used to tune the behaviour of the component.
	 * </p>
	 * <p>
	 * The steps are the following:
	 * <ul>
	 * <li>url and api key are retrieved</li>
	 * <li>the default trackers translation map is built, by either setting the default tracker values for the issue
	 * types or those configured by the user</li>
	 * <li>the method looks for predefied values for project, priority, status, and category attributes and overrides
	 * the corresponding values in the template</li>
	 * <li>finally the tracker filter is read (if present)</li>.
	 * </ul>
	 * Once all these parameters are set up, the method proceeds to build the {@link HttpClient} that will be used
	 * to interact with the remote installation of <i>Redmine</i>, the additional settings:
	 * <ul>
	 * <li>{@link RedmineIssueManager#REDMINE_API_SSL_STRICT}: flag that controls whether to accept self signed 
	 * certificates ({@literal false}) or not ({@literal true}).</li>
	 * <li>{@link RedmineIssueManager#REDMINE_API_SSL_TRUST_PATH}: the location of the trust store to be used to
	 * verify the certificate that is sent by the <i>Redmine</i> installation.</li>
	 * <li>{@link RedmineIssueManager#REDMINE_API_SSL_TRUST_PASSWORD}: the password required to read the store</li>
	 * </ul>
	 * Can be specified but are optional.
	 * </p>
	 * 
	 * @param environment	a {@link Environment} implementation that provides access to the shared attributes of the
	 * 						application and the configuration parameters used to set it up.
	 * @throws Exception 
	 */
	@Override
	protected void doBind(Environment environment) throws Exception {
		

		
		this.redmineApiUrl = environment.getParameter(RedmineIssueManager.REDMINE_API_URL);
		
		if (this.redmineApiUrl.endsWith("/") == false) {
			
			this.redmineApiUrl += "/";
		}
		
		LOGGER.debug("Redmine installation: " + this.redmineApiUrl);
		
		
		this.redmineApiKey = environment.getParameter(RedmineIssueManager.REDMINE_API_KEY, null);
		
		this.types = new HashMap<IssueType, String>();
		
		// we first set the types to the corresponding priority values.
		//
		int value = environment.getParameter(RedmineIssueManager.REDMINE_ISSUE_BUG, RedmineIssueManager.DEFAULT_BUG_ID);
		this.types.put(IssueType.BUG, "" + value);
		
		value = environment.getParameter(RedmineIssueManager.REDMINE_ISSUE_FEATURE, RedmineIssueManager.DEFAULT_FEATURE_ID);
		this.types.put(IssueType.FEATURE, "" + value);
		
		value = environment.getParameter(RedmineIssueManager.REDMINE_ISSUE_WORK_ITEM, RedmineIssueManager.DEFAULT_WORK_ITEM_ID);
		this.types.put(IssueType.TASK, "" + value);

		
		// ok we now try to load a template file if we have it otherwise
		// we get the default resource file.
		this.issues = this.loadIssueTemplates(environment);
		
		LOGGER.debug("Loaded issue templates: found " + this.issues.size() + " templates.");
		
		// this is where we override the default behaviour that has been 
		// setup by the template.
		//
		// 1. issue category, if the user has specified one from the properties we simply override
		//					  the value that is set in the template.
		//
		int hasSet = environment.getParameter(RedmineIssueManager.REDMINE_ISSUE_CATEGORY_ID, RedmineIssueManager.ID_NOT_SET);
		if (hasSet != RedmineIssueManager.ID_NOT_SET) {
			
			LOGGER.debug("Setting category [id: " + hasSet +"] for all the issue templates.");
			
			for(Entry<String, RedmineIssue> entry : this.issues.entrySet()) {
				
				entry.getValue().setCategoryId(hasSet);
			}
		}
		// 2. priority, if the user has specified one from the properties we simply override the 
		//				value that is set in the template.
		//
		hasSet = environment.getParameter(RedmineIssueManager.REDMINE_ISSUE_PRIORITY_ID, RedmineIssueManager.ID_NOT_SET);
		if (hasSet != RedmineIssueManager.ID_NOT_SET) {

			LOGGER.debug("Setting prioirty [id: " + hasSet +"] for all the issue templates.");
			
			for(Entry<String, RedmineIssue> entry : this.issues.entrySet()) {
				
				entry.getValue().setPriorityId(hasSet);
			}
		}
		// 3. project, if the user has specified one from the properties we simply override the 
		//			   value that is set in the template.
		//
		hasSet = environment.getParameter(RedmineIssueManager.REDMINE_ISSUE_PROJECT_ID, RedmineIssueManager.ID_NOT_SET);
		if (hasSet != RedmineIssueManager.ID_NOT_SET) {
			
			for(Entry<String, RedmineIssue> entry : this.issues.entrySet()) {
				

				LOGGER.debug("Setting project [id: " + hasSet +"] for all the issue templates.");
				
				entry.getValue().setProjectId(hasSet);
			}
		}
		// 4. status, if the user has specified one from the properties we simply override the 
		//			   value that is set in the template.
		//
		hasSet = environment.getParameter(RedmineIssueManager.REDMINE_ISSUE_STATUS_ID, RedmineIssueManager.ID_NOT_SET);
		if (hasSet != RedmineIssueManager.ID_NOT_SET) {
			
			for(Entry<String, RedmineIssue> entry : this.issues.entrySet()) {
				

				LOGGER.debug("Setting status [id: " + hasSet +"] for all the issue templates.");
				
				entry.getValue().setStatusId(hasSet);
			}
		}
		
		// ok do we have a tracker filter... ?
		//
		this.trackerId = environment.getParameter(RedmineIssueManager.REDMINE_ISSUE_TRACKER_ID, RedmineIssueManager.ID_NOT_SET);
		
		if (this.trackerId != RedmineIssueManager.ID_NOT_SET) {
			
			LOGGER.debug("Configured tracker filter [id: " + this.trackerId + "].");
		}

		// let's build and configure the client with the appropriate SSL settings to
		// interact with the remote server.
		//
		this.client = this.createClient(environment);
		
		
	}

	/**
	 * This method simply releases the all the internal data structures used to keep translation maps and issues templates.
	 * There is no other need for relinquishing resources.
	 */
	@Override
	protected void doRelease() {

		this.issues.clear();
		this.issues = null;
		
		this.types.clear();
		this.types = null;
		
		this.client = null;
		this.mapper = null;
	}
	

	/**
	 * This method first checks whether a value for the environment attribute {@link RedmineIssueManager#REDMINE_ISSUE_TEMPLATE_PATH}
	 * has been set to load the template issues that can be used to submit them to the Redmine installation. If there is not value
	 * found, the default resource pointed by {@link RedmineIssueManager#DEFAULT_TEMPLATE_PATH} is used as acollection of termplates.
	 * An {@link InputStream} instance is retrieved for the resource to read and the corresponding {@link Map} implementation that
	 * uses {@link String} to map {@link RedmineIssue} instances is deserialised with an {@link ObjectMapper}.
	 * 
	 * @param environment	a {@link Environment} implementation that provides access to shared object and configuration parameters.
	 * 
	 * @return 	a {@link Map} implementationt that maps {@link String} representing the unique identifier of the tracker used to 
	 * 			manage the issue with the corresponding preset of the {@link RedmineIssue} used to submit issues.
	 * 
	 * @throws IOException	if there is any error while readming the information about the presets from the input stream associated
	 * 						to one of the resources.
	 */
	private Map<String, RedmineIssue> loadIssueTemplates(Environment environment) throws IOException {
		
		String issueTemplatePath = environment.getParameter(RedmineIssueManager.REDMINE_ISSUE_TEMPLATE_PATH, null);
		InputStream stream = null;
		
		if (issueTemplatePath != null) {
			
			LOGGER.debug("Found custom issue template: " + issueTemplatePath);
			
			// ok we do have something, we need to load
			
			File f = new File(issueTemplatePath);
			if ((f.isFile() == true) && (f.exists() == true)) {
				
				stream = new FileInputStream(f);
			
			} else {
				
				LOGGER.error("Issue template '" + issueTemplatePath + "' does not exist or is a directory, skipping...");
			}
			
		} 
		
		if (stream == null) {
	
			LOGGER.debug("No custom issue template specified, using default template.");
	
			stream = this.resources.getResourceAsStream(RedmineIssueManager.DEFAULT_TEMPLATE_PATH);
		} 

		this.mapper = new ObjectMapper(); 
		
		// ok if we have got down to here, this means that we have managed
		// to find a valid input stream from which we can read the information
		// about the template issues.
		//
		return this.mapper.readValue(stream, new TypeReference<Map<String,RedmineIssue>>() {
		});


	}
	
	/**
	 * <p>
	 * This method creates a {@link HttpClient} instance that is used to interact with the remote <i>Redmine</i>
	 * installation. According to the configuration parameters set in the environment the client can be configured
	 * with less or more restriction for SSL.
	 * </p>
	 * <p>
	 * This method first checks whether the {@link RedmineIssueManager#REDMINE_API_SSL_TRUST_PATH} has been specified
	 * in <i>environment</i> and if defined it tries to read the trust store. With this settings it is also required
	 * to specify a password via {@link RedmineIssueManager#REDMINE_API_SSL_TRUST_PASSWORD}. Besides the trust store
	 * configuration it is also possible to control whether the SSL connection should accept self-signed certificate
	 * or not, by setting a {@literal boolean} value for {@link RedmineIssueManager#REDMINE_API_SSL_STRICT}.
	 * </p>
	 * 
	 * @param environment	an implementation of {@link Environment} that provides access to the application
	 * 						configuration parameters and the shared object already instantiated during the
	 * 						setup process.
	 * 
	 * @return 	an {@link HttpClient} implementation configured according to the parameters defined in the
	 * 			environment.
	 * 
	 * @throws UnrecoverableKeyException 	if there are errors in recovering keys from the trust store.
	 * @throws KeyManagementException		if an error occurs while configuring the {@link SSLContext} instance.
	 * @throws KeyStoreException			if any generic error with the trust store occurs.
	 * @throws NoSuchAlgorithmException		if there is no decryption algorithm that can be used to read the trust store.
	 * @throws CertificateException			if there is any exception while accessing the certificates information.
	 * @throws IOException					if there is any generic I/O error. 
	 */
	private HttpClient createClient(Environment environment) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
		
		// 1. is there a trust store?
		
		KeyStore trustStore;

		
		// we check whether the user has specified a trust store to use.
		//
		String storePath = environment.getParameter(RedmineIssueManager.REDMINE_API_SSL_TRUST_PATH, null);

		SSLContext sslContext;
		
		if (storePath != null) {

			// if there is a trust store we might also need a password to access it
			//
			String storePassword = environment.getParameter(RedmineIssueManager.REDMINE_API_SSL_TRUST_PASSWORD);
			
			LOGGER.debug("Found trust store [path: " + storePath + ", pwd: *******].");
			
			trustStore = this.loadStore(storePath, storePassword);
		

			// HttpClient v4.5.1
			//
			// SSLContextBuilder builder = SSLContextBuilder.custom();
			
			// HttpClient v4.3.1
			//
			SSLContextBuilder builder = SSLContexts.custom();
			
			// we check what is the enforced policy to be used for the certificates sent by the server. By
			// default the modality is "strict" which means it does not allow for "self-signed" certificates.
			//
			boolean isStrict = environment.getParameter(RedmineIssueManager.REDMINE_API_SSL_STRICT, true);
			
			if (trustStore != null) {
				
				if (isStrict == false) {
				
					// here we are relaxing the behaviour if the user has decided to allow
					// for self signed certificates
					//
					builder = builder.loadTrustMaterial(trustStore, new TrustSelfSignedStrategy());
					
				} else {
					
					builder = builder.loadTrustMaterial(trustStore, null);
				}
			}
		
			sslContext = builder.build();
		
		} else {
			
			// if we do not have specified the trust store, then we fall back to the
			// default implementation.
			//
			sslContext = SSLContext.getDefault();
		}
		
 

	    // Allow TLSv1 protocol only
	    //
		// HttpClient v.4.5.1
		//
		// SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1.2" }, null, new DefaultHostnameVerifier());
		

        // HttpClient v4.3.1
		//
        SSLConnectionSocketFactory sslsf = this.getFactory(sslContext);

		
	    CloseableHttpClient  httpclient = HttpClients.custom().setSSLSocketFactory(sslsf)
        						                       		  .build();
	    

		
		return httpclient;
	}
	
	/**
	 * This method loads and configures the trust store that will be used to validate the certificate
	 * that is sent by the <i>Redmine</i> installation. The resulting {@link KeyStore} implementation
	 * that is created by reading the trust store will then be used to configure the SSL connection
	 * that is used to interact with <i>Redmine</i>.
	 * 
	 * @param storePath			a {@link String} representing the path to the file containing the trust 
	 * 							store. It cannot be {@literal null}.
	 * @param storePassword		a {@link String} representing the password used to read the trust store.
	 * 							It cannot be {@literal null}.
	 * 
	 * @return	a {@link KeyStore} implementation that is configured with the 
	 * 
	 * @throws KeyStoreException			if any generic error with the trust store occurs.
	 * @throws NoSuchAlgorithmException		if there is no decryption algorithm that can be used to read the trust store.
	 * @throws CertificateException			if there is any exception while accessing the certificates information.
	 * @throws IOException					if there is any generic I/O error.
	 */
	private KeyStore loadStore(String storePath, String storePassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		// ok we need to load a trust store.
		
		KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream instream = new FileInputStream(new File(storePath));
	     
		try {
			
           trustStore.load(instream, storePassword.toCharArray());
		
		} finally {
        
			instream.close();
		}
		
		return trustStore;
	}
	
	/**
	 * This method prepares and submit the HTTP request required to submit an issue to <i>Redmine</i>. If there is
	 * an API developer key, it builds a request with the {@link RedmineIssueManager#REDMINE_API_KEY_HEADER} set to
	 * the configured developer key, and composes the specific url for issue submission by using the configured url
	 * as a baseline. It then submits the issue and parses the server response.
	 * 
	 * @param redmineIssue	an instance of {@link RedmineIssue} that contains the information to be submitted. It
	 * 						cannot be {@literal null}.
	 * 
	 * @return	an {@literal int} value representing the HTTP status code returned by <i>Redmine</i>.
	 * 
	 * @throws ClientProtocolException	if there is any protocol-related error during the submission of the request.
	 * @throws IOException				if there is any other I/O error during the interaction with the server.
	 */
	protected int submitIssue(RedmineIssue redmineIssue) throws ClientProtocolException, IOException {
		
		String submissionUrl = String.format(RedmineIssueManager.REDMINE_API_TEMPLATE, this.redmineApiUrl);

		HttpPost request = new HttpPost(submissionUrl); 
		
		if (this.redmineApiKey != null) {
		
			request.addHeader(RedmineIssueManager.REDMINE_API_KEY_HEADER, this.redmineApiKey);
		}
		
		request.addHeader("Content-Type", "application/json");
		request.addHeader("Accept", "application/json");
		
		Map<String, RedmineIssue> baggage = new HashMap<String, RedmineIssue>();
		baggage.put("issue", redmineIssue);
		
		String body = this.mapper.writeValueAsString(baggage);
		HttpEntity entity = new StringEntity(body);
		request.setEntity(entity);
		
		
		
		HttpResponse response = this.execute(request);
		
		StatusLine statusLine = response.getStatusLine();
		
		LOGGER.debug("Received response: [" + statusLine.getStatusCode() + ", " + statusLine.getReasonPhrase() + "]");
		
		return statusLine.getStatusCode();
	}
	
	
	/**
	 * This method incapsulates the call to the {@link HttpClient} implementation that has been created in order
	 * to communicate with the <i>Redmine</i> installation. In the future we might want to change the underlying
	 * implementation with the interaction with the server.
	 * 
	 * @param request					a {@link HttpPost} instance that is used to wrap all the information about
	 * 									the request. This instance should point to the URL of the <i>Redmine</i>
	 * 									installation, the various headers that we require in order to communicate
	 * 									with the server and the information about the specific issue to enter into
	 * 									the issue tracker.
	 * @return	
	 * 
	 * @throws ClientProtocolException	if the server responds with an unsupported protocol.
	 * @throws IOException				if there is a communication issue with the server.
	 * 
	 */
	protected HttpResponse execute(HttpPost request) throws ClientProtocolException, IOException {
				
		return this.client.execute(request);
		
	}
	
	/**
	 * This method creates a {@link SSLConnectionSocketFactory} instance that will be used to
	 * connect to the <i>Redmine</i> installation with the HTTP client. The method retrieves
	 * the collection of protocols to be used from the <i>SSL_PROTOCOLS</i> resource file and
	 * creates an instance of the factory configured with those protocols.
	 * 
	 * @param sslContext	a {@link SSLContext} instance that contains information about the
	 * 						execution context for SSL connections (including trust stores and
	 * 						other parameters).
	 * 
	 * @return 	a {@link SSLConnectionSocketFactory} instance that is configured to create SSL
	 * 			connections with the given <i>sslContext</i>.
	 */
	protected SSLConnectionSocketFactory getFactory(SSLContext sslContext) {
		
		String[] protocols = this.getProtocols();

		return new SSLConnectionSocketFactory(sslContext, protocols, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        
	}

	/**
	 * <p>
	 * This method returns the list of SSL protocols that are configured by the module for
	 * establishing secure connections with the <i>Redmine</i> installation. The protocols
	 * are stored in the resource file that is pointed by the constant field {@link 
	 * RedmineIssueManager#DEFAULT_SSL_PROTOCOLS}.
	 * </p>
	 * <p>
	 * The method scans the lines of the resource files and removes all the lines that start
	 * with a '#' character or empty (trimmed) lines.
	 * </p>
	 * 
	 * @return	a {@link String} array containing the list of protocols.
	 */
	protected String[] getProtocols() {
		
		InputStream stream = this.resources.getResourceAsStream(RedmineIssueManager.DEFAULT_SSL_PROTOCOLS);
		Scanner scanner = new Scanner(stream);
		List<String> lines = new ArrayList<String>();
		while(scanner.hasNextLine() == true) {
			
			String line = scanner.nextLine();
			if (line != null) {
				
				line = line.trim();
				if ((line.startsWith("#") == true) || (line.isEmpty() == true)) {
					continue;
				}
				lines.add(line);
			}
		}
		scanner.close();
		
		String[] protocols = new String[lines.size()];
		lines.toArray(protocols);
		
		return protocols;
	}
	


}