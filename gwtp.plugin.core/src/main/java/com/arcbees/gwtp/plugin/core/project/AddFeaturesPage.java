package com.arcbees.gwtp.plugin.core.project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import com.arcbees.gwtp.plugin.core.project.features.Feature;
import com.arcbees.gwtp.plugin.core.project.features.FeatureCheckStateProvider;
import com.arcbees.gwtp.plugin.core.project.features.FeatureContentProvider;
import com.arcbees.gwtp.plugin.core.project.features.FeatureLabelProvider;
import com.arcbees.gwtp.plugin.core.project.features.Node;

public class AddFeaturesPage extends WizardPage {

    static AddFeaturesPage get() {
        return INSTANCE;
    }

    private final Map<Feature, Boolean> featureSelectionMap = new HashMap<>();

    private final static AddFeaturesPage INSTANCE = new AddFeaturesPage();;

    private AddFeaturesPage() {
        super("Add Features To Your Project", "Add Features To Your Project", null);
    }

    @Override
    public void createControl(final Composite parent) {
        final Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new GridLayout(1, false));

        final CheckboxTreeViewer checkboxTreeViewer = new CheckboxTreeViewer(container, SWT.BORDER);
        checkboxTreeViewer.setAutoExpandLevel(-1);
        checkboxTreeViewer.setExpandPreCheckFilters(false);
        final Tree tree = checkboxTreeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        checkboxTreeViewer.setContentProvider(new FeatureContentProvider());
        checkboxTreeViewer.setLabelProvider(new FeatureLabelProvider());
        checkboxTreeViewer.setCheckStateProvider(new FeatureCheckStateProvider());
        checkboxTreeViewer.setInput("init");

        checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void checkStateChanged(final CheckStateChangedEvent event) {
                if (event.getChecked() == false) {
                    checkboxTreeViewer.setSubtreeChecked(event.getElement(), false);
                }
                featureSelectionMap.put(((Node<Feature>) event.getElement()).getData(), event.getChecked());
            }
        });
    }

    private void fillContext(final List<Node<Feature>> children, final Map<String, Object> context) {
        for (final Node<Feature> node: children) {
            if (isFeatureSelected(node.getData())) {
                context.put(node.getData().name(), true);
            }
            fillContext(node.getChildren(), context);
        }

    }

    public void fillContext(final Map<String, Object> context) {
        fillContext(Feature.getFeatures().getChildren(), context);
    }

    private boolean isFeatureSelected(final Feature feature) {
        return featureSelectionMap.containsKey(feature) ? featureSelectionMap.get(feature) : feature.isRecommended();
    }
}
