package com.arcbees.gwtp.plugin.core.project.features;

import org.eclipse.jface.viewers.ICheckStateProvider;

public class FeatureCheckStateProvider implements ICheckStateProvider {

    @Override
    public boolean isChecked(final Object element) {
        return ((Node<Feature>) element).getData().isRecommended();
    }

    @Override
    public boolean isGrayed(final Object element) {
        return false;
    }

}
