package com.arcbees.gwtp.plugin.core.project.features;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FeatureContentProvider implements ITreeContentProvider {

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        return ((Node<?>) parentElement).getChildren().toArray();
    }

    @Override
    public Object[] getElements(final Object inputElement) {
        return Feature.getFeatures().getChildren().toArray();
    }

    @Override
    public Object getParent(final Object element) {
        return ((Node<?>) element).getParent();
    }

    @Override
    public boolean hasChildren(final Object element) {
        return getChildren(element).length > 0;
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        // TODO Auto-generated method stub

    }

}
