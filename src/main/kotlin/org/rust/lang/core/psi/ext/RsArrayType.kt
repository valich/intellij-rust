/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.psi.ext

import org.rust.lang.core.psi.RsArrayType
import org.rust.lang.core.types.Substitution
import org.rust.lang.core.types.ty.TyInteger
import org.rust.lang.utils.evaluation.ExprValue
import org.rust.lang.utils.evaluation.RsConstExprEvaluator

val RsArrayType.isSlice: Boolean get() = greenStub?.isSlice ?: (expr == null)

fun RsArrayType.getArraySize(subst: Substitution): ExprValue.Integer? {
    val expr = expr ?: return null
    return RsConstExprEvaluator.evaluate(expr, TyInteger.USize, subst) as? ExprValue.Integer
}
