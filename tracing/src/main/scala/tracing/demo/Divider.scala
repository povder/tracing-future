package tracing.demo

import tracing.TracingFuture

import scala.concurrent.ExecutionContext

class Divider {

  def divide2(x: Int)(implicit ec: ExecutionContext): TracingFuture[Int] = {
    TracingFuture {
      2 / x
    }
  }
}
