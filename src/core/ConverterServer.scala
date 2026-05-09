import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext
import proto.conversion.ConverterGrpc
import io.grpc.{Server, ServerBuilder}
import io.grpc.protobuf.services.ProtoReflectionService

class ConverterServer(
    private val port: Int,
    private val service: ConverterGrpc.Converter,
    private val cache: ConverterCache,
    private val ctx: ExecutionContext
) {
  private var server: Option[Server] = None
  private val logger = LoggerFactory.getLogger(getClass)

  def start(): Unit = server match
   	case Some(_) => logger.warn("server is already running, ignoring start request")
   	case None => {
      server = Some(
        ServerBuilder
          .forPort(port)
          .intercept(ConverterInterceptor(cache))
          .addService(ProtoReflectionService.newInstance())
          .addService(ConverterGrpc.bindService(service, ctx))
          .build()
          .start()
      )
      logger.info(s"converter server built and started on port ${port}")
    }

  def stop(): Unit = server match
    case Some(grpcServer) =>
      grpcServer.shutdown()
      server = None
      logger.info("grpc server in Converter stopped")
    case None => logger.warn("server is already stopped, ignoring stop request")

  def blockUntilShutdown(): Unit = server.foreach(_.awaitTermination())
}
