/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.Range;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.store.SpreadsheetRowStore;
import walkingkooka.spreadsheet.store.SpreadsheetRowStores;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetRowReferenceTest extends SpreadsheetColumnOrRowReferenceTestCase<SpreadsheetRowReference> {

    @Test
    public void testMin() {
        final SpreadsheetRowReference min = SpreadsheetRowReference.MIN;
        this.checkEquals(0, min.value(), "value");
        this.checkEquals(SpreadsheetReferenceKind.RELATIVE, min.referenceKind(), "referenceKind");
    }

    @Test
    public void testMax() {
        final SpreadsheetRowReference max = SpreadsheetRowReference.MAX;
        this.checkEquals(SpreadsheetRowReference.MAX_VALUE, max.value(), "value");
        this.checkEquals(SpreadsheetReferenceKind.RELATIVE, max.referenceKind(), "referenceKind");
    }

    @Test
    public void testSetColumnNullFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetReferenceKind.ABSOLUTE.row(23).setColumn(null));
    }

    @Test
    public void testSetColumn() {
        final SpreadsheetColumnReference column = SpreadsheetReferenceKind.ABSOLUTE.column(1);
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.ABSOLUTE.row(23);

        final SpreadsheetCellReference cell = row.setColumn(column);
        this.checkEquals(column, cell.column(), "column");
        this.checkEquals(row, cell.row(), "row");
    }

    // count............................................................................................................

    @Test
    public void testCountA() {
        this.countAndCheck("2", 1);
    }

    @Test
    public void testCountZ() {
        this.countAndCheck("$99", 1);
    }

    // testTestRow......................................................................................................

    @Test
    public void testTestRowAbove() {
        this.testRowAndCheck(
                "3",
                "2",
                false
        );
    }

    @Test
    public void testTestRowBelow() {
        this.testRowAndCheck(
                "3",
                "4",
                false
        );
    }

    @Test
    public void testTestRow() {
        this.testRowAndCheck(
                "3",
                "3",
                true
        );
    }

    @Test
    public void testTestRow2() {
        this.testRowAndCheck(
                "$3",
                "3",
                true
        );
    }

    // Predicate........................................................................................................

    @Test
    public void testTestDifferentRowFalse() {
        final SpreadsheetRowReference selection = this.createSelection();
        this.testFalse(selection
                .add(1)
                .setColumn(this.columnReference())
        );
    }

    @Test
    public void testTestDifferentRowKindTrue() {
        final SpreadsheetRowReference selection = this.createSelection();
        this.testTrue(selection
                .setReferenceKind(selection.referenceKind().flip())
                .setColumn(this.columnReference())
        );
    }

    private SpreadsheetColumnReference columnReference() {
        return SpreadsheetSelection.parseColumn("A");
    }

    // range............................................................................................................

    @Test
    public void testRange() {
        final SpreadsheetRowReference lower = SpreadsheetSelection.parseRow("2");
        final SpreadsheetRowReference upper = SpreadsheetSelection.parseRow("4");

        this.checkEquals(
                Range.greaterThanEquals(lower).and(Range.lessThanEquals(upper)),
                lower.range(upper)
        );
    }

    // toRelative........................................................................................................

    @Test
    public void testToRelativeAbsolute() {
        final int value = 123;
        this.toRelativeAndCheck(SpreadsheetReferenceKind.ABSOLUTE.row(value), SpreadsheetReferenceKind.RELATIVE.row(value));
    }

    @Test
    public void testToRelativeRelative() {
        this.toRelativeAndCheck(SpreadsheetReferenceKind.RELATIVE.row(123));
    }

    @Test
    public void testEqualReferenceKindIgnored() {
        this.compareToAndCheckEquals(
                SpreadsheetReferenceKind.ABSOLUTE.row(VALUE),
                SpreadsheetReferenceKind.RELATIVE.row(VALUE));
    }

    @Test
    public void testLess() {
        this.compareToAndCheckLess(
                SpreadsheetReferenceKind.ABSOLUTE.row(VALUE),
                SpreadsheetReferenceKind.ABSOLUTE.row(VALUE + 999));
    }

    @Test
    public void testLess2() {
        this.compareToAndCheckLess(
                SpreadsheetReferenceKind.ABSOLUTE.row(VALUE),
                SpreadsheetReferenceKind.RELATIVE.row(VALUE + 999));
    }

    @Test
    public void testArraySort() {
        final SpreadsheetRowReference row1 = SpreadsheetSelection.parseRow("1");
        final SpreadsheetRowReference row2 = SpreadsheetSelection.parseRow("2");
        final SpreadsheetRowReference row3 = SpreadsheetSelection.parseRow("3");
        final SpreadsheetRowReference row4 = SpreadsheetSelection.parseRow("$4");

        this.compareToArraySortAndCheck(row3, row1, row4, row2,
                row1, row2, row3, row4);
    }

    // parseString.....................................................................................................

    @Test
    public void testParseEmptyFails() {
        this.parseStringFails("", IllegalArgumentException.class);
    }

    @Test
    public void testParseInvalidFails() {
        this.parseStringFails("!9", IllegalArgumentException.class);
    }

    @Test
    public void testParseAbsolute() {
        this.parseStringAndCheck("$1", SpreadsheetReferenceKind.ABSOLUTE.row(0));
    }

    @Test
    public void testParseAbsolute2() {
        this.parseStringAndCheck("$2", SpreadsheetReferenceKind.ABSOLUTE.row(1));
    }

    @Test
    public void testParseRelative() {
        this.parseStringAndCheck("1", SpreadsheetReferenceKind.RELATIVE.row(0));
    }

    @Test
    public void testParseRelative2() {
        this.parseStringAndCheck("2", SpreadsheetReferenceKind.RELATIVE.row(1));
    }

    // parseRowRange....................................................................................................

    @Test
    public void testParseRange() {
        this.checkEquals(
                SpreadsheetRowReferenceRange.with(
                        Range.greaterThanEquals(SpreadsheetSelection.parseRow("2"))
                                .and(Range.lessThanEquals(SpreadsheetSelection.parseRow("4")))
                ),
                SpreadsheetSelection.parseRowRange("2:4"));
    }

    @Test
    public void testParseRange2() {
        this.checkEquals(
                SpreadsheetRowReferenceRange.with(
                        Range.greaterThanEquals(SpreadsheetSelection.parseRow("$2"))
                                .and(Range.lessThanEquals(SpreadsheetSelection.parseRow("$5")))
                ),
                SpreadsheetSelection.parseRowRange("$2:$5"));
    }

    // add..............................................................................................................

    @Test
    public void testAdd() {
        this.checkEquals(
                SpreadsheetSelection.parseRow("9"),
                SpreadsheetSelection.parseRow("7").add(2)
        );
    }

    // addSaturated......................................................................................................

    @Test
    public void testAddSaturated() {
        this.checkEquals(
                SpreadsheetSelection.parseRow("9"),
                SpreadsheetSelection.parseRow("7").addSaturated(2)
        );
    }

    // max.............................................................................................................

    private final static boolean LEFT = true;
    private final static boolean RIGHT = !LEFT;

    @Test
    public void testMaxNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().max(null));
    }

    @Test
    public void testMaxLess() {
        this.maxAndCheck("5", "6", RIGHT);
    }

    @Test
    public void testMaxLess2() {
        this.maxAndCheck("$5", "6", RIGHT);
    }

    @Test
    public void testMaxLess3() {
        this.maxAndCheck("5", "$6", RIGHT);
    }

    @Test
    public void testMaxLess4() {
        this.maxAndCheck("$5", "$6", RIGHT);
    }

    @Test
    public void testMaxEqual() {
        this.maxAndCheck("5", "5", LEFT);
    }

    @Test
    public void testMaxEqual2() {
        this.maxAndCheck("$5", "5", LEFT);
    }

    @Test
    public void testMaxEqual3() {
        this.maxAndCheck("5", "$5", LEFT);
    }

    @Test
    public void testMaxEqual4() {
        this.maxAndCheck("$5", "$5", LEFT);
    }

    @Test
    public void testMaxMore() {
        this.maxAndCheck("6", "5", LEFT);
    }

    @Test
    public void testMaxMore2() {
        this.maxAndCheck("$6", "5", LEFT);
    }

    @Test
    public void testMaxMore3() {
        this.maxAndCheck("6", "$5", LEFT);
    }

    @Test
    public void testMaxMore4() {
        this.maxAndCheck("$6", "$5", LEFT);
    }

    private void maxAndCheck(final String reference,
                             final String other,
                             final boolean RIGHT) {
        this.maxAndCheck(SpreadsheetSelection.parseRow(reference),
                SpreadsheetSelection.parseRow(other),
                RIGHT);
    }

    private void maxAndCheck(final SpreadsheetRowReference reference,
                             final SpreadsheetRowReference other,
                             final boolean left) {
        this.checkEquals(left ? reference : other,
                reference.max(other),
                () -> "max of " + reference + " and " + other);
    }

    // min.............................................................................................................

    @Test
    public void testMinNullFails() {
        assertThrows(NullPointerException.class, () -> this.createSelection().min(null));
    }

    @Test
    public void testMinLess() {
        this.minAndCheck("5", "6", LEFT);
    }

    @Test
    public void testMinLess2() {
        this.minAndCheck("$5", "6", LEFT);
    }

    @Test
    public void testMinLess3() {
        this.minAndCheck("5", "$6", LEFT);
    }

    @Test
    public void testMinLess4() {
        this.minAndCheck("$5", "$6", LEFT);
    }

    @Test
    public void testMinEqual() {
        this.minAndCheck("5", "5", LEFT);
    }

    @Test
    public void testMinEqual2() {
        this.minAndCheck("$5", "5", LEFT);
    }

    @Test
    public void testMinEqual3() {
        this.minAndCheck("5", "$5", LEFT);
    }

    @Test
    public void testMinEqual4() {
        this.minAndCheck("$5", "$5", LEFT);
    }

    @Test
    public void testMinRight() {
        this.minAndCheck("6", "5", RIGHT);
    }

    @Test
    public void testMinRight2() {
        this.minAndCheck("$6", "5", RIGHT);
    }

    @Test
    public void testMinRight3() {
        this.minAndCheck("6", "$5", RIGHT);
    }

    @Test
    public void testMinRight4() {
        this.minAndCheck("$6", "$5", RIGHT);
    }

    private void minAndCheck(final String reference,
                             final String other,
                             final boolean left) {
        this.minAndCheck(SpreadsheetSelection.parseRow(reference),
                SpreadsheetSelection.parseRow(other),
                left);
    }

    private void minAndCheck(final SpreadsheetRowReference reference,
                             final SpreadsheetRowReference other,
                             final boolean left) {
        this.checkEquals(left ? reference : other,
                reference.min(other),
                () -> "min of " + reference + " and " + other);
    }

    // toCellRange.....................................................................................................

    @Test
    public void testToCellRange() {
        this.toCellRangeAndCheck(
                "2",
                "A2"
        );
    }

    @Test
    public void testToCellRange2() {
        this.toCellRangeAndCheck(
                "3",
                "C3"
        );
    }

    // testCellRange.....................................................................................................

    @Test
    public void testTestCellRangeBefore() {
        this.testCellRangeAndCheck(
                "2",
                "C3:E5",
                false
        );
    }

    @Test
    public void testTestCellRangeLeftEdge() {
        this.testCellRangeAndCheck(
                "3",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeCenter() {
        this.testCellRangeAndCheck(
                "4",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeRightEdge() {
        this.testCellRangeAndCheck(
                "5",
                "C3:E5",
                true
        );
    }

    @Test
    public void testTestCellRangeAfter() {
        this.testCellRangeAndCheck(
                "6",
                "C3:E5",
                false
        );
    }

    // testTestColumn...................................................................................................

    @Test
    public void testTestColumn() {
        this.testColumnAndCheck(
                "1",
                "A",
                false
        );
    }

    // testRowRange...................................................................................................,,,

    @Test
    public void testRowRangeSpreadsheetRowRange() {
        final SpreadsheetRowReference lower = SpreadsheetSelection.parseRow("1");
        final SpreadsheetRowReference upper = SpreadsheetSelection.parseRow("2");

        this.checkEquals(
                SpreadsheetRowReferenceRange.with(
                        Range.greaterThanEquals(lower)
                                .and(
                                        Range.lessThanEquals(upper)
                                )
                ),
                lower.rowRange(upper),
                () -> lower + " rowRange " + upper
        );
    }

    @Test
    public void testToRowRange() {
        final SpreadsheetRowReference row = this.createSelection();

        this.checkEquals(
                SpreadsheetRowReferenceRange.with(Range.singleton(row)),
                row.toRowRange(),
                () -> row + ".toRowRange"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetSelection.parseRow("12"),
                "row 12" + EOL
        );
    }

    // JsonNodeTesting..................................................................................................

    @Test
    public void testUnmarshallStringInvalidFails() {
        this.unmarshallFails(JsonNode.string("!9"));
    }

    @Test
    public void testUnmarshallStringAbsolute() {
        this.unmarshallAndCheck(JsonNode.string("$1"), SpreadsheetReferenceKind.ABSOLUTE.row(0));
    }

    @Test
    public void testUnmarshallStringAbsolute2() {
        this.unmarshallAndCheck(JsonNode.string("$2"), SpreadsheetReferenceKind.ABSOLUTE.row(1));
    }

    @Test
    public void testUnmarshallStringRelative() {
        this.unmarshallAndCheck(JsonNode.string("1"), SpreadsheetReferenceKind.RELATIVE.row(0));
    }

    @Test
    public void testUnmarshallStringRelative2() {
        this.unmarshallAndCheck(JsonNode.string("2"), SpreadsheetReferenceKind.RELATIVE.row(1));
    }

    // isHidden.........................................................................................................

    @Test
    public void testIsHiddenColumnHidden() {
        this.isHiddenAndCheck(
                "1",
                Predicates.fake(),
                Predicates.is(SpreadsheetSelection.parseRow("1")),
                true
        );
    }

    @Test
    public void testIsHiddenNotHidden() {
        this.isHiddenAndCheck(
                "1",
                Predicates.fake(),
                Predicates.never(),
                false
        );
    }

    // navigate.........................................................................................................

    @Test
    public void testLeft() {
        this.leftAndCheck(
                "1",
                "1"
        );
    }

    @Test
    public void testLeftHiddenColumn() {
        final SpreadsheetRowStore store = SpreadsheetRowStores.treeMap();

        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("2");
        store.save(row.row().setHidden(true));

        this.leftAndCheck(
                row,
                store
        );
    }

    @Test
    public void testUp() {
        this.upAndCheck(
                "2",
                "1"
        );
    }

    @Test
    public void testUpFirstRow() {
        this.upAndCheck(
                "1",
                "1"
        );
    }

    @Test
    public void testUpSkipsHidden() {
        final SpreadsheetRowStore store = SpreadsheetRowStores.treeMap();
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));

        this.upAndCheck(
                "4",
                store,
                "2"
        );
    }

    @Test
    public void testUpLastRow() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.upAndCheck(
                row,
                row.add(-1)
        );
    }

    @Test
    public void testRight() {
        this.rightAndCheck(
                "2",
                "2"
        );
    }

    @Test
    public void testRightHiddenColumn() {
        final SpreadsheetRowStore store = SpreadsheetRowStores.treeMap();

        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("2");
        store.save(row.row().setHidden(true));

        this.rightAndCheck(
                row,
                store
        );
    }

    @Test
    public void testDown() {
        this.downAndCheck(
                "2",
                "3"
        );
    }

    @Test
    public void testDownFirstRow() {
        this.downAndCheck(
                "1",
                "2"
        );
    }

    @Test
    public void testDownLastRow() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.downAndCheck(
                row,
                row
        );
    }

    @Test
    public void testDownSkipsHidden() {
        final SpreadsheetRowStore store = SpreadsheetRowStores.treeMap();
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));

        this.downAndCheck(
                "2",
                store,
                "4"
        );
    }
    
    // extendRange......................................................................................................

    @Test
    public void testExtendRange() {
        this.extendRangeAndCheck(
                "2",
                "3",
                "2:3"
        );
    }

    @Test
    public void testExtendRange2() {
        this.extendRangeAndCheck(
                "3",
                "2",
                "2:3"
        );
    }

    @Test
    public void testExtendRangeFirstRow() {
        this.extendRangeAndCheck(
                "1",
                "1"
        );
    }

    @Test
    public void testExtendRangeSame() {
        final String range = "123";

        this.extendRangeAndCheck(
                range,
                range
        );
    }

    @Override
    SpreadsheetRowReferenceRange parseRange(final String range) {
        return SpreadsheetSelection.parseRowRange(range);
    }

    // extendXXXX.......................................................................................................

    @Test
    public void testExtendUp() {
        this.extendUpAndCheck(
                "3",
                SpreadsheetViewportSelectionAnchor.NONE,
                "2:3",
                SpreadsheetViewportSelectionAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendUpFirstRow() {
        final String row = "1";

        this.extendUpAndCheck(
                row,
                row
        );
    }

    @Test
    public void testExtendUpSkipsHiddenColumn() {
        final SpreadsheetRowStore store = SpreadsheetRowStores.treeMap();
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));

        this.extendUpAndCheck(
                "4",
                SpreadsheetViewportSelectionAnchor.NONE,
                store,
                "2:4",
                SpreadsheetViewportSelectionAnchor.BOTTOM
        );
    }

    @Test
    public void testExtendDown() {
        this.extendDownAndCheck(
                "3",
                SpreadsheetViewportSelectionAnchor.NONE,
                "3:4",
                SpreadsheetViewportSelectionAnchor.TOP
        );
    }

    @Test
    public void testExtendDownLastRow() {
        final SpreadsheetRowReference row = SpreadsheetReferenceKind.RELATIVE.lastRow();

        this.extendDownAndCheck(
                row,
                row
        );
    }

    @Test
    public void testExtendDownSkipsHiddenColumn() {
        final SpreadsheetRowStore store = SpreadsheetRowStores.treeMap();
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));

        this.extendDownAndCheck(
                "2",
                SpreadsheetViewportSelectionAnchor.NONE,
                store,
                "2:4",
                SpreadsheetViewportSelectionAnchor.TOP
        );
    }

    @Test
    public void testExtendLeft() {
        final String row = "2";

        this.extendLeftAndCheck(
                row,
                row
        );
    }

    @Test
    public void testExtendLeftHiddenRow() {
        final SpreadsheetRowStore store = SpreadsheetRowStores.treeMap();
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));

        this.extendLeftAndCheck(
                "3",
                SpreadsheetViewportSelectionAnchor.NONE,
                store
        );
    }

    @Test
    public void testExtendRight() {
        final String row = "2";

        this.extendRightAndCheck(
                row,
                row
        );
    }

    @Test
    public void testExtendRightHiddenRow() {
        final SpreadsheetRowStore store = SpreadsheetRowStores.treeMap();
        store.save(SpreadsheetSelection.parseRow("3").row().setHidden(true));

        this.extendRightAndCheck(
                "3",
                SpreadsheetViewportSelectionAnchor.NONE,
                store
        );
    }

    // focused..........................................................................................................

    @Test
    public void testFocused() {
        this.focusedAndCheck(
                "1",
                SpreadsheetViewportSelectionAnchor.NONE,
                "1"
        );
    }

    @Test
    public void testFocused2() {
        this.focusedAndCheck(
                "$2",
                SpreadsheetViewportSelectionAnchor.NONE,
                "$2"
        );
    }

    // equalsIgnoreReferenceKind..........................................................................................

    @Test
    public void testEqualsIgnoreReferenceKindDifferentKind() {
        this.equalsIgnoreReferenceKindAndCheck(
                "1",
                "$1",
                true
        );
    }

    @Test
    public void testEqualsIgnoreReferenceKindDifferent() {
        this.equalsIgnoreReferenceKindAndCheck(
                "1",
                "2",
                false
        );
    }

    // toString........................................................................

    @Test
    public void testToStringRelative() {
        this.checkToString(0, SpreadsheetReferenceKind.RELATIVE, "1");
    }

    @Test
    public void testToStringRelative2() {
        this.checkToString(123, SpreadsheetReferenceKind.RELATIVE, "124");
    }

    @Test
    public void testToStringAbsolute() {
        this.checkToString(0, SpreadsheetReferenceKind.ABSOLUTE, "$1");
    }

    @Override
    SpreadsheetRowReference createReference(final int value, final SpreadsheetReferenceKind kind) {
        return SpreadsheetColumnOrRowReference.row(value, kind);
    }

    @Override
    int maxValue() {
        return SpreadsheetRowReference.MAX_VALUE;
    }

    @Override
    public Class<SpreadsheetRowReference> type() {
        return SpreadsheetRowReference.class;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetRowReference unmarshall(final JsonNode from,
                                              final JsonNodeUnmarshallContext context) {
        return SpreadsheetRowReference.unmarshallRow(from, context);
    }

    // ParseStringTesting............................................................................................

    @Override
    public SpreadsheetRowReference parseString(final String text) {
        return SpreadsheetSelection.parseRow(text);
    }
}
