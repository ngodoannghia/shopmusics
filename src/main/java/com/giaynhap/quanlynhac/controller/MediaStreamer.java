package com.giaynhap.quanlynhac.controller;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class MediaStreamer implements StreamingResponseBody {
    private Long length;
    private RandomAccessFile raf;
    final byte[] buf = new byte[4096];

    public MediaStreamer(Long length, RandomAccessFile raf) {
        this.length = length;
        this.raf = raf;
    }

    public Long getLenth() {
        return length;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        if (this.raf == null){
            return;
        }
        try {
            while( length != 0) {
                int read = raf.read(buf, 0, (int)( buf.length > length ? length : buf.length));
                outputStream.write(buf, 0, read);
                length -= read;
            }
        } finally {
            raf.close();
        }
    }
}
