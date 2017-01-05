package tracing.demo

import tracing.TracingFuture

import scala.concurrent.ExecutionContext

class Calculator {
  val divider = new Divider()

  def calculate(x: Int, y: Int)(implicit ec: ExecutionContext): TracingFuture[Int] = {
    val sumFut: TracingFuture[Int] = for {
      x1 <- divider.divide2(x)
      y1 <- divider.divide2(y)
      sum <- TracingFuture(x1 + y1)
    } yield sum

    sumFut
  }
}
