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

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ResolvedSourceField;

public class PresenterConfigModel extends ModelObject {
    private IJavaProject javaProject;
    private String name;
    private String path;
    private boolean nestedPresenter;
    private boolean presenterWidget;
    private boolean popupPresenter;

    // nested
    private ResolvedSourceField contentSlot;
    private boolean place;
    private String nameToken;
    private boolean crawlable;
    private boolean codeSplit;
    private boolean revealInRoot;
    private boolean revealInRootLayout;
    private boolean revealInSlot;

    // popup
    private boolean overridePopup;
    private String popupPanel;

    // extra
    private boolean singleton;
    private boolean addUiHandlers;
    private boolean onBind;
    private boolean onHide;
    private boolean onReset;
    private boolean onUnbind;
    private boolean useManualReveal;
    private boolean usePrepareFromRequest;
    private String gatekeeper;
    private IPackageFragment selectedPackage;
    private ICompilationUnit nameTokenUnit;

    public PresenterConfigModel() {
        // default settings
        nestedPresenter = true;
        revealInRoot = true;
    }

    public void setJavaProject(IJavaProject javaProject) {
        this.javaProject = javaProject;
    }

    public IJavaProject getJavaProject() {
        return javaProject;
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

    public ResolvedSourceField getContentSlot() {
        return contentSlot;
    }

    public String getContentSlotAsString() {
        if (contentSlot == null) {
            return "";
        }
        String name = contentSlot.getElementName();
        IType type = contentSlot.getDeclaringType();
        String s = type.getElementName() + "." + name;
        return s;
    }

    public String getContentSlotImport() {
        if (contentSlot == null) {
            return "";
        }
        IType itype = contentSlot.getDeclaringType();
        String importString = "import " + itype.getFullyQualifiedName() + ";";
        return importString;
    }

    public void setContentSlot(ResolvedSourceField contentSlot) {
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

    public String getNameTokenMethodName() {
        return "get" + nameToken.substring(0, 1).toUpperCase() + nameToken.substring(1);
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

    public void setSelectedPackage(IPackageFragment selectedPackage) {
        this.selectedPackage = selectedPackage;
    }

    public IPackageFragment getSelectedPackage() {
        return selectedPackage;
    }

    public String getSelectedPackageAndNameAsSubPackage() {
        return selectedPackage.getElementName() + "." + getName().toLowerCase();
    }

    public boolean getRevealInRoot() {
        return revealInRoot;
    }

    public void setRevealInRoot(boolean revealInRoot) {
        firePropertyChange("revealInRoot", this.revealInRoot, this.revealInRoot = revealInRoot);
    }

    public boolean getRevealInRootLayout() {
        return revealInRootLayout;
    }

    public void setRevealInRootLayout(boolean revealInRootLayout) {
        firePropertyChange("revealInRootLayout", this.revealInRootLayout, this.revealInRootLayout = revealInRootLayout);
    }

    public boolean getRevealInSlot() {
        return revealInSlot;
    }

    public void setRevealInSlot(boolean revealInSlot) {
        firePropertyChange("revealInSlot", this.revealInSlot, this.revealInSlot = revealInSlot);
    }

    public void setNameTokenUnit(ICompilationUnit nameTokenunit) {
        this.nameTokenUnit = nameTokenunit;
    }

    public String getNameTokenUnitImport() {
        if (nameTokenUnit == null) {
            return "";
        }

        IType itype = null;
        try {
            itype = nameTokenUnit.getTypes()[0];
        } catch (JavaModelException e) {
            e.printStackTrace();
            return "";
        }
        String importString = "import " + itype.getFullyQualifiedName() + ";";
        return importString;
    }

    public String getNameTokenWithClass() {
        if (nameTokenUnit == null) {
            return "";
        }

        String s = nameTokenUnit.getElementName().replace(".java", "") + "." + nameToken;
        return s;
    }

    public ICompilationUnit getNameTokenUnit() {
        return nameTokenUnit;
    }

    @Override
    public String toString() {
        String s = "{ PresenterConfigModel: ";
        // s += "javaProject=" + javaProject.toString() + " ";
        s += "name=" + name + " ";
        s += "path=" + path + " ";
        s += " }";
        return s;
    }
}
