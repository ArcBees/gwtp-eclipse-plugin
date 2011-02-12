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

package com.imagem.gwtpplugin.projectfile.war;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;

public class AppengineWebXml implements IProjectFile {

	private final String EXTENSION = ".xml";
	private final String NAME = "appengine-web";
	private String path;

	public AppengineWebXml(String path) {
		this.path = path;
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getPackage() {
		return "";
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getExtension() {
		return EXTENSION;
	}

	@Override
	public InputStream openContentStream() {
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		contents += "<appengine-web-app xmlns=\"http://appengine.google.com/ns/1.0\">\n\n";
		contents += "	<application></application>\n";
		contents += "	<version>1</version>\n\n";
		
		contents += "	<!-- Configure serving/caching of GWT files -->\n";
		contents += "	<static-files>\n";
		contents += "		<include path=\"**\" />\n\n";
		
		contents += "		<!-- The following line requires App Engine 1.3.2 SDK -->\n";
		contents += "		<include path=\"**.nocache.*\" expiration=\"0s\" />\n\n";
		
		contents += "		<include path=\"**.cache.*\" expiration=\"365d\" />\n";
		contents += "		<exclude path=\"**.gwt.rpc\" />\n";
		contents += "	</static-files>\n\n";
		
		contents += "	<!-- Configure java.util.logging -->\n";
		contents += "	<system-properties>\n";
		contents += "		<property name=\"java.util.logging.config.file\" value=\"WEB-INF/logging.properties\"/>\n";
		contents += "	</system-properties>\n\n";
		
		contents += "</appengine-web-app>";

		return new ByteArrayInputStream(contents.getBytes());
	}

}
