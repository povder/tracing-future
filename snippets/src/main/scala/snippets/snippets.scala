package snippets

object Sync extends App {
  def m1: String = "a" + m2
  def m2: String = "b" + m3
  def m3: String = throw new Exception

  m1
}

object Async extends App {

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration.Duration
  import scala.concurrent.{Await, Future}

  def m1: Future[String] = m2.map("a" + _)
  def m2: Future[String] = m3.map("b" + _)
  def m3: Future[String] = Future(throw new Exception)

  Await.result(m1, Duration.Inf)
}

object TracingAsync extends App {

  import tracing.TracingFuture

  import scala.concurrent.Await
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration.Duration

  def m1: TracingFuture[String] = m2.map("a" + _)
  def m2: TracingFuture[String] = m3.map("b" + _)
  def m3: TracingFuture[String] = TracingFuture(throw new Exception)

  Await.result(m1, Duration.Inf)
}