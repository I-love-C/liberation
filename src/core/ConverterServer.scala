import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext
import proto.conversion.ConverterGrpc
import io.grpc.{Server, ServerBuilder}
import io.grpc.protobuf.services.ProtoReflectionService

class ConverterServer(
    port: Int,
    service: ConverterGrpc.Converter,
    cache: ConverterCache,
    ctx: ExecutionContext
) { self =>
  private var server: Option[Server] = None
  private val logger = LoggerFactory.getLogger(getClass)

  def start(): Unit = {
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

  def stop(): Unit = server.foreach(_.shutdown())
  def blockUntilShutdown(): Unit = server.foreach(_.awaitTermination())
}
