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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.type.JavaVisibility;

public final class SpreadsheetMetadataDecimalNumberContextComponentsTest implements ClassTesting2,
        ToStringTesting<SpreadsheetMetadataDecimalNumberContextComponents>,
        TypeNameTesting<SpreadsheetMetadataDecimalNumberContextComponents> {

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetMetadataDecimalNumberContextComponents components = SpreadsheetMetadataDecimalNumberContextComponents.with(SpreadsheetMetadata.EMPTY);
        components.components.getOrNull(SpreadsheetMetadataPropertyName.CREATOR);
        components.components.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_POINT);

        this.toStringAndCheck(components, Lists.of(SpreadsheetMetadataPropertyName.CREATOR, SpreadsheetMetadataPropertyName.DECIMAL_POINT).toString());
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataDecimalNumberContextComponents> type() {
        return SpreadsheetMetadataDecimalNumberContextComponents.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetMetadata.class.getSimpleName() + DecimalNumberContext.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}