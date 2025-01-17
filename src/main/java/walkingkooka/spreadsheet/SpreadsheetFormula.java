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

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallException;
import walkingkooka.tree.json.patch.Patchable;

import java.util.Objects;
import java.util.Optional;

/**
 * A spreadsheet formula, including its compiled {@link Expression} and possibly its {@link Object value} or {@link SpreadsheetError}.
 */
public final class SpreadsheetFormula implements HasText,
        Patchable<SpreadsheetFormula>,
        TreePrintable,
        UsesToStringBuilder {

    /**
     * No {@link SpreadsheetParserToken} constant.
     */
    public final static Optional<SpreadsheetParserToken> NO_TOKEN = Optional.empty();

    /**
     * No expression constant.
     */
    public final static Optional<Expression> NO_EXPRESSION = Optional.empty();

    /**
     * No error constant.
     */
    public final static Optional<SpreadsheetError> NO_ERROR = Optional.empty();

    /**
     * No value constant.
     */
    public final static Optional<Object> NO_VALUE = Optional.empty();

    /**
     * A formula with no text, token, expression, value or error.
     */
    public final static SpreadsheetFormula EMPTY = new SpreadsheetFormula(
            "",
            NO_TOKEN,
            NO_EXPRESSION,
            NO_VALUE
    );

    private SpreadsheetFormula(final String text,
                               final Optional<SpreadsheetParserToken> token,
                               final Optional<Expression> expression,
                               final Optional<Object> value) {
        super();

        this.text = text;
        this.token = token;
        this.expression = expression;
        this.value = value;
    }

    // Text ....................................................................................................

    @Override
    public String text() {
        final String text = this.text;
        return null != text ?
                text :
                this.token.get().text();
    }

    public SpreadsheetFormula setText(final String text) {
        checkText(text);
        return this.text().equals(text) ?
                this :
                text.isEmpty() ?
                        EMPTY :
                        this.replace(
                                text,
                                NO_TOKEN,
                                NO_EXPRESSION,
                                NO_VALUE
                        );
    }

    /**
     * The plain text form of the formula. This may or may not be valid and thus may or may not be a {@link #expression}
     * which may be executed.
     */
    private final String text;

    private static void checkText(final String text) {
        Objects.requireNonNull(text, "text");

        final int length = text.length();
        if (length >= MAX_FORMULA_TEXT_LENGTH) {
            throw new IllegalArgumentException("Invalid text length " + length + ">= " + MAX_FORMULA_TEXT_LENGTH);
        }
    }


    public final static int MAX_FORMULA_TEXT_LENGTH = 8192;

    // token .............................................................................................

    public Optional<SpreadsheetParserToken> token() {
        return this.token;
    }

    public SpreadsheetFormula setToken(final Optional<SpreadsheetParserToken> token) {
        checkToken(token);

        return this.token.equals(token) ?
                this :
                this.replace(
                        token.isPresent() ? null : this.text(), // no need to keep text if token is present.
                        token,
                        NO_EXPRESSION,
                        NO_VALUE
                );
    }

    /**
     * The token parsed from the text form of this formula. When loading a stored/persisted formula this should be
     * used to reconstruct the text form.
     */
    private final Optional<SpreadsheetParserToken> token;

    private static void checkToken(final Optional<SpreadsheetParserToken> token) {
        Objects.requireNonNull(token, "token");
    }

    // expression .............................................................................................

    public Optional<Expression> expression() {
        return this.expression;
    }

    public SpreadsheetFormula setExpression(final Optional<Expression> expression) {
        checkExpression(expression);

        return this.expression.equals(expression) ?
                this :
                this.replace(
                        this.text,
                        this.token,
                        expression,
                        NO_VALUE
                );
    }

    /**
     * The expression parsed from the text form of this formula. This can then be executed to produce a {@link #value}
     */
    private final Optional<Expression> expression;

    private static void checkExpression(final Optional<Expression> expression) {
        Objects.requireNonNull(expression, "expression");
    }

    // value .............................................................................................

    public Optional<Object> value() {
        return this.value;
    }

    /**
     * Only returns an error if one is present and ignores any value.
     */
    public Optional<SpreadsheetError> error() {
        final Optional<Object> value = this.value();

        final Object maybeValue = this.value()
                .orElse(null);
        return maybeValue instanceof SpreadsheetError ?
                Cast.to(value) :
                SpreadsheetFormula.NO_ERROR;
    }

    public SpreadsheetFormula setValue(final Optional<Object> value) {
        checkValue(value);

        return this.value.equals(value) ?
                this :
                this.replace(
                        this.text,
                        this.token,
                        this.expression,
                        value
                );
    }

    /**
     * The value parsed from the text form of this formula.
     */
    private final Optional<Object> value;

    private static void checkValue(final Optional<Object> value) {
        Objects.requireNonNull(value, "value");
    }

    // magic...... ....................................................................................................

    /**
     * If the value is an error try and convert into a non error value.
     * <br>
     * This handles the special case of turning formulas to missing cells from #NAME to a value of zero.
     */
    public SpreadsheetFormula replaceErrorWithValueIfPossible(final SpreadsheetEngineContext context) {
        Objects.requireNonNull(context, "context");

        final SpreadsheetError error = this.error()
                .orElse(null);
        return null == error ?
                this :
                this.setValue(
                        Optional.of(
                                error.replaceWithValueIfPossible(context)
                        )
                );
    }

    // clear ....................................................................................................

    /**
     * Clears the expression, value or error if any are present. The {@link SpreadsheetFormula} returned will only
     * have text and possibly a token (if one already was presented)
     */
    public SpreadsheetFormula clear() {
        return this.expression().isPresent() || this.value().isPresent() ?
                new SpreadsheetFormula(this.text, this.token, NO_EXPRESSION, NO_VALUE) :
                this;
    }

    // internal factory .............................................................................................

    private SpreadsheetFormula replace(final String text,
                                       final Optional<SpreadsheetParserToken> token,
                                       final Optional<Expression> expression,
                                       final Optional<Object> value) {
        return new SpreadsheetFormula(
                text,
                token,
                expression,
                value
        );
    }

    // Patchable.......................................................................................................

    @Override
    public SpreadsheetFormula patch(final JsonNode json,
                                    final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(context, "context");

        SpreadsheetFormula patched = this;

        for (final JsonNode propertyAndValue : json.objectOrFail().children()) {
            final JsonPropertyName propertyName = propertyAndValue.name();
            switch (propertyName.value()) {
                case TEXT_PROPERTY_STRING:
                    final String text;
                    try {
                        text = propertyAndValue.stringOrFail();
                    } catch (final JsonNodeException cause) {
                        throw new JsonNodeUnmarshallException("Node " + TEXT_PROPERTY_STRING + " is not a string=" + propertyAndValue, propertyAndValue);
                    }
                    patched = patched.setText(text);
                    break;
                case TOKEN_PROPERTY_STRING:
                case EXPRESSION_PROPERTY_STRING:
                case VALUE_PROPERTY_STRING:
                    Patchable.invalidPropertyPresent(propertyName, propertyAndValue);
                    break;
                default:
                    Patchable.unknownPropertyPresent(propertyName, propertyAndValue);
                    break;
            }
        }

        return patched;
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println("Formula");
        printer.indent();

        final String text = this.text;
        if (null != text) {
            printer.println("text: " + CharSequences.quoteAndEscape(text));
        } else {
            this.printTree0(
                    "token",
                    this.token(),
                    printer
            );

            this.printTree0(
                    "expression",
                    this.expression(),
                    printer
            );

            final Optional<Object> possibleValue = this.value();
            if (possibleValue.isPresent()) {
                final Object value = possibleValue.get();

                printer.print("value: ");
                if (value instanceof TreePrintable) {

                    final TreePrintable treePrintable = Cast.to(value);
                    printer.indent();
                    treePrintable.printTree(printer);
                    printer.outdent();
                } else {
                    printer.println(CharSequences.quoteIfChars(value) + " (" + value.getClass().getName() + ")");
                }
            }
        }

        printer.outdent();
    }

    private void printTree0(final String label,
                            final Optional<? extends TreePrintable> printable,
                            final IndentingPrinter printer) {
        if (printable.isPresent()) {
            printer.println(label + ":");
            printer.indent();
            {
                printable.get().printTree(printer);
            }
            printer.outdent();
        }
    }

    // JsonNodeContext..................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetFormula} from a {@link JsonNode}.
     */
    static SpreadsheetFormula unmarshall(final JsonNode node,
                                         final JsonNodeUnmarshallContext context) {
        String text = null;
        SpreadsheetParserToken token = null;
        Expression expression = null;
        Object value = null;

        for (JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case TEXT_PROPERTY_STRING:
                    try {
                        text = child.stringOrFail();
                    } catch (final JsonNodeException cause) {
                        throw new JsonNodeUnmarshallException("Node " + TEXT_PROPERTY_STRING + " is not a string=" + child, node);
                    }
                    checkText(text);
                    break;
                case TOKEN_PROPERTY_STRING:
                    token = context.unmarshallWithType(child);
                    break;
                case EXPRESSION_PROPERTY_STRING:
                    expression = context.unmarshallWithType(child);
                    break;
                case VALUE_PROPERTY_STRING:
                    value = context.unmarshallWithType(child);
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        SpreadsheetFormula formula = EMPTY;
        if (null == text) {
            if (null == token && null == expression) {
                JsonNodeUnmarshallContext.requiredPropertyMissing(TEXT_PROPERTY, node);
            }
        } else {
            formula = EMPTY.setText(text);
        }

        return formula.setToken(Optional.ofNullable(token))
                .setExpression(Optional.ofNullable(expression))
                .setValue(Optional.ofNullable(value));
    }

    /**
     * Creates an JSON object with all the components of this formula.
     */
    private JsonNode marshall(final JsonNodeMarshallContext context) {
        JsonObject object = JsonNode.object();

        final String text = this.text;
        if (null != text) {
            object = object.set(
                    TEXT_PROPERTY,
                    JsonNode.string(text)
            );
        }

        final Optional<SpreadsheetParserToken> token = this.token;
        if (token.isPresent()) {
            object = object.set(TOKEN_PROPERTY, context.marshallWithType(token.get()));
        }

        final Optional<Expression> expression = this.expression;
        if (expression.isPresent()) {
            object = object.set(EXPRESSION_PROPERTY, context.marshallWithType(expression.get()));
        }

        final Optional<Object> value = this.value;
        if (value.isPresent()) {
            object = object.set(VALUE_PROPERTY, context.marshallWithType(value.get()));
        }

        return object;
    }

    private final static String TEXT_PROPERTY_STRING = "text";
    private final static String TOKEN_PROPERTY_STRING = "token";
    private final static String EXPRESSION_PROPERTY_STRING = "expression";
    private final static String VALUE_PROPERTY_STRING = "value";

    // @VisibleForTesting

    final static JsonPropertyName TEXT_PROPERTY = JsonPropertyName.with(TEXT_PROPERTY_STRING);
    final static JsonPropertyName TOKEN_PROPERTY = JsonPropertyName.with(TOKEN_PROPERTY_STRING);
    final static JsonPropertyName EXPRESSION_PROPERTY = JsonPropertyName.with(EXPRESSION_PROPERTY_STRING);
    final static JsonPropertyName VALUE_PROPERTY = JsonPropertyName.with(VALUE_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetFormula.class),
                SpreadsheetFormula::unmarshall,
                SpreadsheetFormula::marshall,
                SpreadsheetFormula.class
        );
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.text,
                this.token,
                this.expression,
                this.value
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetFormula &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormula other) {
        return Objects.equals(this.text, other.text) && // text could be null when token is present
                this.token.equals(other.token) &&
                this.expression.equals(other.expression) &&
                this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.disable(ToStringBuilderOption.QUOTE);
        builder.value(this.text());

        if (this.value.isPresent()) {
            builder.surroundValues("(=", ")")
                    .value(new Object[]{this.value});
        }
    }
}


