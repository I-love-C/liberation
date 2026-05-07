import java.io.File
import io.grpc.ServerCall.*
import io.grpc.{
  ServerInterceptor,
  ServerCall,
  Metadata,
  ServerCallHandler,
  Context
}
import proto.conversion.ConverterGrpc
import org.slf4j.LoggerFactory
import io.grpc.Metadata.Key
import io.grpc.Contexts

case class ConverterInterceptor(cache: ConverterCache)
    extends ServerInterceptor {
  private val logger = LoggerFactory.getLogger(getClass)
  override def interceptCall[ReqT, RespT](
      call: ServerCall[ReqT, RespT],
      headers: Metadata,
      next: ServerCallHandler[ReqT, RespT]
  ): Listener[ReqT] = {
    val hash = ConverterRequestContext.cacheAttribute.extract(headers)
    if (hash.isDefined) logger.debug("Request with cache headers came in")

    val ctx = hash.foldLeft(Context.current()) { (c, v) =>
      ConverterRequestContext.cacheAttribute.withValue(c, v)
    }

    Contexts.interceptCall(ctx, call, headers, next)
  }
}
