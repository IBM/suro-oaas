/**
 * 
 */
package com.ibm.au.optim.suro.model.admin.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.au.jaws.data.DataValidationException;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.jaws.web.core.runtime.impl.AbstractRuntimeService;

import com.ibm.au.optim.suro.model.admin.Manifest;
import com.ibm.au.optim.suro.model.admin.ManifestEntity;
import com.ibm.au.optim.suro.model.admin.Package;
import com.ibm.au.optim.suro.model.admin.PackageException;
import com.ibm.au.optim.suro.model.admin.PackageManager;

import com.ibm.au.optim.suro.model.control.Core;
import com.ibm.au.optim.suro.model.entities.Attachment;
import com.ibm.au.optim.suro.model.entities.DataSet;
import com.ibm.au.optim.suro.model.entities.Entity;
import com.ibm.au.optim.suro.model.entities.Model;
import com.ibm.au.optim.suro.model.entities.Template;
import com.ibm.au.optim.suro.model.store.DataSetRepository;
import com.ibm.au.optim.suro.model.store.ModelRepository;
import com.ibm.au.optim.suro.model.store.RunDetailsRepository;
import com.ibm.au.optim.suro.model.store.RunRepository;
import com.ibm.au.optim.suro.model.store.TemplateRepository;

/**
 * @author Christian Vecchiola
 *
 */
public class BasicPackageManager extends AbstractRuntimeService implements PackageManager {

	
	
	/**
	 * A {@link String} constant containing the default version when no version is set.
	 */
	public static final String DEFAULT_VERSION = "0.0.0";
	
	/**
	 * A {@link Logger} implementation that is used collect all the log messages generated
	 * by instances of this class.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicPackageManager.class);
	
	/**
	 * A {@link TemplateRepository} implementation that can be used to interact
	 * with the repository of {@link Template} instances.
	 */
	protected TemplateRepository tRepo;
	
	/**
	 * A {@link ModelRepository} implementation that can be used to interact with
	 * the repository of {@link Model} instances.
	 */
	protected ModelRepository mRepo;
	
	/**
	 * A {@link DataSetRepository} implementation that can be used to interact 
	 * with the repository of {@link DataSet} instances.
	 */
	protected DataSetRepository dRepo;
	
	/**
	 * A {@link RunRepository} implementation that can be used to interact with
	 * {@link Run} instances. This repository is primarily used for verification
	 * of updates and changes.
	 */
	protected RunRepository rRepo;
	
	/**
	 * A {@link RunDetailsRepository} implementation that can be used to interact
	 * with {@link RunDetails} instances. This repository is primarily used for
	 * verification of updates and changes.
	 */
	protected RunDetailsRepository rdRepo;
	
	/**
	 * A {@link Core} implementation that can be used to access the core services
	 * and the general configuration settings for the application that are not
	 * sourced from the {@link Environment}.
	 */
	protected Core core;
	
	/**
	 * A {@link Model} instance representing the currently active default model.
	 */
	protected Model defaultModel;
	
	/**
	 * A {@link Environment} implementation that provides access to the services
	 * and the configuration parameters for the application.
	 */
	protected Environment environment;
	
	/**
	 * A {@link ObjectMapper} instance used deserialise the entities from their
	 * JSON representation into the corresponding Java instances.
	 */
	protected ObjectMapper mapper = new ObjectMapper();
	

