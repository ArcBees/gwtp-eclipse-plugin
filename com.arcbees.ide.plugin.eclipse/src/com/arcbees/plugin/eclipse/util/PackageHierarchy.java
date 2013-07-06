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

package com.arcbees.plugin.eclipse.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.ResolvedSourceType;

import com.arcbees.plugin.eclipse.domain.PresenterConfigModel;

public class PackageHierarchy {
    private IProgressMonitor progressMonitor;
    private PresenterConfigModel presenterConfigModel;
    private Map<String, PackageHierarchyElement> packagesIndex;

    public PackageHierarchy(PresenterConfigModel presenterConfigModel, IProgressMonitor progressMonitor) {
        this.presenterConfigModel = presenterConfigModel;
        this.progressMonitor = progressMonitor;
    }

    public void run() {
        packagesIndex = new HashMap<String, PackageHierarchyElement>();

        // TODO logger
        System.out.println("Creating package index.");

        startIndexing();

        // TODO logger
        System.out.println("Finished package index.");
    }

    public PackageHierarchyElement find(String packageElementName) {
        return packagesIndex.get(packageElementName);
    }

    public PackageHierarchyElement findParent(String packageElementName) {
        if (!packageElementName.contains(".")) {
            return null;
        }

        String[] packageUnits = packageElementName.split("\\.");
        String parentPackageElementName = "";
        for (int i = 0; i < packageUnits.length - 1; i++) {
            parentPackageElementName += packageUnits[i];
            if (i < packageUnits.length - 2) {
                parentPackageElementName += ".";
            }
        }
        return find(parentPackageElementName);
    }

    public PackageHierarchyElement findParentClient(String packageElementName) {
        if (!packageElementName.contains(".")) {
            return null;
        }

        String parentPackageElementName = getClientPackageElementName(packageElementName);

        return find(parentPackageElementName);
    }

    public PackageHierarchyElement findParentClientAndAddPackage(String packageElementName, String andFindPackage) {
        if (!packageElementName.contains(".")) {
            return null;
        }

        String parentPackageElementName = getClientPackageElementName(packageElementName);
        parentPackageElementName += "." + andFindPackage;

        return find(parentPackageElementName);
    }

    /**
     * Takes the name like `tld.domain.project.client.child` and returns `tld.domain.project.client`
     */
    public String getClientPackageElementName(String packageElementName) {
        {
            if (packageElementName.matches(".*client$")) {
                return packageElementName;
            }

            String[] packageUnits = packageElementName.split("\\.");
            String parentPackageElementName = "";
            for (int i = 0; i < packageUnits.length - 1; i++) {
                parentPackageElementName += packageUnits[i];
                if (i < packageUnits.length - 2) {
                    parentPackageElementName += ".";
                }

                if (packageUnits[i].equals("client")) {
                    break;
                }
            }
            
            if (parentPackageElementName.matches(".*\\.$")) {
                parentPackageElementName = parentPackageElementName.replaceAll("\\.$", "");
            }
            
            return parentPackageElementName;
        }

    }

    public boolean isParentTheClientPackage(String packageElementName) {
        if (!packageElementName.contains(".")) {
            return false;
        }

        if (packageElementName.matches(".*client$")) {
            return true;
        } else {
            return false;
        }
    }

    public ICompilationUnit findFirstInterfaceType(String findType) {
        ICompilationUnit unit = null;
        for (String packageElementName : packagesIndex.keySet()) {
            unit = findFirstInterfaceTypeInPackage(packageElementName, findType);
            if (unit != null) {
                break;
            }
        }
        return unit;
    }

    public ICompilationUnit findFirstInterfaceTypeInPackage(String packageElementName, String findTypeName) {
        PackageHierarchyElement hierarchyElement = packagesIndex.get(packageElementName);
        Map<String, ICompilationUnit> units = hierarchyElement.getUnits();

        ICompilationUnit foundUnit = null;
        for (String key : units.keySet()) {
            ICompilationUnit unit = units.get(key);
            boolean hasType = findInterfaceUseInUnit(unit, findTypeName);
            if (hasType) {
                foundUnit = unit;
                break;
            }
        }
        return foundUnit;
    }

    public ICompilationUnit findInterfaceTypeInParentPackage(IPackageFragment packageSelected, String findTypeName) {
        ICompilationUnit[] units = null;
        try {
            units = packageSelected.getCompilationUnits();
        } catch (JavaModelException e) {
            e.printStackTrace();
            // TODO display error
            return null;
        }

        for (ICompilationUnit unit : units) {
            boolean found = findInterfaceUseInUnit(unit, findTypeName);
            if (found == true) {
                return unit;
            }
        }

        return null;
    }

    // TODO maybe use or remove?
    public void findAnyOfThisType(String type) {
        IType objectType = null;

        try {
            objectType = presenterConfigModel.getJavaProject().findType(type);
        } catch (JavaModelException e) {
            e.printStackTrace();
        }

        System.out.println("test");
    }

    private boolean findInterfaceUseInUnit(ICompilationUnit unit, String findTypeName) {
        try {
            for (IType type : unit.getTypes()) {
                ITypeHierarchy hierarchy = type.newSupertypeHierarchy(progressMonitor);
                IType[] interfaces = hierarchy.getAllInterfaces();
                for (IType checkInterface : interfaces) {
                    System.out.println("search unit checkInterface=" + checkInterface.getElementName()
                            + " findTypeName=" + findTypeName);
                    if (checkInterface.getFullyQualifiedName('.').contains(findTypeName)) {
                        return true;
                    }
                }
            }
        } catch (JavaModelException e) {
            // TODO display error
            e.printStackTrace();
        }
        return false;
    }

    private void startIndexing() {
        IPackageFragment[] packages = null;
        try {
            packages = presenterConfigModel.getJavaProject().getPackageFragments();
            for (IPackageFragment mypackage : packages) {
                if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    indexPackage(mypackage);
                }
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
    }

    private void indexPackage(IPackageFragment packageFragment) throws JavaModelException {
        String packageName = packageFragment.getElementName();
        PackageHierarchyElement packageIndex = new PackageHierarchyElement(packageName, packageFragment);
        packagesIndex.put(packageName, packageIndex);
        for (ICompilationUnit unit : packageFragment.getCompilationUnits()) {
            packageIndex.addUnit(unit);
        }
    }
    
    public List<ResolvedSourceType> findClassName(String name) {
        int searchFor = IJavaSearchConstants.CLASS;
        int limitTo = IJavaSearchConstants.TYPE;
        int matchRule = SearchPattern.R_EXACT_MATCH;
        SearchPattern searchPattern = SearchPattern.createPattern(name, searchFor, limitTo, matchRule);

        IJavaProject project = presenterConfigModel.getJavaProject();
        IJavaElement[] elements = new IJavaElement[] { project };
        IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

        final List<ResolvedSourceType> found = new ArrayList<ResolvedSourceType>();
        SearchRequestor requestor = new SearchRequestor() {
            public void acceptSearchMatch(SearchMatch match) {
                // TODO
                System.out.println(match);
                Object element = match.getElement();
                found.add((ResolvedSourceType) element);
            }
        };

        SearchEngine searchEngine = new SearchEngine();
        SearchParticipant[] particpant = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
        try {
            searchEngine.search(searchPattern, particpant, scope, requestor, new NullProgressMonitor());
        } catch (CoreException e) {
            // TODO
            e.printStackTrace();
        } 
        return found;
    }
}
