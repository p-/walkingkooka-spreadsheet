package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.MemberVisibility;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetDeltaTestCase2<D extends SpreadsheetDelta> extends SpreadsheetDeltaTestCase<D>
        implements HashCodeEqualsDefinedTesting<D>,
        HasJsonNodeTesting<D>,
        ToStringTesting<D> {

    SpreadsheetDeltaTestCase2() {
        super();
    }

    @Test
    public final void testWindowReadOnly() {
        final SpreadsheetDelta delta = SpreadsheetDelta.with(this.id(), this.cells())
                .setWindow(this.window());
        final List<Range<SpreadsheetCellReference>> window = delta.window();

        assertThrows(UnsupportedOperationException.class, () -> {
            window.add(Range.singleton(SpreadsheetCellReference.parse("Z99")));
        });

        this.checkWindow(delta, this.window());
    }

    @Test
    public final void testSetCellsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setCells(this.cells()));
    }

    @Test
    public final void testSetWindowsSame() {
        final D delta = this.createSpreadsheetDelta();
        assertSame(delta, delta.setWindow(this.window()));
    }

    @Test
    public final void testSetDifferentWindow() {
        final D delta = this.createSpreadsheetDelta();

        final List<Range<SpreadsheetCellReference>> window = this.window0("A1:Z9999");
        assertNotEquals(window, this.window());

        final SpreadsheetDelta different = delta.setWindow(window);

        this.checkId(different);
        this.checkCells(different);
        this.checkWindow(different, window);

        this.checkId(delta);
        this.checkCells(delta);
        this.checkWindow(delta);
    }

    @Test
    public final void testSetDifferentWindowFilters() {
        this.setDifferentWindowFilters("B1:Z99", "Z999:Z9999");
    }

    @Test
    public final void testSetDifferentWindowFilters2() {
        this.setDifferentWindowFilters("A99:A100", "B1:Z99");
    }

    private void setDifferentWindowFilters(final String range1, final String range2) {
        final SpreadsheetDelta delta = this.createSpreadsheetDelta();

        final List<Range<SpreadsheetCellReference>> window = this.window0(range1, range2);
        final SpreadsheetDelta different = delta.setWindow(window);

        this.checkId(different);
        this.checkCells(different, Sets.of(this.b2(), this.c3()));
        this.checkWindow(different, window);

        this.checkId(delta);
        this.checkCells(delta, Sets.of(this.a1(), this.b2(), this.c3()));
        this.checkWindow(delta);
    }

    // equals....................................................................................................

    @Test
    public final void testDifferentId() {
        this.checkNotEquals(this.createSpreadsheetDelta(this.id(), Sets.of(this.cell("A1", "99"))));
    }

    @Test
    public final void testDifferentCells() {
        this.checkNotEquals(this.createSpreadsheetDelta(SpreadsheetId.with(999), this.cells()));
    }

    final D createSpreadsheetDelta() {
        return this.createSpreadsheetDelta(this.id(), this.cells());
    }

    abstract D createSpreadsheetDelta(final SpreadsheetId id, final Set<SpreadsheetCell> cells);

    abstract List<Range<SpreadsheetCellReference>> window();

    final List<Range<SpreadsheetCellReference>> window0(final String... range) {
        return Arrays.stream(range)
                .map(SpreadsheetCellReference::parseRange)
                .collect(Collectors.toList());
    }

    final void checkWindow(final SpreadsheetDelta delta) {
        this.checkWindow(delta, this.window());
    }

    final void checkWindow(final SpreadsheetDelta delta, final List<Range<SpreadsheetCellReference>> window) {
        assertEquals(window, delta.window(), "window");
    }

    // ClassTesting...............................................................................................

    @Override
    public final MemberVisibility typeVisibility() {
        return MemberVisibility.PACKAGE_PRIVATE;
    }

    // HashCodeDefinedTesting...............................................................................................

    @Override
    public final D createObject() {
        return this.createSpreadsheetDelta();
    }

    // HasJsonTesting...............................................................................................

    @Override
    public final D createHasJsonNode() {
        return this.createSpreadsheetDelta();
    }

    // helpers...............................................................................................

    @Override
    public final D fromJsonNode(final JsonNode jsonNode) {
        return Cast.to(SpreadsheetDelta.fromJsonNode(jsonNode));
    }
}