/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust

import org.rust.lang.RsFileType
import java.nio.file.Path
import java.nio.file.Paths

interface RsTestCase {

    fun getTestDataPath(): String

    companion object {
        const val testResourcesPath = "src/test/resources"
    }
}


fun RsTestCase.pathToSourceTestFile(name: String): Path =
    Paths.get("${RsTestCase.testResourcesPath}/${getTestDataPath()}/$name.rs")

fun RsTestCase.pathToGoldTestFile(name: String): Path =
    Paths.get("${RsTestCase.testResourcesPath}/${getTestDataPath()}/$name.txt")

