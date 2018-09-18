package walkingkooka.spreadsheet.engine;

import walkingkooka.ShouldNeverHappenError;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.stack.Stack;
import walkingkooka.collect.stack.Stacks;
import walkingkooka.text.cursor.parser.ParentParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetAdditionParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetBetweenSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetBigDecimalParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetBigIntegerParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReferenceParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCloseParenthesisSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetColumnReferenceParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetDivideSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetDivisionParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetDoubleParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetEqualsParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetEqualsSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetFunctionNameParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetFunctionParameterSeparatorSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetFunctionParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetGreaterThanEqualsParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetGreaterThanEqualsSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetGreaterThanParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetGreaterThanSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetGroupParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelNameParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLessThanEqualsParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLessThanEqualsSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLessThanParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLessThanSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLocalDateParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLocalDateTimeParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLocalTimeParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLongParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetMinusSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetMultiplicationParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetMultiplySymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetNegativeParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetNotEqualsParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetNotEqualsSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetOpenParenthesisSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitor;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetPercentSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetPercentageParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetPlusSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetPowerParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetPowerSymbolParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRangeParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetReferenceKind;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetRowReferenceParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetSubtractionParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetTextParserToken;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetWhitespaceParserToken;
import walkingkooka.tree.visit.Visiting;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SpreadsheetParserTokenVisitor} that handles visiting and updating {@link SpreadsheetCellReferenceParserToken}
 * so cell references after an insert or delete row/column are corrected.
 */
final class BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor extends SpreadsheetParserTokenVisitor {

    /**
     * Accepts a token tree and updates rows and columns.
     */
    static SpreadsheetParserToken expressionFixReferences(final SpreadsheetParserToken token,
                                                          final int xOffset,
                                                          final int yOffset) {
        final BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor visitor = new BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(xOffset, yOffset);
        visitor.accept(token);

        final List<SpreadsheetParserToken> tokens = visitor.children;
        final int count = tokens.size();
        if (1 != count) {
            throw new IllegalStateException("Expected only 1 child but got " + count + "=" + tokens);
        }

        return tokens.get(0);
    }

    /**
     * Package private ctor use static method.
     */
    // @VisibleForTesting
    BasicSpreadsheetEngineCopySpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(final int xOffset, final int yOffset) {
        super();
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetAdditionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetAdditionParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetCellReferenceParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetCellReferenceParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetDivisionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetDivisionParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetEqualsParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetFunctionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetFunctionParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetGreaterThanParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetGreaterThanParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetGroupParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetGroupParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetLessThanParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetLessThanParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetLessThanEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetLessThanEqualsParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetMultiplicationParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetMultiplicationParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetNegativeParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetNegativeParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetNotEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetNotEqualsParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetPercentageParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetPercentageParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetPowerParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetPowerParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetRangeParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetRangeParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetSubtractionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected final void endVisit(final SpreadsheetSubtractionParserToken token) {
        this.exit(token);
        super.endVisit(token);
    }

    // leaf ......................................................................................................

    @Override
    protected final void visit(final SpreadsheetBetweenSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetBigDecimalParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetBigIntegerParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetCloseParenthesisSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetColumnReferenceParserToken token) {
        final SpreadsheetColumnReference reference = token.value();

        SpreadsheetColumnReferenceParserToken replacement;

        final SpreadsheetReferenceKind kind = reference.referenceKind();
        switch (kind) {
            case ABSOLUTE:
                replacement = token;
                break;
            case RELATIVE:
                replacement = this.fixColumnReference(reference);
                break;
            default:
                throw new ShouldNeverHappenError("Unknown " + SpreadsheetColumnReferenceParserToken.class.getSimpleName() + " enum " + kind);
        }

        this.leaf(replacement);
    }

    private SpreadsheetColumnReferenceParserToken fixColumnReference(final SpreadsheetColumnReference reference) {
        final SpreadsheetColumnReference updated = reference.add(this.xOffset);
        return SpreadsheetParserToken.columnReference(updated, updated.toString());
    }

    private final int xOffset;

    @Override
    protected final void visit(final SpreadsheetDivideSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetDoubleParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetFunctionNameParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetFunctionParameterSeparatorSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetGreaterThanSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetGreaterThanEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLabelNameParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLessThanSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLessThanEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLocalDateParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLocalDateTimeParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLocalTimeParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLongParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetMinusSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetMultiplySymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetNotEqualsSymbolParserToken token) {
        this.leaf(token);
        super.visit(token);
    }

    @Override
    protected final void visit(final SpreadsheetOpenParenthesisSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetPercentSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetPlusSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetPowerSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetRowReferenceParserToken token) {
        final SpreadsheetRowReference reference = token.value();

        SpreadsheetRowReferenceParserToken replacement;

        final SpreadsheetReferenceKind kind = reference.referenceKind();
        switch (kind) {
            case ABSOLUTE:
                replacement = token;
                break;
            case RELATIVE:
                replacement = this.fixRowReference(reference);
                break;
            default:
                throw new ShouldNeverHappenError("Unknown " + SpreadsheetRowReferenceParserToken.class.getSimpleName() + " enum " + kind);
        }

        this.leaf(replacement);
    }

    private SpreadsheetRowReferenceParserToken fixRowReference(final SpreadsheetRowReference reference) {
        final SpreadsheetRowReference updated = reference.add(this.yOffset);
        return SpreadsheetParserToken.rowReference(updated, updated.toString());
    }

    private final int yOffset;

    @Override
    protected final void visit(final SpreadsheetTextParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetWhitespaceParserToken token) {
        this.leaf(token);
    }

    private void enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();
    }

    private <P extends SpreadsheetParserToken & ParentParserToken> void exit(final P parent) {
        final List<SpreadsheetParserToken> children = this.children;
        this.children = this.previousChildren.peek();
        this.previousChildren = this.previousChildren.pop();
        this.add(SpreadsheetParserToken.class.cast(parent.setValue(children).setTextFromValues()));
    }

    private void leaf(final SpreadsheetParserToken token) {
        this.add(token);
    }

    private void add(final SpreadsheetParserToken child) {
        Objects.requireNonNull(child, "child");
        this.children.add(child);
    }

    private Stack<List<SpreadsheetParserToken>> previousChildren = Stacks.arrayList();

    private List<SpreadsheetParserToken> children = Lists.array();

    @Override
    public final String toString() {
        return this.children + "," + this.previousChildren;
    }
}
