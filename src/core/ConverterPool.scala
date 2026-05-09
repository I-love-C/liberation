import org.jodconverter.local.office.LocalOfficeManager

final class ConverterPool(serverPort: Int):
  private val maxTaskTimeout = 120000L        // 2 minutes
  private val portList = Seq(serverPort + 1)  // currently has ONE instance and is placed right after the server

  lazy val instanceManager = LocalOfficeManager
    .builder()
    .portNumbers(portList *)
    .taskExecutionTimeout(maxTaskTimeout)
    .build()
