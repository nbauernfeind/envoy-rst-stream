package org.example.client;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.example.Test;
import org.example.TestServiceGrpc;

import java.util.concurrent.CountDownLatch;

public class StartClient {
    public static CountDownLatch countDownLatch = new CountDownLatch(2);

    public static void main(String[] args) throws InterruptedException {
        String target = args[0];
        System.out.println(target);
        final NettyChannelBuilder channelBuilder = NettyChannelBuilder
                .forTarget(target)
                .usePlaintext()
                .maxInboundMessageSize(Integer.MAX_VALUE);

        ManagedChannel channel = channelBuilder.build();
        TestServiceGrpc.TestServiceStub stub = TestServiceGrpc.newStub(channel);

        StreamObserver<Test.MyMessage> c1 = stub.hello(new HelloObserver("c1"));
        StreamObserver<Test.MyMessage> c2 = stub.hello(new HelloObserver("c2"));

        countDownLatch.await();
        c1.onCompleted();
        c2.onCompleted();
    }

    public static class HelloObserver implements StreamObserver<Test.MyMessage> {
        private final String id;

        public HelloObserver(String id) {
            this.id = id;
        }

        @Override
        public void onNext(Test.MyMessage value) {
            System.out.println(id + ":onNext " + value.getData().size());
        }

        @Override
        public void onError(Throwable t) {
            System.out.println(id + ":onError at " + System.currentTimeMillis());
            t.printStackTrace();
            countDownLatch.countDown();
        }

        @Override
        public void onCompleted() {
            System.out.println(id + ":onCompleted at " + System.currentTimeMillis());
            countDownLatch.countDown();
        }
    }
}
