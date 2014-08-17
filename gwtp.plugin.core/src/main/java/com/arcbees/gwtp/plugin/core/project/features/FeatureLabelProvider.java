package com.arcbees.gwtp.plugin.core.project.features;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class FeatureLabelProvider implements ILabelProvider {

    private Set<ILabelProviderListener> listeners = new HashSet<>();

    @Override
    public void addListener(final ILabelProviderListener listener) {
        listeners.add(listener);
    }

    @Override
    public void dispose() {

    }

    @Override
    public Image getImage(final Object element) {
        return null;
    }

    @Override
    public String getText(final Object element) {
        return ((Node<Feature>) element).getData().getTitle();
    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        return false;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {
        listeners.remove(listener);

    }

}
