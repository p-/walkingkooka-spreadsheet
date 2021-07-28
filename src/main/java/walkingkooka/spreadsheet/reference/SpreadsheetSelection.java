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
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.parser.SpreadsheetRowReferenceParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * Base class for all selection types, including columns, rows, cells, labels and ranges.
 */
public abstract class SpreadsheetSelection implements Predicate<SpreadsheetCellReference> {

    // modes used by isTextCellReference
    private final static int MODE_COLUMN_FIRST = 0;
    private final static int MODE_COLUMN = MODE_COLUMN_FIRST + 1;
    private final static int MODE_ROW_FIRST = MODE_COLUMN + 1;
    private final static int MODE_ROW = MODE_ROW_FIRST + 1;
    private final static int MODE_FAIL = MODE_ROW + 1;

    /**
     * Tests if the {@link String name} is a valid cell reference.
     */
    public static boolean isCellReferenceText(final String text) {
        Objects.requireNonNull(text, "text");

        int mode = MODE_COLUMN_FIRST; // -1 too long or contains invalid char
        int columnLength = 0;
        int column = 0;
        int row = 0;

        // AB11 max row, max column
        final int length = text.length();
        for (int i = 0; i < length; i++) {
            final char c = text.charAt(i);

            if (MODE_COLUMN_FIRST == mode) {
                mode = MODE_COLUMN;
                if (SpreadsheetReferenceKind.ABSOLUTE_PREFIX == c) {
                    continue;
                }
                // fall-thru might be column letter
            }

            // try and consume column letters
            if (MODE_COLUMN == mode) {
                final int digit = SpreadsheetParsers.valueFromDigit(c);
                if (-1 != digit) {
                    column = column * SpreadsheetColumnReference.RADIX + digit;
                    if (column > 1 + SpreadsheetColumnReference.MAX_VALUE) {
                        mode = MODE_FAIL;
                        break; // column is too big cant be a cell reference.
                    }
                    columnLength++;
                    continue;
                }
                if (0 == columnLength) {
                    mode = MODE_FAIL;
                    break;
                }
                mode = MODE_ROW_FIRST;
            }

            if (MODE_ROW_FIRST == mode) {
                mode = MODE_ROW;
                if (SpreadsheetReferenceKind.ABSOLUTE_PREFIX == c) {
                    continue;
                }
                // fall-thru might be row letter
            }


            if (MODE_ROW == mode) {
                final int digit = Character.digit(c, SpreadsheetRowReference.RADIX);
                if (-1 != digit) {
                    row = SpreadsheetRowReference.RADIX * row + digit;
                    if (row > 1 + SpreadsheetRowReference.MAX_VALUE) {
                        mode = MODE_FAIL;
                        break; // row is too big cant be a cell reference.
                    }
                    continue;
                }
                mode = MODE_FAIL;
                break;
            }
        }

        // ran out of characters still checking row must be a valid cell reference.
        return MODE_ROW == mode;
    }

    // sub class factories..............................................................................................

    /**
     * {@see SpreadsheetCellReference}
     */
    public static SpreadsheetCellReference cellReference(final SpreadsheetColumnReference column,
                                                         final SpreadsheetRowReference row) {
        return SpreadsheetCellReference.with(column, row);
    }

    /**
     * Creates a new {@link SpreadsheetColumn}
     */
    public static SpreadsheetColumnReference column(final int value, final SpreadsheetReferenceKind referenceKind) {
        return SpreadsheetColumnReference.with(value, referenceKind);
    }

    /**
     * {@see SpreadsheetLabelName}
     */
    public static SpreadsheetLabelName labelName(final String name) {
        return SpreadsheetLabelName.with(name);
    }

    /**
     * Creates a new {@link SpreadsheetRowReference}
     */
    public static SpreadsheetRowReference row(final int value, final SpreadsheetReferenceKind referenceKind) {
        return SpreadsheetRowReference.with(value, referenceKind);
    }

    // parse............................................................................................................

    /**
     * Parsers the given text into of the sub classes of {@link SpreadsheetExpressionReference}.
     */
    public static SpreadsheetExpressionReference parseExpressionReference(final String text) {
        checkText(text);

        final SpreadsheetExpressionReference reference;

        switch (text.split(":").length) {
            case 1:
                reference = isCellReferenceText(text) ?
                        parseCellReference(text) :
                        labelName(text);
                break;
            case 2:
                reference = parseCellRange(text);
                break;
            default:
                throw new IllegalArgumentException("Expected cell, label or range got " + CharSequences.quote(text));
        }

        return reference;
    }

    /**
     * Parsers a range of cell referencs.
     */
    public static Range<SpreadsheetCellReference> parseCellReferenceRange(final String text) {
        return SpreadsheetCellReference.parseCellReferenceRange0(text);
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetCellReference} or fails.
     */
    public static SpreadsheetCellReference parseCellReference(final String text) {
        return SpreadsheetCellReference.parseCellReference0(text);
    }

    /**
     * Parses text expecting either a {@link SpreadsheetCellReference} or {@link SpreadsheetLabelName}
     */
    public static SpreadsheetCellReferenceOrLabelName parseCellReferenceOrLabelName(final String text) {
        checkText(text);

        return isCellReferenceText(text) ?
                parseCellReference(text) :
                labelName(text);
    }

