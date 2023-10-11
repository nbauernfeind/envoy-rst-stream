package org.example.client;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.example.Test;
import org.example.TestServiceGrpc;

import java.util.concurrent.CountDownLatch;

public class StartClient {
    public static void main(String[] args) throws InterruptedException {
        String target = args[0];
        System.out.println(target);
        final NettyChannelBuilder channelBuilder = NettyChannelBuilder
                .forTarget(target)
                .usePlaintext()
                .maxInboundMessageSize(Integer.MAX_VALUE);

        ManagedChannel channel = channelBuilder.build();
        TestServiceGrpc.TestServiceStub stub = TestServiceGrpc.newStub(channel);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //noinspection unused - see commented out call.onCompleted
        StreamObserver<Test.MyMessage> call = stub.hello(new StreamObserver<>() {
            @Override
            public void onNext(Test.MyMessage value) {
                System.out.println("onNext " + value.getData().size());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(System.currentTimeMillis());
                t.printStackTrace();
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
                countDownLatch.countDown();
            }
        });
        // uncommenting this line will prevent the bug
//        call.onCompleted();
        countDownLatch.await();

    }
}
