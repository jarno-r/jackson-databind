package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import com.fasterxml.jackson.databind.*;

/**
 * Unit tests for checking handling of DeserializationConfig.
 */
public class TestConfig
    extends BaseMapTest
{
    @JsonAutoDetect(setterVisibility=Visibility.NONE)
    final static class Dummy { }

    final static class EmptyDummy { }

    static class AnnoBean {
        int value = 3;
        
        @JsonProperty("y")
            public void setX(int v) { value = v; }
    }
    
    /*
    /**********************************************************
    /* Main tests
    /**********************************************************
     */

    public void testDefaults()
    {
        ObjectMapper m = new ObjectMapper();
        DeserializationConfig cfg = m.getDeserializationConfig();

        // Expected defaults:
        assertTrue(cfg.isEnabled(DeserializationConfig.Feature.USE_ANNOTATIONS));
        assertTrue(cfg.isEnabled(DeserializationConfig.Feature.AUTO_DETECT_SETTERS));
        assertTrue(cfg.isEnabled(DeserializationConfig.Feature.AUTO_DETECT_CREATORS));
        assertTrue(cfg.isEnabled(DeserializationConfig.Feature.USE_GETTERS_AS_SETTERS));
        assertTrue(cfg.isEnabled(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS));


        assertFalse(cfg.isEnabled(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS));
        assertFalse(cfg.isEnabled(DeserializationConfig.Feature.USE_BIG_INTEGER_FOR_INTS));

        assertTrue(cfg.isEnabled(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    public void testOverrideIntrospectors()
    {
        ObjectMapper m = new ObjectMapper();
        DeserializationConfig cfg = m.getDeserializationConfig();
        // and finally, ensure we could override introspectors
        cfg = cfg.withClassIntrospector(null); // no way to verify tho
        cfg = cfg.withAnnotationIntrospector(null);
        assertNull(cfg.getAnnotationIntrospector());
    }
        
    public void testAnnotationsDisabled() throws Exception
    {
        // first: verify that annotation introspection is enabled by default
        ObjectMapper m = new ObjectMapper();
        assertTrue(m.getDeserializationConfig().isEnabled(DeserializationConfig.Feature.USE_ANNOTATIONS));
        // with annotations, property is renamed
        AnnoBean bean = m.readValue("{ \"y\" : 0 }", AnnoBean.class);
        assertEquals(0, bean.value);

        m = new ObjectMapper();
        m.configure(DeserializationConfig.Feature.USE_ANNOTATIONS, false);
        // without annotations, should default to default bean-based name...
        bean = m.readValue("{ \"x\" : 0 }", AnnoBean.class);
        assertEquals(0, bean.value);
    }

    /**
     * Test for verifying working of [JACKSON-191]
     */
    public void testProviderConfig() throws Exception   
    {
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(0, mapper.getDeserializerProvider().cachedDeserializersCount());
        // and then should get one constructed for:
        AnnoBean bean = mapper.readValue("{ \"y\" : 3 }", AnnoBean.class);
        assertNotNull(bean);
        assertEquals(1, mapper.getDeserializerProvider().cachedDeserializersCount());
        mapper.getDeserializerProvider().flushCachedDeserializers();
        assertEquals(0, mapper.getDeserializerProvider().cachedDeserializersCount());
    }
}
