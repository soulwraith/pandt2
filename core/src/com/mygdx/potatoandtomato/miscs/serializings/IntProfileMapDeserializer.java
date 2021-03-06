package com.mygdx.potatoandtomato.miscs.serializings;

import com.mygdx.potatoandtomato.models.RoomUser;
import com.potatoandtomato.common.statics.Vars;
import com.shaded.fasterxml.jackson.core.JsonParser;
import com.shaded.fasterxml.jackson.core.ObjectCodec;
import com.shaded.fasterxml.jackson.databind.DeserializationContext;
import com.shaded.fasterxml.jackson.databind.JsonDeserializer;
import com.shaded.fasterxml.jackson.databind.JsonNode;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 16/12/2015.
 */
public class IntProfileMapDeserializer extends JsonDeserializer<Map<String, RoomUser>> {

    @Override
    public ConcurrentHashMap<String, RoomUser> deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        ConcurrentHashMap<String, RoomUser> result = new ConcurrentHashMap();
        ObjectCodec oc = jsonParser.getCodec();

        JsonNode node = oc.readTree(jsonParser);

        ObjectMapper mapper = Vars.getObjectMapper();

        Iterator<Map.Entry<String, JsonNode>> nodeIterator = node.fields();

        while (nodeIterator.hasNext()) {

            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeIterator.next();
            result.put(entry.getKey(), mapper.treeToValue(entry.getValue(), RoomUser.class));
        }

        return result;
    }
}