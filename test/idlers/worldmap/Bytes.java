package idlers.worldmap;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/** Writes test archive entries the way Jagex files store them: big-endian numbers, newline-ended strings. */
final class Bytes {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    Bytes u8(int... values) {
        for (int value : values) {
            out.write(value);
        }
        return this;
    }

    Bytes u16(int value) {
        return u8(value >> 8 & 0xff, value & 0xff);
    }

    Bytes u24(int value) {
        return u8(value >> 16 & 0xff).u16(value & 0xffff);
    }

    Bytes s32(int value) {
        return u16(value >>> 16).u16(value & 0xffff);
    }

    Bytes line(String text) {
        out.writeBytes(text.getBytes(StandardCharsets.ISO_8859_1));
        return u8(10);
    }

    Bytes repeat(int value, int count) {
        for (int i = 0; i < count; i++) {
            out.write(value);
        }
        return this;
    }

    byte[] toArray() {
        return out.toByteArray();
    }
}
