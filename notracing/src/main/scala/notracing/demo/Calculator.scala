package notracing.demo

import scala.concurrent.{ExecutionContext, Future}

class Calculator {
  val divider = new Divider()

  def calculate(x: Int, y: Int)(implicit ec: ExecutionContext): Future[Int] = {
    val sumFut: Future[Int] = for {
      x1 <- divider.divide2(x)
      y1 <- divider.divide2(y)
      sum <- Future(x1 + y1)
    } yield sum

    sumFut
  }
}
