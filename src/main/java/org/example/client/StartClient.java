package org.example.client;

import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;
import org.example.Test;
import org.example.TestServiceGrpc;
import org.example.server.TestServiceGrpcImpl;

import java.util.concurrent.CountDownLatch;

public class StartClient {
    public static void main(String[] args) throws InterruptedException {
        String target = args[0];
        final NettyChannelBuilder channelBuilder = NettyChannelBuilder
                .forTarget(target)
                .usePlaintext()
                .maxInboundMessageSize(Integer.MAX_VALUE);

        ManagedChannel channel = channelBuilder.build();
        TestServiceGrpc.TestServiceStub stub = TestServiceGrpc.newStub(channel);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        StreamObserver<Test.MyMessage> call = stub.hello(new StreamObserver<>() {
            @Override
            public void onNext(Test.MyMessage value) {
                System.out.println("onNext");
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();

    }
}
