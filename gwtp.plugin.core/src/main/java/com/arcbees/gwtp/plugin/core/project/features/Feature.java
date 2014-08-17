package com.arcbees.gwtp.plugin.core.project.features;

import java.util.ArrayList;
import java.util.List;

public enum Feature {
    APP_ENGINE("Google App Engine", "https://developers.google.com/appengine/", true, new FeatureConfigOption("Application Id", "Project", null)),
    OBJECTIFY("Objectify", "https://code.google.com/p/objectify-appengine/", true),
    UNIVERSAL_ANALYTICS("Google Universal Analytics", "https://github.com/ArcBees/universal-analytics", true, new FeatureConfigOption("Web Property ID", "UX-XXXX-Y", null)),
    GWT_PUSH_STATE("HTML5 Push State", "https://github.com/jbarop/gwt-pushstate", true);

    public static Node<Feature> getFeatures() {
        final Node<Feature> root = new Node<>(null);
        final Node<Feature> appengineNode = root.addChild(APP_ENGINE);
        appengineNode.addChild(OBJECTIFY);
        root.addChild(UNIVERSAL_ANALYTICS);
        root.addChild(GWT_PUSH_STATE);
        return root;
    }

    private final boolean recommended;
    private final String url;

    private final String title;

    private final List<FeatureConfigOption> configOptions = new ArrayList<>();

    Feature(final String title, final String url, final boolean recommended, final FeatureConfigOption... configOptions) {
        this.title = title;
        this.url = url;
        this.recommended = recommended;

        for (final FeatureConfigOption option : configOptions) {
            this.configOptions.add(option);
        }

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

    public boolean isRecommended() {
        return recommended;
    }

}
