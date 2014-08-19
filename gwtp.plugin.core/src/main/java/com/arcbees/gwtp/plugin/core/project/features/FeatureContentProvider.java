/**
 * Copyright 2014 ArcBees Inc.
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

package com.arcbees.gwtp.plugin.core.project.features;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FeatureContentProvider implements ITreeContentProvider {
    @Override
    public void dispose() {
        // do nothing
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
        // do nothing
    }
}
