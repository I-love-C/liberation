import scala.concurrent.ExecutionContext
import org.slf4j.LoggerFactory

class ConverterApp(config: Configuration, ec: ExecutionContext) {
  private val logger = LoggerFactory.getLogger(getClass)

  val poolManager = ConverterPool.instanceManager

  private val converterService =
    new ExcelToPdfService(poolManager, ec)
  private val grpcServer =
    new ConverterServer(config.port, converterService, ec)

  def start(): Unit = {
    logger.info("App startup started")

    try {
      poolManager.start(); logger.info("office pool started")
      grpcServer.start(); logger.info("gRPC server started")

      sys.addShutdownHook {
        logger.info("shutdown signal received")
        stop()
      }

      logger.info("App started")
    } catch {
      case e: Exception =>
        logger.error(s"Fatal exception in startup: ${e.getMessage}", e)
        stop()
        sys.exit(1)
    }
  }

  def stop(): Unit = {
    grpcServer.stop(); logger.info("gRPC server shut down")
    poolManager.stop(); logger.info("process pool freed")
  }

  def awaitTermination(): Unit = grpcServer.blockUntilShutdown()
}
