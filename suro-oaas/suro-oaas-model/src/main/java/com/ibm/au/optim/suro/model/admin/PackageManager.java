/**
 * 
 */
package com.ibm.au.optim.suro.model.admin;

import java.nio.file.Path;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.ibm.au.optim.suro.model.SuroService;

/**
 * Interface <b>PackageManager</b>. This interface extends {@link SuroService} and
 * provides capabilities for the management of packages. A {@link Package} defines
 * a collection of entities that can be deployed into the application either by
 * updating existing ones or inserting them anew. 
 * 
 * @author Christian Vecchiola
 *
 */
public interface PackageManager extends SuroService {
	
	/**
	 * A {@link String} constant that is used to reference the unique identifier of the model that 
	 * is being associated to instances of dataset or templates within a given package. This value 
	 * when set to the {@link Template#getModelId()} or {@link DataSet#getModelId()}, will reference
	 * to the model identifier of the model defined in the package (if any).
	 */
	String PACKAGE_SCOPE_ID		=	"[package:model]";
	/**
	 * A {@link String} constant that is used to store the default instance of {@link 
	 * PackageManager} implementation that is used to perform package management operations.
	 */
	String PACKAGE_MANAGER_INSTACE 	= 	"package:manager:instance";
	
	/**
	 * A {@link String} constant that is used as default key for the {@link PackageManger} 
	 * implementation to use to perform package management operations.
	 */
	String PACKAGE_MANAGER_TYPE		=	PackageManager.class.getName();
	
	/**
	 * Deploys the current package into the application. This method executes the following 
	 * operations:
	 * <ul>
	 * <li>
	 * it first tries to deploy/update the {@link Model} instance (if any) that is contained in 
	 * the package. If the model has a unique identifier set, the package manager will look for 
	 * an existing {@link Model} instance and will update its content.
	 * </li>
	 * <li>
	 * it then tries to deploy/update the {@link Template} collection that is defined with the 
	 * package. If the template entities do not have any id defined, they will be created anew, 
	 * otherwise the package manager will look for an existing template instance and update its
	 * content. If the template has a {@link Model} identifier set to {@literal null} then the
	 * package manager will bind the template entities to the default model.
	 * </li>
	 * <li>
	 * it first tries to deploy the {@link DataSet} instance (if any) that is contained in the 
	 * package. The {@link DataSet} instance may have a unique identifier set, in this case
	 * the package manager will look at all the {@link Run} instances in the system that may
	 * have the same value for the data set identifier, and if there is not run the dataset is
	 * updated, otherwise not.
	 * </li>
	 * <ul>
	 * For each entity that has children (i.e. model and templates) the package manager in case
	 * of updates also checks that the update will not disrupt the existing entities. In particular,
	 * for a {@link Model} entity, all the dependending {@link Template} instances that have the
	 * same model will be validated against the new model. If the validation fails, the update
	 * will not be executed. For a {@link Template} instance, first their compliance with the
	 * model they are bound to will be verified and for updates to existing {@link Template}
	 * entities, the package manager will try to retrieve all the dependeding {@link Run} entities
	 * and check that they still validate against the model.
	 * 
	 * @param pkg	a {@link Package} entity that contains the entities to be deployed. It cannot
	 * 				be {@literal null}.
	 * 
	 * @throws IllegalArgumentException if <i>pkg</i> is {@literal null}.
	 * 
	 * @throws PackageException	if deploying <i>pkg</i> leads to inconsistencies or data validation
	 * 							errors.
	 */
	void deploy(Package pkg) throws PackageException;
	
	/**
	 * <p>
	 * This method removes the collection of entities referenced by the package. If the entities that
	 * need to be removed have dependencies the operation will be interrupted. Cases that can lead to
	 * an exception can be the following:
	 * <ul>
	 * <li>A dataset that has depending runs.</li>
	 * <li>A model that has depending template and runs.</li>
	 * <li>A template that has depending runs.</i>
	 * </ul>
	 * The rationale behind this behaviour is to ensure that the application remains always within
	 * a consistent state.
	 * </p>
	 * <p>
	 * To force removal of the entities defined by <i>pkg</i> see {@link PackageManager#remove(Package, 
	 * boolean)}.
	 * </p>
	 * 
	 * 
	 * @param pkg	a {@link Package} entity that contains the entities that will be removed. It cannot
	 * 				be {@literal null}.
	 * 
	 * @throws IllegalArgumentException if <i>pkg</i> is {@literal null}.
	 * 
	 * @throws PackageException	if any of the conditions identified above (or any other error) occurs.
	 */
	void remove(Package pkg) throws PackageException;
	
