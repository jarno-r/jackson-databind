package com.fasterxml.jackson.databind.ser;

import java.io.*;


import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.StdSerializerProvider;

public class TestNullSerialization
    extends BaseMapTest
{
    static class NullSerializer extends JsonSerializer<Object>
    {
        @Override
        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException
        {
            jgen.writeString("foobar");
        }
    }

    public void testSimple() throws Exception
    {
        assertEquals("null", new ObjectMapper().writeValueAsString(null));
    }

    public void testCustom() throws Exception
    {
        StdSerializerProvider sp = new StdSerializerProvider();
        sp.setNullValueSerializer(new NullSerializer());
        ObjectMapper m = new ObjectMapper();
        m.setSerializerProvider(sp);
        assertEquals("\"foobar\"", m.writeValueAsString(null));
    }
}
