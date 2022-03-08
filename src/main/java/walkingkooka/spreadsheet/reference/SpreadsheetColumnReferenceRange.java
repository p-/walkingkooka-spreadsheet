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

import walkingkooka.collect.Range;

import java.util.EnumSet;
import java.util.Set;

/**
 * Holds a column range.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetColumnReferenceRange extends SpreadsheetColumnOrRowReferenceRange<SpreadsheetColumnReference>
        implements Comparable<SpreadsheetColumnReferenceRange> {

    /**
     * Factory that creates a {@link SpreadsheetColumnReferenceRange}
     */
    static SpreadsheetColumnReferenceRange with(final Range<SpreadsheetColumnReference> range) {
        SpreadsheetRangeRangeVisitor.check(range);

        return new SpreadsheetColumnReferenceRange(range);
    }

    /**
     * Private ctor
     */
    private SpreadsheetColumnReferenceRange(final Range<SpreadsheetColumnReference> range) {
        super(range);
    }

    public SpreadsheetColumnReferenceRange setRange(final Range<SpreadsheetColumnReference> range) {
        return this.setRange0(range);
    }

    @Override
    SpreadsheetColumnReferenceRange replace(final Range<SpreadsheetColumnReference> range) {
        return with(range);
    }

    /**
     * Creates a {@link SpreadsheetCellRange} combining this column range and the given row range.
     */
    public SpreadsheetCellRange setRowReferenceRange(final SpreadsheetRowReferenceRange row) {
        checkRowReferenceRange(row);

        final SpreadsheetColumnReference columnBegin = this.begin();
        final SpreadsheetRowReference rowBegin = row.begin();

        final SpreadsheetColumnReference columnEnd = this.end();
        final SpreadsheetRowReference rowEnd = row.end();

        return columnBegin.setRow(rowBegin)
                .cellRange(
                        columnEnd.setRow(rowEnd)
                );
    }

    @Override
    public boolean testCellRange(final SpreadsheetCellRange range) {
        return this.end().compareTo(range.begin().column()) >= 0 &&
                this.begin().compareTo(range.end().column()) <= 0;
    }

    @Override
    public boolean test(final SpreadsheetCellReference reference) {
        return this.testColumn(reference.column());
    }

    /**
     * Tests if the given {@link SpreadsheetColumnReference} is within this {@link SpreadsheetColumnReferenceRange}.
     */
    public boolean testColumn(final SpreadsheetColumnReference column) {
        return this.range.test(column);
    }

    @Override
    public SpreadsheetColumnReferenceRange toRelative() {
        final SpreadsheetColumnReferenceRange relative = this.begin()
                .toRelative()
                .columnRange(this.end()
                        .toRelative());
        return this.equals(relative) ?
                this :
                relative;
    }

    @Override
    Set<SpreadsheetViewportSelectionAnchor> anchors() {
        return ANCHORS;
    }

    private final static Set<SpreadsheetViewportSelectionAnchor> ANCHORS = EnumSet.of(
            SpreadsheetViewportSelectionAnchor.LEFT,
            SpreadsheetViewportSelectionAnchor.RIGHT
    );

    @Override
    public SpreadsheetViewportSelectionAnchor defaultAnchor() {
        return SpreadsheetViewportSelectionAnchor.COLUMN_RANGE;
    }

    @Override
    SpreadsheetColumnReference left(final SpreadsheetViewportSelectionAnchor anchor) {
        return anchor.column(this)
                .left(anchor);
    }

    @Override
    SpreadsheetColumnReferenceRange up(final SpreadsheetViewportSelectionAnchor anchor) {
        return this;
    }

    @Override
    SpreadsheetColumnReference right(final SpreadsheetViewportSelectionAnchor anchor) {
        return anchor.column(this)
                .right(anchor);
    }

    @Override
    SpreadsheetColumnReferenceRange down(final SpreadsheetViewportSelectionAnchor anchor) {
        return this;
    }

    @Override
    SpreadsheetSelection extendRange(final SpreadsheetSelection other,
                                     final SpreadsheetViewportSelectionAnchor anchor) {
        return anchor.fixedColumn(this)
                .columnRange((SpreadsheetColumnReference) other)
                .simplify();
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // Iterable.........................................................................................................

    @Override
    SpreadsheetColumnReference iteratorIntToReference(final int value) {
        return SpreadsheetReferenceKind.RELATIVE.column(value);
    }

    // TreePrintable....................................................................................................

    @Override
    String printTreeLabel() {
        return "column-range";
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetColumnReferenceRange;
    }

    @Override
    public int compareTo(final SpreadsheetColumnReferenceRange other) {
        throw new UnsupportedOperationException();
    }
}
