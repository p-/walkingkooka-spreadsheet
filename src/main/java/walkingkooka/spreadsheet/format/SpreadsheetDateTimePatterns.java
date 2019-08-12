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

package walkingkooka.spreadsheet.format;

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatDateTimeParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.List;

/**
 * Holds a valid {@link SpreadsheetDateTimePatterns}.
 */
public final class SpreadsheetDateTimePatterns extends SpreadsheetPatterns2<SpreadsheetFormatDateTimeParserToken> {

    /**
     * Factory that creates a {@link SpreadsheetDateTimePatterns} from the given tokens.
     */
    static SpreadsheetDateTimePatterns withToken(final ParserToken token) {
        final SpreadsheetDateTimePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetDateTimePatternsSpreadsheetFormatParserTokenVisitor.with();
        visitor.startAccept(token);
        return new SpreadsheetDateTimePatterns(visitor.tokens(), visitor.ampms);
    }

    /**
     * Factory that creates a {@link SpreadsheetDateTimePatterns} from the given tokens.
     */
    static SpreadsheetDateTimePatterns withTokens(final List<SpreadsheetFormatDateTimeParserToken> tokens) {
        check(tokens);

        final SpreadsheetDateTimePatternsSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetDateTimePatternsSpreadsheetFormatParserTokenVisitor.with();
        tokens.forEach(visitor::startAccept);
        return new SpreadsheetDateTimePatterns(visitor.tokens(), visitor.ampms);
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetDateTimePatterns(final List<SpreadsheetFormatDateTimeParserToken> tokens,
                                        final List<Boolean> ampms) {
        super(tokens);
        this.ampms = ampms;
    }

    @Override
    public boolean isDate() {
        return false;
    }

    @Override
    public boolean isDateTime() {
        return true;
    }

    @Override
    public boolean isTime() {
        return false;
    }

    // HashCodeEqualsDefined............................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDateTimePatterns;
    }

    // HasParser........................................................................................................

    @Override
    Parser<ParserContext> createDateTimeFormatterParser(final int i) {
        return Parsers.localDateTime(SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunction.with(this.value().get(i),
                this.ampms.get(i)));
    }

    private final List<Boolean> ampms;
}
