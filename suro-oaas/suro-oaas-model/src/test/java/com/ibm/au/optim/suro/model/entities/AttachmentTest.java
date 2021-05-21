package com.ibm.au.optim.suro.model.entities;

import com.ibm.au.optim.suro.model.entities.Attachment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Random;

import org.junit.Test;
import org.junit.Assert;

/**
 * Class <b>AttachmentTest</b>. This class verifies the implemented behaviour
 * of the {@link Attachment} class. An attachment is defined by the <i>name</i>,
 * <i>content length</i>, and <i>content type</i> properties. The first one
 * cannot be {@literal null} neither an empty string, while the second one
 * cannot be a negative number, and the third one does not have restrictions.
 * 
 * @author Peter Ilfrich and Christian Vecchiola
 */
public class AttachmentTest  {
    
	/**
	 * A {@link Random} instance used to generate random numbers for the 
	 * purpose of generalising tests cases.
	 */
    private Random random = new Random();

    /**
     * <p>
     * This method tests the behaviour of the {@link Attachment#Attachment(String, long)}
     * constructor. This is supposed to initialise the {@link Attachment} instances with
     * a valid value of the <i>name</i> and the <i>contentLength</i> properties, and the
     * value of the <i>contentType</i> property should be set to {@literal null}.
     * </p>
     * <p>
     * The following conditions should trigger an {@link IllegalArgumentException}:
     * <ul>
     * <li>a {@literal null} or empty string value for <i>name</i></li>
     * <li>a negative value for <i>content length</i></li>
     * </ul>
     * </p>
     */
    @Test
    public void testConstructorWithNameAndLength() {
    	
    	long expectedContentLength = this.getContentSize();
    	String expectedName = "text.txt";
    	
    	// sunny day testing, valid parameter we simply check for equality
    	//
    	Attachment att = new Attachment(expectedName, expectedContentLength);
    	
    	Assert.assertEquals(expectedContentLength, att.getContentLength());
    	Assert.assertNull(att.getContentType());
    	Assert.assertEquals(expectedName, att.getName());
    	Assert.assertNull(att.getStream());
    	
    	
    	// edge testing, we need to check what happens
    	// we want to create an attachment that:
    	//
    	// 1. has a null name.
    	
    	try {

    		att = new Attachment("", expectedContentLength);
    		Assert.fail("Attachment(null,contentLength) should throw IllegalArgumentException.");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok all good.
    	}
    	
    	// 2. has an empty name.
    	//
    	
    	try {
    	
    		att = new Attachment("", expectedContentLength);
    		Assert.fail("Attachment('',contentLength) should throw IllegalArgumentException.");
    	
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, all good.
    		
    	}

    	// 3. a negative number for the content length
    	//

    	try {
    	
    		// this is a way to ensure a random negative long number. We
    		// get the absolute value, we diminish by one unit to be sure
    		// that when we flip the sign it is still in the range.
    		//
    		expectedContentLength = -(Math.abs(random.nextLong()) - 1);
    		
    		att = new Attachment(expectedName, expectedContentLength);
    		Assert.fail("Attachment(name,contentLength,contentType) should throw IllegalArgumentException for a negative content length.");
    	
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, all good.
    		
    	}
    }

