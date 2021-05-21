package com.ibm.au.optim.suro.model.store.impl;

import com.ibm.au.optim.suro.model.entities.Attachment;
import com.ibm.au.optim.suro.model.entities.Entity;
import com.ibm.au.optim.suro.model.entities.Run;
import com.ibm.au.optim.suro.model.store.Repository;
import com.ibm.au.jaws.web.core.runtime.Environment;
import com.ibm.au.jaws.web.core.runtime.impl.EnvironmentHelper;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

/**
 * @author Peter Ilfrich
 */
public class AbstractTransientRepositoryTest {

	@Test
    public void testGetNotExistingItem() {
        Repository<Entity> repo = new AbstractTransientRepositoryImpl();

        repo.addItem(new Run());

        Assert.assertNull(repo.getItem("not-existing-id"));
        Assert.assertNull(repo.getItem(null));
    }
	
	@Test
    public void testRemoveItem() {
        Repository<Entity> repo = new AbstractTransientRepositoryImpl();

        Entity obj = new Run();
        repo.addItem(obj);
        Entity obj2 = new Run();
        repo.addItem(obj2);
        Assert.assertEquals(2, repo.getAll().size());

        repo.removeItem(obj.getId());
        Assert.assertEquals(1, repo.getAll().size());

        Assert.assertEquals(obj2.getId(), repo.getAll().get(0).getId());

        repo.addItem(obj);
        Assert.assertEquals(2, repo.getAll().size());

        Assert.assertNull(repo.getItem("not-existing-id"));
        Assert.assertNull(repo.getItem(null));
    }
	
	@Test
    public void testRemoveAll() {
        Repository<Entity> repo = new AbstractTransientRepositoryImpl();

        Entity obj = new Run();
        repo.addItem(obj);
        Entity obj2 = new Run();
        repo.addItem(obj2);
        Assert.assertEquals(2, repo.getAll().size());

        repo.removeAll();
        Assert.assertEquals(0, repo.getAll().size());
    }

    public void testUpdateNotExisting() {
        Repository<Entity> repo = new AbstractTransientRepositoryImpl();

        Entity notInStore = new Run();
        notInStore.setId("not-in-db");

        Entity obj = new Run();
        repo.addItem(obj);

        repo.updateItem(notInStore);

        Assert.assertEquals(1, repo.getAll().size());
        Assert.assertEquals(obj.getId(), repo.getAll().get(0).getId());
        Assert.assertNull(repo.getItem("not-in-db"));
    }

    @Test
    public void testAttachmentsEmptyRepo() {
        AbstractTransientRepository<Entity, Entity> repo = new AbstractTransientRepositoryImpl();
        repo.attach(null, null, null, null);

        Entity obj = new Run();
        repo.attach(obj, "filename.txt", "text/plain", new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));
        repo.addItem(obj);

        Assert.assertNull(repo.getAttachment(obj.getId(), "filename.txt"));
    }
    
    @Test
    public void testAttachment() {
        AbstractTransientRepository<Entity, Entity> repo = new AbstractTransientRepositoryImpl();
        Entity obj = new Run();
        repo.addItem(obj);

        repo.attach(obj, "filename.txt", "text/plain", new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));

        Assert.assertNotNull(repo.getAttachment(obj.getId(), "filename.txt"));
        Assert.assertNull(repo.getAttachment(obj.getId(), "not-existing.txt"));

        repo.attach(null, "filename2.txt", "text/plain", new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));
        Assert.assertEquals(1, repo.repositoryAttachments.keySet().size());
        Assert.assertEquals(1, repo.repositoryAttachments.get(obj.getId()).size());

        repo.attach(obj, null, "text/plain", new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));
        Assert.assertEquals(1, repo.repositoryAttachments.keySet().size());
        Assert.assertEquals(1, repo.repositoryAttachments.get(obj.getId()).size());

        repo.attach(obj, "filename3.txt", "text/plain", null);
        Assert.assertEquals(1, repo.repositoryAttachments.keySet().size());
        Assert.assertEquals(1, repo.repositoryAttachments.get(obj.getId()).size());


        repo.attach(obj, "filename4.txt", "text/plain", new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));
        Assert.assertEquals(1, repo.repositoryAttachments.keySet().size());
        Assert.assertEquals(2, repo.repositoryAttachments.get(obj.getId()).size());
    }
    
    @Test
    public void testMultipleAttachments() {
        AbstractTransientRepository<Entity, Entity> repo = new AbstractTransientRepositoryImpl();
        Entity obj = new Run();
        repo.addItem(obj);

        repo.attach(obj, "filename.txt", "text/plain", new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));
        repo.attach(obj, "filename2.txt", "text/plain", new ByteArrayInputStream("data2".getBytes(StandardCharsets.UTF_8)));

        Assert.assertNotNull(repo.getAttachment(obj.getId(), "filename.txt"));
        Assert.assertNotNull(repo.getAttachment(obj.getId(), "filename2.txt"));
    }
    
    @Test
    public void testServiceBinding() throws Exception {
        Properties props = new Properties();
        props.setProperty("test", "test");
        Environment env = EnvironmentHelper.mockEnvironment(props);

        AbstractTransientRepository<Entity, Entity> repo = new AbstractTransientRepositoryImpl();
        repo.doBind(env);
        Assert.assertNotNull(repo.environment);
        Assert.assertEquals("test", repo.environment.getParameter("test"));

        repo.doRelease();
        Assert.assertNull(repo.environment);
    }

    @Test
    public void testAttachmentMetaInfo() throws Exception {
        AbstractTransientRepository<Entity, Entity> repo = new AbstractTransientRepositoryImpl();

        Entity entity = new Entity();
        repo.addItem(entity);
        List<Attachment> actual = repo.getAttachments(entity.getId());
        Assert.assertNull(actual);

        String expectedContent = "some-content";
        String expectedName = "test.json";
        String expectedContentType = "text/plain";
        repo.attach(entity, expectedName, expectedContentType, IOUtils.toInputStream(expectedContent));

        actual = repo.getAttachments(entity.getId());
        Assert.assertEquals(1, actual.size());
        
        Attachment actualAtt = actual.get(0);
        Assert.assertNotNull(actualAtt);
        Assert.assertEquals(expectedName, actualAtt.getName());
        Assert.assertEquals(expectedContentType, actualAtt.getContentType());
        
        
        InputStream streamContent = repo.getAttachment(entity.getId(), expectedName);
        Assert.assertNotNull(streamContent);	
        String actualContent = IOUtils.toString(streamContent);
        
        Assert.assertEquals(expectedContent, actualContent);

        Assert.assertNull(repo.getAttachments("foobar"));
        Assert.assertNull(repo.getAttachments(null));
    }



    private class AbstractTransientRepositoryImpl extends AbstractTransientRepository<Entity, Entity> implements Repository<Entity> {

    }
}