	/**
	 * This method removes the collection of entities referenced by the package. The behaviour of the
	 * method is controlled by <i>cascade</i>. If this flag is set to {@literal true} then package manager
	 * will force the removal of entities and will remove all the dependencies that are attached to the
	 * entities defined in the package. If this flag is set to {@literal false} than the removal will
	 * only occur if there are no dependencies. 
	 * 
	 * @param pkg		a {@link Package} entity that contains the entities that will be removed. It cannot
	 * 					be {@literal null}.
	 * 
	 * @param cascade	a {@literal boolean} flag indicating whether the package manager should cascade
	 * 					the deletions or stop the operations and raise a {@link PackageException} if the
	 * 					specified entities have dependencies.
	 * 
	 * @throws IllegalArgumentException if <i>pkg</i> is {@literal null}.
	 * 
	 * @throws PackageException	if <i>cascade<i> is set to {@literal false} and the entities specified in
	 * 							<i>pkg</i> have dependencies. Or, for any other error occurring during the
	 * 							process.
	 */
	void remove(Package pkg, boolean force) throws PackageException;
	
	/**
	 * This method extracts the package from the given <i>ZIP</i> stream. The package manager expects to
	 * find a manifest file named {@link Manifest#DEFAULT_MANIFEST_FILE} and it will create a package by
	 * reading the content of the manifest. In this case all the file references are to be considered
	 * relative to the root of the zip file.
	 * 
	 * @param zipPackage	a {@link ZipInputStream} instance that provides access to the zip file that 
	 * 						contains the manifest file and the package entities. It cannot be {@literal 
	 * 						null}.
	 * 
	 * @return	a {@link Package} instance that has been extracted by the package manager from the zip
	 * 			file.
	 * 
	 * @throws IllegalArgumentException	if <>stream</i> is {@literal null}.
	 * 
	 * @throws PackageException	if any other error occurs while extracting the package.
	 */
	Package getPackage(ZipFile zipPackage) throws PackageException;
	
	/**
	 * This method extracts the package reading the content from the given <i>path</i>. This can either
	 * point to a directory or to the manifest file. If <i>path</i> points to a directory then the package
	 * manager will try to find the default manifest file (i.e. {@link Manifest#DEFAULT_MANIFEST_FILE}).
	 * If <i>path</i> points to a file that one will be used as manifest file.
	 * 
	 * @param path		a {@link Path} instance. This should point to either an existing directory containing
	 * 					the manifest file or to an existing manifest file. It cannot be {@literal null}.
	 * 
	 * @return	a {@link Package} instance created by using the specified path as location for its
	 * 			corresponding manifest.
	 * 
	 * @throws IllegalArgumentException	if <i>path</i> is {@literal null}.
	 * 
	 * @throws PackageException			if there is any other error in extracting the package.
	 */
	Package getPackage(Path path) throws PackageException;
	
	/**
	 * This method extracts the package by reading the given <i>manifest</i> and using <i>rootPath</i>
	 * as a root reference path for all the relative paths mentioned in the manifest.
	 * 
	 * @param manifest	a {@link Manifest} instance defining the content of the package. It cannot be
	 * 					{@literal null}.
	 * 
	 * @param rootPath	a {@link Path} instance representing the reference root directory that will be
	 * 					used to resolve the relative paths in the defined in the <i>manifest</i>. It
	 * 					can be {@literal null} if <i>manifest</i> declares all absolute paths.
	 * 
	 * @return	a {@link Package} instance created out of the given <i>manifest</i>.
	 * 
	 * @throws IllegalArgumentException	if <i>manifest</i> is {@literal null}.
	 * 
	 * @throws PackageException			if there is any error in the creation of the package.
	 */
	Package getPackage(Manifest manifest, Path rootPath) throws PackageException;
	
	/**
	 * <p>
	 * Gets a {@link Package} instance that represents the current default setup. This method retrieves
	 * both the default {@link Model} instance, the collection of {@link Template} instances that are
	 * associated to that model. It then populates the {@link DataSet} instance by keeping the first
	 * instance that is associated to the selected {@link Model}. The version is set to the database
	 * version supporting the application.
	 * </p>
	 * <p>
	 * This version of the method does not load the attachments. For a {@link Package} instance that
	 * has the attachments loaded, please refer to {@link PackageManager#getDefaultPackage(boolean)}.
	 * </p>
	 * 
	 * @return	a {@link Package} instance or {@literal null} if there is no default model.
	 * 
	 * @throws PackageException	if there is any error when collecting the information about the package
	 * 							from the existing application.
	 */
	Package getDefaultPackage() throws PackageException;
	
	/**
	 * <p>
	 * Gets a {@link Package} instance that represents the current default setup. This method retrieves
	 * both the default {@link Model} instance, the collection of {@link Template} instances that are
	 * associated to that model. It then populates the {@link DataSet} instance by keeping the first
	 * instance that is associated to the selected {@link Model}. The version is set to the database
	 * version supporting the application.
	 * </p>
	 * 
	 * @param loadAttachment	a {@literal boolean} flag indicating whether to load the attachments
	 * 							or not.
	 * 
	 * @return	a {@link Package} instance or {@literal null} if there is no default model.
	 * 
	 * @throws PackageException	if there is any error when collecting the information about the package
	 * 							from the existing application.
	 */
	Package getDefaultPackage(boolean loadAttachments) throws PackageException;

}
