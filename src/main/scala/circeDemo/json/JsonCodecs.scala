package circeDemo.json
import java.time.{Instant}
import cats.implicits._

import circeDemo.model.{Person, University}
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.joda.time.DateTime
// import io.circe.{Decoder, Encoder, HCursor, Json}

object  PersonCodec {
  implicit val encodeInstant: Encoder[Instant] = Encoder.encodeString.contramap[Instant](_.toString)
  // encodeInstant: io.circe.Encoder[java.time.Instant] = io.circe.Encoder$$anon$1@1f30c0bf

  implicit val decodeInstant: Decoder[Instant] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(Instant.parse(str)).leftMap(t => "Instant")
  }

  implicit val encodePerson: Encoder[Person] = new Encoder[Person] {
    final def apply(a: Person): Json = Json.obj(
      ("student_name", Json.fromString(a.name)),
      ("student_age", Json.fromInt(a.age)),
      ("isAdult", Json.fromBoolean(a.b)),
      ("reg-time", Encoder[Instant].apply(Instant.ofEpochMilli(a.RegData.getMillis)))
    )
  }
  // encodeFoo: io.circe.Encoder[Thing] = $anon$1@50acf339

  implicit val decodePerson: Decoder[Person] = new Decoder[Person] {
    final def apply(c: HCursor): Decoder.Result[Person] =
      for {
        name <- c.downField("student_name").as[String]
        age <- c.downField("student_age").as[Int]
        isAdult <- c.downField("isAdult").as[Boolean]
        date <- c.downField("reg-time").as[Instant]
      } yield {
        new Person(name, age, isAdult,new DateTime(date.getEpochSecond * 1000))
      }
  }

  implicit val encodeUniversity: Encoder[University] = new Encoder[University] {
    final def apply(a: University): Json = Json.obj(
      ("uni_name", Json.fromString(a.name)),
      ("students", Encoder[List[Person]].apply(a.students))
    )
  }
  // encodeFoo: io.circe.Encoder[Thing] = $anon$1@50acf339

  implicit val decodeUniversity: Decoder[University] = new Decoder[University] {
    final def apply(c: HCursor): Decoder.Result[University] =
      for {
        name <- c.downField("uni_name").as[String]
        students <- c.downField("students").as[List[Person]]
      } yield {
        new University(name,students)
      }
  }
}