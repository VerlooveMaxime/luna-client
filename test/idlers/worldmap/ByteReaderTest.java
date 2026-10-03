package idlers.worldmap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ByteReaderTest {

    @Test
    void readsBigEndianNumbers() {
        ByteReader reader = new ByteReader(new Bytes().u8(0xfe).u16(0x1234).u24(0x56789a).s32(0xcafebabe).toArray());

        assertEquals(0xfe, reader.u8());
        assertEquals(0x1234, reader.u16());
        assertEquals(0x56789a, reader.u24());
        assertEquals(0xcafebabe, reader.s32());
    }

    @Test
    void readsANewlineEndedLatinOneString() {
        ByteReader reader = new ByteReader(new Bytes().line("Seers'/Villageé").u8(7).toArray());

        assertEquals("Seers'/Villageé", reader.line());
        assertEquals(7, reader.u8());
    }

    @Test
    void skipMovesForwardAndRemainingCountsDown() {
        ByteReader reader = new ByteReader(new byte[5]);

        reader.skip(3);

        assertEquals(2, reader.remaining());
    }
}
