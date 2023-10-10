import grpc

import test_pb2_grpc

MAX_MESSAGE_SIZE = 100 * 1024 * 1024

import sys

with grpc.insecure_channel(sys.argv[1], options=[
    ('grpc.max_message_length', MAX_MESSAGE_SIZE),
    ('grpc.max_send_message_length', MAX_MESSAGE_SIZE),
    ('grpc.max_receive_message_length', MAX_MESSAGE_SIZE),
]) as channel:
    stub = test_pb2_grpc.TestServiceStub(channel)
    for response in stub.Hello(iter([])):
        print('got response')