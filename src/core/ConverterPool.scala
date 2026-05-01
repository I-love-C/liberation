import org.jodconverter.local.office.LocalOfficeManager

class ConverterPool(config: Configuration) {
  val maxDocumentSize = 200
  val maxTaskTimeout = 120000L // 2 minutes
  private val portList = Seq(
    config.port + 1
  ) // should instance count be modifiable via config file?

  lazy val instanceManager = LocalOfficeManager
    .builder()
    .maxTasksPerProcess(maxDocumentSize)
    .portNumbers(portList *)
    .taskExecutionTimeout(maxTaskTimeout)
    .build()
}
