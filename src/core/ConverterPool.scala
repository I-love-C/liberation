import org.jodconverter.local.office.LocalOfficeManager

object ConverterPool {
  val maxDocumentSize = 200
  val maxTaskTimeout = 120000L // 2 minutes
  private val portList = Seq(
    Configuration.defaultPort + 1
  ) // should instance count be modifiable via config file?

  lazy val instanceManager = LocalOfficeManager
    .builder()
    .maxTasksPerProcess(maxDocumentSize)
    .portNumbers(portList *)
    .taskExecutionTimeout(maxTaskTimeout)
    .build()
}
