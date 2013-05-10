package com.arcbees.ide.plugin.eclipse.wizard.createproject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

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
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.mapper.factory.GsonObjectMapperFactory;

public class SelectArchetypePage extends WizardPage {
    private static final String DIRECTORY_URL = "https://project-directory.appspot.com/_ah/api/archetypeendpoint/v1/archetype";

    private ProjectConfigModel projectConfigModel;
    private Table table;
    private TableViewer tableViewer;

    public SelectArchetypePage(ProjectConfigModel projectConfigModel) {
        super("wizardPageSelectArchetype");

        this.projectConfigModel = projectConfigModel;

        setTitle("Select Archetype");
        setDescription("Select a project template to start with.");
    }

    /**
     * TODO add loading spinner
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        fetchArchetypes();
    }

    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new FillLayout(SWT.VERTICAL));

        tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnName = tableViewerColumn.getColumn();
        tblclmnName.setWidth(193);
        tblclmnName.setText("Name");

        TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnTags = tableViewerColumn_1.getColumn();
        tblclmnTags.setWidth(409);
        tblclmnTags.setText("Tags");

        tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Archetype a = (Archetype) element;
                return a.getName();
            }
        });

        tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Archetype a = (Archetype) element;
                List<Tag> tags = a.getTags();
                String s = "";
                if (tags != null) {
                    for (int i = 0; i < tags.size(); i++) {
                        Tag t = tags.get(i);
                        s += t.getName();
                        if (i < tags.size() - 1) {
                            s += ", ";
                        }
                    }
                }
                return s;
            }
        });

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                Archetype archetypeSelected = (Archetype) selection.getFirstElement();
                projectConfigModel.seArchetypeSelected(archetypeSelected);
                System.out.println("selected archetype: " + archetypeSelected);
            }
        });
    }

    /**
     * TODO add to thread? 
     * TODO deal with network connection 
     * TODO deal with fetch timeout 
     * TODO deal with fetch error
     */
    private void fetchArchetypes() {
        initRestAssured();

        ArchetypeCollection archetypeCollection = RestAssured.given().expect().when().get(DIRECTORY_URL)
                .as(ArchetypeCollection.class);
        List<Archetype> archetypes = archetypeCollection.getArchetypes();

        tableViewer.setInput(archetypes);
    }

    private void initRestAssured() {
        GsonObjectMapperFactory gsonFactory = new GsonObjectMapperFactory() {
            public Gson create(Class claszz, String s) {
                return createGsonBuilder().create();
            }
        };
        ObjectMapperConfig mapperConfig = RestAssuredConfig.config().getObjectMapperConfig()
                .gsonObjectMapperFactory(gsonFactory);
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(mapperConfig);
    }

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
