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

import walkingkooka.Value;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * Captures the common features shared by a row or column.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
abstract public class SpreadsheetColumnOrRowReference extends SpreadsheetSelection implements Value<Integer> {

    /**
     * A {@link Comparator} which may be used to sort a combination of columns or rows ignoring their {@link SpreadsheetReferenceKind}.
     * This means columns will appear before rows and is useful when creating a single {@link java.util.Set} of all
     * hidden columns or rows.
     * <br>
     * This means a map with a key of $A will match a get with just "A" and return the value.
     */
    public final static Comparator<SpreadsheetColumnOrRowReference> COLUMN_OR_ROW_REFERENCE_KIND_IGNORED_COMPARATOR =
            (l, r) -> l.compareSpreadsheetColumnOrRowReference(r);

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetColumnOrRowReference(final int value, final SpreadsheetReferenceKind referenceKind) {
        this.value = value;
        this.referenceKind = referenceKind;
    }

    /**
     * Only returns true if this is the first column or row.
     */
    public final boolean isFirst() {
        return this.value == 0;
    }

    /**
     * Only returns true if this is the last column or row.
     */
    public final boolean isLast() {
        return this.value == this.max();
    }

    /**
     * Adds a delta to the value and returns an instance with the result.
     */
    abstract SpreadsheetColumnOrRowReference add(final int value);

    final SpreadsheetColumnOrRowReference add0(final int value) {
        return 0 == value ?
                this :
                this.setValue(this.value + value);
    }

    /**
     * A saturated adds of delta to the value and returns an instance with the result.
     */
    abstract SpreadsheetColumnOrRowReference addSaturated(final int value);

    final SpreadsheetColumnOrRowReference addSaturated0(final int value) {
        return this.setValue(
                Math.min(
                        Math.max(this.value + value, 0),
                        this.max()
                )
        );
    }

    /**
     * Returns the maximum valid value, this is used during {@link #addSaturated(int)}
     */
    abstract int max();

    abstract SpreadsheetColumnOrRowReference setValue(final int value);

    @Override
    public final Integer value() {
        return this.value;
    }

    final int value;

    public final SpreadsheetReferenceKind referenceKind() {
        return this.referenceKind;
    }

    abstract SpreadsheetColumnOrRowReference setReferenceKind(final SpreadsheetReferenceKind referenceKind);

    final SpreadsheetColumnOrRowReference setReferenceKind0(final SpreadsheetReferenceKind referenceKind) {
        checkReferenceKind(referenceKind);

        return this.referenceKind == referenceKind ?
                this :
                this.replaceReferenceKind(referenceKind);
    }

    private final SpreadsheetReferenceKind referenceKind;

    /**
     * Unconditionally creates a new {@link SpreadsheetColumnOrRowReference} with the given {@link SpreadsheetReferenceKind}.
     */
    abstract SpreadsheetColumnOrRowReference replaceReferenceKind(final SpreadsheetReferenceKind referenceKind);

    public SpreadsheetColumnOrRowReference toRelative() {
        return this.toRelative();
    }

    // column or row have no default anchor.
    @Override
    public final Optional<SpreadsheetViewportSelectionAnchor> defaultAnchor() {
        return SpreadsheetViewportSelection.NO_ANCHOR;
    }

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return Objects.hash(this.value, this.referenceKind);
    }

    @Override
    boolean equals0(final Object other,
                    final boolean includeKind) {
        return this.equals1(
                (SpreadsheetColumnOrRowReference) other,
                includeKind
        );
    }

    boolean equals1(final SpreadsheetColumnOrRowReference other,
                    final boolean includeKind) {
        return this.value == other.value &&
                (includeKind ? this.referenceKind == other.referenceKind : true);
    }

    // Comparable.......................................................................................................

    static void checkOther(final SpreadsheetColumnOrRowReference other) {
        Objects.requireNonNull(other, "other");
    }

    // COLUMN_OR_ROW_REFERENCE_COMPARATOR..............................................................................

    /**
     * Sub classes must call other.compareSpreadsheetColumnOrRowReference0(this).
     * This allows polymorphic dispatch in reverse on this.
     */
    abstract int compareSpreadsheetColumnOrRowReference(final SpreadsheetColumnOrRowReference other);

    abstract int compareSpreadsheetColumnOrRowReference0(final SpreadsheetColumnReference other);

    abstract int compareSpreadsheetColumnOrRowReference0(final SpreadsheetRowReference other);
}
