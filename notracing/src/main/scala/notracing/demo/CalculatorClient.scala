package notracing.demo

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

object CalculatorClient extends App {

  implicit val ec = ExecutionContext.global
  val calc = new Calculator()

  val finalResultFut = for {
    r1 <- calc.calculate(1, 2)
    r2 <- calc.calculate(0, 4)
    r3 <- calc.calculate(5, 3)
  } yield r1 * r2 * r3

  val finalResult = Await.result(finalResultFut, Duration.Inf)
  println(s"final result = $finalResult")

}
