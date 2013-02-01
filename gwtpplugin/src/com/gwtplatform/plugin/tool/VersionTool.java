/**
 * Copyright 2011 IMAGEM Solutions TI sant
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

package com.gwtplatform.plugin.tool;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import com.google.gwt.eclipse.core.preferences.GWTPreferences;
import com.gwtplatform.plugin.projectfile.war.Jar;

/**
 *
 * @author Michael Renaud
 *
 */
public class VersionTool {

  public static final String AOPALLIANCE = "aopalliance";

  public static final String GIN_1_5_pre = "gin-1.5-post-gwt-2.2";
  public static final String GIN_2_0 = "gin-2.0";

  public static final String GUICE_3 = "guice-3.0";

  public static final String GUICE_ASSISTEDINJECT_3 = "guice-assistedinject-3.0";

  public static final String GUICE_SERVLET_3 = "guice-servlet-3.0";

  public static final String GWTP_5 = "gwtp-all-0.5";
  public static final String GWTP_7 = "gwtp-all-0.7";

  public static final String JAVAC_INJECT = "javax.inject";

  public static Jar[] getLibs(IProject project, IPath libPath) {
    Jar[] libs = null;
    String version = GWTPreferences.getDefaultRuntime().getVersion();

    try {
      libs = new Jar[7];

      // GWT < 2.2.0
      if (compare(version, "2.2.0") == -1) {
        libs[0] = new Jar(project, libPath, GIN_1_5_pre);
        libs[0].createFile();

        libs[1] = new Jar(project, libPath, GWTP_5);
        libs[1].createFile();
      } else {
        libs[0] = new Jar(project, libPath, GIN_2_0);
        libs[0].createFile();

        libs[1] = new Jar(project, libPath, GWTP_7);
        libs[1].createFile();
      }

      libs[2] = new Jar(project, libPath, AOPALLIANCE);
      libs[2].createFile();

      libs[3] = new Jar(project, libPath, GUICE_3);
      libs[3].createFile();

      libs[4] = new Jar(project, libPath, GUICE_ASSISTEDINJECT_3);
      libs[4].createFile();

      libs[5] = new Jar(project, libPath, GUICE_SERVLET_3);
      libs[5].createFile();

      libs[6] = new Jar(project, libPath, JAVAC_INJECT);
      libs[6].createFile();
    } catch (Exception e) {
      return libs;
    }
    return libs;
  }

  /**
   * Compare versions. Return -1 if v1 < v2, 0 if v1 == v2 and 1 if v1 > v2.
   *
   * @param v1
   *          Client version
   * @param v2
   *          Comparison
   * @return
   */
  public static int compare(String v1, String v2) {
    String[] split1 = v1.split("\\.");
    String[] split2 = v2.split("\\.");

    int max = split1.length > split2.length ? split1.length : split2.length;

    for (int i = 0; i < max; i++) {
      int t1 = getToken(split1, i);
      int t2 = getToken(split2, i);

      if (t1 < t2) {
        return -1;
      } else if (t1 > t2) {
        return 1;
      }
    }
    return 0;
  }

  private static int getToken(String[] split, int index) {
    if (index < split.length) {
      return Integer.parseInt(split[index]);
    }
    return 0;
  }
}
