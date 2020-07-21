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
import walkingkooka.net.email.EmailAddress;

public final class SpreadsheetMetadataPropertyValueHandlerEmailAddressTest extends SpreadsheetMetadataPropertyValueHandlerTestCase3<SpreadsheetMetadataPropertyValueHandlerEmailAddress, EmailAddress> {

    @Test
    public void testInvalidEmailFails() {
        this.checkFails("invalid email", "Expected EmailAddress but got \"invalid email\" (java.lang.String) in \"creator\"");
    }

    @Test
    public void testJsonNodeUnmarshall() {
        final EmailAddress emailAddress = this.propertyValue();
        this.unmarshallAndCheck(this.marshall(emailAddress), emailAddress);
    }

    @Test
    public void testJsonNodeMarshall() {
        final EmailAddress emailAddress = this.propertyValue();
        this.marshallAndCheck(emailAddress, this.marshall(emailAddress));
    }

    @Override
    SpreadsheetMetadataPropertyValueHandlerEmailAddress handler() {
        return SpreadsheetMetadataPropertyValueHandlerEmailAddress.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<EmailAddress> propertyName() {
        return SpreadsheetMetadataPropertyName.CREATOR;
    }

    @Override
    EmailAddress propertyValue() {
        return EmailAddress.parse("user@example.com");
    }

    @Override
    String propertyValueType() {
        return EmailAddress.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerEmailAddress> type() {
        return SpreadsheetMetadataPropertyValueHandlerEmailAddress.class;
    }
}
