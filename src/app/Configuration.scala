import org.slf4j.LoggerFactory

case class Configuration(host: String, port: Int)

object Configuration {
  private val logger = LoggerFactory.getLogger(getClass)

  lazy val load: Configuration = {
    Configuration(
      host = sys.env.getOrElse("HOST", sys.error("HOST not set in .env")),
      port = sys.env.getOrElse("PORT", sys.error("PORT not set in .env")).toInt
    )
  }
}
