import io.grpc.Contexts
import io.grpc.ServerCall.*
import org.slf4j.LoggerFactory
import proto.conversion.ConverterGrpc
import io.grpc.{
  ServerInterceptor,
  ServerCall,
  Metadata,
  ServerCallHandler,
  Context
}

final case class ConverterInterceptor(cache: ConverterCache)
    extends ServerInterceptor {
  private val logger = LoggerFactory.getLogger(getClass)
  override def interceptCall[ReqT, RespT](
      call: ServerCall[ReqT, RespT],
      headers: Metadata,
      next: ServerCallHandler[ReqT, RespT]
  ): Listener[ReqT] = {
    val ctx = ConverterRequestContext.cacheAttribute.extract(headers) match
      case Some(hash) =>
        logger.debug(s"Request with cache header: $hash")
        ConverterRequestContext.cacheAttribute.withValue(Context.current(), hash)
      case None => Context.current()

    Contexts.interceptCall(ctx, call, headers, next)
  }
}
