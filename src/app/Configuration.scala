import org.slf4j.LoggerFactory
import java.nio.file.{Path, Files}
import java.nio.file.LinkOption

final case class Configuration(host: String, port: Int, cachePath: Path):
  require(host.nonEmpty,                "HOST must not be empty")
  require(port > 0 && port < 65536,     s"PORT must be a valid port number, got: ${port}")

  if !Files.exists(cachePath) then Files.createDirectories(cachePath)
  else if !Files.isDirectory(cachePath) then
    throw new IllegalArgumentException(s"cachePath exists but is not a directory: $cachePath")



object Configuration {
  private val logger = LoggerFactory.getLogger(getClass)

  lazy val load: Configuration = {
    Configuration(
      host = sys.env.getOrElse("HOST", sys.error("HOST not set in .env")),
      port = sys.env.getOrElse("PORT", sys.error("PORT not set in .env")).toInt,
      cachePath = Path.of(
        sys.env.getOrElse("CACHE_PATH", sys.error("CACHE_PATH not set in .env"))
      )
    )
  }
}
