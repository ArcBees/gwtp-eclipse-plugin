package com.arcbees.ide.plugin.eclipse.wizard.createproject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.arcbees.ide.plugin.eclipse.domain.Archetype;
import com.arcbees.ide.plugin.eclipse.domain.ArchetypeCollection;
import com.arcbees.ide.plugin.eclipse.domain.ProjectConfigModel;
import com.arcbees.ide.plugin.eclipse.domain.Tag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jayway.restassured.RestAssured;

public class SelectArchetypePage extends WizardPage {
    private static final String DIRECTORY_URL = "https://project-manager-directory.appspot.com/_ah/api/archetypeendpoint/v1/archetype";

    private ProjectConfigModel projectConfigModel;
    private Table table;

    public SelectArchetypePage(ProjectConfigModel projectConfigModel) {
        super("wizardPageSelectArchetype");

        this.projectConfigModel = projectConfigModel;

        setTitle("Select Archetype");
        setDescription("Select a project template to start with.");
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(1, false));

        table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
        GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_table.heightHint = 127;
        table.setLayoutData(gd_table);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableColumn tblclmnKey = new TableColumn(table, SWT.NONE);
        tblclmnKey.setWidth(100);
        tblclmnKey.setText("Key");

        TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
        tblclmnName.setWidth(150);
        tblclmnName.setText("Name");

        TableColumn tblclmnTags = new TableColumn(table, SWT.NONE);
        tblclmnTags.setWidth(325);
        tblclmnTags.setText("Tags");
    }

    private void fetchArchetypes(List<Archetype> archetypes ) {
        String json = RestAssured.given().expect().when().get(DIRECTORY_URL).asString();
        
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

        Gson gson = gsonBuilder.create();
        ArchetypeCollection ac = gson.fromJson(json, ArchetypeCollection.class);        
        
        for (Archetype archetype : archetypes) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, getKey(archetype.getKey()));
            item.setText(1, getName(archetype.getName()));
            item.setText(2, getTags(archetype.getTags()));
        }
    }

    private String getName(String name) {
        if (name == null) {
            return "";
        }
        return name;
    }

    private String getKey(String key) {
        if (key == null) {
            return "";
        }
        return key;
    }

    private String getTags(List<Tag> tags) {
        if (tags == null) {
            return "";
        }
        String s = "";
        for (int i = 0; i < tags.size(); i++) {
            s += tags.get(i);
            if (i < tags.size() - 1) {
                s += ", ";
            }
        }
        return s;
    }
}
