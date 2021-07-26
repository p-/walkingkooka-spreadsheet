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

import walkingkooka.Cast;
import walkingkooka.InvalidTextLengthException;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.util.Objects;

/**
 * A label or {@link Name} is a name to a cell reference, range and so on.
 * <pre>
 * A Defined Name must begin with a letter or an underscore ( _ ) and consist of only letters, numbers, or underscores.
 * Spaces are not permitted in a Defined Name. Moreover, a Defined Name may not be the same as a valid cell reference.
 * For example, the name AB11 is invalid because AB11 is a valid cell reference. Names are not case sensitive.
 * </pre>
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
final public class SpreadsheetLabelName extends SpreadsheetCellReferenceOrLabelName
        implements Comparable<SpreadsheetLabelName>,
        Name {

    private final static CharPredicate LETTER = CharPredicates.range('A', 'Z').or(CharPredicates.range('a', 'z'));

    private final static CharPredicate INITIAL = LETTER;

    private final static CharPredicate DIGIT = CharPredicates.range('0', '9');

    private final static CharPredicate PART = INITIAL.or(DIGIT.or(CharPredicates.is('_')));

    /**
     * The maximum valid length for a label name.
     */
    public final static int MAX_LENGTH = 255;

    /**
     * Factory that creates a {@link SpreadsheetLabelName}
     */
    static SpreadsheetLabelName with(final String name) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(name, "name", INITIAL, PART);

        if (name.length() >= MAX_LENGTH) {
            throw new InvalidTextLengthException("Label", name, 0, MAX_LENGTH);
        }

        if (isCellReferenceText(name)) {
            throw new IllegalArgumentException("Label is a valid cell reference=" + CharSequences.quote(name));
        }

        return new SpreadsheetLabelName(name);
    }

    /**
     * Private constructor
     */
    private SpreadsheetLabelName(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    /**
     * Creates a {@link SpreadsheetLabelMapping} using this label and the given {@link SpreadsheetExpressionReference}.
     */
    public SpreadsheetLabelMapping mapping(final SpreadsheetExpressionReference reference) {
        return SpreadsheetLabelMapping.with(this, reference);
    }

    public String hateosLinkId() {
        return this.name;
    }

    // SpreadsheetSelectionVisitor......................................................................................

    @Override
    void accept(final SpreadsheetSelectionVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetExpressionReferenceVisitor............................................................................

    @Override
    void accept(final SpreadsheetExpressionReferenceVisitor visitor) {
        visitor.visit(this);
    }

    // Predicate<SpreadsheetCellReference>..............................................................................

    /**
     * Always throws {@link UnsupportedOperationException}.
     */
    @Override
    public boolean test(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
        throw new UnsupportedOperationException();
    }

    // testRange........................................................................................................

    @Override
    public boolean testRange(final SpreadsheetRange range) {
        throw new UnsupportedOperationException();
    }

    // Comparable........................................................................................................

    @Override
    public int compareTo(final SpreadsheetLabelName other) {
        return CASE_SENSITIVITY.comparator().compare(this.name, other.name);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetLabelName;
    }

    @Override
    boolean equals0(final Object other) {
        return this.equals1(Cast.to(other));
    }

    private boolean equals1(final SpreadsheetLabelName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Delegates to {@link #equals0(Object)} which is equivalent because {@link SpreadsheetLabelName} has no
     * {@link SpreadsheetReferenceKind} property.
     */
    @Override
    boolean equalsIgnoreReferenceKind0(final Object other) {
        return this.equals0(other);
    }

    /**
     * Always returns this.
     */
    @Override
    public SpreadsheetLabelName toRelative() {
        return this;
    }

    // HasCaseSensitivity................................................................................................

    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    public final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.INSENSITIVE;
}