    /**
     * <p>
     * This method tests the behaviour of the {@link Attachment#Attachment(String, long, String)}
     * constructor. This is supposed to initialise the {@link Attachment} instances with a valid 
     * value of the <i>name</i> and the <i>contentLength</i> properties, and the value of the <i>
     * contentType</i> property does not have any constraints.
     * </p>
     * <p>
     * The following conditions should trigger an {@link IllegalArgumentException}:
     * <ul>
     * <li>a {@literal null} or empty string value for <i>name</i></li>
     * <li>a negative value for <i>content length</i></li>
     * </ul>
     * </p>
     */
    @Test
    public void testConstructorWithNameLengthAndType() {
    	
    	String expectedContentType = "text/plain";
    	long expectedContentLength = this.getContentSize();
    	String expectedName = "text.txt";
    	
    	// sunny day testing, valid parameter we simply check for equality
    	//
    	Attachment att = new Attachment(expectedName, expectedContentLength, expectedContentType);
    	
    	Assert.assertEquals(expectedContentLength, att.getContentLength());
    	Assert.assertEquals(expectedContentType, att.getContentType());
    	Assert.assertEquals(expectedName, att.getName());
    	Assert.assertNull(att.getStream());
    	
    	
    	// edge testing, we need to check what happens
    	// we want to create an attachment that:
    	//
    	// 1. has a null name.
    	
    	try {

    		att = new Attachment("", expectedContentLength, expectedContentType);
    		Assert.fail("Attachment(null,contentLength,contentType) should throw IllegalArgumentException.");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok all good.
    	}
    	
    	// 2. has an empty name.
    	//
    	
    	try {
    	
    		att = new Attachment("", expectedContentLength, expectedContentType);
    		Assert.fail("Attachment('',contentLength,contentType) should throw IllegalArgumentException.");
    	
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, all good.
    		
    	}
    	
    	// 3. a null content type
    	
    	att = new Attachment(expectedName, expectedContentLength, null);
    	Assert.assertNull(att.getContentType());
    	
    	
    	// 4. a negative number for the content length
    	//

    	try {
    	
    		// this is a way to ensure a random negative long number. We
    		// get the absolute value, we diminish by one unit to be sure
    		// that when we flip the sign it is still in the range.
    		//
    		expectedContentLength = -(Math.abs(random.nextLong()) - 1);
    		
    		att = new Attachment(expectedName, expectedContentLength, expectedContentType);
    		Assert.fail("Attachment(name,contentLength,contentType) should throw IllegalArgumentException for a negative content length.");
    	
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, all good.
    		
    	}
    	
    	
    }
    /**
     * This method tests the implementation of the setter and getter methods
     * for the <i>name</i> property of an {@link Attachment} instance. The
     * setter should not allow to set an empty or {@literal null} name.
     */
    @Test
    public void testGetSetName() {
    	
    	String expected = "test.txt";
    	
    	// 1. does the getter return the value of the property set 
    	//    in the constructor?
    	//
    	Attachment att = new Attachment(expected, 0, null);
    	String actual = att.getName();
    	Assert.assertEquals(expected, actual);
    	
    	
    	// let's give it another try, with the other constructor
    	//
    	att = new Attachment(expected, 0);
    	actual = att.getName();
    	Assert.assertEquals(expected, actual);
    	
    	// 2. does the setter enables to set a valid value?
    	//
    	expected = "ninja.mod";
    	att.setName(expected);
    	actual = att.getName();
    	Assert.assertEquals(expected, actual);
    	
    	// 3. does the setter allows for null values?
    	//
    	try {

    		att.setName(null);
    		Assert.fail("Attachment.setName(null) should throw IllegalArgumentException.");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok all good.
    	}
    	
    	
    	// 4. does the setter allows for empty string values?
    	//
    	try {

    		att.setName("");
    		Assert.fail("Attachment.setName('') should throw IllegalArgumentException.");
    		
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok all good.
    	}
    	
    }
    /**
     * This method tests the implementation of the setter and getter methods for
     * the <i>content length</i> property of an {@link Attachment} instance. The 
     * setter should not allow for negative numbers and should throw an {@link 
     * IllegalArgumentException} in that case.
     */
    @Test
    public void testGetSetContentLength() {
    	
    	long expected = this.getContentSize();

    	// 1. does the getter retrieve the value passed for the property in the
    	//    constructor.
    	//
    	Attachment att = new Attachment("ninja.mod", expected, null);
    	long actual = att.getContentLength();
    	Assert.assertEquals(expected, actual);
    	
    	// let's try with the two parameters constructor
    	//
    	att = new Attachment("sifu.opl", expected);
    	actual = att.getContentLength();
    	Assert.assertEquals(expected, actual);
    	
    	
    	// 2. does the getter retrieves the value set by the setter, when the
    	//    the value is valid?
    	expected = this.getContentSize();
    	att.setContentLength(expected);
    	actual = att.getContentLength();
    	Assert.assertEquals(expected, actual);
    	
    	// what happens if we set 0? It should be ok.
    	//
    	att.setContentLength(0);
    	actual = att.getContentLength();
    	Assert.assertEquals(0, actual);
    	
    	
    	// 3. a negative value is definitely not allowed
    	//
    	try {
        	
    		// this is a way to ensure a random negative long number. We
    		// get the absolute value, we diminish by one unit to be sure
    		// that when we flip the sign it is still in the range.
    		//
    		expected = -(Math.abs(random.nextLong()) - 1);
    		
    		att.setContentLength(expected);
    		Assert.fail("Attachment(name,contentLength,contentType) should throw IllegalArgumentException for a negative content length.");
    	
    	} catch(IllegalArgumentException ilex) {
    		
    		// ok, all good.
    		
    	}
    }
    
