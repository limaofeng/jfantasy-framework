/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.util.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ScriptHelpTest {

  @Test
  public void mainTest() {
    // final String dir = "C:/workspace/fantasy_projects/itsm/WebContent/";
    // String commonJs = FileUtil.readFile(dir + "static/js/common.js");
    // // System.out.println( commonJs);
    // // 去掉注释掉的js
    // commonJs = RegexpUtil.replace(commonJs, "//jQuery\\.include\\([^\\n]*\\);",
    // new
    // RegexpUtil.AbstractReplaceCallBack() {
    //
    // @Override
    // public String doReplace(String text, int index, Matcher matcher) {
    // return "";
    // }
    //
    // });
    // commonJs = RegexpUtil.replace(commonJs,
    // "jQuery\\.include\\(\\[([^\\n]*)\\]\\);", new
    // RegexpUtil.AbstractReplaceCallBack() {
    //
    // @Override
    // public String doReplace(String text, int index, Matcher matcher) {
    // StringBuilder newJs = new StringBuilder();
    // LOG.debug("=>" + index);
    // for (String js : $(1).split(",")) {
    // js = js.replaceAll("^'|'$", "");
    // if (js.endsWith(".css")) {
    // continue;
    // }
    // newJs.append(FileUtil.readFile(dir + js)).append("\n");
    // }
    // return newJs.toString().replaceAll("\\\\", "\\u005C").replaceAll("\\$",
    // "\\u0024");
    // }
    //
    // });
    // commonJs = RegexpUtil.replace(commonJs, "jQuery\\.include\\(([^\\n]*)\\);",
    // new
    // RegexpUtil.AbstractReplaceCallBack() {
    //
    // @Override
    // public String doReplace(String text, int index, Matcher matcher) {
    // String js = $(1).replaceAll("^'|'$", "");
    // if (!js.endsWith(".css")) {
    // if (StringUtil.isBlank(js)) {
    // LOG.debug(js + "|" + text);
    // }
    // return FileUtil.readFile(dir + js).replaceAll("\\$",
    // "\\u0024").replaceAll("\\\\", "\\u005C") + "\n";
    // }
    // return text;
    // }
    //
    // });
    // FileUtil.writeFile(commonJs.replaceAll("u0024", "\\$").replaceAll("u005C",
    // "\\\\"),
    // dir + "static/js/common.min.js");

  }
}
