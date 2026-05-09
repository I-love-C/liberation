import java.nio.file.{Path, Files}
import java.util.Base64
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import org.slf4j.LoggerFactory

enum DigestAlgorithm(val name: String) { case SHA256 extends DigestAlgorithm("SHA-256") }

object ConverterCache {
  private val encoding = DigestAlgorithm.SHA256;

  opaque type CacheKey = String; object CacheKey:
    def hash(content: Array[Byte]): CacheKey =
      require(content.nonEmpty, "no content provided, cannot hash empty content")
      val hashedBytes = MessageDigest.getInstance(encoding.name).digest(content);
      Base64.getUrlEncoder.withoutPadding.encodeToString(hashedBytes)

    def fromAttribute(attribute: String): CacheKey = attribute

  // relative path because I might down the line want to do directories too
  // and keeping the path might be useful, it would otherwise just be fileName and be 1 level deep
  opaque type LocalFilePath = String; object LocalFilePath:
    def getPath(dirCache: Path, relativePath: Path): Option[LocalFilePath] =
      val path = dirCache.resolve(relativePath)
      if Files.exists(path) then Some(path.toString()) else None

    def fromPath(pathString : String): LocalFilePath = pathString
}

final case class ConverterCache(private val cacheDirectoryPath: Path):
  import ConverterCache.*
  private val logger = LoggerFactory.getLogger(getClass)
  private lazy val cache = ConcurrentHashMap[CacheKey, LocalFilePath]()

  def get(key: CacheKey): Option[LocalFilePath] = {
    logger.info(s"Searched for ${key.toString()} in cache")
    Option(cache.get(key))
  }
  def put(key: CacheKey, filename: Path): Unit =  {
    LocalFilePath.getPath(cacheDirectoryPath, filename) match
     	case Some(value) => {
        logger.info(s"Placed ${key.toString()} : ${filename.toString()} in cache")
        cache.put(key, value)
      }
     	case None => logger.warn(s"file ${filename.toString()} couldn't be placed in cache")
  }
