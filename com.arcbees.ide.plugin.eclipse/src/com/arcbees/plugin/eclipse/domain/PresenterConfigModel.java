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

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;


public class PresenterConfigModel extends ModelObject {
    private IJavaProject project;    
    private String name;

    public PresenterConfigModel() {
    }
    
    public void setProject(IJavaProject project) {
        this.project = project;
    }

    public IJavaProject getJavaProject() {
        return project;
    }

    public String getProjectName() {
        return name;
    }

    public void setName(String name) {
        firePropertyChange("name", this.name, this.name = name);
    }
    
    public String getPackageSelection() {
        ISelectionService selectionservice = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
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
                IPath path = selectedPackage.getPath();
                spath = path.toString();
            }
        } catch (Exception e) {
        }
        return spath;
    }
    
    @Override
    public String toString() {
        String s = "{ PresenterConfigModel: ";
        s += "project=" + project.toString() + " ";
        s += "name=" + name + " ";
        s += " }"; 
        return s;
    }
}
