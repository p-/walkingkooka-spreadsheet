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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class FixedSpreadsheetTextFormatterTest extends SpreadsheetTextFormatter2TestCase<FixedSpreadsheetTextFormatter<Object>, Object> {

    private final static Class<Object> TYPE = Object.class;

    @Test
    public void testWithNullTypeFails() {
        assertThrows(NullPointerException.class, () -> {
            FixedSpreadsheetTextFormatter.with(null, this.formattedText());
        });
    }

    @Test
    public void testWithNullFormattedTextFails() {
        assertThrows(NullPointerException.class, () -> {
            FixedSpreadsheetTextFormatter.with(TYPE, null);
        });
    }

    @Test
    public void testWithObjectNoText() {
        final FixedSpreadsheetTextFormatter<Object> formatter = FixedSpreadsheetTextFormatter.with(Object.class,
                SpreadsheetTextFormatter.NO_TEXT);
        assertSame(formatter, FixedSpreadsheetTextFormatter.with(Object.class, SpreadsheetTextFormatter.NO_TEXT));

        this.formatAndCheck(formatter, "", this.createContext(), SpreadsheetTextFormatter.NO_TEXT);
    }

    @Test
    public void testBigInteger() {
        this.formatAndCheck2(BigInteger.valueOf(123));
    }

    @Test
    public void testBigDecimal() {
        this.formatAndCheck2(BigDecimal.valueOf(123.5));
    }

    @Test
    public void testDouble() {
        this.formatAndCheck2(Double.valueOf(123.5));
    }

    @Test
    public void testLocalDate() {
        this.formatAndCheck2(LocalDate.now());
    }

    @Test
    public void testLocalDateTime() {
        this.formatAndCheck2(LocalDateTime.now());
    }

    @Test
    public void testLocalTime() {
        this.formatAndCheck2(LocalTime.now());
    }

    @Test
    public void testLong() {
        this.formatAndCheck2(Long.valueOf(123));
    }

    @Test
    public void testText() {
        this.formatAndCheck2("HEllo");
    }

    private void formatAndCheck2(final Object value) {
        this.formatAndCheck(this.createFormatter(), value, this.createContext(), this.formattedText());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createFormatter(), this.formattedText().toString());
    }

    @Override
    public FixedSpreadsheetTextFormatter<Object> createFormatter() {
        return FixedSpreadsheetTextFormatter.with(TYPE, this.formattedText());
    }

    private Optional<SpreadsheetFormattedText> formattedText() {
        return Optional.of(SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, "Formatted"));
    }

    @Override
    public Object value() {
        return "Hello";
    }

    @Override
    public SpreadsheetTextFormatContext createContext() {
        return SpreadsheetTextFormatContexts.fake();
    }

    @Override
    public Class<FixedSpreadsheetTextFormatter<Object>> type() {
        return Cast.to(FixedSpreadsheetTextFormatter.class);
    }
}