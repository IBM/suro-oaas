/**
 * 
 */
package com.ibm.au.optim.oaas.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.sun.jersey.core.spi.scanning.JarFileScanner;
import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import com.sun.jersey.core.spi.scanning.uri.UriSchemeScanner;
import com.sun.jersey.core.spi.scanning.uri.JarZipSchemeScanner;
import com.sun.jersey.core.util.Closing;

/**
 * This class implements a specific {@link UriSchemeScanner} that is required
 * by the <i>WebSphere</i> application server family. This scheme scanner is
 * identical to the {@link JarZipSchemeScanner} but reacts to a different
 * type of scheme that is: <i>wsjar</i>. The logic implemented is the same
 * as the one in the {@link JarZipSchemeScanner}.
 *
 */
public class WsJarSchemeScanner implements UriSchemeScanner {

	/**
	 * Gets a {@link Set} of {@link String} representing the 
	 * collection of schemes that this scanner is able to
	 * scan.
	 * 
	 * @return a {@link Set} containing the <i>wsjar</i> string.
	 */
	// @Override
	public Set<String> getSchemes() {
		
		return new HashSet<String>(Arrays.asList("wsjar"));
	}

	/**
	 * Scans the given URI and passes the events to the {@link ScannerListener}. The
	 * method obtains a {@link Closing} of the jar file.
     * <p>
     * The format of the platform where the scanner is applied is the  following:
     * <ul>
     *   <li><code>wsjar:file:/tmp/fishfingers.jar!/example.txt</code></li>
     * </ul>
     * 
     *
     * @param u the raw scheme specific part of a URI minus the jar entry
	 *
     * @throws ScannerException if there is an error opening the stream.
     */
	// @Override
	public void scan(final URI u, final ScannerListener cfl) throws ScannerException {
		final String ssp = u.getRawSchemeSpecificPart();
        final String jarUrlString = ssp.substring(0, ssp.lastIndexOf('!'));
        final String parent = ssp.substring(ssp.lastIndexOf('!') + 2);
        try {
        	new Closing(new URL(jarUrlString).openStream()).f(new Closing.Closure() {

                public void f(final InputStream in) throws IOException {
                    JarFileScanner.scan(in, parent, cfl);
                }
            });
        } catch (IOException ex) {
            throw new ScannerException("IO error when scanning jar " + u, ex);
        }

	}
}