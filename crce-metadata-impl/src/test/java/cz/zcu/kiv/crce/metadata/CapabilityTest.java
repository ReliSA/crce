package cz.zcu.kiv.crce.metadata;

import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceCreatorImpl;
import java.util.HashSet;
import java.util.Set;
import org.junit.*;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class CapabilityTest {
    
    private ResourceCreator rc;
    
    @Before
    public void before(){
        rc = new ResourceCreatorImpl();
    }
    
    @Test
    public void testChain() throws Exception {
        Capability c = rc.createCapability("a").setProperty("p1", "1").setProperty("p2", 2);
        
        assert "a".equals(c.getName());
        assert "1".equals(c.getProperty("p1").getConvertedValue());
        assert 2  == (Long) c.getProperty("p2").getConvertedValue();
    }

    @Test
    public void unsetProperty() throws Exception {
        Capability c = rc.createCapability("a").setProperty("p1", "1").setProperty("p2", 2);
        
        assert c.getProperty("p1") != null;
        assert c.getProperty("p2") != null;
        
        c.unsetProperty("p1");
        
        assert c.getProperty("p1") == null;
        assert c.getProperty("p2") != null;
        
        c.unsetProperty("p2");
        
        assert c.getProperty("p1") == null;
        assert c.getProperty("p2") == null;
    }
    
    @Test
    public void uniqueProperties() throws Exception {
        Capability c1 = rc.createCapability("a");
        
        c1.setProperty("a", "a");
        assert c1.getProperties().length == 1;
        
        c1.setProperty("a", "a");
        assert c1.getProperties().length == 1;
        
        c1.setProperty("a", "b");
        assert c1.getProperties().length == 1;

        c1.setProperty("b", "a");
        assert c1.getProperties().length == 2;
    }
    
//    @Test
//    public void equals() throws Exception {
//        Capability c1 = rc.createCapability("a");
//        Capability c2 = rc.createCapability("a");
//        
//        assert c1.equals(c2);
//        
//        c1.setProperty("p1", "p1");
//        assert !c1.equals(c2);
//        
//        c2.setProperty("p1", "p1");
//        assert c1.equals(c2);
//        
//        
//        
//        Set<Capability> cs = new HashSet<Capability>();
//        
//        cs.add(c1);
//        
//        assert cs.contains(c1);
//        assert !cs.contains(c2);
//        
//        c1.setProperty("p1", "p1");
//        
//        assert cs.contains(c1);
//    }
    
    @Test
    public void hash() throws Exception {
        
    }
}