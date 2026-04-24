import io.grpc.ServerBuilder
import java.util.logging.Logger
import scala.concurrent.{ExecutionContext, Future}
import proto.conversion.{ConverterGrpc, ConversionRequest, ConversionResponse}
import io.grpc.protobuf.services.ProtoReflectionService

object ConverterServer {
  private val logger = Logger.getLogger(classOf[ConverterServer].getName)

  def main(args: Array[String]): Unit = {
    val config = Configuration.load
    val server = new ConverterServer(ExecutionContext.global)
    server.start(config)
    server.blockUntilShutdown()
  }
}

class ConverterServer(ctx: ExecutionContext) { self =>
  private var server: Option[io.grpc.Server] = None

  private def start(config: Configuration): Unit = {
    server = Some(
      ServerBuilder
        .forPort(config.port)
        .addService(ConverterGrpc.bindService(new ConverterImpl, ctx))
        .addService(ProtoReflectionService.newInstance())
        .build()
        .start()
    )
    ConverterServer.logger.info(s"Server started, listening on ${config.port}")
    sys.addShutdownHook {
      self.stop()
      ConverterServer.logger.info("Server shut down")
    }
  }

  private def stop(): Unit = server.foreach(_.shutdown())
  private def blockUntilShutdown(): Unit = server.foreach(_.awaitTermination())

  private class ConverterImpl extends ConverterGrpc.Converter {
    override def convert(req: ConversionRequest) = {
      // call to libre office here

      val resp = ConversionResponse(filePath = s"Converted : ${req.filePath}")
      Future.successful(resp)
    }
  }
}
