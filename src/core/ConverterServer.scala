import io.grpc.ServerBuilder
import org.slf4j.LoggerFactory
import proto.conversion.ConverterGrpc
import scala.concurrent.ExecutionContext
import io.grpc.protobuf.services.ProtoReflectionService

class ConverterServer(
    port: Int,
    service: ConverterGrpc.Converter,
    ctx: ExecutionContext
) { self =>
  private var server: Option[io.grpc.Server] = None
  private val logger = LoggerFactory.getLogger(getClass)

  def start(): Unit = {
    server = Some(
      ServerBuilder
        .forPort(port)
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
