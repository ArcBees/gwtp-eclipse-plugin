/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.arcbees.plugin.eclipse.wizard.createproject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.arcbees.plugin.eclipse.domain.Archetype;
import com.arcbees.plugin.eclipse.domain.ArchetypeCollection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.mapper.factory.GsonObjectMapperFactory;

public class FetchArchetypes {
    private static final String DIRECTORY_URL = "https://project-directory.appspot.com/_ah/api/archetypeendpoint/v1/archetype";
    
    /**
     * TODO deal with network connection 
     * TODO deal with fetch timeout 
     * TODO deal with fetch error
     * TODO cache
     */
    public ArchetypeCollection fetchArchetypes() {
        initRestAssured();

        ArchetypeCollection archetypeCollection = RestAssured.get(DIRECTORY_URL).as(ArchetypeCollection.class);
        return archetypeCollection;
    }

    private void initRestAssured() {
        GsonObjectMapperFactory gsonFactory = new GsonObjectMapperFactory() {
            public Gson create(Class clazz, String s) {
                return createGsonBuilder().create();
            }
        };
        ObjectMapperConfig mapperConfig = RestAssuredConfig.config().getObjectMapperConfig()
                .gsonObjectMapperFactory(gsonFactory);
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(mapperConfig);
    }

    // TODO extract more of this
    private GsonBuilder createGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        gsonBuilder.registerTypeAdapter(ArchetypeCollection.class, new JsonDeserializer<ArchetypeCollection>() {
            public ArchetypeCollection deserialize(JsonElement json, Type typeOft, JsonDeserializationContext context)
                    throws JsonParseException {
                JsonObject parentJson = json.getAsJsonObject();

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                            throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                });
                Gson gson = gsonBuilder.create();

                ArchetypeCollection parent = gson.fromJson(json, ArchetypeCollection.class);
                List<Archetype> archetypes = null;

                if (parentJson.get("items").isJsonArray()) {
                    JsonElement itemsJson = parentJson.get("items");
                    archetypes = gson.fromJson(itemsJson, new TypeToken<List<Archetype>>() {
                    }.getType());
                } else {
                    Archetype single = gson.fromJson(parentJson.get("items"), Archetype.class);
                    archetypes = new ArrayList<Archetype>();
                    archetypes.add(single);
                }
                parent.setArchetypes(archetypes);
                return parent;
            }
        });
        return gsonBuilder;
    }
    
}
