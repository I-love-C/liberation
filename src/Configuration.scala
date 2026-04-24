import io.github.cdimascio.dotenv.Dotenv
import org.slf4j.LoggerFactory

case class Configuration(host: String, port: Int)

object Configuration {
  private val logger = LoggerFactory.getLogger(getClass)
  private val env = Dotenv
    .configure()
    .ignoreIfMissing() // ignore allowed, we have fallbacks
    .load()
  private def getEnv(key: String): Option[String] = Option(env.get(key))

  val defaultPort = 8008
  val defaultHost = "localhost"

  lazy val load: Configuration = {
    Configuration(
      host = getEnv("HOST").getOrElse {
        logger.warn("HOST missing in .env, falling back to '{}'", defaultHost)
        defaultHost
      },
      port = getEnv("PORT").map(_.toInt).getOrElse {
        logger.warn("PORT missing in .env, falling back to {}", defaultPort)
        defaultPort
      }
    )
  }
}
