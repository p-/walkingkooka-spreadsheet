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

import walkingkooka.spreadsheet.parser.SpreadsheetDateParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.ExpressionEvaluationContext;

import java.time.LocalDate;

/**
 * Holds a valid {@link SpreadsheetDateParsePattern}.
 */
public final class SpreadsheetDateParsePattern extends SpreadsheetParsePattern2<LocalDate> {

    /**
     * Factory that creates a {@link ParserToken} from the given tokens.
     */
    static SpreadsheetDateParsePattern with(final ParserToken token) {
        final SpreadsheetDateParsePatternSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetDateParsePatternSpreadsheetFormatParserTokenVisitor.with();
        visitor.startAccept(token);
        return new SpreadsheetDateParsePattern(token);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetDateParsePattern(final ParserToken token) {
        super(token);
    }

    @Override
    Class<LocalDate> targetType() {
        return LocalDate.class;
    }

    @Override
    LocalDate converterTransformer0(final ParserToken token,
                                    final ExpressionEvaluationContext context) {
        return token.cast(
                SpreadsheetDateParserToken.class
        ).toLocalDate(context);
    }

    // TreePrintable....................................................................................................

    @Override
    String printTreeTypeName() {
        return "date-parse-pattern";
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDateParsePattern;
    }
}
