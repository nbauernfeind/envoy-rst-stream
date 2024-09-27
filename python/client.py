import grpc

import test_pb2_grpc

MAX_MESSAGE_SIZE = 1024 * 1024 * 1024

import sys

with grpc.insecure_channel('localhost:8000', options=[
    ('grpc.max_message_length', MAX_MESSAGE_SIZE),
    ('grpc.max_send_message_length', MAX_MESSAGE_SIZE),
    ('grpc.max_receive_message_length', MAX_MESSAGE_SIZE),
]) as channel:
    stub = test_pb2_grpc.TestServiceStub(channel)
    print(stub)
    print(channel)
    hello = stub.Hello(iter([]))
    for response in hello:
        print('got response', len(response.data))
    print('stream completed')
    print(hello)

