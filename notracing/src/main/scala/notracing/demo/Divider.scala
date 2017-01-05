package notracing.demo

import scala.concurrent.{ExecutionContext, Future}

class Divider {

  def divide2(x: Int)(implicit ec: ExecutionContext): Future[Int] = {
    Future(2 / x)
  }
}
