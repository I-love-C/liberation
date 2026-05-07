import java.io.File
import org.jodconverter.core.document.DocumentFormat

extension(file: File) {
  def withExtensionFor(format: DocumentFormat): File = {
    val name = file.getName
    val extension = format.getExtension()

    require(name.nonEmpty, s"Filename is empty: ${file.getAbsolutePath}")
    require(
      extension.nonEmpty && !extension.startsWith("."),
      s"malformed or non-existent extension: '$extension'"
    )

    val lastDot = name.lastIndexOf('.')
    val base = lastDot match {
      case lastDotPosition if lastDotPosition > 0 => name.take(lastDotPosition)
      case _ => name // can't be negative -> extensionless
    }

    new File(file.getParentFile, s"$base.$extension")
  }
}
