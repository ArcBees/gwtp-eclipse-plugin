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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;

public class PackageHierarchyElement {
    private String packageElementName;
    private IPackageFragment packageFragment;
    private HashMap<String, ICompilationUnit> units;
    
    public PackageHierarchyElement(String packageName, IPackageFragment packageFragment) {
        this.packageFragment = packageFragment;
        this.packageElementName = packageName;
        
        units = new HashMap<String, ICompilationUnit>();
    }
    
    public String getPackageElementName() {
        return packageElementName;
    }

    public IPackageFragment getPackageFragment() {
        return packageFragment;
    }
   
    public void addUnit(ICompilationUnit unit) {
        units.put(unit.getElementName(), unit);
    }
   
    public Map<String, ICompilationUnit> getUnits() {
        return units;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packageElementName == null) ? 0 : packageElementName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PackageHierarchyElement other = (PackageHierarchyElement) obj;
        if (packageElementName == null) {
            if (other.packageElementName != null)
                return false;
        } else if (!packageElementName.equals(other.packageElementName))
            return false;
        return true;
    }
}
