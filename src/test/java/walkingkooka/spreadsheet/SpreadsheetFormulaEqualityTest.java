package walkingkooka.spreadsheet;

import org.junit.*;
import walkingkooka.test.*;
import walkingkooka.tree.expression.*;

public final class SpreadsheetFormulaEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetFormula> {

    @Test
    public void testDifferentSpreadsheetFormula() {
        this.checkNotEquals(SpreadsheetFormula.with(ExpressionNode.text("different")));
    }

    @Override
    protected SpreadsheetFormula createObject() {
        return SpreadsheetFormula.with(ExpressionNode.text("text"));
    }
}
