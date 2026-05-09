import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext

final class ConverterApp(
    private val config: Configuration,
    private val ec: ExecutionContext
) {
  private val logger = LoggerFactory.getLogger(getClass)
  private val port = config.port
  private val cachePath = config.cachePath

  private val poolManager = new ConverterPool(port).instanceManager
  private val cache = new ConverterCache(cachePath)
  private val converter = new ExcelToPdfService(poolManager, cache, ec)
  private val grpcServer = new ConverterServer(port, converter, cache, ec)

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
        logger.error(s"Fatal exception during startup: ${e.getMessage}", e)
        stop()
        sys.exit(1)
    }
  }

  def stop(): Unit = {
    grpcServer.stop(); logger.info("gRPC server shut down")
    poolManager.stop(); logger.info("office pool freed")
  }

  def awaitTermination(): Unit = grpcServer.blockUntilShutdown()
}
