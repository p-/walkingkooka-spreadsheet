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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.spreadsheet.SpreadsheetValueVisitorTesting;

public final class GeneralSpreadsheetConverterSpreadsheetValueVisitorTest extends GeneralSpreadsheetConverterTestCase<GeneralSpreadsheetConverterSpreadsheetValueVisitor>
        implements SpreadsheetValueVisitorTesting<GeneralSpreadsheetConverterSpreadsheetValueVisitor> {

    @Override
    public void testAllConstructorsVisibility() {
    }

    @Test
    public void testConverterUnknownValue() {
        this.checkEquals(
                null,
                GeneralSpreadsheetConverterSpreadsheetValueVisitor.converter(
                        this,
                        Void.class,
                        null
                )
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createVisitor(), "\"String\"");
    }

    @Override
    public GeneralSpreadsheetConverterSpreadsheetValueVisitor createVisitor() {
        return new GeneralSpreadsheetConverterSpreadsheetValueVisitor(String.class, null);
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<GeneralSpreadsheetConverterSpreadsheetValueVisitor> type() {
        return Cast.to(GeneralSpreadsheetConverterSpreadsheetValueVisitor.class);
    }
}
