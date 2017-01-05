package tracing

import scala.concurrent.{CanAwait, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

class TracingFuture[+T](underlying: Future[T], val trace: Vector[FutureTraceElement]) extends Future[T] {
  def isCompleted: Boolean = underlying.isCompleted

  def value: Option[Try[T]] = underlying.value.map(tracedTry)

  def ready(atMost: Duration)(implicit permit: CanAwait): TracingFuture.this.type = {
    underlying.ready(atMost)
    this
  }

  def result(atMost: Duration)(implicit permit: CanAwait): T = {
    tracedTry(Try(underlying.result(atMost))).get
  }

  def onComplete[U](f: (Try[T]) => U)(implicit ec: ExecutionContext): Unit = underlying.onComplete(t => f(tracedTry(t)))

  def transform[S](f: (Try[T]) => Try[S])(implicit ec: ExecutionContext): Future[S] = underlying.transform(t => f(tracedTry(t)))

  def transformWith[S](f: (Try[T]) => Future[S])(implicit ec: ExecutionContext): Future[S] = underlying.transformWith(t => f(tracedTry(t)))

  def flatMap[S](f: (T) => Future[S])(implicit ec: ExecutionContext, enclosing: sourcecode.Enclosing, file: sourcecode.File, line: sourcecode.Line): TracingFuture[S] = {
    val mapped = underlying.flatMap(f)
    new TracingFuture[S](mapped, trace :+ FutureTraceElement(enclosing.value, "flatMap", file.value, line.value))
  }

  def map[S](f: (T) => S)(implicit ec: ExecutionContext, enclosing: sourcecode.Enclosing, file: sourcecode.File, line: sourcecode.Line): TracingFuture[S] = {
    val mapped = underlying.map(f)
    new TracingFuture[S](mapped, trace :+ FutureTraceElement(enclosing.value, "map", file.value, line.value))
  }

  private def tracedTry[A](t: Try[A]): Try[A] = t match {
    case s: Success[A] => s
    case Failure(ex) =>
      val origSt = ex.getStackTrace.toVector
      val enrichedSt = origSt ++ trace.map(fte => new StackTraceElement(fte.enclosing, fte.method, fte.file, fte.line))
      ex.setStackTrace(enrichedSt.toArray)
      Failure(ex)
  }

}

object TracingFuture {
  def apply[T](body: => T)(implicit ec: ExecutionContext, enclosing: sourcecode.Enclosing, file: sourcecode.File, line: sourcecode.Line): TracingFuture[T] = {
    new TracingFuture[T](Future(body), Vector(FutureTraceElement(enclosing.value, "apply", file.value, line.value)))
  }
}