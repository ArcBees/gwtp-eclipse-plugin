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

package com.imagem.gwtpplugin.projectfile.src.client.core;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;

/**
 * 
 * @author Michael Renaud
 *
 */
public class Ui {
	
	private IFile file;
	
	public Ui(IPackageFragmentRoot root, String packageName, String elementName) throws CoreException {
		IContainer container = (IContainer) root.createPackageFragment(packageName, false, null).getResource();
		
		file = container.getFile(new Path(elementName + ".ui.xml"));
	}
	
	public IFile getFile() {
		return file;
	}
	
	public IFile createFile() throws CoreException {
		String contents = "<!DOCTYPE ui:UiBinder SYSTEM \"http://dl.google.com/gwt/DTD/xhtml.ent\">\n\n";
		
		contents += "<ui:UiBinder xmlns:ui='urn:ui:com.google.gwt.uibinder'\n";
		contents += "	xmlns:g='urn:import:com.google.gwt.user.client.ui'\n";
		contents += "	ui:generateFormat='com.google.gwt.i18n.rebind.format.PropertiesFormat'\n";
		contents += "	ui:generateKeys='com.google.gwt.i18n.rebind.keygen.MD5KeyGenerator'\n";
		contents += "	ui:generateLocales='default'>\n\n";

		contents += "	<g:HTMLPanel/>\n";
		
		contents += "</ui:UiBinder>\n";
		
		file.create(new ByteArrayInputStream(contents.getBytes()), false, null);
		
		return file;
	}
	
}
