/**
 * Copyright 2011 Les Systèmes Médicaux Imagem Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imagem.gwtpplugin.projectfile.src.server.guice;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.imagem.gwtpplugin.projectfile.ProjectClass;

public class ServletModule extends ProjectClass {

	private static final String C_SERVLET_MODULE = "com.google.inject.servlet.ServletModule";
	private static final String C_ACTION_IMPL = "com.gwtplatform.dispatch.shared.ActionImpl";
	private static final String C_DISPATCH_SERVICE_IMPL = "com.gwtplatform.dispatch.server.guice.DispatchServiceImpl";
	
	public ServletModule(IPackageFragmentRoot root, String fullyQualifiedName) throws JavaModelException {
		super(root, fullyQualifiedName);
	}
	
	public ServletModule(IPackageFragmentRoot root, String packageName, String elementName) throws JavaModelException {
		super(root, packageName, elementName);
		if(type == null) {
			cu.createPackageDeclaration(packageName, null);
			
			cu.createImport(C_SERVLET_MODULE, null, null);
			String contents = "public class " + elementName + " extends ServletModule {\n\n}";
			
			type = cu.createType(contents, null, false, null);
		}
	}
	
	public IMethod createConfigureServletsMethod(String gwtVersion) throws JavaModelException {
		String contents = "";
		
		contents += "@Override\n";
		contents += "public void configureServlets() {\n";
		
		// GWT < 2.1
		if(compare(gwtVersion, "2.1") == -1) {
			String projectName = cu.getJavaProject().getElementName();
			
			cu.createImport(C_ACTION_IMPL, null, null);
			cu.createImport(C_DISPATCH_SERVICE_IMPL, null, null);
			contents += "	serve(\"/" + projectName.toLowerCase() + "/\" + ActionImpl.DEFAULT_SERVICE_NAME).with(DispatchServiceImpl.class);\n\n";
		}
		// GWT >= 2.1
		else {
			cu.createImport(C_ACTION_IMPL, null, null);
			cu.createImport(C_DISPATCH_SERVICE_IMPL, null, null);
			contents += "	serve(\"/\" + ActionImpl.DEFAULT_SERVICE_NAME).with(DispatchServiceImpl.class);\n";
		}
		contents += "}";
		
		// TODO SessionID
		
		return type.createMethod(contents, null, false, null);
	}
	
	private int compare(String v1, String v2) {
		String[] split1 = v1.split("\\.");
		String[] split2 = v2.split("\\.");
		
		int max = split1.length > split2.length ? split1.length : split2.length;
		
		for(int i = 0; i < max; i++) {
			int t1 = getToken(split1, i);
			int t2 = getToken(split2, i);
			
			if(t1 < t2) {
				return -1;
			}
			else if(t1 > t2) {
				return 1;
			}
		}
		return 0;
	}
	
	private int getToken(String[] split, int index) {
		if(index < split.length) {
			return Integer.parseInt(split[index]);
		}
		return 0;
	}
}
