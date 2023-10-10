import test_pb2_grpc
import test_pb2
from concurrent import futures
import grpc

class TestServerGrpcImpl(test_pb2_grpc.TestServiceServicer):
    def Hello(self, request_iterator, context):
        yield test_pb2.MyMessage(data=bytes([0 for i in range(0, 8000000)]))


server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
test_pb2_grpc.add_TestServiceServicer_to_server(TestServerGrpcImpl(), server)
server.add_insecure_port("localhost:8080")
server.start()
server.wait_for_termination()
