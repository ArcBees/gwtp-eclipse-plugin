/**
 * Copyright 2011 IMAGEM Solutions TI santé
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

package com.imagem.gwtpplugin.projectfile.src;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;

/**
 * 
 * @author Michael Renaud
 *
 */
public class GwtXmlModule {

	private IFile file;
	private IPackageFragmentRoot root;
	
	public GwtXmlModule(IPackageFragmentRoot root, String packageName, String elementName) throws CoreException {
		this.root = root;
		IContainer container = (IContainer) root.createPackageFragment(packageName, false, null).getResource();
		
		file = container.getFile(new Path(elementName + ".gwt.xml"));
	}
	
	public IFile getFile() {
		return file;
	}
	
	public IFile createFile(IType entryPoint, IType ginjector) throws CoreException {
		String projectName = root.getJavaProject().getElementName();
		
		String contents = "<?xml version='1.0' encoding='UTF-8'?>\n";
		contents += "<module rename-to='" + projectName.toLowerCase() + "'>\n";
		contents += "	<!-- Inherit the core Web Toolkit stuff.                        -->\n";
		contents += "	<inherits name='com.google.gwt.user.User'/>\n";
		contents += "	<inherits name='com.google.gwt.inject.Inject'/>\n";
		contents += "	<inherits name='com.google.gwt.resources.Resources'/>\n\n";

		contents += "	<!-- Inherit the default GWT style sheet.  You can change       -->\n";
		contents += "	<!-- the theme of your GWT application by uncommenting          -->\n";
		contents += "	<!-- any one of the following lines.                            -->\n";
		contents += "	<inherits name='com.google.gwt.user.theme.standard.Standard'/>\n";
		contents += "	<!-- <inherits name='com.google.gwt.user.theme.chrome.Chrome'/> -->\n";
		contents += "	<!-- <inherits name='com.google.gwt.user.theme.dark.Dark'/>     -->\n\n";

		contents += "	<!-- Other module inherits                                      -->\n";
		contents += "	<inherits name='com.gwtplatform.mvp.Mvp'/>\n";
		contents += "	<inherits name='com.gwtplatform.dispatch.Dispatch'/>\n\n";

		contents += "	<!-- Specify the app entry point class.                         -->\n";
		contents += "	<entry-point class='" + entryPoint.getFullyQualifiedName() + "'/>\n\n";

		contents += "	<!-- Specify the paths for translatable code                    -->\n";
		contents += "	<source path='client'/>\n";
		contents += "	<source path='shared'/>\n\n";

		contents += "	<define-configuration-property name='gin.ginjector' is-multi-valued='false' />\n";
		contents += "	<set-configuration-property name='gin.ginjector' value='" + ginjector.getFullyQualifiedName() + "' />\n\n";
		
		contents += "</module>";
		
		file.create(new ByteArrayInputStream(contents.getBytes()), false, null);
		
		return file;
	}
	
}
