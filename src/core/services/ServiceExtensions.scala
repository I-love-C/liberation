import java.io.File
import org.jodconverter.core.document.DocumentFormat

extension(file: File) {
  def withExtensionFor(format: DocumentFormat): File = {
    val name = file.getName
    val lastDotPosition = name.lastIndexOf('.')
    val base = if (lastDotPosition > 0) name.take(lastDotPosition) else name
    new File(file.getParentFile, s"${base}.${format.getExtension()}")
  }
}
