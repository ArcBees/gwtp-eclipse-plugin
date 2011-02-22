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

package com.imagem.gwtpplugin.tool;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import com.google.gwt.eclipse.core.preferences.GWTPreferences;
import com.imagem.gwtpplugin.projectfile.war.Jar;

/**
 * 
 * @author Michael Renaud
 *
 */
@SuppressWarnings("restriction")
public class VersionTool {

	public static final String AOPALLIANCE = "aopalliance";

	public static final String GIN_137 = "gin-r137";
	public static final String GIN_170 = "gin-1.0-r170";

	public static final String GUICE_2 = "guice-2.0";
	public static final String GUICE_3 = "guice-3.0-rc2";

	public static final String GUICE_ASSISTEDINJECT_3 = "guice-assistedinject-3.0-rc2";

	public static final String GUICE_SERVLET_2 = "guice-servlet-2.0";
	public static final String GUICE_SERVLET_3 = "guice-servlet-3.0-rc2";

	public static final String GWTP_4 = "gwtp-0.4";
	public static final String GWTP_5 = "gwtp-all-0.5";
	public static final String GWTP_6 = "gwtp-all-0.6-SNAPSHOT";

	public static final String JAVAC_INJECT = "javax.inject";

	public static Jar[] getLibs(IProject project, IPath libPath) {
		Jar[] libs = null;
		String version = GWTPreferences.getDefaultRuntime().getVersion();

		try {
			// GWT < 2.2.0
			if(compare(version, "2.2.0") == -1) {
				libs = new Jar[5];

				libs[0] = new Jar(project, libPath, AOPALLIANCE);
				libs[0].createFile();

				libs[1] = new Jar(project, libPath, GIN_137);
				libs[1].createFile();

				libs[2] = new Jar(project, libPath, GUICE_2);
				libs[2].createFile();

				libs[3] = new Jar(project, libPath, GUICE_SERVLET_2);
				libs[3].createFile();

				libs[4] = new Jar(project, libPath, GWTP_5);
				libs[4].createFile();
			}
			else {
				libs = new Jar[7];

				libs[0] = new Jar(project, libPath, AOPALLIANCE);
				libs[0].createFile();

				libs[1] = new Jar(project, libPath, GIN_170);
				libs[1].createFile();

				libs[2] = new Jar(project, libPath, GUICE_3);
				libs[2].createFile();

				libs[3] = new Jar(project, libPath, GUICE_ASSISTEDINJECT_3);
				libs[3].createFile();

				libs[4] = new Jar(project, libPath, GUICE_SERVLET_3);
				libs[4].createFile();

				libs[5] = new Jar(project, libPath, GWTP_6);
				libs[5].createFile();

				libs[6] = new Jar(project, libPath, JAVAC_INJECT);
				libs[6].createFile();
			}
		}
		catch (Exception e) {
			return libs;
		}
		return libs;
	}

	public static int compare(String v1, String v2) {
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

	private static int getToken(String[] split, int index) {
		if(index < split.length) {
			return Integer.parseInt(split[index]);
		}
		return 0;
	}
}
