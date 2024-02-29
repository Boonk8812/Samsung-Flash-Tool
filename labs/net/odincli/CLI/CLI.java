import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HexFormat;

public class CLI {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java MemFileChecker <path_to_mem_file>");
            return;
        }

        String filePath = args[0];
        File memFile = new File(filePath);

        // Check if file exists
        if (!memFile.exists()) {
            System.err.println("Error: File does not exist.");
            return;
        }

        long fileSize = memFile.length();
        byte[] headerBytes = readHeader(filePath, 0, HEADER_LENGTH);

        HexFormat formatter = new HexFormat();
        StringBuilder sb = new StringBuilder();

        // Display file size
        System.out.printf("File Size: %d bytes%n", fileSize);

        // Display header data and expected data
        System.out.print("Header Data: ");
        for (byte b : headerBytes) {
            sb.append(String.format("%02X ", b));
        }
        System.out.println("\tExpected: " + getExpectedHeader());
        System.out.println("-------------------------------");
        System.out.println("Comparison Result:");

        boolean valid = true;
        int i = 0;
        while (i < HEADER_LENGTH && valid) {
            if ((headerBytes[i] & 0xFF) != (getExpectedHeader()[i] & 0xFF)) {
                valid = false;
            }
            i++;
        }

        if (valid) {
            System.out.println("Valid Header!");
        } else {
            System.out.println("Invalid Header!");
        }

        System.out.println("Detailed Comparison:");
        for (int index = 0; index < HEADER_LENGTH; index++) {
            System.out.printf("%s\t%s%n", formatter.formatData(new byte[]{headerBytes[index]}), formatter.formatData(getExpectedHeader()[index]));
        }
    }

    private static byte[] readHeader(String filePath, long offset, int length) throws IOException {
        try (RandomAccessFile input = new RandomAccessFile(filePath, "r")) {
            input.seek(offset);
            byte[] buffer = new byte[length];
            input.read(buffer);
            return buffer;
        }
    }

    private static byte[] getExpectedHeader() {
        return new byte[]{
                0xF7, 0x20, 0xC7, 0x29, 0xA7, 0x39, 0xF9, 0x7C,
                0x08, 0xEF, 0xC6, 0x28, 0xA8, 0x36, 0xF8, 0x63,
                0x00, 0xC7, 0xA7, 0xF7, 0xC6, 0x18, 0x00, 0x00,
                0x00, 0x0C, 0x7E, 0x41, 0xA8, 0x8C, 0x60, 0x0E,
                0x8A, 0x82, 0x6F, 0xC8, 0xFF, 0x9F, 0x7A, 0x93,
                0x7C, 0x92, 0x7E, 0xA0, 0x92, 0xE8, 0x37, 0xA0,
                0x00, 0x17, 0xC8, 0x39, 0xA8, 0x29, 0xFF, 0x92,
                0x20, 0xF8, 0xFC, 0x79, 0xA8
        };
    }

    private static final int HEADER_LENGTH = getExpectedHeader().length;
}