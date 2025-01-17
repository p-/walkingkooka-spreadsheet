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

import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

/**
 * Holds a valid {@link SpreadsheetNumberParsePattern}.
 */
public final class SpreadsheetNumberParsePattern extends SpreadsheetParsePattern {

    /**
     * Factory that creates a {@link SpreadsheetNumberParsePattern} from the given tokens.
     */
    static SpreadsheetNumberParsePattern with(final ParserToken token) {
        final SpreadsheetNumberParsePatternSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetNumberParsePatternSpreadsheetFormatParserTokenVisitor.with();
        visitor.startAccept(token);
        return new SpreadsheetNumberParsePattern(
                token,
                visitor.patterns
        );
    }

    /**
     * Private ctor use factory
     */
    private SpreadsheetNumberParsePattern(final ParserToken token,
                                          final List<List<SpreadsheetNumberParsePatternComponent>> patterns) {
        super(token);
        this.patterns = patterns;
    }

    // TreePrintable....................................................................................................

    @Override
    String printTreeTypeName() {
        return "number-parse-pattern";
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetNumberParsePattern;
    }

    // HasConverter.....................................................................................................

    @Override
    SpreadsheetNumberParsePatternConverter createConverter() {
        return SpreadsheetNumberParsePatternConverter.with(this);
    }

    // HasParser........................................................................................................

    @Override
    SpreadsheetNumberParsePatternParser createParser() {
        return SpreadsheetNumberParsePatternParser.with(this, SpreadsheetNumberParsePatternMode.VALUE);
    }

    /**
     * Returns a {@link Parser} which will try all the patterns.
     */
    public Parser<SpreadsheetParserContext> expressionParser() {
        if (null == this.expressionParser) {
            this.expressionParser = this.createExpressionParser();
        }
        return this.expressionParser;
    }

    private Parser<SpreadsheetParserContext> expressionParser;

    private SpreadsheetNumberParsePatternParser createExpressionParser() {
        return SpreadsheetNumberParsePatternParser.with(this, SpreadsheetNumberParsePatternMode.EXPRESSION);
    }

    /**
     * The outer {@link List} contains an element for each pattern, with the inner {@link List} containing the components.
     */
    final List<List<SpreadsheetNumberParsePatternComponent>> patterns;
}
