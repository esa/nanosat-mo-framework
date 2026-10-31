/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
package esa.mo.com.impl.util;

import org.ccsds.moims.mo.com.structures.ExpressionOperator;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.junit.Assert;
import org.junit.Test;

/**
 * Holds how the ordering operators compare strings.
 *
 * A string is ordered by its numeric value. Archive query filters and the
 * validity expressions of parameters come from the ground, so a string that
 * is not a number must leave the expression not evaluated (null), and not
 * throw a NumberFormatException out of the provider.
 */
public class HelperCOMExpressionTest {

    private static final ExpressionOperator[] ORDERING = {
        ExpressionOperator.GREATER,
        ExpressionOperator.GREATER_OR_EQUAL,
        ExpressionOperator.LESS,
        ExpressionOperator.LESS_OR_EQUAL
    };

    @Test
    public void numericStringsAreOrderedByTheirValue() {
        Assert.assertEquals(Boolean.TRUE, evaluate("10", ExpressionOperator.GREATER, "9"));
        Assert.assertEquals(Boolean.TRUE, evaluate("9", ExpressionOperator.LESS, "10"));
        Assert.assertEquals(Boolean.TRUE, evaluate("2.5", ExpressionOperator.GREATER_OR_EQUAL, "2.5"));
        Assert.assertEquals(Boolean.FALSE, evaluate("3", ExpressionOperator.LESS_OR_EQUAL, "2"));
    }

    @Test
    public void aStringThatIsNotANumberIsNotOrdered() {
        for (ExpressionOperator operator : ORDERING) {
            Assert.assertNull(operator.toString(), evaluate("abc", operator, "1"));
            Assert.assertNull(operator.toString(), evaluate("1", operator, "abc"));
        }
    }

    @Test
    public void aStringThatIsNotANumberCanStillBeCompared() {
        Assert.assertEquals(Boolean.TRUE, evaluate("abc", ExpressionOperator.EQUAL, "abc"));
        Assert.assertEquals(Boolean.TRUE, evaluate("abc", ExpressionOperator.CONTAINS, "b"));
    }

    private static Boolean evaluate(String left, ExpressionOperator operator, String right) {
        return HelperCOM.evaluateExpression((Attribute) Attribute.javaType2Attribute(left), operator,
                (Attribute) Attribute.javaType2Attribute(right));
    }
}
