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

package walkingkooka.spreadsheet.engine;

import walkingkooka.Cast;
import walkingkooka.Either;
import walkingkooka.ToStringBuilder;
import walkingkooka.color.Color;
import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContexts;
import walkingkooka.spreadsheet.format.SpreadsheetFormatters;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatExpressionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionContext;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A basic and simple {@link SpreadsheetEngineContext}. Its accepts a variety of dependencies and uses them to handle
 * public methods requests.
 */
final class BasicSpreadsheetEngineContext implements SpreadsheetEngineContext {

    /**
     * Creates a new {@link BasicSpreadsheetEngineContext}
     */
    static BasicSpreadsheetEngineContext with(final Parser<SpreadsheetParserContext> valueParser,
                                              final char valueSeparator,
                                              final Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions,
                                              final SpreadsheetEngine engine,
                                              final SpreadsheetLabelStore labelStore,
                                              final ExpressionNumberConverterContext converterContext,
                                              final Function<Integer, Optional<Color>> numberToColor,
                                              final Function<SpreadsheetColorName, Optional<Color>> nameToColor,
                                              final int width,
                                              final Function<BigDecimal, Fraction> fractioner,
                                              final SpreadsheetFormatter defaultSpreadsheetFormatter) {
        Objects.requireNonNull(valueParser, "valueParser");
        Objects.requireNonNull(functions, "functions");
        Objects.requireNonNull(engine, "engine");
        Objects.requireNonNull(labelStore, "labelStore");
        Objects.requireNonNull(converterContext, "converterContext");
        Objects.requireNonNull(numberToColor, "numberToColor");
        Objects.requireNonNull(nameToColor, "nameToColor");
        if (width <= 0) {
            throw new IllegalArgumentException("Invalid width " + width + " <= 0");
        }
        Objects.requireNonNull(fractioner, "fractioner");
        Objects.requireNonNull(defaultSpreadsheetFormatter, "defaultSpreadsheetFormatter");

        return new BasicSpreadsheetEngineContext(
                valueParser,
                valueSeparator,
                functions,
                engine,
                labelStore,
                converterContext,
                numberToColor,
                nameToColor,
                width,
                fractioner,
                defaultSpreadsheetFormatter
        );
    }

    /**
     * Private ctor use factory.
     */
    private BasicSpreadsheetEngineContext(final Parser<SpreadsheetParserContext> valueParser,
                                          final char valueSeparator,
                                          final Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions,
                                          final SpreadsheetEngine engine,
                                          final SpreadsheetLabelStore labelStore,
                                          final ExpressionNumberConverterContext converterContext,
                                          final Function<Integer, Optional<Color>> numberToColor,
                                          final Function<SpreadsheetColorName, Optional<Color>> nameToColor,
                                          final int width,
                                          final Function<BigDecimal, Fraction> fractioner,
                                          final SpreadsheetFormatter defaultSpreadsheetFormatter) {
        super();
        this.labelStore = labelStore;

        this.valueParser = valueParser;
        this.parserContext = SpreadsheetParserContexts.basic(
                converterContext,
                converterContext,
                converterContext.expressionNumberKind(),
                valueSeparator
        );

        this.functions = functions;
        this.function = SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunction.with(engine, labelStore, this);

        this.converterContext = converterContext;

        this.spreadsheetFormatContext = SpreadsheetFormatterContexts.basic(numberToColor,
                nameToColor,
                width,
                defaultSpreadsheetFormatter,
                converterContext);
        this.fractioner = fractioner;
        this.defaultSpreadsheetFormatter = defaultSpreadsheetFormatter;
    }

    // resolveCellReference.............................................................................................

    @Override
    public SpreadsheetCellReference resolveCellReference(final String text) {
        Objects.requireNonNull(text, "text");

        final SpreadsheetExpressionReference reference;
        try {
            reference = SpreadsheetExpressionReference.parse(text);
        } catch (final RuntimeException cause) {
            throw new IllegalArgumentException("Invalid cell, label or range got " + CharSequences.quoteAndEscape(text));
        }
        return BasicSpreadsheetEngineContextLookupSpreadsheetExpressionReferenceVisitor.lookup(reference, this.labelStore);
    }

    private final SpreadsheetLabelStore labelStore;

    // parsing formula and executing.....................................................................................

    @Override
    public SpreadsheetParserToken parseFormula(final String formula) {
        return SpreadsheetParsers.valueOrExpression(this.valueParser)
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(formula), this.parserContext)
                .get()
                .cast(SpreadsheetParserToken.class);
    }

    /**
     * This parser is used to parse strings, date, date/time, time and numbers outside an expression but within a formula.
     */
    private final Parser<SpreadsheetParserContext> valueParser;
    private final SpreadsheetParserContext parserContext;

    @Override
    public Object evaluate(final Expression node) {
        return node.toValue(ExpressionEvaluationContexts.basic(this.expressionNumberKind(),
                this.functions,
                this.function,
                this.converterContext));
    }

    /**
     * Handles dispatching of functions.
     */
    private final Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions;

    private final SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunction function;

    // Converter........................................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.converterContext.canConvert(value, type);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.converterContext.convert(value, type);
    }

    private final ExpressionNumberConverterContext converterContext;

    // ExpressionNumberContext..........................................................................................

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.converterContext.expressionNumberKind();
    }

    @Override
    public MathContext mathContext() {
        return this.converterContext.mathContext();
    }

    // parsing and formatting text......................................................................................

    @Override
    public SpreadsheetFormatter parsePattern(final String pattern) {
        return SpreadsheetFormatParsers.expression()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(TextCursors.charSequence(pattern), SpreadsheetFormatParserContexts.basic())
                .map(t -> SpreadsheetFormatters.expression(t.cast(SpreadsheetFormatExpressionParserToken.class), this.fractioner))
                .get();
    }

    /**
     * Used to convert a number into a fraction within expressions.
     */
    private final Function<BigDecimal, Fraction> fractioner;

    @Override
    public Optional<SpreadsheetText> format(final Object value,
                                            final SpreadsheetFormatter formatter) {
        return formatter.format(Cast.to(value), this.spreadsheetFormatContext);
    }

    private final SpreadsheetFormatterContext spreadsheetFormatContext;

    @Override
    public SpreadsheetFormatter defaultSpreadsheetFormatter() {
        return this.defaultSpreadsheetFormatter;
    }

    private final SpreadsheetFormatter defaultSpreadsheetFormatter;

    // HasLocale........................................................................................................

    @Override
    public Locale locale() {
        return this.converterContext.locale();
    }

    // YearContext......................................................................................................

    @Override
    public int defaultYear() {
        return this.converterContext.defaultYear();
    }

    @Override
    public int twoToFourDigitYear(final int year) {
        return this.converterContext.twoToFourDigitYear(year);
    }

    @Override
    public int twoDigitYear() {
        return this.converterContext.twoDigitYear();
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("converterContext").value(this.converterContext)
                .label("fractioner").value(this.fractioner)
                .label("defaultSpreadsheetFormatter").value(this.defaultSpreadsheetFormatter)
                .build();
    }
}
