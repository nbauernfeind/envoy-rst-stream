package org.example.server;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.example.Test;
import org.example.TestServiceGrpc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestServiceGrpcImpl extends TestServiceGrpc.TestServiceImplBase {
    private static final ExecutorService execService = Executors.newSingleThreadExecutor();
    private static final ByteString data = ByteString.copyFrom(new byte[8_000_000]);
    @Override
    public StreamObserver<Test.MyMessage> hello(StreamObserver<Test.MyMessage> responseObserver) {
        // end a bunch of data off-thread
        execService.submit(() -> {
            responseObserver.onNext(Test.MyMessage.newBuilder()
                    .setData(data)
                    .build());
            responseObserver.onCompleted();
//            responseObserver.onError(new StatusRuntimeException(Status.DATA_LOSS));
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
