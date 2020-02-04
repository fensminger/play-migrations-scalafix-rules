package test

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

object FakeDataCatalogEntry {
  def apply(fs: Seq[String]): FakeDataCatalogEntry = new FakeDataCatalogEntry(fs)
}
class FakeDataCatalogEntry(fs: Seq[String]) {
  val location = java.util.UUID.randomUUID.toString
}

class FlatSpecTest extends AnyFlatSpec with Matchers with ScalaCheckDrivenPropertyChecks {

  class Fraction(n: Int, d: Int) {

    require(d != 0)
    require(d != Integer.MIN_VALUE)
    require(n != Integer.MIN_VALUE)

    val numer = if (d < 0) -1 * n else n
    val denom = d.abs

    override def toString = numer + " / " + denom
  }

  "test" must "be ok" in {
    forAll { (n: Int, d: Int) =>

      whenever(d != 0 && d != Integer.MIN_VALUE
        && n != Integer.MIN_VALUE) {

        val f = new Fraction(n, d)

        if (n < 0 && d < 0 || n > 0 && d > 0)
          f.numer must be > 0
        else if (n != 0)
          f.numer must be < 0
        else
          f.numer must be === 0

        f.denom must be > 0
      }
    }
  }

}
