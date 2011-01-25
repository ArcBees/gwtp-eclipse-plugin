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

package com.imagem.gwtpplugin.projectfile.src.server.handler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.imagem.gwtpplugin.projectfile.IProjectFile;
import com.imagem.gwtpplugin.tool.Formatter;

public class ActionHandler implements IProjectFile {

	private final String EXTENSION = ".java";
	private String projectName;
	private String name;
	private String handlerPackage;
	private String actionPackage;
	private boolean isSecure = false;
	
	public ActionHandler(String projectName, String name, String handlerPackage, String actionPackage) {
		this.projectName = projectName;
		this.name = name;
		this.handlerPackage = handlerPackage;
		this.actionPackage = actionPackage;
	}

	public void setSecureAction(boolean isSecure) {
		this.isSecure  = isSecure;
	}

	@Override
	public String getName() {
		return name + "ActionHandler";
	}

	@Override
	public String getPackage() {
		return handlerPackage;
	}

	@Override
	public String getPath() {
		return "src/" + getPackage().replace('.', '/');
	}

	@Override
	public String getExtension() {
		return EXTENSION;
	}

	@Override
	public InputStream openContentStream() {
		String baseHandlerPackage = handlerPackage;
		if(isSecure) {
			while(baseHandlerPackage.length() > 15 && !baseHandlerPackage.endsWith(".server.handler")) {
				baseHandlerPackage = baseHandlerPackage.substring(0, baseHandlerPackage.length() - 1);
			}
		}

		String contents = "package " + getPackage() + ";\n\n";

		//contents += "import java.sql.SQLException;\n\n";

		contents += "import com.google.inject.Inject;\n";
		contents += "import com.gwtplatform.dispatch.server.ExecutionContext;\n";
		if(isSecure)
			contents += "import " + baseHandlerPackage +"." + projectName + "ActionHandler;\n";
		else
			contents += "import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;\n";
		contents += "import com.gwtplatform.dispatch.shared.ActionException;\n";
		contents += "import " + actionPackage + "." + name + "Action;\n";
		contents += "import " + actionPackage + "." + name + "Result;\n\n";

		if(isSecure)
			contents += "public class " + getName() + " extends " + projectName + "ActionHandler<" + name + "Action, " + name + "Result> {\n\n";
		else
			contents += "public class " + getName() + " implements ActionHandler<" + name + "Action, " + name + "Result> {\n\n";

		contents += "	@Inject\n";
		contents += "	public " + getName() + "() {\n";
		contents += "		\n";
		contents += "	}\n\n";

		contents += "	@Override\n";
		contents += "	public " + name + "Result execute(" + name + "Action action, ExecutionContext context) throws ActionException {\n";
		contents += "		" + name + "Result result = null;\n";
		contents += "		try {\n";
		if(isSecure)
			contents += "			updateTimeStamp();\n\n";
		contents += "			// TODO Put your action handling here\n";
		contents += "		}\n";
		contents += "		catch(Exception e) {\n";
		contents += "			// TODO Error handling\n";
		contents += "			throw new ActionException(e);\n";
		contents += "		}\n";
		contents += "		return result;\n";
		contents += "	}\n\n";

		contents += "	@Override\n";
		contents += "	public Class<" + name + "Action> getActionType() {\n";
		contents += "		return " + name + "Action.class;\n";
		contents += "	}\n\n";

		contents += "	@Override\n";
		contents += "	public void undo(" + name + "Action action, " + name + "Result result, ExecutionContext context) throws ActionException {\n";
		contents += "	}\n\n";

		contents += "}";

		return new ByteArrayInputStream(Formatter.formatImports(contents).getBytes());
	}

}
