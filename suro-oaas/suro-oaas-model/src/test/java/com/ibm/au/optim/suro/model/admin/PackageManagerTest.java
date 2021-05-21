/**
 * 
 */
package com.ibm.au.optim.suro.model.admin;

import org.junit.Test;

/**
 * Class <b>PackageManagerTest</b>. This class defines the test suite that all the 
 * {@link PackageManager} implementation should pass in order to be compliant with 
 * the semantics defined by the interface. The test suite does not run any test, but 
 * it can be as a base class for any test class that is defined for a concrete type 
 * that implements the {@link PackageManager} interface.
 * 
 * @author Christian Vecchiola
 *
 */
public abstract class PackageManagerTest {
	
	
	@Test
	public void testBind() {
		
	}
	
	@Test
	public void testRelease() {
		
	}
	
	@Test
	public void testIsBound() {
		
	}
	
	@Test
	public void testDeployPackage() {
		
	}
	
	@Test
	public void testGetDefaultPackage() {
		
	}
	
	@Test
	public void testGetPackageFromZipStream() {
		
	}
	
	@Test
	public void testGetPackageFromPath() {
		
	}
	
	@Test
	public void testGetPackageFromManifestAndPath() {
		
	}
	
	@Test
	public void testRemovePackage() {
		
	}
	
	@Test
	public void testRemovePackageWithCascade() {
		
	}
	
	/**
	 * This method is a callback for inherited classes that can be used to provide
	 * an instance of {@link PackageManager} for the purpose of testing.
	 * 
	 * @return	a {@link PackageManager} instance.
	 */
	protected abstract PackageManager getPackageManager();

}
