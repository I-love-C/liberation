import java.io.File
import io.grpc.Status
import org.slf4j.LoggerFactory
import scala.concurrent.{Future, ExecutionContext, blocking}
import proto.conversion.{ConverterGrpc, ConversionRequest, ConversionResponse}
import org.jodconverter.local.LocalConverter
import org.jodconverter.core.office.{OfficeException, OfficeManager}
import org.jodconverter.core.document.DefaultDocumentFormatRegistry
import DefaultDocumentFormatRegistry.*
import ConverterCache.CacheKey
import ConverterCache.LocalFilePath
import java.nio.file.Path

class ExcelToPdfService(
    officeManager: OfficeManager,
    cache: ConverterCache,
    ec: ExecutionContext
) extends ConverterGrpc.Converter {
  val targetFormat = PDF
  private val logger = LoggerFactory.getLogger(getClass)

  override def convert(req: ConversionRequest): Future[ConversionResponse] = {
    val hashAttribute = ConverterRequestContext.cacheAttribute.get()
    val cachedFilePath = hashAttribute.flatMap(a => cache.get(CacheKey.fromAttribute(a)))
    cachedFilePath match
      case Some(value) => {
        val cachedPath = value.toString()
        logger.info(s"fetched ${cachedPath} from cache")
        Future(ConversionResponse(filePath = cachedPath))(ec)
      }
      case None => Future {
        blocking {
          val excelFile = new File(req.filePath)
          val pdfFile = excelFile.withExtensionFor(targetFormat)

          try {
            LocalConverter
              .make(officeManager)
              .convert(excelFile)
              .to(pdfFile)
              .as(targetFormat)
              .execute()

            val bytes = pdfFile.toString().getBytes()
            val cachedFilePath =  Path.of(pdfFile.getName()) 
            cache.put(CacheKey.hash(bytes), cachedFilePath)

            ConversionResponse(filePath = pdfFile.getAbsolutePath)
          } catch {
            case e: OfficeException =>
              logger.error(s"Conversion failed: ${e.getMessage}")
              throw Status.INTERNAL
                .withDescription(
                  s"LibreOffice failed to convert the file: ${e.getMessage}"
                )
                .asRuntimeException()
          }
        }
      }(ec)
  }
}
