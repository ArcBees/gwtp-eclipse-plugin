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

import java.util.ArrayList;
import java.util.List;

public enum Feature {
    APP_ENGINE("Google App Engine", "https://developers.google.com/appengine/", true, new FeatureConfigOption(
            "Application Id", "Project", null)),
    OBJECTIFY("Objectify", "https://code.google.com/p/objectify-appengine/", true),
    UNIVERSAL_ANALYTICS("Google Universal Analytics", "https://github.com/ArcBees/universal-analytics", true,
                    new FeatureConfigOption("Web Property ID", "UX-XXXX-Y", null)),
    GWT_PUSH_STATE("HTML5 Push State", "https://github.com/jbarop/gwt-pushstate", true);

    private boolean selected;
    private final String url;
    private final String title;
    private final List<FeatureConfigOption> configOptions = new ArrayList<>();
   
    Feature(String title, String url, boolean recommended,
            FeatureConfigOption... configOptions) {
        this.title = title;
        this.url = url;
        this.selected = recommended;

        for (FeatureConfigOption option : configOptions) {
            this.configOptions.add(option);
        }
    }

    public static Node<Feature> getFeatures() {
        Node<Feature> root = new Node<>(null);
        Node<Feature> appengineNode = root.addChild(APP_ENGINE);
        appengineNode.addChild(OBJECTIFY);
        root.addChild(UNIVERSAL_ANALYTICS);
        root.addChild(GWT_PUSH_STATE);
        return root;
    }

    public List<FeatureConfigOption> getConfigOptions() {
        return configOptions;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public boolean hasConfigOptions() {
        return !configOptions.isEmpty();
    }

    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