    /**
     * This method tests the behaviour of the setter and getter methods for
     * the <i>content type</i> property. There are no restrictions on the
     * range of acceptable values of the property. Therefore, we just test
     * that the setter and the getter are linked to the property and they
     * respectively set and get its value.
     */
    @Test
    public void testGetSetContentType() {
    	
    	// 1. The default value should be set to null.
    	//
    	Attachment att = new Attachment("godzilla.bin", this.getContentSize());
    	String actual = att.getContentType();
    	Assert.assertNull(actual);
    	
    	// 2. Does the value assigned to the constructor
    	//    gets passed in the getter retrieves it?
    	
    	String expected = "application/json";
    	att = new Attachment("godzilla.bin", this.getContentSize(), expected);
    	actual = att.getContentType();
    	Assert.assertEquals(expected, actual);

    	// 3. Does the setter actually sets the value and
    	//    the getter retrieves it?
    	//
    	expected = "application/xml";
        att.setContentType(expected);
        actual = att.getContentType();
        Assert.assertEquals(expected, actual);

        // 4. Does the setter actually sets the null value?
        //
        att.setContentType(null);
        Assert.assertNull(att.getContentType());
    }
    
    /**
     * This method tests the combined implementation of {@link Attachment#store(java.io.InputStream)}
     * and {@link Attachment#getStream()}. The intended behaviour of these two method is to store the
     * content of the attachment accessible via the {@link InputStream} interface. These method do not
     * guarantee that the given instance of {@link InputStream} passed to the <i>store</i> method is
     * the same as the one returned by the method <i>getStream</i>. What is tested that they provide
     * a way to access the same content.
     */
    @Test
    public void testStoreAndGetStream() {
    	
    	// 1. first test, we check that when we initialise the instance we do not have
    	//    any stream attached and the value returned by getStream() should be null.
    	
    	Attachment att = new Attachment("batman.csv", this.getContentSize());
    	InputStream actual = att.getStream();
    	Assert.assertNull(actual);
    	
    	// let's do it again with the other constructor...
    	
    	att = new Attachment("wonderwoman.lst", this.getContentSize(), "text/plain");
    	actual = att.getStream();
    	Assert.assertNull(actual);
    	
    	// 2. we now check that when we send valid values we actually can retrieve
    	//    the same content that was stored. 
    	//
    	int size = this.random.nextInt(512) + 1;
    	
    	byte[] expectedPayload = new byte[size];
    	for(int i=0; i<size; i++) {
    		
    		expectedPayload[i] = (byte) this.random.nextInt(256);
    	}
    	
    	ByteArrayInputStream bais = new ByteArrayInputStream(expectedPayload);
    	att.store(bais);
    	InputStream stream = att.getStream();
    	Assert.assertNotNull(stream);
    	
    	// ok the stream is not null, we can now read everything into
    	// byte array output stream and convert everything into an 
    	// array of bytes.
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] chunk = new byte[512];
    	int nRead = 0;
    	while((nRead = bais.read(chunk, 0, chunk.length)) != -1) {
    		
    		baos.write(chunk, 0, nRead);
    	}
    	byte[] actualPayload = baos.toByteArray();
    	
