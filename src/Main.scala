import scala.concurrent.ExecutionContext

object Main {
  def main(args: Array[String]): Unit = {
    val config = Configuration.load
    val app = new ConverterApp(config, ExecutionContext.global)

    app.start()
    app.awaitTermination()
  }
}
