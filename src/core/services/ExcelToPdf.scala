import java.io.File
import scala.concurrent.{Future, ExecutionContext, blocking}
import proto.conversion.{ConverterGrpc, ConversionRequest, ConversionResponse}
import org.jodconverter.local.LocalConverter
import org.jodconverter.core.office.{OfficeException, OfficeManager}
import org.jodconverter.core.document.DefaultDocumentFormatRegistry
import org.slf4j.LoggerFactory
import io.grpc.Status

class ExcelToPdfService(officeManager: OfficeManager, ec: ExecutionContext)
    extends ConverterGrpc.Converter {
  val targetFormat = DefaultDocumentFormatRegistry.PDF
  private val logger = LoggerFactory.getLogger(getClass)

  override def convert(req: ConversionRequest): Future[ConversionResponse] =
    Future {
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

          ConversionResponse(filePath = pdfFile.getAbsolutePath)
        } catch {
          case e: OfficeException =>
            if (pdfFile.exists()) pdfFile.delete()

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
