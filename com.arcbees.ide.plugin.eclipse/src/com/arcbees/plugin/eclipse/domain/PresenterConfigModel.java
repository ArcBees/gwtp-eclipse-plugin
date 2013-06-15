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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class PresenterConfigModel extends ModelObject {
    private IJavaProject project;
    private String name;
    private String path;
    private boolean nestedPresenter;
    private boolean presenterWidget;
    private boolean popupPresenter;
    private String contentSlot;
    private boolean place;
    private String nameToken;
    private boolean crawlable;
    private boolean codeSplit;

    private boolean overridePopup;
    private String popupPanel;

    private boolean singleton;

    private boolean addUiHandlers;
    private boolean onBind;
    private boolean onHide;
    private boolean onReset;
    private boolean onUnbind;
    private boolean useManualReveal;
    private boolean usePrepareFromRequest;
    private String gatekeeper;

    public PresenterConfigModel() {
        nestedPresenter = true;
    }

    public void setProject(IJavaProject project) {
        this.project = project;
    }

    public IJavaProject getProject() {
        return project;
    }

    public String getProjectName() {
        return name;
    }

    public void setName(String name) {
        firePropertyChange("name", this.name, this.name = name);
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        firePropertyChange("path", this.path, this.path = path);
    }

    public boolean getNestedPresenter() {
        return nestedPresenter;
    }

    public void setNestedPresenter(boolean nestedPresenter) {
        firePropertyChange("nestedPresenter", this.nestedPresenter, this.nestedPresenter = nestedPresenter);
    }

    public boolean getPresenterWidget() {
        return presenterWidget;
    }

    public void setPresenterWidget(boolean presenterWidget) {
        firePropertyChange("presenterWidget", this.presenterWidget, this.presenterWidget = presenterWidget);
    }

    public boolean getPopupPresenter() {
        return popupPresenter;
    }

    public void setPopupPresenter(boolean popupPresenter) {
        firePropertyChange("popupPresenter", this.popupPresenter, this.popupPresenter = popupPresenter);
    }

    public String getContentSlot() {
        return contentSlot;
    }

    public void setContentSlot(String contentSlot) {
        firePropertyChange("contentSlot", this.contentSlot, this.contentSlot = contentSlot);
    }

    public boolean getPlace() {
        return place;
    }

    public void setPlace(boolean place) {
        firePropertyChange("place", this.place, this.place = place);
    }

    public String getNameToken() {
        return nameToken;
    }

    public void setNameToken(String nameToken) {
        firePropertyChange("nameToken", this.nameToken, this.nameToken = nameToken);
    }

    public boolean getCrawlable() {
        return crawlable;
    }

    public void setCrawlable(boolean crawlable) {
        firePropertyChange("crawlable", this.crawlable, this.crawlable = crawlable);
    }

    public boolean getCodeSplit() {
        return codeSplit;
    }

    public void setCodeSplit(boolean codeSplit) {
        firePropertyChange("codeSplit", this.codeSplit, this.codeSplit = codeSplit);
    }

    public boolean getOverridePopup() {
        return overridePopup;
    }

    public void setOverridePopup(boolean overridePopup) {
        firePropertyChange("overridePopup", this.overridePopup, this.overridePopup = overridePopup);
    }

    public String getPopupPanel() {
        return popupPanel;
    }

    public void setPopupPanel(String popupPanel) {
        firePropertyChange("popupPanel", this.popupPanel, this.popupPanel = popupPanel);
    }

    public boolean getSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        firePropertyChange("singleton", this.singleton, this.singleton = singleton);
    }

    public boolean getAddUiHandlers() {
        return addUiHandlers;
    }

    public void setAddUiHandlers(boolean addUiHandlers) {
        firePropertyChange("addUiHandlers", this.addUiHandlers, this.addUiHandlers = addUiHandlers);
    }

    public boolean getOnBind() {
        return onBind;
    }

    public void setOnBind(boolean onBind) {
        firePropertyChange("onBind", this.onBind, this.onBind = onBind);
    }

    public boolean getOnHide() {
        return onHide;
    }

    public void setOnHide(boolean onHide) {
        firePropertyChange("onHide", this.onHide, this.onHide = onHide);
    }

    public boolean getOnReset() {
        return onReset;
    }

    public void setOnReset(boolean onReset) {
        firePropertyChange("onReset", this.onReset, this.onReset = onReset);
    }

    public boolean getOnUnbind() {
        return onUnbind;
    }

    public void setOnUnbind(boolean onUnbind) {
        firePropertyChange("onUnbind", this.onUnbind, this.onUnbind = onUnbind);
    }

    public boolean getUseManualReveal() {
        return useManualReveal;
    }

    public void setUseManualReveal(boolean useManualReveal) {
        firePropertyChange("useManualReveal", this.useManualReveal, this.useManualReveal = useManualReveal);
    }

    public boolean getUsePrepareFromRequest() {
        return usePrepareFromRequest;
    }

    public void setUsePrepareFromRequest(boolean usePrepareFromRequest) {
        firePropertyChange("usePrepareFromRequest", this.usePrepareFromRequest,
                this.usePrepareFromRequest = usePrepareFromRequest);
    }

    public String getGatekeeper() {
        return gatekeeper;
    }

    public void setGatekeeper(String gatekeeper) {
        firePropertyChange("gatekeeper", this.gatekeeper, this.gatekeeper = gatekeeper);
    }

    public String getPackageSelection() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        ISelectionService selectionservice = window.getSelectionService();
        if (selectionservice == null) {
            return null;
        }

        TreeSelection selection = (TreeSelection) selectionservice.getSelection();
        if (selection == null) {
            return null;
        }

        String spath = null;
        try {
            IPackageFragment selectedPackage = (IPackageFragment) selection.getFirstElement();
            if (selectedPackage != null) {
                spath = selectedPackage.getElementName();
                System.out.println("path=" + spath);
            }
        } catch (Exception e) {
        }
        return spath;
    }

    @Override
    public String toString() {
        String s = "{ PresenterConfigModel: ";
        // s += "project=" + project.toString() + " ";
        s += "name=" + name + " ";
        s += "path=" + path + " ";
        s += " }";
        return s;
    }
}
