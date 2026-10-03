package idlers.worldmap;

import java.nio.charset.StandardCharsets;

/** Big-endian reads over an archive entry, the way Jagex files store numbers and newline-ended strings. */
final class ByteReader {

    private static final int LINE_END = 10;

    private final byte[] data;
    private int position;

    ByteReader(byte[] data) {
        this.data = data;
    }

    void skip(int count) {
        position += count;
    }

    int remaining() {
        return data.length - position;
    }

    int u8() {
        return data[position++] & 0xff;
    }

    int u16() {
        return u8() << 8 | u8();
    }

    int u24() {
        return u8() << 16 | u16();
    }

    int s32() {
        return u16() << 16 | u16();
    }

    String line() {
        int start = position;
        while (data[position] != LINE_END) {
            position++;
        }
        String text = new String(data, start, position - start, StandardCharsets.ISO_8859_1);
        position++;
        return text;
    }
}
