import grpc
import test_pb2
import test_pb2_grpc

MAX_MESSAGE_SIZE = 1024 * 1024 * 1024

import sys
from queue import SimpleQueue

class RequestStream:
    def __init__(self, req_queue: SimpleQueue):
        self.req_queue = req_queue
        self._sentinel = object()

    def write(self, payload: bytes) -> None:
        msg = test_pb2.MyMessage(data=payload)
        self.req_queue.put(msg)

    def __next__(self):
        if (req := self.req_queue.get()) != self._sentinel:
            return req
        else:
            raise StopIteration

    def __iter__(self):
        return self

    def close(self) -> None:
        self.req_queue.put(self._sentinel)

host = 'localhost:8000'
if (len(sys.argv) > 1):
    host = sys.argv[1]

with grpc.insecure_channel(host, options=[
    ('grpc.max_message_length', MAX_MESSAGE_SIZE),
    ('grpc.max_send_message_length', MAX_MESSAGE_SIZE),
    ('grpc.max_receive_message_length', MAX_MESSAGE_SIZE),
]) as channel:
    stub = test_pb2_grpc.TestServiceStub(channel)
    print(stub)
    print(channel)

    rs1 = RequestStream(SimpleQueue())
    rs2 = RequestStream(SimpleQueue())

    r1 = stub.Hello(rs1)
    rs1.write(b'1')

    # if we open this after completing r1, it no longer RST_STREAM errors
    r2 = stub.Hello(rs2)
    rs2.write(b'2')
    for response in r2:
        print('r2 got response', len(response.data))
    print('r2 stream completed')
    rs2.close()

    for response in r1:
        print('r1 got response', len(response.data))
    print('r1 stream completed')
    rs1.close()

    # if we open this after completing r1, it no longer RST_STREAM errors
    # r2 = stub.Hello(rs2)

