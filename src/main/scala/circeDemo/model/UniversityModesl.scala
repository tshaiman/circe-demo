package circeDemo.model

import org.joda.time.DateTime

case class Person(name:String,age:Int,b:Boolean,RegData:DateTime)

case class University(name:String, students:List[Person])

