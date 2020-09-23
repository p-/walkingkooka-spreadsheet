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

package walkingkooka.spreadsheet.format.parser;

import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.BigDecimalParserToken;
import walkingkooka.text.cursor.parser.BigIntegerParserToken;
import walkingkooka.text.cursor.parser.CharacterParserToken;
import walkingkooka.text.cursor.parser.DoubleQuotedParserToken;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.StringParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfGrammarParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserContexts;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Parsers that know how to parse formatting patterns<br>.
 * <a href="https://support.office.com/en-us/article/number-format-codes-5026bbd6-04bc-48cd-bf33-80f18b4eae68>Formatting</a>
 * <a href="https://support.google.com/docs/answer/56470?p=drive_custom_numbers&visit_id=637014719918429541-764379239&rd=1"</a>
 */
public final class SpreadsheetFormatParsers implements PublicStaticHelper {

    // shared

    private static final EbnfIdentifierName WHITESPACE_IDENTIFIER = EbnfIdentifierName.with("WHITESPACE");
    private final static Parser<ParserContext> WHITESPACE = Parsers.stringCharPredicate(CharPredicates.whitespace(), 1, Integer.MAX_VALUE)
            .transform(SpreadsheetFormatParsers::transformWhitespace)
            .setToString(SpreadsheetFormatWhitespaceParserToken.class.getSimpleName());

    private static ParserToken transformWhitespace(final ParserToken token, final ParserContext context) {
        return SpreadsheetFormatParserToken.whitespace(((StringParserToken) token).value(), token.text());
    }

    // color..............................................................................................................

