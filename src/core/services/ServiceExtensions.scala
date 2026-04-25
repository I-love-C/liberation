import java.io.File
import org.jodconverter.core.document.DocumentFormat

extension(file: File) {
  def withExtensionFor(format: DocumentFormat): File = {
    val name = file.getName
    val base =
      if (name.lastIndexOf('.') > 0) name.take(name.lastIndexOf('.')) else name
    new File(file.getParentFile, s"${base}.${format.getExtension()}")
  }
}
