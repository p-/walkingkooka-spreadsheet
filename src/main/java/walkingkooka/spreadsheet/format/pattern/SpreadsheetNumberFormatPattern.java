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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.math.BigDecimal;
import java.util.function.Consumer;

/**
 * Holds a valid {@link SpreadsheetNumberFormatPattern}.
 */
public final class SpreadsheetNumberFormatPattern extends SpreadsheetFormatPattern {

    /**
     * Factory that creates a {@link SpreadsheetNumberFormatPattern} from the given token.
     */
    static SpreadsheetNumberFormatPattern with(final ParserToken token) {
        SpreadsheetNumberFormatPatternSpreadsheetFormatParserTokenVisitor.with()
                .startAccept(token);

        return new SpreadsheetNumberFormatPattern(token);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetNumberFormatPattern(final ParserToken token) {
        super(token);
    }

    @Override
    void missingCondition(final int index,
                          final int total,
                          final SpreadsheetFormatter formatter,
                          final Consumer<SpreadsheetFormatter> formatters) {
        switch (total) {
            case 0:
            case 1:
                formatters.accept(formatter);
                break;
            case 2:
                this.missingCondition2(
                        index,
                        formatter,
                        formatters
                );
                break;
            case 3:
                this.missingCondition3(
                        index,
                        formatter,
                        formatters
                );
                break;
            case 4:
                this.missingCondition4(
                        index,
                        formatter,
                        formatters
                );
                break;
            default:
                throw new IllegalArgumentException("Incorrect pattern count " + total + " not between 0 and 4");
        }
    }

    // https://support.microsoft.com/en-us/office/number-format-codes-5026bbd6-04bc-48cd-bf33-80f18b4eae68
    private void missingCondition2(final int index,
                                   final SpreadsheetFormatter formatter,
                                   final Consumer<SpreadsheetFormatter> formatters) {
        switch (index) {
            case 0:
                formatters.accept(
                        SpreadsheetFormatters.conditional(
                                POSITIVE_OR_ZERO,
                                formatter
                        )
                );
                break;
            case 1:
                formatters.accept(
                        SpreadsheetFormatters.conditional(
                                NEGATIVE,
                                formatter
                        )
                );
                formatters.accept(TEXT);
                break;
            default:
                throw new IllegalArgumentException("Invalid pattern index " + index);
        }
    }

    private void missingCondition3(final int index,
                                   final SpreadsheetFormatter formatter,
                                   final Consumer<SpreadsheetFormatter> formatters) {
        switch (index) {
            case 0:
                formatters.accept(
                        SpreadsheetFormatters.conditional(
                                POSITIVE,
                                formatter
                        )
                );
                break;
            case 1:
                formatters.accept(
                        SpreadsheetFormatters.conditional(
                                NEGATIVE,
                                formatter
                        )
                );
                break;
            case 2:
                formatters.accept(
                        SpreadsheetFormatters.conditional(
                                ZERO,
                                formatter
                        )
                );
                formatters.accept(TEXT);
                break;
            default:
                throw new IllegalArgumentException("Invalid pattern index " + index);
        }
    }

    private void missingCondition4(final int index,
                                   final SpreadsheetFormatter formatter,
                                   final Consumer<SpreadsheetFormatter> formatters) {
        switch (index) {
            case 0:
                formatters.accept(
                        SpreadsheetFormatters.conditional(
                                POSITIVE,
                                formatter
                        )
                );
                break;
            case 1:
                formatters.accept(
                        SpreadsheetFormatters.conditional(
                                NEGATIVE,
                                formatter
                        )
                );
                break;
            case 2:
                formatters.accept(
                        SpreadsheetFormatters.conditional(
                                ZERO,
                                formatter
                        )
                );
                break;
            case 3:
                formatters.accept(formatter);
                break;
            default:
                throw new IllegalArgumentException("Invalid pattern index " + index);
        }
    }

    private final static SpreadsheetFormatParserToken NUMBER_ZERO = SpreadsheetFormatParserToken.conditionNumber(
            BigDecimal.ZERO, "0"
    );

    private final static SpreadsheetFormatConditionParserToken ZERO = SpreadsheetFormatParserToken.equalsParserToken(
            Lists.of(
                    SpreadsheetFormatParserToken.equalsSymbol("=", "="),
                    NUMBER_ZERO
            ),
            "=0"
    );

    private final static SpreadsheetFormatConditionParserToken POSITIVE = SpreadsheetFormatParserToken.greaterThan(
            Lists.of(
                    SpreadsheetFormatParserToken.greaterThanSymbol(">", ">"),
                    NUMBER_ZERO
            ),
            ">0"
    );

    private final static SpreadsheetFormatConditionParserToken POSITIVE_OR_ZERO = SpreadsheetFormatParserToken.greaterThanEquals(
            Lists.of(
                    SpreadsheetFormatParserToken.greaterThanEqualsSymbol(">=", ">="),
                    NUMBER_ZERO
            ),
            ">=0"
    );

    private final static SpreadsheetFormatConditionParserToken NEGATIVE = SpreadsheetFormatParserToken.lessThan(
            Lists.of(
                    SpreadsheetFormatParserToken.lessThanSymbol("<", "<"),
                    NUMBER_ZERO
            ),
            "<0"
    );

    private final static SpreadsheetFormatter TEXT = SpreadsheetFormatters.text(
            SpreadsheetFormatParserToken.text(
                    Lists.of(
                            SpreadsheetFormatParserToken.textPlaceholder("@", "@")
                    ),
                    "@"
            )
    );

    // TreePrintable....................................................................................................

    @Override
    String printTreeTypeName() {
        return "number-format-pattern";
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetNumberFormatPattern;
    }
}
