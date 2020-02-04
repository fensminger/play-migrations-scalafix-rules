package change.collection

sealed trait Filter {
  def field: String
}

case class EqualFilter(field: String, value: String) extends Filter

case class DiffFilter(field: String, value: String) extends Filter

case class SupFilter(field: String, value: String) extends Filter

case class InfFilter(field: String, value: String) extends Filter

case class SupEqualFilter(field: String, value: String) extends Filter

case class InfEqualFilter(field: String, value: String) extends Filter

case class InListIndexedFilter(field: String, value: List[Vector[String]]) extends Filter
case class InIndexedFilter(field: String, value: Vector[String]) extends Filter
case class InFilter(field: String, value: List[String]) extends Filter

case class BetweenFilter(field: String, bot: String, up: String) extends Filter

case class LikeFilter(field: String, value: String) extends Filter

object Filter {
  private val symbolMap = Map(
    "~" -> ((k: String, v: String) => LikeFilter(k, v.substring(1))),
    "!" -> ((k: String, v: String) => DiffFilter(k, v.substring(1))),
    ">" -> ((k: String, v: String) => SupFilter(k, v.substring(1))),
    ">=" -> ((k: String, v: String) => SupEqualFilter(k, v.substring(2))),
    "<" -> ((k: String, v: String) => InfFilter(k, v.substring(1))),
    "<=" -> ((k: String, v: String) => InfEqualFilter(k, v.substring(2))),
    "[" -> ((k: String, v: String) => {
      val lowerBound = v.substring(1).takeWhile(_ != '~')
      val upperBound = v.substring(1).dropWhile(_ != '~').tail.takeWhile(_ != ']')
      BetweenFilter(k, lowerBound, upperBound)
    })
  )

  def test(toList : List[Vector[Long]]) = {
    toList
  }

  def test2(toList : Vector[Long]) = {
    toList.map(v => " " + v).toVector
  }


  def apply(key: String, value: String): Filter = {
    if (value.contains(",")) {
      InFilter(key, value.split(",").map(_.trim).toList)
    } else {
      symbolMap.find(x => value.startsWith(x._1)).map(_._2(key, value)).getOrElse(EqualFilter(key, value))
    }
  }
}
