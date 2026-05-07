import io.grpc.{Metadata, Context}

enum ConverterAttributes(val name : String) { case CacheHash extends ConverterAttributes("x-converter-cache-hash") }

case class ConverterAttribute[T](val metadataKey : Metadata.Key[T], val contextKey : Context.Key[T]) {
  def get(): Option[T] = Option(contextKey.get())
  def extract(headers: Metadata): Option[T] = Option(headers.get(metadataKey))
  def withValue(ctx: Context, value: T): Context = ctx.withValue(contextKey, value)
}

object  ConverterRequestContext {
  val cacheAttribute = ConverterAttribute(
    metadataKey = Metadata.Key.of(ConverterAttributes.CacheHash.name, Metadata.ASCII_STRING_MARSHALLER),
    contextKey = Context.key(ConverterAttributes.CacheHash.name)
  )
}
