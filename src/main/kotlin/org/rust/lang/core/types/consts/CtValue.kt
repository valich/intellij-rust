/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.types.consts

import org.rust.lang.utils.evaluation.ExprValue

data class CtValue(val expr: ExprValue) : Const() {
    override fun toString(): String = expr.toString()
}