	@Override
	public void deploy(Package pkg) throws PackageException {
		
		if (this.isBound() == false) {
			
			throw new IllegalStateException("Package manager is not bound.");
		}
		
		if (pkg == null) {
			
			throw new IllegalArgumentException("Parameter 'pkg' cannot be null.");
		}
		
		String modelId = null;
		
		// we check the model first.
		//
		Model model = pkg.getModel();
		
		if (model != null) {
			
			modelId = this.deployModel(model);

		}
		
		List<Template> templates = pkg.getTemplates();
		if ((templates != null) && (templates.size() > 0)) {
			
			this.deployTemplates(templates, modelId);
		}
		
		
		DataSet dataSet = pkg.getDataSet();
		if (dataSet != null) {
			
			this.deployDataSet(dataSet, modelId);
		}
			
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.au.optim.suro.model.admin.PackageManager#remove(com.ibm.au.optim.suro.model.admin.Package)
	 */
	@Override
	public void remove(Package pkg) throws PackageException {

		this.remove(pkg, false);
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.au.optim.suro.model.admin.PackageManager#remove(com.ibm.au.optim.suro.model.admin.Package, boolean)
	 */
	@Override
	public void remove(Package pkg, boolean force) throws PackageException {
		
		if (this.isBound() == false) {
			
			throw new IllegalStateException("Package manager is not bound.");
		}
		
		if (pkg == null) {
			
			throw new IllegalArgumentException("Parameter 'pkg' cannot be null.");
		}

	}

	/**
	 * This method creates a {@link Package} instance from the content of the given zip file. 
	 * The method first looks into the zip file to locate the manifest file named as {@link 
	 * Manifest#DEFAULT_MANIFEST_FILE}. It then uses the manifest and the zip file to create 
	 * all the components declared in the package and resolve the relative paths.
	 * 
	 * @param zipPackage	a {@link ZipFile} containing the package. The only required 
	 * 						element of the package is the manifest file. It cannot be 
	 * 						{@literal null}.
	 * 
	 * @return 	a {@link Package} instance created from the manifest contained in the 
	 * 			zip file and the associated resources.
	 * 
	 * @throws IllegalArgumentException	if <i>path</i> is {@literal null}.
	 * 
	 * @throws PackageException 	if there is any error while materializing the package from 
	 * 								the given manifest and the reference path. These errors could 
	 * 								be due to: 
	 * 								<ul>
	 * 								<li>I/O errors, related to unreadable files references</li>
	 * 								<li>invalid manifest</li>
	 * 								<li>deserialisation errors</li>
	 * 								<li>inconsistency errors between the entity metadata and the
	 * 							 	file references</li>
	 * 								</ul>
	 * 
	 */
	@Override
	public Package getPackage(ZipFile zipPackage) throws PackageException {
		
		if (zipPackage == null) {
			
			throw new IllegalArgumentException("Parameter 'zipPackage' cannot be null.");
		}
		
		try {
			
			ZipEntry manifestEntry = zipPackage.getEntry(Manifest.DEFAULT_MANIFEST_FILE);
			if (manifestEntry == null) {
				
				throw new PackageException("The default manifest file is missing.");
			}
			
			if (manifestEntry.isDirectory() == true) {
				
				throw new PackageException("Default manifest file is a directory.");
			}
			
			Manifest manifest = null;
			
			try {
				
				InputStream stream = zipPackage.getInputStream(manifestEntry);
				manifest = this.mapper.readValue(stream, Manifest.class);
				
				
			} catch (IOException ioex) {
	
				throw new PackageException("Cannot read manifest file from zip file.", ioex);
			}
	
			
			if (manifest.isValid() == false) {
				
				throw new PackageException("The manifest is not valid. A valid manifest needs to at least declare a non-null entity.");
			}
			
	 		// ok, now we are sure that we have the information that we need. At least one of these three will be
			// not null. This implies that the Package constructor will not throw IllegalStateException.
			//
			Model model = (manifest.getModel() == null ? null : this.materialize(manifest.getModel(), zipPackage, new TypeReference<Model>() {}));
			List<Template> templates = (manifest.getTemplates() == null ? null : this.materialize(manifest.getTemplates(), zipPackage, new TypeReference<List<Template>>() {}));
			DataSet dataSet = (manifest.getDataSet() == null ? null : this.materialize(manifest.getDataSet(), zipPackage, new TypeReference<DataSet>() {}));
	
			Package pkg = new Package(manifest.getVersion(), model, templates, dataSet, manifest.getDescription());
			
			return pkg;
		
		} finally {
			
			try {
				zipPackage.close();
				
			} catch (IOException ioex) {
				
				LOGGER.error("Could not close the zip file.", ioex);
			}
		}
	}

	/**
	 * This method materialises a {@link Package} instance by reading the manifest and extracting
	 * all the required information from the given <i>path</i>.
	 * 
	 * @param path	a {@link Path} implementation that can either represent the path to the manifest
	 * 				file or the directory containing the manifest file. If the path resolves to a
	 * 				directory, the method then looks for {@link Manifest#DEFAULT_MANIFEST_FILE} in 
	 * 				it to obtain the package metadata information. It cannot be {@literal null}.
	 * 
	 * @return a {@link Package} instance that is defined by <i>path</i>
	 * 
	 * @throws IllegalArgumentException	if <i>path</i> is {@literal null}.
	 * 
	 * @throws PackageException 	if there is any error while materializing the package from the 
	 * 								given manifest and the reference path. These errors could be due 
	 * 								to: 
	 * 								<ul>
	 * 								<li>I/O errors, related to unreadable files references</li>
	 * 								<li>invalid manifest</li>
	 * 								<li>deserialisation errors</li>
	 * 								<li>inconsistency errors between the entity metadata and the file 
	 * 								references</li>
	 * 								</ul>
	 */
	@Override
	public Package getPackage(Path path) throws PackageException {
		
		if (path == null) {
			
			throw new IllegalArgumentException("Parameter 'path' cannot be null.");
		}
		
		File file = path.toFile();
		if (file.exists() == false) {
			
			throw new PackageException("Path '" + file.getPath() + "' does not exist.");
		}
		
		File baseDir = null;
		
		// at this point we assume that the path passed is
		// the path to the manifest file.
		//
		File manifestFile = file;		
		Manifest manifest = null;
		
		try {
			
			boolean isDirectory = file.isDirectory();
			if (isDirectory == true) {
				baseDir = file;
				
				manifestFile = new File(baseDir, Manifest.DEFAULT_MANIFEST_FILE);
				if (manifestFile.exists() == false) {
					
					throw new PackageException("Could not find package manifest file: " + manifestFile.getPath() + ".");
				}
				
			} else {
				
				baseDir = file.getParentFile();
				
			}
			
			// ok now that we have resolved the path and the
			// manifest file, we can deserialise the manifest
			// and create a package.
			
			manifest = this.mapper.readValue(manifestFile, Manifest.class);
			
			
		} catch (IOException | SecurityException ex) {
			
			throw new PackageException("Error while accessing the package folder.", ex);
			
		}
		
		// ok if we got here, this means that the we have found a manifest.
		// we can then continue the rest of the process by invoking the
		// method that accepts the manifest as an argument.
		
		Package pkg = this.getPackage(manifest, baseDir.toPath());
		
		return pkg;
	}

	/**
	 * This method materialises a package from the given <i>manifest</i> and the given root path.
	 * 
	 * @param manifest	a {@link Manifest} entity that contains the metadata about the package. It cannot be {@literal 
	 * 					null}.
	 * 	
	 * @param rootPath	a {@link Path} implementation representing the reference directory for the path where to resolve
	 * 					the local file references declared in the manifest. It can be {@literal null} if all the file
	 * 					references declared in the manifest are absolute.
	 * 
	 * @throws IllegalArgumentException	if <i>manifest</i> is {@literal null}.
	 * 
	 * @throws PackageException 	if there is any error while materializing the package from the given manifest and 
	 * 								the reference path. These errors could be due to: 
	 * 								<ul>
	 * 								<li>I/O errors, related to unreadable files references</li>
	 * 								<li>invalid manifest</li>
	 * 								<li>deserialisation errors</li>
	 * 								<li>inconsistency errors between the entity metadata and the file references</li>
	 * 								</ul>
	 */
	@Override
	public Package getPackage(Manifest manifest, Path rootPath) throws PackageException {

		if (manifest == null) {
			
			throw new IllegalArgumentException("Parameter 'manifest' cannot be null.");
		}
		
		if (manifest.isValid() == false) {
			
			throw new PackageException("The manifest is not valid. A valid manifest needs to at least declare a non-null entity.");
		}
		
		// we just now have to deserialise the entities into objects and composed a package out of
		// that.
		//
		Model model = (manifest.getModel() == null ? null : this.materialize(manifest.getModel(), rootPath, new TypeReference<Model>() {}));
		List<Template> templates = (manifest.getTemplates() == null ? null : this.materialize(manifest.getTemplates(), rootPath, new TypeReference<List<Template>>() {}));
		DataSet dataSet = (manifest.getDataSet() == null ? null : this.materialize(manifest.getDataSet(), rootPath, new TypeReference<DataSet>() {}));
		
		Package pkg = new Package(manifest.getVersion(), model, templates, dataSet, manifest.getDescription());
		
		return pkg;
	}

	/**
	 * <p.
	 * This method composes the default package by retrieving first the default {@link Model} instance installed 
	 * from the model repository. It then retrieves all the templates that are stored in the application that 
	 * belong to the default model, and the first {@link DataSet} instance assigned to the model. The version is 
	 * then extracted from the {@link Core#getCurrentDatabaseVersion()} value. If this value is set to {@literal 
	 * null}, it then selects the {@link BasicPackageManager#DEFAULT_VERSION}.
	 * </p>
	 * <p>
	 * This method does not load the attachments to the {@link Model} and the {@link DataSet} instances that 
	 * define the package.
	 * </p>
	 * 
	 * @return a {@link Package} instance that represents the default package.
	 */
	public Package getDefaultPackage() throws PackageException {
		
		return this.getDefaultPackage(false);
	}
	
	/**
	 * <p.
	 * This method composes the default package by retrieving first the default {@link Model} instance installed 
	 * from the model repository. It then retrieves all the templates that are stored in the application that 
	 * belong to the default model, and the first {@link DataSet} instance assigned to the model. The version is 
	 * then extracted from the {@link Core#getCurrentDatabaseVersion()} value. If this value is set to {@literal 
	 * null}, it then selects the {@link BasicPackageManager#DEFAULT_VERSION}.
	 * </p>
	 * 
	 * @param loadAttachment	a {@literal boolean} flag indicating whether to load the attachments or not.
	 * 
	 * @return	a {@link Package} instance or {@literal null} if there is no default model.
	 * 
	 * @throws PackageException	if there is any error when collecting the information about the package from the
	 * 							existing application.
	 * 
	 */
	@Override
	public Package getDefaultPackage(boolean loadAttachments) throws PackageException {

		if (this.isBound() == false) {
			
			throw new IllegalStateException("Package manager is not bound.");
		}
		
		Package pkg = null;
		
		Model model = this.mRepo.findByDefaultFlag();
		if (model != null) {

			
			String modelId = model.getId();
			
			if (loadAttachments == true) {
				
				List<Attachment> attachments = model.getAttachments();
				for(Attachment attachment : attachments) {
					
					InputStream is = this.mRepo.getAttachment(modelId, attachment.getName());
					attachment.store(is);
				}
			}
			
			List<Template> templates = this.tRepo.findByModelId(modelId);
			
			DataSet dataSet = null;
			List<DataSet> dataSets = this.dRepo.findByModelId(modelId);
			if (dataSets.size() > 0) {
				
				// [CV] NOTE: this is compliant with the same method that
				//			  is used to compose the run, from the API and
				//			  assign to it a specific dataset.
				//
				//			  See: RunApi.validate(Run run)
				//
				dataSet = dataSets.get(0);
				
				if (loadAttachments == true) {
					
					List<Attachment> attachments = dataSet.getAttachments();
					for(Attachment attachment : attachments) {
						
						InputStream is = this.dRepo.getAttachment(dataSet.getId(), attachment.getName());
						attachment.store(is);
					}
				}
			}
			
			// we need
			
			String version = this.core.getCurrentDatabaseVersion();
			
			Date now = new Date();
			
			Calendar cal = Calendar.getInstance();
			
			
			String description = String.format("Default package extracted on %tB %te, %tY (%d:%d:%d).", 
												now, now, now, 
												cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
			
			// we do not need to cater for the IllegalStateException because
			// we are sure that the model is not null, therefore the constructor
			// will not raise the Exception.
			// 
			pkg = new Package(version, model,  templates, dataSet, description);
			
		}
		
		
		return pkg;
	}

	/**
	 * This method is a callback for to initialise the package manager with the given
	 * {@link Environment} implementation. The method simply stores references to the
	 * components that later will be used within the corresponding instance fields.
	 * 
	 * @param environment	a {@link Environment} instance that provides access to the
	 * 						services loaded in the application. It is guaranteed not to
	 * 						be {@literal null}.
	 * 
	 * @throws Exception	the current implementation does not throw any exception.
	 */
	@Override
	protected void doBind(Environment environment) throws Exception {
		
		this.environment = environment;
		
		this.mRepo = (ModelRepository) this.environment.getAttribute(ModelRepository.OPTIMISATION_MODEL_REPOSITORY_INSTANCE);
		this.tRepo = (TemplateRepository) this.environment.getAttribute(TemplateRepository.TEMPLATE_REPOSITORY_INSTANCE);
		this.dRepo = (DataSetRepository) this.environment.getAttribute(DataSetRepository.DATA_SET_REPOSITORY_INSTANCE);
		this.rRepo = (RunRepository) this.environment.getAttribute(RunRepository.RUN_REPOSITORY_INSTANCE);
		this.rdRepo = (RunDetailsRepository) this.environment.getAttribute(RunDetailsRepository.DETAILS_REPOSITORY_INSTANCE);
		this.core = (Core) this.environment.getAttribute(Core.CORE_INSTANCE);
		
		
	}

	/**
	 * This method releases all the stored references to the services that have been
	 * retrieved ealy on.
	 * 
	 * @throws Exception	the current implementation does not throw any exception.
	 */
	@Override
	protected void doRelease() throws Exception {
		// TODO Auto-generated method stub
		
		this.mRepo = null;
		this.tRepo = null;
		this.rRepo = null;
		this.rdRepo = null;
		this.dRepo = null;
		
		this.core = null;
		
		this.environment = null;
	}
	
	/**
	 * This method materialises the specified <i>entity</i> into the corresponding instance of the specified
	 * generic type. The method retrieves the path that contains the definition of the entity and deserialises
	 * it into the corresponding instance. It then access the list of attachment and tries to load the binary
	 * data of each of the files identified in {@link ManifestEntity#getAttachments()} with the declared {@link 
	 * Entity#getAttachments()}.
	 * 
	 * @param entity		a {@link ManifestEntity} instance that defines the metadata about the entity to
	 * 						create. It cannot be {@literal null}.
	 * @param rootPath		a {@link Path} implementation that can be used as a base directory to resolve the 
	 * 						relative paths that are declared in <i>entity</i>.
	 * @param type			a {@link TypeReference} instance that contains information about the concrete type
	 * 						to instantiate when deserialising the entity from the file.
	 * 
	 * @return	an instance of the type specified by <i>type</i>.
	 * 
	 * @throws PackageException		if any errors occurs while trying to de-serialise the entity by using the
	 * 								information contained in <i>entity</i>. Some of the possible errors wrapped
	 * 								in this exception are:
	 * 								<ul>
	 * 								<li>IO errors related to the deserialisation.</li>
	 * 								<li>IO errors related to file and directory access.</li>
	 * 								<li>inconsistency errors between the file references and their content and
	 * 								what is declared in the <i>entity</i></li>
	 * 								</ul>
	 */
	protected <T> T materialize(ManifestEntity entity, Path rootPath, TypeReference<T> type) throws PackageException {
	
		T zombie = null;
		
		String path = entity.getPath();
		
		File file = new File(path);
		if (file.isAbsolute() == false) {
			
			if (rootPath == null) {
				
				throw new PackageException("Manifest file, references relative path: " + path + " but base path is null.");
			}
			
			file = new File(rootPath.toFile(), path);
		}
		
		if (file.exists() == false) {
			
			throw new PackageException("Resource file '" + file.toString() + "' does not exist.");
		}
		
		if (file.isDirectory() == true) {
			
			throw new PackageException("Resource file '" + file.toString() + "' is a directory.");
			
		}
	
		// at this point we do have an absolute path to the resource
		// and we are sure that is a file, therefore we can deserialise
		// it into the instance of the specified type.
		//
		try {
		
			zombie = this.mapper.readValue(file, type);
		
			
			// ok, we now check whether there are attachments
			// to load into the entity.
			List<String> attachments = entity.getAttachments();
			if ((attachments != null) && (attachments.isEmpty() == false)) {
				
				if (zombie instanceof Entity) {
					
					Entity ez = (Entity) zombie;
					
					
					List<Attachment> declared = ez.getAttachments();

					// this counter is to check that the all the attachments
					// that have been declared are loaded.
					//
					int loaded = 0;
					
					for(String attachment : attachments) {
						
						// we need to compose the file
						// together with the root directory
						//
						Path pa = rootPath.resolve(attachment);
						File fa = pa.toFile();
						if (file.isFile() == false) {
							
							throw new PackageException("Attachment '" + fa.getName() + "' to resource '" + file.toString() + "' does not exist or it is not a file.");
						}
						
						// the assumption is that the names need to
						// match with what defined in the entity.
						//
						String attachmentName = file.getName();
						
						Attachment a = ez.getAttachment(attachmentName);
						if (a == null) {
							
							throw new PackageException("Attachment '" + fa.getName() + "' is not declared for resource '" + file.toString() + "'.");
						}  
						

						// ok we found a match, let's load the binary
						// file associated with the metadata and update
						// the file size.
						//
						InputStream is = new FileInputStream(file);
						a.store(is);
						a.setContentLength(file.length());
						
						loaded++;
						
					}
					
					int toLoad = declared.size();
					if (loaded < toLoad) {
						
						throw new PackageException("Resource '" + file.toString() + "' has attachment mismatch [declared : " + toLoad + ", found: " + loaded + "].");
					}
					
				} else {
					
					LOGGER.warn("Resource file '" + file.toString() + "' declares unexpected attachments, skipping attachments setup.");
				}
			}
	
		
		} catch(IOException ioex) {
			
			throw new PackageException("Could not read package resource: '" +  file.toString() + "'.", ioex);
		}
		
		
		
		return zombie;
	}

	/**
	 * This method materialises the specified <i>entity</i> into the corresponding instance of the specified
	 * generic type. The method retrieves the path that contains the definition of the entity and deserialises
	 * it into the corresponding instance. It then access the list of attachment and tries to load the binary
	 * data of each of the files identified in {@link ManifestEntity#getAttachments()} with the declared {@link 
	 * Entity#getAttachments()}.
	 * 
	 * @param entity		a {@link ManifestEntity} instance that defines the metadata about the entity to
	 * 						create. It cannot be {@literal null}.
	 * @param zipPackage	a {@link ZipFile} that contains all the resources required to create the resource
	 * 						represented by the <i>entity</i>.
	 * @param type			a {@link TypeReference} instance that contains information about the concrete type
	 * 						to instantiate when deserialising the entity from the file.
	 * 
	 * @return	an instance of the type specified by <i>type</i>.
	 * 
	 * @throws PackageException		if any errors occurs while trying to de-serialise the entity by using the
	 * 								information contained in <i>entity</i>. Some of the possible errors wrapped
	 * 								in this exception are:
	 * 								<ul>
	 * 								<li>IO errors related to the deserialisation.</li>
	 * 								<li>IO errors related to file and directory access.</li>
	 * 								<li>inconsistency errors between the file references and their content and
	 * 								what is declared in the <i>entity</i></li>
	 * 								</ul>
	 */
	protected <T> T materialize(ManifestEntity entity, ZipFile zipPackage, TypeReference<T> type) throws PackageException {
		
		T zombie = null;
		
		String path = entity.getPath();
		
		ZipEntry entry = zipPackage.getEntry(path);
		
		
		if (entry.isDirectory() == true) {
			
			throw new PackageException("Resource file '" + entry.toString() + "' is a directory within the zip file.");
		}
		
		try {
			
			InputStream stream = zipPackage.getInputStream(entry);
			zombie = this.mapper.readValue(stream, type);
			
		} catch(IOException ioex) {
			
			throw new PackageException("Cannot deserialise the information about '" + path + "' from the zip file.", ioex);
		}
		
		// ok, now we simply have to deserialise attachments if there are ones
		// otherwise we are done.
		
		List<String> attachments = entity.getAttachments();
		if ((attachments != null) && (attachments.isEmpty() == false)) {
			
			if (zombie instanceof Entity) {
				
				Entity ez = (Entity) zombie;
				
				
				List<Attachment> declared = ez.getAttachments();

				// this counter is to check that the all the attachments
				// that have been declared are loaded.
				//
				int loaded = 0;
				
				for(String attachment : attachments) {
					
					// we need to compose the file
					// together with the root directory
					//
					ZipEntry aEntry = zipPackage.getEntry(attachment);
					

					if ((aEntry == null) || (aEntry.isDirectory() == true)) {
						
						throw new PackageException("Attachment '" + aEntry.getName() + "' to resource '" + entry.getName() + "' does not exist or it is not a file.");
					}
					
					// the assumption is that the names need to
					// match with what defined in the entity.
					//
					File fa = new File(attachment);
					String attachmentName = fa.getName();
					
					Attachment a = ez.getAttachment(attachmentName);
					if (a == null) {
						
						throw new PackageException("Attachment '" + fa.getName() + "' is not declared for resource '" + entry.toString() + "'.");
					}  
					
					try {

						// ok we found a match, let's load the binary
						// file associated with the metadata and update
						// the file size.
						//
						InputStream is = zipPackage.getInputStream(aEntry);
						
						// we cannot have multiple input stream open from the
						// same zip file so we copy all the content into a 
						// by array and put into the attachment.
						//
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						IOUtils.copy(is, bos);
						ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
						
						a.store(bis);
						long size = aEntry.getSize();
						if (size >= 0) {
							a.setContentLength(size);
						}
						loaded++;
					
					} catch(IOException ioex) {
						
						throw new PackageException("Cannot access attachment '" + fa.getName() + "' of resource '" + entry.toString() + "'.", ioex);
					}
					
				}
				
				int toLoad = declared.size();
				if (loaded < toLoad) {
					
					throw new PackageException("Resource '" + entry.toString() + "' has attachment mismatch [declared : " + toLoad + ", found: " + loaded + "].");
				}
				
			} else {
				
				LOGGER.warn("Resource file '" + entry.toString() + "' declares unexpected attachments, skipping attachments setup.");
			}
		}
		
		
		return zombie;
		
	}
	
	/**
	 * This is a utility method that is used to update or add the collection of attachments that are stored 
	 * with the given <i>model</i>. The method iterates over the list of declared attachments and for each
	 * of them it calls {@link ModelRepository#attach(Model, String, String, InputStream)}.
	 * 
	 * @param model		a {@link Model} instance containing the information about the attachments to insert 
	 * 					into the repository.	
	 */
	protected void setModelAttachments(Model model) {
		
		List<Attachment> attachments = model.getAttachments();
		if ((attachments != null) && (attachments.size() > 0)) {
			
			for(Attachment attachment : attachments) {
				
				this.mRepo.attach(model, attachment.getName(), attachment.getContentType(), attachment.getStream());
			}
		}
	}
	
	/**
	 * This is a utility method that is used to update or add the collection of attachments that are stored 
	 * with the given <i>dataSet</i>. The method iterates over the list of declared attachments and for each
	 * of them it calls {@link DataSetRepository#attach(DataSet, String, String, InputStream)}.
	 * 
	 * @param dataSet	a {@link DataSet} instance containing the information about the attachments to insert 
	 * 					into the repository.	
	 */
	protected void setDataSetAttachments(DataSet dataSet) {
		
		List<Attachment> attachments = dataSet.getAttachments();
		if ((attachments != null) && (attachments.size() > 0)) {
			
			for(Attachment attachment : attachments) {
				
				this.dRepo.attach(dataSet, attachment.getName(), attachment.getContentType(), attachment.getStream());
			}
		}
		
	}
	
	/**
	 * <p>
	 * This method deploys the given <i>model</i>. The method first checks whether the model as a unique identifier 
	 * defined. If this is defined, the model is expected to replace an existing model, which needs to be present in 
	 * the repository. If there is not model identifier defined, this is expected to be a new model. 
	 * </p>
	 * <p>
	 * Before replacing an existing model, all the child templates are retrieved and re-validated against the new model
	 * to be sure that the replacement of the new model will not cause inconsistencies with the existing applications.
	 * </p>
	 * <p>
	 * Moreover, if the model is declared as default model, the previous default model (if not the same) is changed and
	 * its default flag is set to {@literal false}
	 * </p>
	 * 
	 * @param model		a {@link Model} instance representing the model to deploy into the application.
	 * 
	 * @return	a {@link String} representing the unique identifier of the model if deployed.
	 * 
	 * @throws PackageException		if there is any error in deploying the model. These errors could be related to the
	 * 								following cases:
	 * 								<ul>
	 * 								<li>
	 * 								child templates of an existing model will not validate anymore against the new
	 * 								model passed as argument.
	 * 								</li>
	 * 								<li>
	 * 								the model is declared with an id that does not map to any existing model in the
	 * 								application.
	 * 								</li>
	 * 								</ul>
	 * 										
	 */
	protected String deployModel(Model model) throws PackageException {
		
		Model existing = null;
		
		// [CV] NOTE: we should implement this to be sure
		//            that we upload a valid model.
		//
		// model.validate();
		
		String modelId = model.getId();
		
		// the presence of an identifier indicates a replacement of the 
		// model 
		//
		if (modelId != null) {
			
			existing = this.mRepo.getItem(modelId);
		
		} else {
			
			if (model.isDefaultModel() == true) {
			
				existing = this.getDefaultModel();
			}
		}
		
		if (existing != null) {
			
			
			// we have found an existing model, this can either be
			// the default model or an existing model, which is not
			// the default.
			//
			if (modelId == null) {
				
				modelId = existing.getId();
				model.setId(modelId);
			}
			
			LOGGER.info("Found existing model for the current package (id: '" + modelId + ").");
			
			
			// ok before we check whether we update the content of the
			// model, we need to revalidate all the templates and ensure
			// that none of them is invalid with the new model.
			
			List<Template> children = this.tRepo.findByModelId(modelId);
			
			try {
			
				if (children.size() > 0) {
					for(Template template : children) {
						model.validate(template);
					}
				}
				
				if (model.isDefaultModel() == true) {
					
					
					
					// we need to check whether this replace
					// another "default" model.
					
					Model defaultModel = this.getDefaultModel();
					
					if ((defaultModel != null) && (modelId.equals(defaultModel.getId()) == false)) {
						
						LOGGER.info("Model (id: " + modelId + ") replaces existing default model (id: " + defaultModel.getId() + ").");
						
						// ok we need to replace the existing model with a new default 
						// model, we then set the default flag of the previous one to
						// false.
						//
						defaultModel.setDefaultModel(false);
						this.mRepo.updateItem(defaultModel);
					}
					
					this.setDefaultModel(model);
				}
				
				
				this.mRepo.updateItem(model);
				
				this.setModelAttachments(model);
				
				List<Attachment> attachments = model.getAttachments();
				if ((attachments != null) && (attachments.size() > 0)) {
					
					for(Attachment attachment : attachments) {
						
						this.mRepo.attach(model, attachment.getName(), attachment.getContentType(), attachment.getStream());
					}
				}
			
			} catch(DataValidationException dvex) {
				
				throw new PackageException("Cannot deploy update for model '" + modelId + "' at least one child template will become invalid.", dvex);
			}
		
		} else {
			
			if (modelId == null) {
				
				LOGGER.info("Model has no corresponding identifier, will be added as new.");
				
				if (model.isDefaultModel() == true) {
					
					Model defaultModel = this.getDefaultModel();
					if (defaultModel != null) {
						
						LOGGER.info("Model (id: <new>) replaces existing default model (id: " + defaultModel.getId() + ").");
						
						// ok we need to replace the existing model with a new default 
						// model, we then set the default flag of the previous one to
						// false.
						//
						defaultModel.setDefaultModel(false);
						this.mRepo.updateItem(defaultModel);	
						
					}

					this.setDefaultModel(model);

				}
				
				
				// in this case we are adding a brand new model
				// and we do not have any issue.
				//
				this.mRepo.addItem(model);
				this.setModelAttachments(model);
				
				
				modelId = model.getId();
			
			} else {
				
				throw new PackageException("Cannot deploy model (id: " + modelId + ") because there is no such model with the same id to replace.");
			}
		}
		
		return modelId;
	}
	
	protected void deployTemplates(List<Template> templates, String modelId) throws PackageException {
	
		int index = 0;
		
		for(Template template : templates) {
			
			String templateId = template.getId();
			if (templateId == null) {
				
				String tModelId = template.getModelId();
				
				if (tModelId == null) {
				
					Model defaultModel = this.getDefaultModel();
					
					if (defaultModel == null) {
					
						throw new PackageException("Template[" + index + "] cannot be bound to default model, because default model is missing.");
					
					} else {
						
						LOGGER.info("Template[" + index + "] has no corresponding modelId, it will be bound to the default model (id: " + defaultModel.getId() +").");
						
						template.setModelId(defaultModel.getId());
					}
				
				} else if (tModelId.equals(PackageManager.PACKAGE_SCOPE_ID)) {
					
					if (modelId == null) {
						
						throw new PackageException("Template[" + index + "] cannot be bound to the model defined in the package because, there is no model defined in the package.");
					}
					
					template.setModelId(modelId);
					
				} else {
					
					Model existing = this.mRepo.getItem(tModelId);
					if (existing == null) {
						
						throw new PackageException("Template[" + index + "] cannot be bound to the model specified (id: " + tModelId + ") because such model does not exist.");
					}
				}
				
				// we can now make all the check needed.
				
				// [TODO] Implement check against the owning model

				
			} else {
				
				Template existing = this.tRepo.getItem(templateId);
				if (existing == null) {
					
					throw new PackageException("Template cannot be deployed because there is no corresponding template to update (id: " + templateId + ").");
				}
				
				LOGGER.info("Template will be deployed as a replacement of existing template (id: " + templateId + ").");
				
				// ok, now we simply update the dataset and
				// also the attachments.
				
				
				// [TODO] Implement check against the owning model
				//
				
			}
			
			index++;
		}
	}
	
	protected void deployDataSet(DataSet dataSet, String modelId) throws PackageException {
		
		String dataSetId = dataSet.getId();
		
		if (dataSetId == null) {
			
			LOGGER.info("DataSet has no corresponding identifier, will be added as new.");
			
			String dsModelId = dataSet.getModelId();
			
			if (dsModelId == null) {
				
				Model defaultModel = this.getDefaultModel();
				
				if (defaultModel == null) {
				
					throw new PackageException("Dataset cannot be bound to default model, because default model is missing.");
				
				} else {
					
					LOGGER.info("DataSet has no corresponding modelId, it will be bound to the default model (id: " + defaultModel.getId() +").");
					
					dataSet.setModelId(defaultModel.getId());
				}
				
			} else if (dsModelId.equals(PackageManager.PACKAGE_SCOPE_ID) == true) {
				
				if (modelId == null) {
					
					throw new PackageException("Dataset cannot be bound to the model defined in the package because, there is no model defined in the package.");
				}
				
				dataSet.setModelId(modelId);
				
			} else {
				
				// in this case we need to have an existing model
				// to bind the dataaset.
				//
				
				Model existing = this.mRepo.getItem(dsModelId);
				
				if (existing == null) {
					
					throw new PackageException("Dataset cannot be bound to the model specified (id: " + dsModelId + ") because such model does not exist.");
				}
			}
			
			// ok if we get here... we can add the dataset.
			//
			
			this.dRepo.addItem(dataSet);
			this.setDataSetAttachments(dataSet);
			
			
		} else {
			
			DataSet existing = this.dRepo.getItem(dataSetId);
			if (existing == null) {
				
				throw new PackageException("Dataset cannot be deployed because there is no corresponding dataset to update (id: " + dataSetId + ").");
			}
			
			LOGGER.info("Dataset will be deployed as a replacement of existing dataset (id: " + dataSetId + ").");
			
			// ok, now we simply update the dataset and
			// also the attachments.
			
			this.dRepo.updateItem(dataSet);
			this.setDataSetAttachments(dataSet);
			
		}
	}
	
	
	protected Model getDefaultModel() {
		
		if (this.defaultModel == null) {
 		
			this.defaultModel = this.mRepo.findByDefaultFlag();
		}
		
		return this.defaultModel;
	}
	
	protected void setDefaultModel(Model model) {
		
		this.defaultModel = model;
	}
}


