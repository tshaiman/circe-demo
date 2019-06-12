import circeDemo.json.{PersonCodec}
import circeDemo.model.{Person, University}
import io.circe
import io.circe.Printer
import io.circe.parser._
import io.circe.syntax._
import io.circe.parser.decode
import org.joda.time.DateTime

object CirceDemoApp extends App{
  implicit val jsonReads = PersonCodec.decodePerson
  implicit val jsonWrites = PersonCodec.encodePerson
  implicit  val uniRead = PersonCodec.decodeUniversity
  implicit  val uniWriter = PersonCodec.encodeUniversity

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

  val uni2 = decode[University](uniJson).right
  println(uni2)



}
