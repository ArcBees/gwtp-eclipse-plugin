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

package com.arcbees.plugin.eclipse.domain;

import org.eclipse.jdt.internal.core.SourceType;

public class SelectedPanelModel {
    private SourceType presenterSourceType;
    private SourceType viewSourceType;
    private int selectedIndexPanel;

    public SourceType getPresenterSourceType() {
        return presenterSourceType;
    }

    public void setPresenterSourceType(SourceType presenterSourceType) {
        this.presenterSourceType = presenterSourceType;
    }

    public SourceType getViewSourceType() {
        return viewSourceType;
    }

    public void setViewSourceType(SourceType viewSourceType) {
        this.viewSourceType = viewSourceType;
    }

    public int getSelectedIndexPanel() {
        return selectedIndexPanel;
    }

    public void setSelectedIndexPanel(int selectedIndexPanel) {
        this.selectedIndexPanel = selectedIndexPanel;
    }
}