    /**
     * {@see #parse}
     */
    public static SpreadsheetExpressionReference parseSpreadsheetExpressionReference(final String text) {
        checkText(text);

        final SpreadsheetExpressionReference reference;

        switch (text.split(":").length) {
            case 1:
                reference = isCellReferenceText(text) ?
                        parseCellReference(text) :
                        labelName(text);
                break;
            default:
                reference = parseCellRange(text);
                break;
        }

        return reference;
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetCellRange} or fails.
     */
    public static SpreadsheetCellRange parseCellRange(final String text) {
        return SpreadsheetCellRange.parseCellRange0(text);
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetColumnReference} or fails.
     */
    public static SpreadsheetColumnReference parseColumn(final String text) {
        return parseColumnOrRow(text, COLUMN_PARSER, SpreadsheetColumnReferenceParserToken.class).value();
    }

    /**
     * Leverages the {@link SpreadsheetParsers#column()} combined with an error reporter.
     */
    private static final Parser<SpreadsheetParserContext> COLUMN_PARSER = SpreadsheetParsers.column().orReport(ParserReporters.basic());

    /**
     * Parsers a range of columns.
     */
    public static Range<SpreadsheetColumnReference> parseColumnRange(final String text) {
        return Range.parse(text, SpreadsheetParsers.RANGE_SEPARATOR.character(), SpreadsheetColumnReference::parseColumn);
    }

    /**
     * Parsers the text expecting a valid {@link SpreadsheetRowReference} or fails.
     */
    public static SpreadsheetRowReference parseRow(final String text) {
        return parseColumnOrRow(text, ROW_PARSER, SpreadsheetRowReferenceParserToken.class).value();
    }

    /**
     * Leverages the {@link SpreadsheetParsers#row()} combined with an error reporter.
     */
    private static final Parser<SpreadsheetParserContext> ROW_PARSER = SpreadsheetParsers.row().orReport(ParserReporters.basic());

    /**
     * Parsers the text expecting a valid {@link SpreadsheetRowReference} or fails.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    static <T extends SpreadsheetParserToken> T parseColumnOrRow(final String text,
                                                                 final Parser<SpreadsheetParserContext> parser,
                                                                 final Class<T> type) {
        try {
            return parser.parse(TextCursors.charSequence(text), SpreadsheetReferenceSpreadsheetParserContext.INSTANCE)
                    .get()
                    .cast(type);
        } catch (final ParserException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    /**
     * Parsers a range of rows.
     */
    public static Range<SpreadsheetRowReference> parseRowRange(final String text) {
        return Range.parse(text, SpreadsheetParsers.RANGE_SEPARATOR.character(), SpreadsheetRowReference::parseRow);
    }

    final static int CACHE_SIZE = 100;

    /**
     * Fills an array with what will become a cache of {@link SpreadsheetColumnOrRowReference}.
     */
    static <R extends SpreadsheetColumnOrRowReference> R[] fillCache(final IntFunction<R> reference, final R[] array) {
        for (int i = 0; i < CACHE_SIZE; i++) {
            array[i] = reference.apply(i);
        }

        return array;
    }

    SpreadsheetSelection() {
        super();
    }

    /**
     * Tests if the selection be it a column, row or cell is within the given range.
     */
    public abstract boolean testCellRange(final SpreadsheetCellRange range);

    public final boolean isCellRange() {
        return this instanceof SpreadsheetCellRange;
    }

    public final boolean isCellReference() {
        return this instanceof SpreadsheetCellReference;
    }

    public final boolean isColumnReference() {
        return this instanceof SpreadsheetColumnReference;
    }

    public final boolean isLabelName() {
        return this instanceof SpreadsheetLabelName;
    }

    public final boolean isRowReference() {
        return this instanceof SpreadsheetRowReference;
    }

    /**
     * If the sub class has a {@link SpreadsheetReferenceKind} return a new instance with that set to {@link SpreadsheetReferenceKind#RELATIVE}.
     * The sub class {@link SpreadsheetLabelName} will always return <code>this</code>.
     */
    public abstract SpreadsheetSelection toRelative();

    // SpreadsheetSelectionVisitor......................................................................................

    abstract void accept(final SpreadsheetSelectionVisitor visitor);

    // JsonNodeContext..................................................................................................

    /**
     * Expects a {@link JsonNode} and returns a {@link SpreadsheetColumnReference}.
     */
    static SpreadsheetColumnReference unmarshallColumn(final JsonNode from,
                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.parseColumn(from.stringOrFail());
    }

    /**
     * Expects a {@link JsonNode} and returns a {@link SpreadsheetRowReference}.
     */
    static SpreadsheetRowReference unmarshallRow(final JsonNode from,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetSelection.parseRow(from.stringOrFail());
    }

    final JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        register(
                SpreadsheetColumnReference::unmarshallColumn,
                SpreadsheetColumnReference::marshall,
                SpreadsheetColumnReference.class
        );

        //noinspection StaticInitializerReferencesSubClass
        register(
                SpreadsheetRowReference::unmarshallRow,
                SpreadsheetRowReference::marshall,
                SpreadsheetRowReference.class
        );
    }

    private static <RR extends SpreadsheetColumnOrRowReference> void register(
            final BiFunction<JsonNode, JsonNodeUnmarshallContext, RR> from,
            final BiFunction<RR, JsonNodeMarshallContext, JsonNode> to,
            final Class<RR> type) {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(type),
                from,
                to,
                type
        );
    }

    // guards............................................................................................................

    static void checkCellRange(final SpreadsheetCellRange range) {
        Objects.requireNonNull(range, "range");
    }

    static void checkCellReference(final SpreadsheetCellReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    static void checkReferenceKind(final SpreadsheetReferenceKind referenceKind) {
        Objects.requireNonNull(referenceKind, "referenceKind");
    }

    static void checkText(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");
    }
}
