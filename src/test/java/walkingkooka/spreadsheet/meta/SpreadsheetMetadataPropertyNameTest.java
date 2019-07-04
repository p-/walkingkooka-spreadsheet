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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.naming.NameTesting;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.text.TextStylePropertyName;
import walkingkooka.type.FieldAttributes;
import walkingkooka.type.JavaVisibility;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetMetadataPropertyNameTest extends SpreadsheetMetadataTestCase2<SpreadsheetMetadataPropertyName<?>>
        implements NameTesting<SpreadsheetMetadataPropertyName<?>, SpreadsheetMetadataPropertyName<?>> {

    @Test
    public void testConstants() {
        assertEquals(Lists.empty(),
                Arrays.stream(SpreadsheetMetadataPropertyName.class.getDeclaredFields())
                        .filter(FieldAttributes.STATIC::is)
                        .filter(f -> f.getType() == SpreadsheetMetadataPropertyName.class)
                        .filter(SpreadsheetMetadataPropertyNameTest::constantNotCached)
                        .collect(Collectors.toList()),
                "");
    }

    private static boolean constantNotCached(final Field field) {
        try {
            final SpreadsheetMetadataPropertyName<?> name = Cast.to(field.get(null));
            return name != SpreadsheetMetadataPropertyName.with(name.value());
        } catch (final Exception cause) {
            throw new AssertionError(cause.getMessage(), cause);
        }
    }

    @Override
    public void testCompareDifferentCase() {
    }

    @Test
    public void testJsonNodeNameCached() {
        final SpreadsheetMetadataPropertyName<?> propertyName = this.createObject();
        assertSame(propertyName.jsonNodeName, propertyName.jsonNodeName);
    }

    @Override
    public SpreadsheetMetadataPropertyName<?> createName(final String name) {
        return SpreadsheetMetadataPropertyName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return SpreadsheetMetadataPropertyName.CREATOR.name;
    }

    @Override
    public String differentNameText() {
        return this.nameTextLess();
    }

    @Override
    public String nameTextLess() {
        return SpreadsheetMetadataPropertyName.CREATE_DATE_TIME.name;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyName<?>> type() {
        return Cast.to(SpreadsheetMetadataPropertyName.class);
    }

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TypeNameTesting...................................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetMetadata.class.getSimpleName();
    }
}