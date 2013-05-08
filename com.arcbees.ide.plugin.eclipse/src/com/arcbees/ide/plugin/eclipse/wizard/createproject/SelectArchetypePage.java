package com.arcbees.ide.plugin.eclipse.wizard.createproject;

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
import com.arcbees.ide.plugin.eclipse.domain.ProjectConfigModel;
import com.arcbees.ide.plugin.eclipse.domain.Tag;
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

        fetchArchetypes();
    }

    private void fetchArchetypes() {
        List<Archetype> archetypes = RestAssured.given().expect().when().get(DIRECTORY_URL)
                .jsonPath().getList("items");

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
