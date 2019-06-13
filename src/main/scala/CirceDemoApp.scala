import circeDemo.json.PersonCodec
import circeDemo.model.{Person, University}
import io.circe
import io.circe.{Json, Printer}
import io.circe.parser._
import io.circe.syntax._
import io.circe.parser.decode
import org.joda.time.DateTime


import scala.util.{Failure, Success, Try}
// Throw this exception if the list of items can't even be retrieved
case class ParseException(msg: String) extends Exception(msg)

object CirceDemoApp extends App{
  /*implicit val jsonReads = PersonCodec.decodePerson
  implicit val jsonWrites = PersonCodec.encodePerson
  implicit  val uniRead = PersonCodec.decodeUniversity
  implicit  val uniWriter = PersonCodec.encodeUniversity*/
  import PersonCodec._

  val person = Person("tomer",32,true,DateTime.now().minusYears(1))
  val pJson = person.asJson.noSpaces;

  println(pJson)

  val p2: Either[circe.Error, Person] = decode[Person](pJson)
  p2 match {
    case Right(p) => println(p)
    case Left(e) => println(e.fillInStackTrace().getMessage)
  }


  val person2 = Person("ronit",33,false,DateTime.now().minusYears(1))
  val pList = person :: person2 :: Nil
  val university = University("Ben Gurion",pList);
  val uniJson = university.asJson.pretty(Printer.indented(" "))
  println(uniJson)
  val uniStr =
    """
      |{
      | "uni_name" : "Ben Gurion",
      | "students" : [
      |  {
      |   "student_name1" : "tomer",
      |   "student_age" : 32,
      |   "isAdult" : true,
      |   "reg-time" : "2018-06-13T08:18:27.393Z"
      |  },
      |  {
      |   "student_name" : "ronit",
      |   "student_age" : 33,
      |   "isAdult" : false,
      |   "reg-time" : "2018-06-13T08:18:28.369Z"
      |  }
      | ]
      |}
    """.stripMargin
  //val uni2 = decode[University](uniStr)
  //println(uni2)
  val lst = tryListOfTries(uniStr)
  val filtered: Seq[Person] = lst match  {
    case Success(l1) => l1.filter(p=>p.isSuccess).map(x=>x.get)
    case Failure(exception) => Nil
  }
  println(filtered)

  // Turn a string into a json doc
  def jsonDoc(str: String) = parse(str).getOrElse(Json.Null)

  // Attempt to retrieve the list of json objects appearing in "items"
  def getListOfItemsAsJsonObjects(doc: Json): Try[List[Json]] = doc.hcursor.downField("students").focus match {
    case None => Failure(ParseException("Couldn't get items"))
    case Some(obj) => obj.asArray match {
      case None => Failure(ParseException("Couldn't turn to array"))
      case Some(arr) => Success(arr.toList)
    }
  }

  def tryListOfTries(str: String) = getListOfItemsAsJsonObjects(jsonDoc(str)).map(ok => ok.map(_.as[Person].toTry))


}
