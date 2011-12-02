package se.stendahls.krabbelsack

import akka.actor.Actor.actorOf
import akka.actor.{ActorRef, Actor}
import collection.mutable.{DoubleLinkedList, Queue}
import java.util.Random
import javax.print.attribute.standard.PresentationDirection

object Application extends App {

  val krabbelsack = new Krabbelsack

  val krabbelsackActor = actorOf[KrabbelsackActor]
  krabbelsackActor.start

  val erik = actorOf(new PersonActor("Erik", Beer, 1000, krabbelsackActor))
  val matsola = actorOf(new PersonActor("Mats-Ola", Honey, 1000, krabbelsackActor))
  val daniel = actorOf(new PersonActor("Daniel", Volleyball, 60000, krabbelsackActor))
  val anders = actorOf(new PersonActor("Anders", Chicken, 45000, krabbelsackActor))
  Set(erik, matsola, daniel, anders).foreach(_.start)

  val begin = new Start(krabbelsack)
  Set(erik, matsola, daniel, anders).foreach(_ ! begin)
}

// messages
case class Start(krabbelsack:Krabbelsack)

case class Present(name:String)

object Beer extends Present("Beer")
object Honey extends Present("Honey")
object Volleyball extends Present("Volleyball")
object Chicken extends Present("Chicken")

class PersonActor(name:String, initialPresent:Present, waitingTime:Long, krabbelsackActor:ActorRef) extends Actor {

  def receive = {
    case Start(krabbelsack) => {
      println(name + " is putting his present " + initialPresent + " into the krabbelsack.")
      krabbelsackActor ! initialPresent
    }
    case Present(p) => {
      println(name + " got a " + p + " as present.")
      Thread.sleep(waitingTime)
      println(name + " does not want his " + p + " and returns it to the krabbelsack.")
      krabbelsackActor ! Present(p)
    }
  }
}

class Krabbelsack {
  val presents:Set[Present] = Set()
}


class KrabbelsackActor extends Actor {

  var personQueue:Queue[ActorRef] = Queue()
  var presents:Vector[ScalaObject] = Vector()

  def receive = {

    case Present(p) => {

      println("Adding " + p + " to the krabbelsack.")
      personQueue += self.sender.get
      presents = Present(p) +: presents
      println("Krabbelsack now contains " + presents.size + " presents.")
      println("------------------------------------")

      while(presents.size > 1) {

        // fetch random present
        val randomIndex = new Random().nextInt(presents.size)
        val present = presents(randomIndex)
        presents = presents filter (_ == present)

        // give present to next person in queue
        val personActor = personQueue.dequeue
        personActor ! present
      }
    }
  }
}

case class EmployeeWish(previousPresent:Present)