    	Assert.assertArrayEquals(expectedPayload, actualPayload);
    	
    	// ok, we now check with null and see whether we get a non-null
    	// stream.
    	
    	att.store(null);
    	actual = att.getStream();
    	Assert.assertNull(actual);
    }
    

    /**
     * This method tests the implemented behaviour of {@link Attachment#clone()}.
     * The method is expected to clone complex properties and to shallow
     * copy immutable properties or simple ones.
     */
    @Test
    public void testClone() {
    	
    	// Test 1. fresh instance.
    	//
    	Attachment expected = new Attachment("a1", 0, null);
    	Attachment actual = expected.clone();
    	
    	Assert.assertEquals(expected.getName(), actual.getName());
    	Assert.assertEquals(expected.getContentLength(), actual.getContentLength());
    	Assert.assertEquals(expected.getContentType(), actual.getContentType());
    	
    	
    	// Test 2. some modifications.
    	//
    	expected.setName("a2");
    	expected.setContentLength(241241);
    	expected.setContentType("text/plain");
    	
    	actual = expected.clone();
    	
    	Assert.assertEquals(expected.getName(), actual.getName());
    	Assert.assertEquals(expected.getContentLength(), actual.getContentLength());
    	Assert.assertEquals(expected.getContentType(), actual.getContentType());
    	
    }
    
    /**
     * This method tests the implemented behaviour of {@link Attachment#equals(Object)}.
     * The method is expected to return {@literal true} if:
     * <ul>
     * <li>the argument is a non {@literal null} {@link Attachment} instance; AND</li>
     * <li>the instance has the same value for the <i>name</i> property.</li>
     * </ul>
     * The value {@literal false} is returned in all the other cases.
     */
    @Test
    public void testEquals() {
    	
    	Attachment att = new Attachment("a1", 23, "text/plain");
    	
    	// Test 1. false, when called on null.
    	//
    	Assert.assertFalse(att.equals(null));
    	
    	// Test 2. false when called on a generic object not of type attachment.
    	//
    	Assert.assertFalse(att.equals(new Object()));
    	
    	// Test 3. false when called on an attachment instance with a different name.
    	//
    	Assert.assertFalse(att.equals(new Attachment("a2", 45, "image/png")));
    	
    	// Test 4. true when called on an attachment instance with the same name.
    	//
    	Assert.assertTrue(att.equals(new Attachment("a1", 100, "image/jpeg")));
    	
    	// Test 5. true, for obviousness test.
    	//
    	Assert.assertTrue(att.equals(att));
    }
    
    /**
     * The method tests the implemented behaviour of the {@link Attachment#hashCode()}.
     * This method is expected to return the hashcode value of the <i>name</i> property
     * of the instance. 
     */
    @Test
    public void testHashCode() {
    	
    	String name = "a1";

    	Attachment att = new Attachment(name, 23, "text/plain");
    	
    	Assert.assertEquals(name.hashCode(), att.hashCode());
    }
    
    /**
     * Utility method for returning a random content size that is positive or zero.
     * The method uses the {@link Random#nextLong()} method to generate a random
     * number and then invokes {@link Math#abs(long)} to ensure that a non-negative
     * value is returned.
     * 
     * @return	a {@literal long} value that is either a positive number or zero.
     */
    protected long getContentSize() {
    	
    	return Math.abs(this.random.nextLong());
    }
}
