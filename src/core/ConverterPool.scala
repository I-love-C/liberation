import org.jodconverter.local.office.LocalOfficeManager

class ConverterPool(config: Configuration):
  val maxTaskTimeout = 120000L // 2 minutes
  private val portList = Seq(
    config.port + 1
  ) // should instance count be modifiable via config file?

  // not sure if this is useful since +1 is there and it might just default to 0
  require(portList.nonEmpty, "office manager requires at least one port/instance")

  lazy val instanceManager = LocalOfficeManager
    .builder()
    .portNumbers(portList *)
    .taskExecutionTimeout(maxTaskTimeout)
    .build()
