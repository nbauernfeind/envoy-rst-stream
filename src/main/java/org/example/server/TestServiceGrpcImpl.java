package org.example.server;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.example.Test;
import org.example.TestServiceGrpc;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class TestServiceGrpcImpl extends TestServiceGrpc.TestServiceImplBase {
    private static final ExecutorService execService = Executors.newSingleThreadExecutor();
    private static final ByteString data;
    static {
        // 1m bytes is usually too small, 4m is usually about right
        int bytes = 4_000_000;
        int count = bytes / 4;

        // send negative values, ending in 0x00 00 00 00, so we can visually how many are left
        ByteBuffer bb = ByteBuffer.allocate(bytes);
        IntStream.rangeClosed(-count + 1, 0).forEach(bb::putInt);
        bb.flip();
        data = ByteString.copyFrom(bb);
        System.out.println(data.size() + " bytes");
    }
    @Override
    public StreamObserver<Test.MyMessage> hello(StreamObserver<Test.MyMessage> responseObserver) {
        // end a bunch of data off-thread
        execService.submit(() -> {
            responseObserver.onNext(Test.MyMessage.newBuilder()
                    .setData(data)
                    .build());
            // half-close right away - note that a delay here will prevent the bug
            responseObserver.onCompleted();
        });
        // return a no-op observer
        return new StreamObserver<>() {
            @Override
            public void onNext(Test.MyMessage value) {}
            @Override
            public void onError(Throwable t) {}
            @Override
            public void onCompleted() {}
        };
    }
}