    /**
     * Returns a {@link Parser} that returns a color format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> color() {
        return COLOR_PARSER;
    }

    static final EbnfIdentifierName COLOR_IDENTIFIER = EbnfIdentifierName.with("COLOR");
    private final static Parser<SpreadsheetFormatParserContext> COLOR_PARSER;

    private static void color(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(COLOR_AND_NUMBER_IDENTIFIER, COLOR_AND_NUMBER);
        predefined.put(COLOR_NAME_IDENTIFIER, COLOR_NAME);
        predefined.put(COLOR_NUMBER_IDENTIFIER, COLOR_NUMBER);
    }

    private static final EbnfIdentifierName COLOR_AND_NUMBER_IDENTIFIER = EbnfIdentifierName.with("COLOR_AND_NUMBER");

    private static final EbnfIdentifierName COLOR_NAME_IDENTIFIER = EbnfIdentifierName.with("COLOR_NAME");

    private static final Parser<ParserContext> COLOR_NAME = Parsers.stringCharPredicate(CharPredicates.letter(), 1, Integer.MAX_VALUE)
            .transform(SpreadsheetFormatParsers::colorName)
            .setToString(COLOR_NAME_IDENTIFIER.toString());

    private static SpreadsheetFormatColorNameParserToken colorName(final ParserToken string, final ParserContext context) {
        return SpreadsheetFormatParserToken.colorName(((StringParserToken) string).value(), string.text());
    }

    private static final EbnfIdentifierName COLOR_NUMBER_IDENTIFIER = EbnfIdentifierName.with("COLOR_NUMBER");

    private static final Parser<ParserContext> COLOR_NUMBER = Parsers.bigInteger(10)
            .transform(SpreadsheetFormatParsers::transformColorNumber)
            .setToString(COLOR_NUMBER_IDENTIFIER.toString());

    private static ParserToken transformColorNumber(final ParserToken token, final ParserContext context) {
        return SpreadsheetFormatParserToken.colorNumber(((BigIntegerParserToken) token).value().intValueExact(), token.text());
    }

    private static final Parser<ParserContext> COLOR_AND_NUMBER = Parsers.string("COLOR", CaseSensitivity.INSENSITIVE)
            .transform(SpreadsheetFormatParsers::transformColorLiteral)
            .builder()
            .required(WHITESPACE)
            .required(COLOR_NUMBER)
            .build()
            .setToString(COLOR_NAME_IDENTIFIER.toString());

    private static SpreadsheetFormatColorLiteralSymbolParserToken transformColorLiteral(final ParserToken string, final ParserContext context) {
        return SpreadsheetFormatParserToken.colorLiteralSymbol(((StringParserToken) string).value(), string.text());
    }

    // conditional..............................................................................................................

    /**
     * Returns a {@link Parser} that returns a condition format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> condition() {
        return CONDITION_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> CONDITION_PARSER;

    private static void condition(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(CONDITION_NUMBER_LITERAL_IDENTIFIER, Parsers.bigDecimal()
                .transform(SpreadsheetFormatParsers::transformConditionNumber)
                .setToString(CONDITION_NUMBER_LITERAL_IDENTIFIER.toString()));

        predefined.put(EQUALS_SYMBOL_IDENTIFIER, EQUALS_SYMBOL);
        predefined.put(NOT_EQUALS_SYMBOL_IDENTIFIER, NOT_EQUALS_SYMBOL);
        predefined.put(GREATER_THAN_SYMBOL_IDENTIFIER, GREATER_THAN_SYMBOL);
        predefined.put(GREATER_THAN_EQUALS_SYMBOL_IDENTIFIER, GREATER_THAN_EQUALS_SYMBOL);
        predefined.put(LESS_THAN_SYMBOL_IDENTIFIER, LESS_THAN_SYMBOL);
        predefined.put(LESS_THAN_EQUALS_SYMBOL_IDENTIFIER, LESS_THAN_EQUALS_SYMBOL);
    }

    private static final EbnfIdentifierName CONDITION_NUMBER_LITERAL_IDENTIFIER = EbnfIdentifierName.with("CONDITION_NUMBER");

    private static SpreadsheetFormatConditionNumberParserToken transformConditionNumber(final ParserToken token, final ParserContext context) {
        return SpreadsheetFormatConditionNumberParserToken.with(((BigDecimalParserToken) token).value(), token.text());
    }

    private static final Parser<ParserContext> EQUALS_SYMBOL = symbol('=',
            SpreadsheetFormatParserToken::equalsSymbol,
            SpreadsheetFormatEqualsSymbolParserToken.class);
    private static final EbnfIdentifierName EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("EQUALS");

    private static final Parser<ParserContext> NOT_EQUALS_SYMBOL = symbol("!=",
            SpreadsheetFormatParserToken::notEqualsSymbol,
            SpreadsheetFormatNotEqualsSymbolParserToken.class);
    private static final EbnfIdentifierName NOT_EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("NOT_EQUALS");

    private static final Parser<ParserContext> GREATER_THAN_SYMBOL = symbol('>',
            SpreadsheetFormatParserToken::greaterThanSymbol,
            SpreadsheetFormatGreaterThanSymbolParserToken.class);
    private static final EbnfIdentifierName GREATER_THAN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("GREATER_THAN");

    private static final Parser<ParserContext> GREATER_THAN_EQUALS_SYMBOL = symbol(">=",
            SpreadsheetFormatParserToken::greaterThanEqualsSymbol,
            SpreadsheetFormatGreaterThanEqualsSymbolParserToken.class);
    private static final EbnfIdentifierName GREATER_THAN_EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("GREATER_THAN_EQUALS");

    private static final Parser<ParserContext> LESS_THAN_SYMBOL = symbol('<',
            SpreadsheetFormatParserToken::lessThanSymbol,
            SpreadsheetFormatLessThanSymbolParserToken.class);
    private static final EbnfIdentifierName LESS_THAN_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("LESS_THAN");

    private static final Parser<ParserContext> LESS_THAN_EQUALS_SYMBOL = symbol("<=",
            SpreadsheetFormatParserToken::lessThanEqualsSymbol,
            SpreadsheetFormatLessThanEqualsSymbolParserToken.class);
    private static final EbnfIdentifierName LESS_THAN_EQUALS_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("LESS_THAN_EQUALS");

    // date..............................................................................................................

    /**
     * Returns a {@link Parser} that returns a date format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> date() {
        return DATE_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> DATE_PARSER;

    private static void date(final Map<EbnfIdentifierName, Parser<ParserContext>> parsers) {
        parsers.put(DAY_IDENTIFIER, DAY);
        parsers.put(YEAR_IDENTIFIER, YEAR);
    }

    private static final EbnfIdentifierName DAY_IDENTIFIER = EbnfIdentifierName.with("DAY");
    private static final Parser<ParserContext> DAY = repeatingSymbol('D',
            SpreadsheetFormatParserToken::day,
            SpreadsheetFormatDayParserToken.class);

    private static final EbnfIdentifierName YEAR_IDENTIFIER = EbnfIdentifierName.with("YEAR");
    private static final Parser<ParserContext> YEAR = repeatingSymbol('Y',
            SpreadsheetFormatParserToken::year,
            SpreadsheetFormatYearParserToken.class);

    // dateTime..............................................................................................................

    /**
     * Returns a {@link Parser} that returns a datetime format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> dateTime() {
        return DATETIME_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> DATETIME_PARSER;

    private static void dateAndTime(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(MONTH_MINUTE_IDENTIFIER, MONTH_MINUTE);
    }

    private static final EbnfIdentifierName MONTH_MINUTE_IDENTIFIER = EbnfIdentifierName.with("MONTH_MINUTE");

    private static final Parser<ParserContext> MONTH_MINUTE = repeatingSymbol('M',
            SpreadsheetFormatParserToken::monthOrMinute,
            SpreadsheetFormatMonthOrMinuteParserToken.class);

    // expression...............................................................................................................

    /**
     * Returns a {@link Parser} that given text returns a {@link SpreadsheetFormatParserToken}.
     */
    public static Parser<SpreadsheetFormatParserContext> expression() {
        return EXPRESSION_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> EXPRESSION_PARSER;

    static final EbnfIdentifierName EXPRESSION_IDENTIFIER = EbnfIdentifierName.with("EXPRESSION");

    // expression...............................................................................................................

    /**
     * Returns a {@link Parser} that given text returns a {@link SpreadsheetFormatParserToken}.
     */
    public static Parser<SpreadsheetFormatParserContext> expressionSeparator() {
        return EXPRESSION_SEPARATOR_SYMBOL_PARSER.cast();
    }

    private static final Parser<ParserContext> EXPRESSION_SEPARATOR_SYMBOL_PARSER = symbol(';',
            SpreadsheetFormatParserToken::separatorSymbol,
            SpreadsheetFormatSeparatorSymbolParserToken.class);

    private static final EbnfIdentifierName EXPRESSION_SEPARATOR_IDENTIFIER = EbnfIdentifierName.with("EXPRESSION_SEPARATOR");

    private static void expressionSeparator(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(EXPRESSION_SEPARATOR_IDENTIFIER, EXPRESSION_SEPARATOR_SYMBOL_PARSER);
    }
    // general ..........................................................................................................

    /**
     * Returns a {@link Parser} that matches a general token.
     */
    public static Parser<SpreadsheetFormatParserContext> general() {
        return GENERAL_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> GENERAL_PARSER;
    static final EbnfIdentifierName GENERAL_IDENTIFIER = EbnfIdentifierName.with("GENERAL");

    private static void general(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(GENERAL_SYMBOL_IDENTIFIER, GENERAL_SYMBOL);
    }

    private static final EbnfIdentifierName GENERAL_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("GENERAL_SYMBOL");

    private static final Parser<ParserContext> GENERAL_SYMBOL = Parsers.string("GENERAL", CaseSensitivity.INSENSITIVE)
            .transform(SpreadsheetFormatParsers::transformGeneralSymbol)
            .setToString(GENERAL_SYMBOL_IDENTIFIER.toString());

    private static ParserToken transformGeneralSymbol(final ParserToken token, final ParserContext context) {
        return SpreadsheetFormatParserToken.generalSymbol(((StringParserToken) token).value(), token.text());
    }

    // fraction..........................................................................................................

    /**
     * Returns a {@link Parser} that given text returns a {@link SpreadsheetFormatParserToken}.
     */
    public static Parser<SpreadsheetFormatParserContext> fraction() {
        return FRACTION_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> FRACTION_PARSER;

    /**
     * Returns a {@link Parser} that given text returns a {@link SpreadsheetFormatParserToken}.
     */
    public static Parser<SpreadsheetFormatParserContext> number() {
        return NUMBER_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> NUMBER_PARSER;

    private static void number(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(CURRENCY_IDENTIFIER, CURRENCY);
        predefined.put(DECIMAL_POINT_IDENTIFIER, DECIMAL_POINT_PARSER);
        predefined.put(DIGIT_IDENTIFIER, DIGIT);
        predefined.put(DIGIT_SPACE_IDENTIFIER, DIGIT_SPACE);
        predefined.put(DIGIT_ZERO_IDENTIFIER, DIGIT_ZERO);
        predefined.put(FRACTION_SYMBOL_IDENTIFIER, FRACTION_SYMBOL);
        predefined.put(PERCENTAGE_IDENTIFIER, PERCENTAGE);
        predefined.put(THOUSANDS_IDENTIFIER, THOUSANDS);
    }

    private static final EbnfIdentifierName CURRENCY_IDENTIFIER = EbnfIdentifierName.with("CURRENCY");
    private static final Parser<ParserContext> CURRENCY = symbol('$',
            SpreadsheetFormatParserToken::currency,
            SpreadsheetFormatCurrencyParserToken.class);

    private static final EbnfIdentifierName DECIMAL_POINT_IDENTIFIER = EbnfIdentifierName.with("DECIMAL_POINT");
    private static final Parser<ParserContext> DECIMAL_POINT_PARSER = symbol('.',
            SpreadsheetFormatParserToken::decimalPoint,
            SpreadsheetFormatDecimalPointParserToken.class);

    private static final EbnfIdentifierName DIGIT_IDENTIFIER = EbnfIdentifierName.with("DIGIT");
    private static final Parser<ParserContext> DIGIT = symbol('#',
            SpreadsheetFormatParserToken::digit,
            SpreadsheetFormatDigitParserToken.class);

    private static final EbnfIdentifierName DIGIT_SPACE_IDENTIFIER = EbnfIdentifierName.with("DIGIT_SPACE");
    private static final Parser<ParserContext> DIGIT_SPACE = symbol('?',
            SpreadsheetFormatParserToken::digitSpace,
            SpreadsheetFormatDigitSpaceParserToken.class);

    private static final EbnfIdentifierName DIGIT_ZERO_IDENTIFIER = EbnfIdentifierName.with("DIGIT_ZERO");
    private static final Parser<ParserContext> DIGIT_ZERO = symbol('0',
            SpreadsheetFormatParserToken::digitZero,
            SpreadsheetFormatDigitZeroParserToken.class);

    private static final EbnfIdentifierName FRACTION_SYMBOL_IDENTIFIER = EbnfIdentifierName.with("FRACTION_SYMBOL");
    private static final Parser<ParserContext> FRACTION_SYMBOL = symbol('/',
            SpreadsheetFormatParserToken::fractionSymbol,
            SpreadsheetFormatFractionSymbolParserToken.class);

    private static final EbnfIdentifierName PERCENTAGE_IDENTIFIER = EbnfIdentifierName.with("PERCENTAGE");
    private static final Parser<ParserContext> PERCENTAGE = symbol('%',
            SpreadsheetFormatParserToken::percent,
            SpreadsheetFormatPercentParserToken.class);

    private static final Parser<ParserContext> THOUSANDS = symbol(',',
            SpreadsheetFormatParserToken::thousands,
            SpreadsheetFormatThousandsParserToken.class);
    private static final EbnfIdentifierName THOUSANDS_IDENTIFIER = EbnfIdentifierName.with("THOUSANDS");

    // text..............................................................................................................

    /**
     * Returns a {@link Parser} that returns a text format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> text() {
        return TEXT_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> TEXT_PARSER;
    static final EbnfIdentifierName TEXT_IDENTIFIER = EbnfIdentifierName.with("TEXT");

    private static void text(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(QUOTED_IDENTIFIER, QUOTED);
        predefined.put(STAR_IDENTIFIER, STAR);
        predefined.put(TEXT_PLACEHOLDER_IDENTIFIER, TEXT_PLACEHOLDER);
        predefined.put(UNDERSCORE_IDENTIFIER, UNDERSCORE);
        predefined.put(WHITESPACE_IDENTIFIER, WHITESPACE);
    }

    private static final EbnfIdentifierName QUOTED_IDENTIFIER = EbnfIdentifierName.with("QUOTED");
    private static final Parser<ParserContext> QUOTED = Parsers.doubleQuoted()
            .transform(SpreadsheetFormatParsers::transformQuoted)
            .setToString("QUOTED");

    private static ParserToken transformQuoted(final ParserToken token, final ParserContext context) {
        return SpreadsheetFormatParserToken.quotedText(((DoubleQuotedParserToken) token).value(), token.text());
    }

    private static final EbnfIdentifierName STAR_IDENTIFIER = EbnfIdentifierName.with("STAR");
    private static final Parser<ParserContext> STAR = escapeStarOrUnderline('*',
            SpreadsheetFormatParserToken::star,
            SpreadsheetFormatStarParserToken.class);

    private static final EbnfIdentifierName TEXT_PLACEHOLDER_IDENTIFIER = EbnfIdentifierName.with("TEXT_PLACEHOLDER");
    private static final Parser<ParserContext> TEXT_PLACEHOLDER = symbol('@',
            SpreadsheetFormatParserToken::textPlaceholder,
            SpreadsheetFormatTextPlaceholderParserToken.class);

    private static final EbnfIdentifierName UNDERSCORE_IDENTIFIER = EbnfIdentifierName.with("UNDERSCORE");
    private static final Parser<ParserContext> UNDERSCORE = escapeStarOrUnderline('_',
            SpreadsheetFormatParserToken::underscore,
            SpreadsheetFormatUnderscoreParserToken.class);

    // time..............................................................................................................

    /**
     * Returns a {@link Parser} that returns a time format expression as {@link SpreadsheetFormatParserToken tokens}.
     */
    public static Parser<SpreadsheetFormatParserContext> time() {
        return TIME_PARSER;
    }

    private final static Parser<SpreadsheetFormatParserContext> TIME_PARSER;

    private static void time(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(A_SLASH_P_IDENTIFIER, A_SLASH_P);
        predefined.put(AM_SLASH_PM_IDENTIFIER, AM_SLASH_PM);
        predefined.put(HOUR_IDENTIFIER, HOUR);
        predefined.put(SECOND_IDENTIFIER, SECOND);
    }

    private static final EbnfIdentifierName A_SLASH_P_IDENTIFIER = EbnfIdentifierName.with("A_SLASH_P");
    private static final Parser<ParserContext> A_SLASH_P = symbol("A/P",
            SpreadsheetFormatParserToken::amPm,
            SpreadsheetFormatAmPmParserToken.class);

    private static final EbnfIdentifierName AM_SLASH_PM_IDENTIFIER = EbnfIdentifierName.with("AM_SLASH_PM");
    private static final Parser<ParserContext> AM_SLASH_PM = symbol("AM/PM",
            SpreadsheetFormatParserToken::amPm,
            SpreadsheetFormatAmPmParserToken.class);

    private static final EbnfIdentifierName HOUR_IDENTIFIER = EbnfIdentifierName.with("HOUR");
    private static final Parser<ParserContext> HOUR = repeatingSymbol('H',
            SpreadsheetFormatParserToken::hour,
            SpreadsheetFormatHourParserToken.class);

    private static final EbnfIdentifierName SECOND_IDENTIFIER = EbnfIdentifierName.with("SECOND");
    private static final Parser<ParserContext> SECOND = repeatingSymbol('S',
            SpreadsheetFormatParserToken::second,
            SpreadsheetFormatSecondParserToken.class);

    // misc..............................................................................................................

    private static void misc(final Map<EbnfIdentifierName, Parser<ParserContext>> predefined) {
        predefined.put(BRACKET_OPEN_IDENTIFIER, BRACKET_OPEN);
        predefined.put(BRACKET_CLOSE_IDENTIFIER, BRACKET_CLOSE);

        predefined.put(ESCAPE_IDENTIFIER, ESCAPE);
        predefined.put(NUMBER_LITERAL_IDENTIFIER, NUMBER_LITERAL);
        predefined.put(DATETIME_TEXT_LITERAL_IDENTIFIER, DATETIME_TEXT_LITERAL);
    }

    private static final EbnfIdentifierName BRACKET_OPEN_IDENTIFIER = EbnfIdentifierName.with("BRACKET_OPEN");
    private static final Parser<ParserContext> BRACKET_OPEN = symbol('[',
            SpreadsheetFormatParserToken::bracketOpenSymbol,
            SpreadsheetFormatBracketOpenSymbolParserToken.class);

    private static final EbnfIdentifierName BRACKET_CLOSE_IDENTIFIER = EbnfIdentifierName.with("BRACKET_CLOSE");
    private static final Parser<ParserContext> BRACKET_CLOSE = symbol(']',
            SpreadsheetFormatParserToken::bracketCloseSymbol,
            SpreadsheetFormatBracketCloseSymbolParserToken.class);

    private static final EbnfIdentifierName ESCAPE_IDENTIFIER = EbnfIdentifierName.with("ESCAPE");
    private static final Parser<ParserContext> ESCAPE = escapeStarOrUnderline('\\',
            SpreadsheetFormatParserToken::escape,
            SpreadsheetFormatEscapeParserToken.class);

    private static final EbnfIdentifierName NUMBER_LITERAL_IDENTIFIER = EbnfIdentifierName.with("NUMBER_LITERAL");
    private static final Parser<ParserContext> NUMBER_LITERAL = literal("(): +-", NUMBER_LITERAL_IDENTIFIER);

    private static final EbnfIdentifierName DATETIME_TEXT_LITERAL_IDENTIFIER = EbnfIdentifierName.with("DATETIME_TEXT_LITERAL");
    private static final Parser<ParserContext> DATETIME_TEXT_LITERAL = literal("$-+(): /+-", DATETIME_TEXT_LITERAL_IDENTIFIER);

    // helpers..............................................................................................................

    /*
     * Parsers the grammar and returns the selected parser.
     */
    static {
        //noinspection CaughtExceptionImmediatelyRethrown
        try {
            final TextCursor grammarFile = TextCursors.charSequence(new SpreadsheetFormatParsersGrammarProvider().text());

            final Map<EbnfIdentifierName, Parser<ParserContext>> predefined = Maps.sorted();

            expressionSeparator(predefined);
            color(predefined);
            condition(predefined);
            date(predefined);
            dateAndTime(predefined);
            general(predefined);
            number(predefined);
            text(predefined);
            time(predefined);

            misc(predefined);

            final Map<EbnfIdentifierName, Parser<ParserContext>> parsers = EbnfParserToken.grammarParser()
                    .orFailIfCursorNotEmpty(ParserReporters.basic())
                    .parse(grammarFile, EbnfParserContexts.basic())
                    .orElseThrow(() -> new IllegalStateException("Unable to parse format parsers grammar file."))
                    .cast(EbnfGrammarParserToken.class)
                    .combinator(predefined, SpreadsheetFormatEbnfParserCombinatorSyntaxTreeTransformer.INSTANCE);


            COLOR_PARSER = parsers.get(COLOR_IDENTIFIER).cast();
            CONDITION_PARSER = parsers.get(EbnfIdentifierName.with("CONDITION")).cast();
            DATE_PARSER = parsers.get(EbnfIdentifierName.with("DATE")).cast();
            DATETIME_PARSER = parsers.get(EbnfIdentifierName.with("DATETIME")).cast();
            EXPRESSION_PARSER = parsers.get(EXPRESSION_IDENTIFIER).cast();
            FRACTION_PARSER = parsers.get(EbnfIdentifierName.with("FRACTION")).cast();
            GENERAL_PARSER = parsers.get(GENERAL_IDENTIFIER).cast();
            NUMBER_PARSER = parsers.get(EbnfIdentifierName.with("NUMBER")).cast();
            TEXT_PARSER = parsers.get(TEXT_IDENTIFIER).cast();
            TIME_PARSER = parsers.get(EbnfIdentifierName.with("TIME")).cast();

        } catch (final SpreadsheetFormatParserException rethrow) {
            throw rethrow;
        } catch (final Exception cause) {
            throw new SpreadsheetFormatParserException("Failed to return parsers from excel format grammar file, message: " + cause.getMessage(), cause);
        }
    }

    private static Parser<ParserContext> literal(final String any, final EbnfIdentifierName name) {
        return Parsers.character(CharPredicates.any(any))
                .transform(((characterParserToken, parserContext) -> SpreadsheetFormatParserToken.textLiteral(((CharacterParserToken) characterParserToken).value().toString(), characterParserToken.text())))
                .setToString(name.toString());
    }

    /**
     * Matches a token filled with the given c ignoring case.
     */
    private static Parser<ParserContext> repeatingSymbol(final char c,
                                                         final BiFunction<String, String, ParserToken> factory,
                                                         final Class<? extends SpreadsheetFormatLeafParserToken> tokenClass) {
        return Parsers.stringCharPredicate(CaseSensitivity.INSENSITIVE.charPredicate(c), 1, Integer.MAX_VALUE)
                .transform((stringParserToken, context) -> factory.apply(((StringParserToken) stringParserToken).value(), stringParserToken.text()))
                .setToString(tokenClass.getSimpleName());
    }

    /**
     * Matches a token holding a single character.
     */
    private static Parser<ParserContext> symbol(final char c,
                                                final BiFunction<String, String, ParserToken> factory,
                                                final Class<? extends SpreadsheetFormatLeafParserToken> tokenClass) {
        return Parsers.character(CaseSensitivity.SENSITIVE.charPredicate(c))
                .transform((characterParserToken, context) -> factory.apply(((CharacterParserToken) characterParserToken).value().toString(), characterParserToken.text()))
                .setToString(tokenClass.getSimpleName());
    }

    private static Parser<ParserContext> symbol(final String text,
                                                final BiFunction<String, String, ParserToken> factory,
                                                final Class<? extends SpreadsheetFormatLeafParserToken> tokenClass) {
        return Parsers.string(text, CaseSensitivity.INSENSITIVE)
                .transform((stringParserToken, context) -> factory.apply(((StringParserToken) stringParserToken).value(), stringParserToken.text()))
                .setToString(tokenClass.getSimpleName());
    }

    private static Parser<ParserContext> escapeStarOrUnderline(final char initial,
                                                               final BiFunction<Character, String, ParserToken> factory,
                                                               final Class<? extends SpreadsheetFormatLeafParserToken> tokenClass) {
        return Parsers.stringInitialAndPartCharPredicate(CharPredicates.is(initial), CharPredicates.always(), 1, 2)
                .transform((stringParserToken, context) -> factory.apply(((StringParserToken) stringParserToken).value().charAt(1), stringParserToken.text()))
                .setToString(tokenClass.getSimpleName());
    }

    /**
     * Stop construction
     */
    private SpreadsheetFormatParsers() {
        throw new UnsupportedOperationException();
    }
}
